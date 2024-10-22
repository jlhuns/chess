package service;


import dataAccess.*;
import model.AuthData;
import model.GameData;


import java.util.List;

public class GameService {
    private static GameService instance;  // Singleton instance

    private final AuthDAO authDAO;  // For authentication validation
    private final GameDAO gameDAO;  // For retrieving games

    private int currentGameId = 0;

    // Private constructor to prevent external instantiation
    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    // Static method to retrieve the singleton instance
    public static GameService getInstance() {
        if (instance == null) {
            AuthDAO authDAO = MemoryAuthDao.getInstance();
            GameDAO gameDAO = MemoryGameDao.getInstance();
            instance = new GameService(authDAO, gameDAO);
        }
        return instance;
    }

    public List<GameData> listGames(String authToken) throws DataAccessException {
        if (authDAO.getAuthData(authToken) == null) {
            throw new DataAccessException("Error: unauthorized");  // Return 401 error
        }

        try {
            // Return the list of games from the GameDAO
            return gameDAO.getAllGames();
        } catch (Exception e) {
            throw new DataAccessException("Error: " + e.getMessage());  // Return 500 error on failure
        }
    }

    public int createGame(String authToken, String gameName) throws DataAccessException {
        // Check if the auth token is valid
        if (authDAO.getAuthData(authToken) == null) {
            throw new DataAccessException("Error: unauthorized");  // Return 401 error
        }

        // Validate game data
        if (gameName == null || gameName.isEmpty()) {
            throw new DataAccessException("Error: bad request");  // Return 400 error
        }

        try {
            currentGameId++;
            // Create a new GameData object
            GameData gameData = new GameData(gameName, currentGameId, null, null, null);

            // Insert the new game into the database and retrieve the generated game ID
            int gameID = gameDAO.insertGame(gameData);

            return gameID;  // Return the generated game ID
        } catch (Exception e) {
            throw new DataAccessException("Error: " + e.getMessage());  // Return 500 error
        }
    }

    public boolean joinGame(String authToken, String playerColor, int gameID) throws DataAccessException {
        // Verify authorization
        AuthData authData = authDAO.getAuthData(authToken);
        if (authData == null) {
            throw new DataAccessException("Error: unauthorized");  // Return 401 error
        }

        GameData gameData;
        // Fetch the game data
        try {
            gameData = gameDAO.getGame(gameID);
        } catch (DataAccessException e) {
            throw new DataAccessException("Error: game not found");  // Return 404 error
        }

        String whiteUser = gameData.whiteUsername();
        String blackUser = gameData.blackUsername();
        String intendedColor;
        if (playerColor.equalsIgnoreCase("WHITE")) {
            intendedColor = "WHITE";
        } else if (playerColor.equalsIgnoreCase("BLACK")) {
            intendedColor = "BLACK";
        } else {
            // Handle case where input is "WHITE/BLACK"
            // You might want to prompt for a valid choice or randomly select a color.
            intendedColor = decideColor(authData.username(), whiteUser, blackUser);
        }

        // Check for the intended player color
        if (intendedColor.equals("WHITE")) {
            // Check if the user is already playing as black
            if (blackUser != null && blackUser.equals(authData.username())) {
                return false; // User can't join as white if they are already black
            }
            // Check if the white spot is taken
            if (whiteUser != null && !whiteUser.equals(authData.username())) {
                return false; // Spot taken by someone else
            } else {
                whiteUser = authData.username(); // Assign username to white player
            }
        } else if (intendedColor.equals("BLACK")) {
            // Check if the user is already playing as white
            if (whiteUser != null && whiteUser.equals(authData.username())) {
                return false; // User can't join as black if they are already white
            }
            // Check if the black spot is taken
            if (blackUser != null && !blackUser.equals(authData.username())) {
                return false; // Spot taken by someone else
            } else {
                blackUser = authData.username(); // Assign username to black player
            }
        }

        // Create updated GameData with new usernames
        GameData updatedGameData = new GameData(gameData.gameName(), gameData.gameID(), whiteUser, blackUser, gameData.game());

        // Update the game data in the DAO
        gameDAO.updateGame(updatedGameData);

        return true;  // Return true if successfully joined
    }

    private String decideColor(String username, String whiteUser, String blackUser) {
        // Logic to decide color for the user
        // For example, you could randomize, prefer one color, or simply return a specific message
        // Here’s an example logic:
        if (whiteUser == null) {
            return "WHITE"; // Join as white if no one is assigned
        } else if (blackUser == null) {
            return "BLACK"; // Join as black if no one is assigned
        } else {
            return ""; // No spots available
        }
    }
}