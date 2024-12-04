package server;


import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.Connect;
import websocket.commands.Leave;
import websocket.commands.MakeMove;
import websocket.commands.Resign;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.io.IOException;

import static java.lang.System.out;

@WebSocket
public class WebSocketHandler {


    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception {
        Server.gameSessions.put(session, 0);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        Server.gameSessions.remove(session);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        out.printf("Received: %s\n", message);

        if (message.contains("\"commandType\":\"RESIGN\"")) {
            Resign cmd = new Gson().fromJson(message, Resign.class);
            handleResign(session, cmd);
        }
        if(message.contains("\"commandType\":\"CONNECT\"")){
            Connect cmd = new Gson().fromJson(message, Connect.class);
            Server.gameSessions.replace(session, cmd.getGameID());
            handleConnect(session, cmd);
        }
        if(message.contains("\"commandType\":\"LEAVE\"")){
            Leave cmd = new Gson().fromJson(message, Leave.class);
            handleLeave(session, cmd);
        }
        if(message.contains("\"commandType\":\"MAKE_MOVE\"")){
            MakeMove cmd = new Gson().fromJson(message, MakeMove.class);
            handleMakeMove(session, cmd)
        }
    }

    private void handleMakeMove(Session session, MakeMove cmd) {

    }

    private void handleLeave(Session session, Leave cmd) throws IOException {
        try {
            AuthData auth = Server.userService.getAuthToken(cmd.getAuthToken());

            Notification notif = new Notification("%s has left the game".formatted(auth.username()));
            broadcastMessage(session, notif);

            session.close();
        } catch (UnauthorizedException e) {
            sendError(session, new Error("Error: Not authorized"));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    private void handleConnect(Session session, Connect cmd) throws UnauthorizedException, DataAccessException, IOException {
        AuthData authData = Server.userService.getAuthToken(cmd.getAuthToken());
        GameData gameData = Server.gameService.getGameData(cmd.getGameID());
        String username = authData.username();
        ChessGame.TeamColor color = getTeamColor(username, gameData);

        if(color == null){
            Notification notif = new Notification("%s has joined the game as an observer".formatted(username));
            broadcastMessage(session, notif);
        }else{
            Notification notif = new Notification("%s has joined the game as %s".formatted(username, color.toString()));
            broadcastMessage(session, notif);
        }

    }

    private void handleResign(Session session, Resign cmd) throws UnauthorizedException, DataAccessException, IOException, BadRequestException {
        AuthData authData = Server.userService.getAuthToken(cmd.getAuthToken());
        out.println(authData);
        GameData gameData = Server.gameService.getGameData(cmd.getGameID());
        out.println(gameData);

        ChessGame.TeamColor userColor = getTeamColor(authData.username(), gameData);

        String opponentUsername = userColor == ChessGame.TeamColor.WHITE ? gameData.blackUsername() : gameData.whiteUsername();

        if (userColor == null) {
            sendError(session, new Error("Error: You are observing this game"));
            return;
        }

        if (gameData.game().getGameOver()) {
            sendError(session, new Error("Error: The game is already over!"));
            return;
        }

        gameData.game().setGameOver(true);

        Server.gameService.updateGame(authData.authToken(), gameData);

        Notification notif = new Notification("%s has forfeited, %s wins!".formatted(authData.username(), opponentUsername));
        broadcastMessage(session, notif, true);
    }

    public void broadcastMessage(Session currSession, ServerMessage message) throws IOException {
        broadcastMessage(currSession, message, false);
    }

    // Send the notification to all clients on the current game
    public void broadcastMessage(Session currSession, ServerMessage message, boolean toSelf) throws IOException {
        System.out.printf("Broadcasting (toSelf: %s): %s%n", toSelf, new Gson().toJson(message));
        for (Session session : Server.gameSessions.keySet()) {
            boolean inAGame = Server.gameSessions.get(session) != 0;
            boolean sameGame = Server.gameSessions.get(session).equals(Server.gameSessions.get(currSession));
            boolean isSelf = session == currSession;
            if ((toSelf || !isSelf) && inAGame && sameGame) {
                sendMessage(session, message);
            }
        }
    }

    public void sendMessage(Session session, ServerMessage message) throws IOException {
        session.getRemote().sendString(new Gson().toJson(message));
    }

    private void sendError(Session session, Error error) throws IOException {
        System.out.printf("Error: %s%n", new Gson().toJson(error));
        session.getRemote().sendString(new Gson().toJson(error));
    }

    private ChessGame.TeamColor getTeamColor(String username, GameData game) {
        if (username.equals(game.whiteUsername())) {
            return ChessGame.TeamColor.WHITE;
        }
        else if (username.equals(game.blackUsername())) {
            return ChessGame.TeamColor.BLACK;
        }
        else return null;
    }
}
