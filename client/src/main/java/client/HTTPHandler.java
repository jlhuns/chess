package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import model.GameData;
import model.ListGamesResponse;

import java.io.*;
import java.lang.reflect.Type;
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
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ChessBoard.class, new ChessBoardDeserializer())  // Register ChessBoard deserializer
                .registerTypeAdapter(new TypeToken<List<ChessGame>>() {}.getType(), new ListChessGamesDeserializer())  // Register list deserializer
                .create();

        String response = stringRequest("GET", "/game", null);

        // Check if the response contains an error
        if (response.contains("Error")) {
            return null;
        }

        // Deserialize response into ListGamesResponse
        ListGamesResponse gamesResponse = gson.fromJson(response, ListGamesResponse.class);

        return gamesResponse.games();  // Return list of games
    }

    public boolean joinGame(int gameID, String teamcolor){
        Map body;
        if (teamcolor != null) {
            body = Map.of("playerColor", teamcolor, "gameID", gameID);
        } else {
            body = Map.of("gameID", gameID);
        }
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
class ChessBoardDeserializer implements JsonDeserializer<ChessBoard> {
    @Override
    public ChessBoard deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        ChessBoard chessBoard = new ChessBoard();

        // Get the outer 'board' object
        JsonObject outerBoardObject = json.getAsJsonObject();

        // Check if there is an inner 'board' key and get its value
        if (!outerBoardObject.has("board")) {
            throw new JsonParseException("Missing 'board' key in JSON");
        }

        // Get the inner 'board' object that contains the actual positions and pieces
        JsonObject boardObject = outerBoardObject.getAsJsonObject("board");

        // Now iterate over the entries in this inner board object
        for (Map.Entry<String, JsonElement> entry : boardObject.entrySet()) {
            String positionKey = entry.getKey();

            // Parse the position key into a ChessPosition
            ChessPosition position = parsePositionKey(positionKey);

            // Deserialize piece details (teamColor and pieceType)
            JsonObject pieceObject = entry.getValue().getAsJsonObject();
            String teamColor = pieceObject.get("teamColor").getAsString();
            String pieceType = pieceObject.get("pieceType").getAsString();

            ChessGame.TeamColor color = teamColor.equals("WHITE") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
            ChessPiece.PieceType type;

            switch (pieceType) {
                case "KING" -> type = ChessPiece.PieceType.KING;
                case "QUEEN" -> type = ChessPiece.PieceType.QUEEN;
                case "BISHOP" -> type = ChessPiece.PieceType.BISHOP;
                case "KNIGHT" -> type = ChessPiece.PieceType.KNIGHT;
                case "ROOK" -> type = ChessPiece.PieceType.ROOK;
                case "PAWN" -> type = ChessPiece.PieceType.PAWN;
                default -> throw new IllegalArgumentException("Invalid piece type: " + pieceType);
            }

            // Create a new ChessPiece and add it to the board
            ChessPiece piece = new ChessPiece(color, type);
            chessBoard.addPiece(position, piece);
        }

        return chessBoard;
    }

    private ChessPosition parsePositionKey(String positionKey) {
        // Strip braces and split by comma to get row and col values
        String cleanedKey = positionKey.replace("{", "").replace("}", "");

        // Check if the cleaned position key contains both row and col components
        String[] parts = cleanedKey.split(",");

        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid position key format: " + positionKey);
        }

        // Split the row and col components and extract their integer values
        int row = Integer.parseInt(parts[0].split(":")[1].trim());
        int col = Integer.parseInt(parts[1].split(":")[1].trim());

        return new ChessPosition(row, col);
    }
}

class ListChessGamesDeserializer implements JsonDeserializer<List<ChessGame>> {
    @Override
    public List<ChessGame> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        List<ChessGame> chessGames = new ArrayList<>();
        JsonArray jsonArray = json.getAsJsonArray();

        for (JsonElement element : jsonArray) {
            ChessGame chessGame = context.deserialize(element, ChessGame.class);
            chessGames.add(chessGame);
        }

        return chessGames;
    }
}
