package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDao implements UserDAO {
    private static MemoryUserDao instance;

    private final Map<String, UserData> userDatabase = new HashMap<>();

    public static MemoryUserDao getInstance() {
        if (instance == null) {
            instance = new MemoryUserDao();
        }
        return instance;
    }

    @Override
    public void insertUser(UserData user) throws DataAccessException {
        userDatabase.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        UserData user = userDatabase.get(username);
        return user;
    }
    @Override
    public void clearUserData(){
        userDatabase.clear();
    }
}