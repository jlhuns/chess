package service;

import dataaccess.*;
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
        SQLUserDAO dao = new SQLUserDAO();
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
        SQLGameDAO dao = new SQLGameDAO();
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
        SQLAuthDAO dao = new SQLAuthDAO();
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
        SQLUserDAO dao = new SQLUserDAO();
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
}
