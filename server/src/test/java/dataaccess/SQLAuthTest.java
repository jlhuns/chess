package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import passoff.server.TestServerFacade;
import server.Server;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
public class SQLAuthTest {

    private static TestServerFacade serverFacade;
    private static Server server;

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


    }

    @BeforeEach
    public void setup() {
        serverFacade.clear();
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
    public void addAuthDuplicateUserThrowsException() throws DataAccessException {
        SQLAuthDAO dao = SQLAuthDAO.getInstance();
        dao.configureDatabase();

        // Create a sample user
        AuthData authData = new AuthData("duplicateToken", "duplicateUser");

        // Insert the user once
        dao.insertAuth(authData);

        // Attempt to insert the same user again and expect an exception
        assertThrows(RuntimeException.class, () -> {
            dao.insertAuth(authData); // This should raise an exception for duplicate entry
        });
    }
    @Test
    public void getAuthInvalidTokenReturnsNull() throws Exception {
        SQLAuthDAO dao = SQLAuthDAO.getInstance();
        dao.configureDatabase();

        // Attempt to retrieve AuthData with an invalid token
        AuthData result = dao.getAuthData("invalidToken");

        assertNull(result, "Retrieving AuthData with an invalid token should return null.");
    }
    @Test
    public void deleteAuthNonExistentUserDoesNotThrowException() throws Exception {
        SQLAuthDAO dao = SQLAuthDAO.getInstance();
        dao.configureDatabase();

        // Create a non-existent AuthData
        AuthData authData = new AuthData("123456", "nonExistentUser");

        // Attempt to delete a non-existent user and expect no exceptions
        assertDoesNotThrow(() -> {
            dao.deleteAuth(authData); // Should complete without throwing an exception
        });
    }
    @Test
    public void clearAuthNoUsersDoesNotThrowException() throws Exception {
        SQLAuthDAO dao = SQLAuthDAO.getInstance();
        dao.configureDatabase();

        // Clear auth data when no users are present
        assertDoesNotThrow(() -> {
            dao.clearAuthData(); // Should complete without throwing an exception
        });
    }
}