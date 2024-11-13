package client;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import model.ListGamesResponse;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HTTPHandler {
    String baseURL;
    ServerFacade facade;

    public HTTPHandler(ServerFacade facade, String serverDomain) {
        baseURL = "http://" + serverDomain;
        this.facade = facade;
    }

    public boolean register(String username, String password, String email) {
        var body = Map.of("username", username, "password", password, "email", email);
        var jsonBody = new Gson().toJson(body);
        Map resp = request("POST", "/user", jsonBody);
        if (resp.containsKey("Error")) {
            return false;
        }
        facade.setAuthToken((String) resp.get("authToken"));
        return true;
    }

    public boolean login(String username, String password) {
        var body = Map.of("username", username, "password", password);
        var jsonBody = new Gson().toJson(body);
        var resp = request("POST", "/session", jsonBody);
        if (resp.containsKey("Error")) {
            return false;
        }
        facade.setAuthToken((String) resp.get("authToken"));
        return true;
    }
    public boolean logout(String authToken) {
        var resp = request("DELETE", "/session", null);
        if (resp.containsKey("Error")) {
            return false;
        }
        facade.setAuthToken(null);
        return true;
    }

    public int createGame(String gameName){
        var body = Map.of("gameName", gameName);
        var jsonBody = new Gson().toJson(body);
        var resp = request("POST", "/game", jsonBody);
        if (resp.containsKey("Error")) {
            return -1;
        }
        double gameID = (double) resp.get("gameID");
        return (int) gameID;
    }

    public List<GameData> listGames() {
        String response = stringRequest("GET", "/game", null);
        // Check if the response is an error
        if (response.contains("Error")) {
            return null;
        }
        ListGamesResponse gamesResponse = new Gson().fromJson(response, ListGamesResponse.class);
        return gamesResponse.games();
    }
    public boolean joinGame(int gameID, String teamcolor){
        var body = Map.of("playerColor", teamcolor, "gameID", gameID);
        var jsonBody = new Gson().toJson(body);
        var resp = request("PUT", "/game", jsonBody);
        return !resp.containsKey("Error");
    }

    private Map request(String method, String endpoint, String jsonBody) {
        Map respMap;
        try {
            HttpURLConnection http = makeConnection(method, endpoint, jsonBody);

            try {
                if (http.getResponseCode() == 401) {
                    return Map.of("Error", 401);
                }
            } catch (IOException e) {
                return Map.of("Error", 401);
            }


                try (InputStream respBody = http.getInputStream()) {
                InputStreamReader inputStreamReader = new InputStreamReader(respBody);
                respMap = new Gson().fromJson(inputStreamReader, Map.class);
            }

        } catch (URISyntaxException | IOException e) {
            return Map.of("Error", e.getMessage());
        }

        return respMap;
    }

    private HttpURLConnection makeConnection(String method, String endpoint, String body) throws URISyntaxException, IOException {
        URI uri = new URI(baseURL + endpoint);
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod(method);

        if (facade.getAuthToken() != null) {
            http.addRequestProperty("authorization", facade.getAuthToken());
        }

        if (!Objects.equals(body, null)) {
            http.setDoOutput(true);
            http.addRequestProperty("Content-Type", "application/json");
            try (var outputStream = http.getOutputStream()) {
                outputStream.write(body.getBytes());
            }
        }
        return http;
    }
    private String stringRequest(String method, String endpoint, String jsonBody) {
        StringBuilder response = new StringBuilder();
        try {
            HttpURLConnection http = makeConnection(method, endpoint, jsonBody);

            // Check for 401 Unauthorized
            if (http.getResponseCode() == 401) {
                return "Error: 401 Unauthorized";
            }

            // Read response body
            try (InputStream respBody = http.getInputStream();
                 InputStreamReader inputStreamReader = new InputStreamReader(respBody);
                 BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line);
                }
            }

        } catch (URISyntaxException | IOException e) {
            return "Error: " + e.getMessage();
        }

        return response.toString();
    }

}
