package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUserDAO implements UserDAO {
    private static SQLUserDAO instance;

    // Private constructor to prevent instantiation
    private SQLUserDAO() {}

    // Public static method to get the instance
    public static SQLUserDAO getInstance() {
        if (instance == null) {
            synchronized (SQLUserDAO.class) {
                if (instance == null) {
                    instance = new SQLUserDAO();
                }
            }
        }
        return instance;
    }

    @Override
    public void insertUser(UserData user) throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()){
            try(var stmt = conn.prepareStatement("INSERT INTO user (username, password, email) VALUES (?, ?, ?)")){
                stmt.setString(1, user.username());
                stmt.setString(2, BCrypt.hashpw(user.password(), BCrypt.gensalt()));
                stmt.setString(3, user.email());
                stmt.executeUpdate();
            }

        }catch (SQLException e){
            throw new RuntimeException(e);
        }

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String sql = "SELECT username, password, email FROM user WHERE username = ?";
        try(var conn = DatabaseManager.getConnection()){
            var pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            try(var stmt = pstmt.executeQuery()){
                if(stmt.next()){
                    return new UserData(stmt.getString(1), stmt.getString(2), stmt.getString(3));
                }else{
                    return null; //no user found with that name
                }
            }
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clearUserData() throws DataAccessException {
        try(var conn = DatabaseManager.getConnection()){
            var stmt = conn.prepareStatement("DELETE FROM user");
            stmt.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE if NOT EXISTS user (
                username VARCHAR(255) NOT NULL,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(255),
                PRIMARY KEY (username)
            )"""
    };

    public void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection connection = DatabaseManager.getConnection();) {
            for (String statement : createStatements) {
                try (var preparedStatement = connection.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

//    public void storeUserPassword(String username, String clearTextPassword) throws DataAccessException {
//        String hashedPassword = BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());
//        writeHashedPasswordToDatabase(username, hashedPassword);
//    }

    private void writeHashedPasswordToDatabase(String username, String hashedPassword) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var stmt = conn.prepareStatement("UPDATE user SET password = ? WHERE username = ?");
            stmt.setString(1, hashedPassword);
            stmt.setString(2, username);
            stmt.executeUpdate();
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

//    public boolean verifyUser(String username, String providedClearTextPassword) throws DataAccessException {
//        String hashedPassword = readHashedPasswordFromDatabase(username);
//        return hashedPassword != null && BCrypt.checkpw(providedClearTextPassword, hashedPassword);
//    }

    private String readHashedPasswordFromDatabase(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var stmt = conn.prepareStatement("SELECT password FROM user WHERE username = ?");
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("password");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null; // No user found
    }
}

