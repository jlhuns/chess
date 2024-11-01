package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import passoff.model.TestAuthResult;
import passoff.model.TestUser;
import passoff.server.TestServerFacade;
import server.Server;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
public class SQLDAOTests {

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


        //one user already logged in
//        TestAuthResult regResult = serverFacade.register(existingUser);
//        existingAuth = regResult.getAuthToken();
    }


    @Test
    public void createUserDB() throws Exception {
        SQLUserDAO dao = SQLUserDAO.getInstance();
        dao.configureDatabase();

        // Verify that the users table was created
        try (Connection connection = DatabaseManager.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getTables(null, null, "users", null);
            assertTrue(resultSet.next(), "The 'users' table should exist after database creation.");
        } catch (SQLException e) {
            fail("Database connection or table verification failed: " + e.getMessage());
        }
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
    public void createAuthDB() throws Exception {
        SQLAuthDAO dao = SQLAuthDAO.getInstance();
        dao.configureDatabase();

        // Verify that the users table was created
        try (Connection connection = DatabaseManager.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getTables(null, null, "auth", null);
            assertTrue(resultSet.next(), "The 'auth' table should exist after database creation.");
        } catch (SQLException e) {
            fail("Database connection or table verification failed: " + e.getMessage());
        }
    }
    @Test
    public void addUser() throws Exception {
        SQLUserDAO dao = SQLUserDAO.getInstance();
        dao.configureDatabase();
        // Create a sample user
        UserData existingUser = new UserData("testUser", "password", "testUser@example.com");
        // Insert the user
        dao.insertUser(existingUser);

        // Verify the user was added
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM user WHERE username = ?")) {

            preparedStatement.setString(1, existingUser.username());
            ResultSet resultSet = preparedStatement.executeQuery();

            // Check that the result set is not empty, which confirms the user was inserted
            assertTrue(resultSet.next(), "User should be present in the database after insertion.");

            // Optionally, verify the inserted data matches
            assertEquals(existingUser.username(), resultSet.getString("username"));
            assertEquals(existingUser.email(), resultSet.getString("email"));
//            assertEquals(existingUser.password(), resultSet.getString("password"));

        } catch (SQLException e) {
            fail("Database connection or table verification failed: " + e.getMessage());
        }

        //DELETE FROM users WHERE username = 'user_to_delete';
    }

    @Test
    public void getUser() throws Exception {
        SQLUserDAO dao = SQLUserDAO.getInstance();
        dao.configureDatabase();
        dao.insertUser(existingUser);
        UserData retrievedUser = dao.getUser(existingUser.username());
        assertNotNull(retrievedUser, "Retrieved user should not be null.");
        assertEquals(existingUser.username(), retrievedUser.username(), "Usernames should match.");
        assertEquals(existingUser.email(), retrievedUser.email(), "Emails should match.");
//        assertEquals(existingUser.password(), retrievedUser.password(), "Passwords should match.");
    }

    @Test
    public void clearUserDB() throws Exception {
        SQLUserDAO dao = SQLUserDAO.getInstance();
        dao.configureDatabase();
        dao.insertUser(existingUser2);
        dao.clearUserData();
        try (Connection connection = DatabaseManager.getConnection();
             var stmt = connection.prepareStatement("SELECT * FROM user");
             ResultSet resultSet = stmt.executeQuery()) {

            // Assert that no records are found in the table
            assertFalse(resultSet.next(), "Users table should be empty after calling clearUserData.");

        } catch (SQLException e) {
            fail("Database connection or query failed: " + e.getMessage());
        }
    }

    @Test
    public void addAuth() throws Exception {
        SQLAuthDAO dao = SQLAuthDAO.getInstance();
        dao.configureDatabase();
        // Create a sample user
        UserData existingUser = new UserData("testUser", "password", "testUser@example.com");
        AuthData authData = new AuthData("1234", "testUser");
        // Insert the user
        dao.insertAuth(authData);

        // Verify the user was added
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM auth WHERE username = ?")) {

            preparedStatement.setString(1, existingUser.username());
            ResultSet resultSet = preparedStatement.executeQuery();

            // Check that the result set is not empty, which confirms the user was inserted
            assertTrue(resultSet.next(), "User should be present in the database after insertion.");

            // Optionally, verify the inserted data matches
            assertEquals(authData.username(), resultSet.getString("username"));
            assertEquals(authData.authToken(), resultSet.getString("authToken"));
//            assertEquals(existingUser.password(), resultSet.getString("password"));

        } catch (SQLException e) {
            fail("Database connection or table verification failed: " + e.getMessage());
        }
    }
    @Test
    public void getAuth() throws Exception {
        SQLAuthDAO dao = SQLAuthDAO.getInstance();
        dao.configureDatabase();
        // Create a sample user
        UserData existingUser = new UserData("testUser3", "password", "testUser@example.com");
        AuthData authData = new AuthData("12345", "testUser3");
        // Insert the user
        dao.insertAuth(authData);
        AuthData result = dao.getAuthData(authData.authToken());

        // Verify the user was added
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM auth WHERE username = ?")) {

            preparedStatement.setString(1, existingUser.username());
            ResultSet resultSet = preparedStatement.executeQuery();

            // Check that the result set is not empty, which confirms the user was inserted
            assertTrue(resultSet.next(), "User should be present in the database after insertion.");

            // Optionally, verify the inserted data matches
            assertEquals(result.username(), resultSet.getString("username"));
            assertEquals(result.authToken(), resultSet.getString("authToken"));
//            assertEquals(existingUser.password(), resultSet.getString("password"));

        } catch (SQLException e) {
            fail("Database connection or table verification failed: " + e.getMessage());
        }
    }
    @Test
    public void deleteAuth() throws Exception {
        SQLAuthDAO dao = SQLAuthDAO.getInstance();
        dao.configureDatabase();
        // Create a sample user
        UserData existingUser = new UserData("testUser4", "password", "testUser@example.com");
        AuthData authData = new AuthData("123456", "testUser4");
        // Insert the user
        dao.insertAuth(authData);
        dao.deleteAuth(authData);

        // Verify the user was added
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM auth WHERE username = ?")) {

            preparedStatement.setString(1, existingUser.username());
            ResultSet resultSet = preparedStatement.executeQuery();

            // Check that the result set is not empty, which confirms the user was inserted
            assertFalse(resultSet.next(), "auth should not be present in the database deletion.");

        } catch (SQLException e) {
            fail("Database connection or table verification failed: " + e.getMessage());
        }
    }
    @Test
    public void clearAuth() throws Exception {
        SQLAuthDAO dao = SQLAuthDAO.getInstance();
        dao.configureDatabase();
        // Create a sample user
        UserData existingUser = new UserData("testUser4", "password", "testUser@example.com");
        AuthData authData = new AuthData("123456", "testUser4");
        // Insert the user
        dao.insertAuth(authData);
        dao.clearAuthData();

        // Verify the user was added
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM auth WHERE username = ?")) {

            preparedStatement.setString(1, existingUser.username());
            ResultSet resultSet = preparedStatement.executeQuery();

            // Check that the result set is not empty, which confirms the user was inserted
            assertFalse(resultSet.next(), "auth should not be present in the database after cleared.");

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
}
