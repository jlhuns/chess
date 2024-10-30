package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.mockito.internal.matchers.text.ValuePrinter.print;

public class SQLUserDAO implements UserDAO {

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

    void storeUserPassword(String username, String clearTextPassword) {
        String hashedPassword = BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());

        // write the hashed password in database along with the user's other information
        writeHashedPasswordToDatabase(username, hashedPassword);
    }

    private void writeHashedPasswordToDatabase(String username, String hashedPassword) {
    }

    boolean verifyUser(String username, String providedClearTextPassword) {
        // read the previously hashed password from the database
        var hashedPassword = readHashedPasswordFromDatabase(username);

        return BCrypt.checkpw(providedClearTextPassword, hashedPassword);
    }

    private String readHashedPasswordFromDatabase(String username) {
        return "TEST";
    }

}

