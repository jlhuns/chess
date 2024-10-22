package dataaccess;

import model.AuthData;

public interface AuthDAO {

    void insertAuth(AuthData authData) throws DataAccessException;

    AuthData getAuthData(String authToken) throws DataAccessException, UnauthorizedException;

    void deleteAuth(AuthData authData) throws DataAccessException, UnauthorizedException;

    void clearAuthData() throws DataAccessException;
}
