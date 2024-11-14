package dataaccess;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.*;
import model.GameData;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SQLGameDAO implements GameDAO {
    private static SQLGameDAO instance;

    // Private constructor to prevent instantiation
    private SQLGameDAO() {}

    // Public static method to get the instance
    public static SQLGameDAO getInstance() {
        if (instance == null) {
            synchronized (SQLUserDAO.class) {
                if (instance == null) {
                    instance = new SQLGameDAO();
                }
            }
        }
        return instance;
    }
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(ChessPosition.class, new ChessPositionSerializer())
            .create();

    @Override
    public int insertGame(GameData game) throws DataAccessException {
        String pstmt = "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, chessGame) VALUES(?, ?, ?, ?, ?)";
        try(var conn = DatabaseManager.getConnection()){
            var json = gson.toJson(game.game().getBoard());
            try (var statement = conn.prepareStatement(pstmt)) {
                statement.setInt(1, game.gameID());
                statement.setString(2, game.whiteUsername());
                statement.setString(3, game.blackUsername());
                statement.setString(4, game.gameName());
                statement.setString(5, json);
                statement.executeUpdate();
            }
            return game.gameID();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ChessBoard.class, new ChessBoardDeserializer())
                .create();
        try(var conn = DatabaseManager.getConnection()){
            try(var statement = conn.prepareStatement("SELECT * FROM game WHERE gameID = ?")) {
                statement.setInt(1, gameID);
                try (var resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        var whiteUsername = resultSet.getString("whiteUsername");
                        var blackUsername = resultSet.getString("blackUsername");
                        var gameName = resultSet.getString("gameName");
                        ChessGame chessGame = gson.fromJson(resultSet.getString("chessGame"), ChessGame.class);
                        return new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
                    }
                }
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var json = new Gson().toJson(game.game());
            try (var statement = conn.prepareStatement("UPDATE game SET whiteUsername=?, blackUsername=?, gameName=?, chessGame=? WHERE gameID=?")) {
                statement.setString(1, game.whiteUsername());
                statement.setString(2, game.blackUsername());
                statement.setString(3, game.gameName());
                statement.setString(4, json);
                statement.setInt(5, game.gameID());
                int rowsUpdated = statement.executeUpdate();
                if (rowsUpdated == 0) {
                    throw new DataAccessException("Item requested to be updated not found");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public List<GameData> getAllGames() throws DataAccessException {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ChessBoard.class, new ChessBoardDeserializer())
                .create();
        List<GameData> games = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement("SELECT * FROM game");
             var resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int gameID = resultSet.getInt("gameID");
                String whiteUsername = resultSet.getString("whiteUsername");
                String blackUsername = resultSet.getString("blackUsername");
                String gameName = resultSet.getString("gameName");
                ChessGame chessGame = gson.fromJson(resultSet.getString("chessGame"), ChessGame.class);
                games.add(new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame));
            }
        }  catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return games;
    }

    @Override
    public void clearGameData() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement("TRUNCATE game")) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void configureDatabase() throws DataAccessException {
        String[] createStatements = {
                """
            CREATE TABLE if NOT EXISTS game (
                gameID INT NOT NULL,
                whiteUsername VARCHAR(255),
                blackUsername VARCHAR(255),
                gameName VARCHAR(255),
                chessGame TEXT,
                PRIMARY KEY (gameID)
            )"""
        };
        DatabaseManager.createDatabase();
        try (Connection connection = DatabaseManager.getConnection();){
            for(String statement: createStatements) {
                try (var preparedStatement = connection.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
class ChessBoardDeserializer implements JsonDeserializer<ChessBoard> {
    @Override
    public ChessBoard deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        ChessBoard chessBoard = new ChessBoard();
        if (json == null || !json.isJsonObject()) {
            return chessBoard;  // Return an empty board if the JSON is null or malformed
        }

        JsonObject boardObject = json.getAsJsonObject();

        if (boardObject.has("board")) {
            boardObject.remove("board");
        }

        for (Map.Entry<String, JsonElement> entry : boardObject.entrySet()) {
            String positionKey = entry.getKey();
            if(positionKey.equals("board")) {
                continue;
            }
            ChessPosition position = parsePositionKey(positionKey);  // Safely parse the position

            JsonObject pieceObject = entry.getValue().getAsJsonObject();
            if (pieceObject == null) continue;  // Handle missing pieces

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
                default -> throw new JsonParseException("Invalid piece type: " + pieceType);
            }

            ChessPiece piece = new ChessPiece(color, type);
            chessBoard.addPiece(position, piece);
        }

        return chessBoard;
    }

    private ChessPosition parsePositionKey(String positionKey) {
        // Safely parse the position string by removing curly braces and splitting the coordinates
        String[] parts = positionKey.replace("{", "").replace("}", "").split(",");

        try {
            int row = Integer.parseInt(parts[0].split(":")[1]);
            int col = Integer.parseInt(parts[1].split(":")[1]);
            return new ChessPosition(row, col);
        } catch (NumberFormatException e) {
            throw new JsonParseException("Invalid position values in key: " + positionKey, e);
        }
    }
}
