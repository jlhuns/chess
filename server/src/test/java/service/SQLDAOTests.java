package service;

import dataaccess.*;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
public class SQLDAOTests {
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
            assertTrue(resultSet.next(), "The 'users' table should exist after database creation.");
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
            assertTrue(resultSet.next(), "The 'users' table should exist after database creation.");
        } catch (SQLException e) {
            fail("Database connection or table verification failed: " + e.getMessage());
        }
    }
}
