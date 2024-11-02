package dataaccess;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.GameData;

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
        try(var conn = DatabaseManager.getConnection()){
            try(var statement = conn.prepareStatement("SELECT * FROM game WHERE gameID = ?")) {
                statement.setInt(1, gameID);
                try (var resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        var whiteUsername = resultSet.getString("whiteUsername");
                        var blackUsername = resultSet.getString("blackUsername");
                        var gameName = resultSet.getString("gameName");
                        var chessGame = deserializeGame(resultSet);
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
        List<GameData> games = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection();
             var statement = conn.prepareStatement("SELECT * FROM game");
             var resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int gameID = resultSet.getInt("gameID");
                String whiteUsername = resultSet.getString("whiteUsername");
                String blackUsername = resultSet.getString("blackUsername");
                String gameName = resultSet.getString("gameName");
                ChessGame chessGame = deserializeGame(resultSet);
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
        }  catch (SQLException e) {
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

    private ChessGame deserializeGame(ResultSet resultSet) throws SQLException {
        // Get the JSON string from the ResultSet
        String json = resultSet.getString("chessGame");

        // Deserialize JSON back into ChessGame object
        ChessGame chessGame = new Gson().fromJson(json, ChessGame.class);

        // Accessing ChessBoard from ChessGame
        ChessBoard board = chessGame.getBoard();

        // Example: Iterate through positions on the board
        for (Map.Entry<ChessPosition, ChessPiece> entry : board.getBoard().entrySet()) {
            ChessPosition position = entry.getKey();
            int row = position.getRow();  // Extract row as an integer
            int col = position.getColumn();  // Extract column as an integer

            System.out.println("Row: " + row + ", Column: " + col);
        }

        return chessGame;  // Return deserialized ChessGame object
    }
}
