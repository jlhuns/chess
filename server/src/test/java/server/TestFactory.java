package server;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TestFactory {

    public static Long getMessageTime() {
        /*
         * Changing this will change how long tests will wait for the server to send messages.
         * 3000 Milliseconds (3 seconds) will be enough for most computers. Feel free to change as you see fit,
         * just know increasing it can make tests take longer to run.
         * (On the flip side, if you've got a good computer feel free to decrease it)
         *
         * WHILE DEBUGGING the websocket tests, it might be useful to increase this time to give the tests
         * enough time to receive messages you send while debugging. Just make sure to decrease it when you
         * stop debugging and start running the tests again.
         */
        return 3000L;
    }

    public static GsonBuilder getGsonBuilder() {
        /*                  **NOT APPLICABLE TO MOST STUDENTS**
         * If you would like to change the way the web socket test cases serialize
         * or deserialize chess objects like ChessMove, you may add type adapters here.
         */
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(ChessBoard.class, new ChessBoardDeserializer());
        builder.registerTypeAdapter(new TypeToken<List<ChessGame>>() {}.getType(), new ListChessGamesDeserializer());
        return builder;
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
