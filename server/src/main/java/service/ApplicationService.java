package service;

import dataaccess.*;

public class ApplicationService {
    private static ApplicationService instance;
    private final AuthDAO authDAO;  // For authentication validation
    private final GameDAO gameDAO;
    private final UserDAO userDAO;

    public ApplicationService(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }

    public static ApplicationService getInstance() {
        if (instance == null) {
            AuthDAO authDAO = SQLAuthDAO.getInstance();
            GameDAO gameDAO = SQLGameDAO.getInstance();
            UserDAO userDAO = SQLUserDAO.getInstance();
            instance = new ApplicationService(authDAO, gameDAO, userDAO);
        }
        return instance;
    }

    public void clearDatabase() throws DataAccessException {
        authDAO.clearAuthData();
        gameDAO.clearGameData();
        userDAO.clearUserData();
    }
}
