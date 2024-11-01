package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import passoff.server.TestServerFacade;
import server.Server;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class SQLGameTests {

    private static UserData existingUser;
    private static UserData existingUser2;
    private static TestServerFacade serverFacade;
    private static Server server;
    private String existingAuth;

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        serverFacade = new TestServerFacade("localhost", Integer.toString(port));

        existingUser = new UserData("ExistingUser", "existingUserPassword", "eu@mail.com");
        existingUser2 = new UserData("ExistingUser2", "existingUserPassword2", "eu2@mail.com");

    }

    @BeforeEach
    public void setup() {
        serverFacade.clear();
    }

    @Test
    public void createGameDB() throws Exception {
        SQLGameDAO dao = SQLGameDAO.getInstance();
        dao.configureDatabase();

        // Verify that the users table was created
        try (Connection connection = DatabaseManager.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getTables(null, null, "game", null);
            assertTrue(resultSet.next(), "The 'game' table should exist after database creation.");
        } catch (SQLException e) {
            fail("Database connection or table verification failed: " + e.getMessage());
        }
    }
    @Test
    public void addGame() throws Exception {
        SQLGameDAO dao = SQLGameDAO.getInstance();
        dao.configureDatabase();
        // Create a sample user
        ChessGame new_game = new ChessGame();
        GameData game = new GameData(1, "test1","test2", "Game1", new_game);
        // Insert the user
        dao.insertGame(game);

        // Verify the user was added
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM game WHERE gameID = ?")) {

            preparedStatement.setInt(1, game.gameID());
            ResultSet resultSet = preparedStatement.executeQuery();

            // Check that the result set is not empty, which confirms the user was inserted
            assertTrue(resultSet.next(), "game should be present in the database after insertion.");

        } catch (SQLException e) {
            fail("Database connection or table verification failed: " + e.getMessage());
        }
    }
    @Test
    public void getGame() throws Exception {
        SQLGameDAO dao = SQLGameDAO.getInstance();
        dao.configureDatabase();
        ChessGame new_game = new ChessGame();
        GameData game = new GameData(3, "test1","test2", "Game2", new_game);
        // Insert the user
        dao.insertGame(game);
        GameData result = dao.getGame(3);
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM game WHERE gameID = ?")) {

            preparedStatement.setInt(1, game.gameID());
            ResultSet resultSet = preparedStatement.executeQuery();

            // Check that the result set is not empty, which confirms the user was inserted
            assertTrue(resultSet.next(), "game should be present in the database after insertion.");
            assertEquals(result.gameID(), resultSet.getInt("gameID"));

        } catch (SQLException e) {
            fail("Database connection or table verification failed: " + e.getMessage());
        }
    }
    @Test
    public void addGameDuplicateThrowsException() throws DataAccessException {
        SQLGameDAO dao = SQLGameDAO.getInstance();
        dao.configureDatabase();

        // Create and insert a game
        ChessGame newGame = new ChessGame();
        GameData game = new GameData(1, "test1", "test2", "Game1", newGame);
        dao.insertGame(game);

        // Attempt to insert the same game again and expect an exception
        assertThrows(RuntimeException.class, () -> {
            dao.insertGame(game); // Should throw an exception for duplicate entry
        });
    }
    @Test
    public void getGameNonExistentReturnsNull() throws DataAccessException {
        SQLGameDAO dao = SQLGameDAO.getInstance();
        dao.configureDatabase();

        // Attempt to retrieve a non-existent game
        GameData retrievedGame = dao.getGame(999); // Non-existent gameID
        assertNull(retrievedGame, "Retrieved game should be null for non-existent games.");
    }
    @Test
    public void getAllGames() throws Exception {
        SQLGameDAO dao = SQLGameDAO.getInstance();
        dao.configureDatabase();

        // Create and insert multiple games
        ChessGame newGame1 = new ChessGame();
        GameData game1 = new GameData(4, "test1", "test2", "Game4", newGame1);
        dao.insertGame(game1);

        ChessGame newGame2 = new ChessGame();
        GameData game2 = new GameData(5, "test3", "test4", "Game5", newGame2);
        dao.insertGame(game2);

        // Retrieve all games
        List<GameData> games = dao.getAllGames();

        assertNotNull(games, "Game list should not be null.");
        assertEquals(2, games.size(), "There should be two games in the list.");
    }
    @Test
    public void clearGameData() throws Exception {
        SQLGameDAO dao = SQLGameDAO.getInstance();
        dao.configureDatabase();

        // Insert a game
        ChessGame newGame = new ChessGame();
        GameData game = new GameData(6, "test1", "test2", "Game6", newGame);
        dao.insertGame(game);

        // Clear game data
        dao.clearGameData();

        // Verify that no games exist
        assertTrue(dao.getAllGames().isEmpty(), "Game list should be empty after clearing.");
    }
}