package client;

import model.GameData;

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
}

