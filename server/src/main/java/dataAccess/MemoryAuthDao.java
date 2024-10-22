package dataAccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDao implements AuthDAO {
    private static MemoryAuthDao instance;  // Singleton instance
    private final Map<String, AuthData> authDatabase = new HashMap<>();

    public static MemoryAuthDao getInstance() {
        if (instance == null) {
            instance = new MemoryAuthDao();
        }
        return instance;
    }

    @Override
    public void insertAuth(AuthData authData) throws DataAccessException {
        if (authDatabase.containsKey(authData.authToken())) {
            throw new DataAccessException("Auth token already exists.");
        }
        authDatabase.put(authData.authToken(), authData);
    }

    @Override
    public AuthData getAuthData(String authToken) throws UnauthorizedException {
        AuthData auth = authDatabase.get(authToken);
        if (auth == null) {
            throw new UnauthorizedException();
        }
        return auth;
    }

    @Override
    public void deleteAuth(AuthData authData) throws UnauthorizedException {
        if (authDatabase.remove(authData.authToken()) == null) {
            throw new UnauthorizedException();
        }
    }
    @Override
    public void clearAuthData(){
        authDatabase.clear();
    }
}
