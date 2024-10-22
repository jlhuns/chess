package dataaccess;

import model.AuthData;

public interface AuthDAO {

    void insertAuth(AuthData authData) throws DataAccessException;

    AuthData getAuthData(String authToken) throws DataAccessException;

    void updateAuth(AuthData authData) throws DataAccessException;

    void deleteAuth(AuthData authData) throws DataAccessException;

    void clearAuthData() throws DataAccessException;
}
