package service;

import dataaccess.*;
import model.UserData;
import model.AuthData;

import java.util.UUID;

public class UserService {
    private static UserService instance;
    private final UserDAO userDAO;  // For managing user data
    private final AuthDAO authDAO;

    // Constructor to initialize UserDAO and AuthDAO
    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }
    public static UserService getInstance() {
        if (instance == null) {
            // Create your DAOs here
            UserDAO userDAO = MemoryUserDao.getInstance();
            AuthDAO authDAO = MemoryAuthDao.getInstance();
            instance = new UserService(userDAO, authDAO);
        }
        return instance;
    }

    // Register a new user and create an AuthData object
    public AuthData register(UserData user) throws DataAccessException {
        // Check if the user does not exist before inserting
        if (userDAO.getUser(user.username()) == null) {
            userDAO.insertUser(user);
        } else {
            throw new DataAccessException("User already exists.");
        }

        // Generate a session token
        String token = UUID.randomUUID().toString();
        AuthData authData = new AuthData(token, user.username());

        // Store the session using AuthDAO
        authDAO.insertAuth(authData);

        return authData;
    }

    // Login method to authenticate a user
    public AuthData login(UserData user) throws DataAccessException {
        UserData storedUser = userDAO.getUser(user.username());

        if (storedUser == null || !storedUser.password().equals(user.password())) {
            throw new DataAccessException("Invalid username or password.");
        }

        // Generate a session token
        String token = UUID.randomUUID().toString();
        AuthData authData = new AuthData(token, user.username());

        // Store the session using AuthDAO
        authDAO.insertAuth(authData);

        return authData;
    }

    // Logout method to invalidate a session
    public void logout(String authToken) throws DataAccessException {
        // Remove the session using AuthDAO
        System.out.println(authToken);
        AuthData authData = authDAO.getAuthData(authToken);
        authDAO.deleteAuth(authData);
    }
}
