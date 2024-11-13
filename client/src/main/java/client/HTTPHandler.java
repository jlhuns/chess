package client;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
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
        var resp = request("POST", "/user", jsonBody);
        if (resp.containsKey("Error")) {
            return false;
        }
        facade.setAuthToken((String) resp.get("authToken"));
        return true;
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
}
