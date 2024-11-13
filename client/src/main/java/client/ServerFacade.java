package client;

import model.GameData;

import java.util.List;

public class ServerFacade {
    private final String serverDomain;
    HTTPHandler handler;
    String authToken;


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

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }
}

