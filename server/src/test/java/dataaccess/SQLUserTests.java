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

import static org.junit.jupiter.api.Assertions.*;
public class SQLUserTests {

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
    public void addUserDuplicateThrowsException() throws DataAccessException {
        SQLUserDAO dao = SQLUserDAO.getInstance();
        dao.configureDatabase();

        // Insert a user first
        dao.insertUser(existingUser);

        // Attempt to insert the same user again and expect an exception
        assertThrows(RuntimeException.class, () -> {
            dao.insertUser(existingUser); // Should throw an exception for duplicate entry
        });
    }
    @Test
    public void getUserNonExistentReturnsNull() throws DataAccessException {
        SQLUserDAO dao = SQLUserDAO.getInstance();
        dao.configureDatabase();

        // Attempt to retrieve a non-existent user
        UserData retrievedUser = dao.getUser("nonExistentUser");

        assertNull(retrievedUser, "Retrieved user should be null for non-existent users.");
    }
    @Test
    public void clearUserDataWhenEmptyDoesNotThrowException() throws DataAccessException {
        SQLUserDAO dao = SQLUserDAO.getInstance();
        dao.configureDatabase();

        // Clear user data when no users are present
        assertDoesNotThrow(() -> {
            dao.clearUserData(); // Should complete without throwing an exception
        });
    }
    @Test
    public void addUserWithDuplicateEmailThrowsException() throws DataAccessException {
        SQLUserDAO dao = SQLUserDAO.getInstance();
        dao.configureDatabase();

        // Insert a user first
        dao.insertUser(existingUser);

        // Create a new user with the same email but different username
        UserData userWithDuplicateEmail = new UserData("newUser", "password", existingUser.email());

        // Attempt to insert the new user and expect an exception
        assertThrows(RuntimeException.class, () -> {
            dao.insertUser(userWithDuplicateEmail); // Should throw an exception for duplicate email
        });
    }


}