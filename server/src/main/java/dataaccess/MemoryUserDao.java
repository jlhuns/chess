package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDao implements UserDAO {
    private static MemoryUserDao instance;

    private final Map<String, UserData> userDatabase = new HashMap<>();

    public static MemoryUserDao getInstance() {
        if (instance == null) {
            // Initialize the singleton instance if it hasn't been initialized yet
            instance = new MemoryUserDao();
        }
        return instance;
    }

    @Override
    public void insertUser(UserData user) throws DataAccessException {
        if (userDatabase.containsKey(user.username())) {
            throw new DataAccessException("User already exists.");
        }
        userDatabase.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        UserData user = userDatabase.get(username);
        return user;
    }

    @Override
    public void updateUser(UserData user) throws DataAccessException {
        if (!userDatabase.containsKey(user.username())) {
            throw new DataAccessException("User not found.");
        }
        userDatabase.put(user.username(), user);
    }

    @Override
    public void deleteUser(String username) throws DataAccessException {
        if (userDatabase.remove(username) == null) {
            throw new DataAccessException("User not found.");
        }
    }
    @Override
    public void clearUserData(){
        userDatabase.clear();
    }
}