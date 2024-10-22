package service;

import dataAccess.*;
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
    public AuthData register(UserData user) throws DataAccessException, BadRequestException {
        // Check if the user data is valid
        if (user == null || user.username() == null || user.username().trim().isEmpty() || user.password() == null || user.password().trim().isEmpty()) {
            throw new BadRequestException("Username or password cannot be null or empty.");
        }

        // Check if the user does not exist before inserting
        if (userDAO.getUser(user.username()) == null) {
            try {
                userDAO.insertUser(user);
            } catch (DataAccessException e) {
                throw new BadRequestException("Failed to insert user: " + e.getMessage());
            }
        } else {
            throw new AlreadyTakenException("User already exists.");
        }

        // Generate a session token
        String token = UUID.randomUUID().toString();
        AuthData authData = new AuthData(token, user.username());

        // Store the session using AuthDAO
        authDAO.insertAuth(authData);

        return authData;
    }

    // Login method to authenticate a user
    public AuthData login(UserData user) throws UnauthorizedException, DataAccessException {
        UserData storedUser = userDAO.getUser(user.username());

        if (storedUser == null || !storedUser.password().equals(user.password())) {
            throw new UnauthorizedException();
        }

        // Generate a session token
        String token = UUID.randomUUID().toString();
        AuthData authData = new AuthData(token, user.username());

        // Store the session using AuthDAO
        authDAO.insertAuth(authData);

        return authData;
    }

    // Logout method to invalidate a session
    public void logout(String authToken) throws DataAccessException, UnauthorizedException {
        // Remove the session using AuthDAO
        AuthData authData = authDAO.getAuthData(authToken);
        authDAO.deleteAuth(authData);
    }
}
