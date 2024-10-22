package service;

import dataAccess.*;
import model.UserData;
import model.AuthData;

import java.util.UUID;

public class UserService {
    private static UserService instance;
    private final UserDAO userDAO;  // For managing user data
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }
    public static UserService getInstance() {
        if (instance == null) {
            UserDAO userDAO = MemoryUserDao.getInstance();
            AuthDAO authDAO = MemoryAuthDao.getInstance();
            instance = new UserService(userDAO, authDAO);
        }
        return instance;
    }

    // Register a new user and create an AuthData object
    public AuthData register(UserData user) throws DataAccessException, BadRequestException {
        if (user == null || user.username() == null || user.username().trim().isEmpty() || user.password() == null || user.password().trim().isEmpty()) {
            throw new BadRequestException("Username or password cannot be null or empty.");
        }

        if (userDAO.getUser(user.username()) == null) {
            try {
                userDAO.insertUser(user);
            } catch (DataAccessException e) {
                throw new BadRequestException("Failed to insert user: " + e.getMessage());
            }
        } else {
            throw new AlreadyTakenException("User already exists.");
        }

        String token = UUID.randomUUID().toString();
        AuthData authData = new AuthData(token, user.username());

        authDAO.insertAuth(authData);

        return authData;
    }

    // Login method to authenticate a user
    public AuthData login(UserData user) throws UnauthorizedException, DataAccessException {
        UserData storedUser = userDAO.getUser(user.username());

        if (storedUser == null || !storedUser.password().equals(user.password())) {
            throw new UnauthorizedException();
        }

        String token = UUID.randomUUID().toString();
        AuthData authData = new AuthData(token, user.username());

        authDAO.insertAuth(authData);

        return authData;
    }

    public void logout(String authToken) throws DataAccessException, UnauthorizedException {
        AuthData authData;
        try{
            authData = authDAO.getAuthData(authToken);
        }catch (DataAccessException e) {
            throw new UnauthorizedException();
        }
        authDAO.deleteAuth(authData);
    }
}
