package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO {

    @Override
    public void insertAuth(AuthData authData) throws DataAccessException {

    }

    @Override
    public AuthData getAuthData(String authToken) throws DataAccessException, UnauthorizedException {
        return null;
    }

    @Override
    public void deleteAuth(AuthData authData) throws DataAccessException, UnauthorizedException {

    }

    @Override
    public void clearAuthData() throws DataAccessException {

    }

    private final String[] createStatements = {
            """
            CREATE TABLE if NOT EXISTS auth (
                username VARCHAR(255) NOT NULL,
                authToken VARCHAR(255) NOT NULL,
                PRIMARY KEY (authToken)
            )"""
    };

    public void configureDatabase() throws DataAccessException {
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
