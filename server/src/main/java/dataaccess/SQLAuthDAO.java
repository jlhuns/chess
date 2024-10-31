package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO {

    @Override
    public void insertAuth(AuthData authData) throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()){
            try(var stmt = conn.prepareStatement("INSERT INTO auth(username, authToken) VALUES (?,?)")){
                stmt.setString(1, authData.username());
                stmt.setString(2, authData.authToken());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AuthData getAuthData(String authToken) throws DataAccessException, UnauthorizedException {
        try(var conn = DatabaseManager.getConnection()){
            var pstmt = conn.prepareStatement("SELECT username, authToken FROM auth WHERE username = ?");
            pstmt.setString(1, authToken);
            try(var rs = pstmt.executeQuery()){
                if(rs.next()){
                    return new AuthData(rs.getString("username"), rs.getString("authToken"));
                } else{
                    return null;
                }
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
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
