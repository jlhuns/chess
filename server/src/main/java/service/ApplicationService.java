package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class ApplicationService {
    private final AuthDAO authDAO;  // For authentication validation
    private final GameDAO gameDAO;
    private final UserDAO userDAO;

    public ApplicationService(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }

    public void clearDatabase() throws DataAccessException {
        authDAO.clearAuthData();
        gameDAO.clearGameData();
        userDAO.clearUserData();
    }
}
