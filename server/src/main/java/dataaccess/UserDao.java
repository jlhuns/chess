package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class UserDao {
    private final Map<String, UserData> userDatabase = new HashMap<>();

    public void insertUser(UserData user) throws DataAccessException {
        if (userDatabase.containsKey(user.username())) {
            throw new DataAccessException("User already exists.");
        }
        userDatabase.put(user.username(), user);
    }

    public UserData getUser(String username) throws DataAccessException {
        UserData user = userDatabase.get(username);
        if (user == null) {
            throw new DataAccessException("User not found.");
        }
        return user;
    }

    public void updateUser(UserData user) throws DataAccessException {
        if (!userDatabase.containsKey(user.username())) {
            throw new DataAccessException("User not found.");
        }
        userDatabase.put(user.username(), user);
    }

    public void deleteUser(String username) throws DataAccessException {
        if (userDatabase.remove(username) == null) {
            throw new DataAccessException("User not found.");
        }
    }
}
