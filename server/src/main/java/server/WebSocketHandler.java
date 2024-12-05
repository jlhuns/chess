package server;


import chess.ChessGame;
import chess.InvalidMoveException;
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
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;
import websocket.messages.Error;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.System.out;

@WebSocket
public class WebSocketHandler {
    static ConcurrentHashMap<Session, Integer> gameSessions = new ConcurrentHashMap<>();


    @OnWebSocketConnect
    public void onConnect(Session session) throws Exception {

        gameSessions.put(session, 0);
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        gameSessions.remove(session);
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
            gameSessions.replace(session, cmd.getGameID());
            handleConnect(session, cmd);
        }
        if(message.contains("\"commandType\":\"LEAVE\"")){
            Leave cmd = new Gson().fromJson(message, Leave.class);
            handleLeave(session, cmd);
        }
        if(message.contains("\"commandType\":\"MAKE_MOVE\"")){
            MakeMove cmd = new Gson().fromJson(message, MakeMove.class);
            handleMakeMove(session, cmd);
        }
    }

    private void handleMakeMove(Session session, MakeMove cmd) throws IOException {
        try {
            AuthData auth = Server.userService.getAuthToken(cmd.getAuthToken());
            GameData game = Server.gameService.getGameData(cmd.getGameID());
            ChessGame.TeamColor userColor = getTeamColor(auth.username(), game);
            if (userColor == null) {
                sendError(session, new Error("Error: You are observing this game"));
                return;
            }

            if (game.game().getGameOver()) {
                sendError(session, new Error("Error: can not make a move, game is over"));
                return;
            }

            if (game.game().getTeamTurn().equals(userColor)) {
                game.game().makeMove(cmd.getMove());

                Notification notif;
                ChessGame.TeamColor opponentColor = userColor == ChessGame.TeamColor.WHITE ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;

                if (game.game().isInCheckmate(opponentColor)) {
                    notif = new Notification("Checkmate! %s wins!".formatted(auth.username()));
                    game.game().setGameOver(true);
                }
                else if (game.game().isInStalemate(opponentColor)) {
                    notif = new Notification("Stalemate caused by %s's move! It's a tie!".formatted(auth.username()));
                    game.game().setGameOver(true);
                }
                else if (game.game().isInCheck(opponentColor)) {
                    notif = new Notification("A move has been made by %s, %s is now in check!".formatted(auth.username(), opponentColor.toString()));
                }
                else {
                    notif = new Notification("A move has been made by %s".formatted(auth.username()));
                }
                broadcastMessage(session, notif);

                Server.gameService.updateGame(auth.authToken(), game);

                LoadGame load = new LoadGame(game.game());
                broadcastMessage(session, load, true);
            }
            else {
                sendError(session, new Error("Error: it is not your turn"));
            }
        }
        catch (UnauthorizedException e) {
            sendError(session, new Error("Error: Not authorized"));
        } catch (BadRequestException e) {
            sendError(session, new Error("Error: invalid game"));
        } catch (InvalidMoveException e) {
            System.out.println("****** error: " + e.getMessage() + "  " + cmd.getMove().toString());
            sendError(session, new Error("Error: invalid move (you might need to specify a promotion piece)"));
        } catch (DataAccessException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleLeave(Session session, Leave cmd) throws IOException {
        try {
            AuthData auth = Server.userService.getAuthToken(cmd.getAuthToken());
            GameData gameData = Server.gameService.getGameData(cmd.getGameID());

            Notification notif = new Notification("%s has left the game".formatted(auth.username()));
            broadcastMessage(session, notif);

            ChessGame.TeamColor color = getTeamColor(auth.username(), gameData);

            GameData updatedGameData = switch (color) {
                case WHITE -> new GameData(gameData.gameID(), null, gameData.blackUsername(), gameData.gameName(), gameData.game());
                case BLACK -> new GameData(gameData.gameID(), gameData.whiteUsername(), null, gameData.gameName(), gameData.game());
                default -> gameData;
            };

            session.close();
            Server.gameService.updateGame(auth.authToken(), updatedGameData);
        } catch (UnauthorizedException e) {
            sendError(session, new Error("Error: Not authorized"));
        } catch (DataAccessException | BadRequestException e) {
            throw new RuntimeException(e);
        }
    }
    private void handleConnect(Session session, Connect cmd) throws UnauthorizedException, DataAccessException, IOException {
        ChessGame.TeamColor userColor = null;
        try {
            AuthData auth = Server.userService.getAuthToken(cmd.getAuthToken());
            GameData game = Server.gameService.getGameData(cmd.getGameID());
            if(game == null){
                sendError(session, new Error("Error: Not a valid game"));
            }
            if(auth == null){
                sendError(session, new Error("Error: Not authorized"));
            }

            userColor = getTeamColor(auth.username(), game);


            Notification notif;
            if(userColor != null){
                notif = new Notification("%s has joined the game as %s".formatted(auth.username(), userColor.toString()));
            }else{
                notif = new Notification("%s has joined the game as an observer".formatted(auth.username()));

            }
            broadcastMessage(session, notif);
            try{
                LoadGame load = new LoadGame(game.game());
                sendMessage(session, load);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (UnauthorizedException e) {
            sendError(session, new Error("Error: Not authorized"));
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
            sendError(session, new Error ("Error: You are observing this game"));
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
        for (Session session : gameSessions.keySet()) {
            boolean inAGame = gameSessions.get(session) != 0;
            boolean sameGame = gameSessions.get(session).equals(gameSessions.get(currSession));
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
        if(game == null){
            return null;
        }
        if (username.equals(game.whiteUsername())) {
            return ChessGame.TeamColor.WHITE;
        }
        else if (username.equals(game.blackUsername())) {
            return ChessGame.TeamColor.BLACK;
        }
        else {
            return null;
        }
    }
}
