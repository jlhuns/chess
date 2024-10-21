package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import dataaccess.DataAccessException;
import model.UserData;
import model.AuthData;

import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;  // For managing user data
    private final AuthDAO authDAO;  // For managing authentication tokens

    // Constructor to initialize UserDAO and AuthDAO
    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
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
    public void logout(AuthData auth) throws DataAccessException {
        // Remove the session using AuthDAO
        authDAO.deleteAuth(auth.authToken());
    }
}
