package client;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import model.GameData;
import websocket.commands.*;

import java.util.List;

public class ServerFacade {
    private final String serverDomain;
    HTTPHandler handler;
    String authToken;
    WebSocketCommunicator ws;



    public ServerFacade() throws Exception {
        this("localhost:8080");
    }

    public ServerFacade(String serverDomain) throws Exception {
        this.serverDomain = serverDomain;
        handler = new HTTPHandler(this, serverDomain);
    }

    public boolean register(String username, String password, String email){
        return handler.register(username, password, email);
    }
    public boolean login(String username, String password){
        return handler.login(username, password);
    }
    public boolean logout(){
        return handler.logout(this.authToken);
    }
    public int createGame(String gameName){
        return handler.createGame(gameName);
    }
    public List<GameData> listGames(){
        return handler.listGames();
    }
    public boolean joinGame(int gameName, String teamColor){
        return handler.joinGame(gameName, teamColor);
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }

    //Web Socket Functions:
    public void connectWS() {
        try {
            ws = new WebSocketCommunicator(serverDomain);
        }
        catch (Exception e) {
            System.out.println("Failed to make connection with server");
        }
    }
    public void sendCommand(UserGameCommand cmd){
        String message = new Gson().toJson(cmd);
        ws.sendMessage(message);
    }

    public void connect(int gameID){
        sendCommand(new Connect(authToken, gameID));
    }

    public void leave(int gameID){
        sendCommand(new Leave(authToken, gameID));
    }
    public void makeMove(int gameID, ChessMove move){
        sendCommand(new MakeMove(authToken, gameID, move));
    }

    public void resign(int gameID){
        sendCommand(new Resign(authToken, gameID));
    }

}

