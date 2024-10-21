package dataaccess;

import model.UserData;

public interface UserDAO {

    void insertUser(UserData user) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    void updateUser(UserData user) throws DataAccessException;

    void deleteUser(String username) throws DataAccessException;
}