package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDao implements AuthDAO {
    private final Map<String, AuthData> authDatabase = new HashMap<>();

    @Override
    public void insertAuth(AuthData authData) throws DataAccessException {
        if (authDatabase.containsKey(authData.authToken())) {
            throw new DataAccessException("Auth token already exists.");
        }
        authDatabase.put(authData.authToken(), authData);
    }

    @Override
    public AuthData getAuthData(String authToken) throws DataAccessException {
        AuthData auth = authDatabase.get(authToken);
        if (auth == null) {
            throw new DataAccessException("Auth data not found.");
        }
        return auth;
    }

    @Override
    public void updateAuth(AuthData authData) throws DataAccessException {
        if (!authDatabase.containsKey(authData.authToken())) {
            throw new DataAccessException("Auth data not found.");
        }
        authDatabase.put(authData.authToken(), authData);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if (authDatabase.remove(authToken) == null) {
            throw new DataAccessException("Auth data not found.");
        }
    }
}