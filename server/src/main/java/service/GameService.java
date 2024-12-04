package service;


import chess.ChessBoard;
import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;


import java.util.List;

public class GameService {
    private static GameService instance;  // Singleton instance

    private final AuthDAO authDAO;  // For authentication validation
    private final GameDAO gameDAO;  // For retrieving games

    private int currentGameId;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public static GameService getInstance() {
        if (instance == null) {
            AuthDAO authDAO = SQLAuthDAO.getInstance();
            GameDAO gameDAO = SQLGameDAO.getInstance();
            instance = new GameService(authDAO, gameDAO);
        }
        return instance;
    }

    public List<GameData> listGames(String authToken) throws DataAccessException, UnauthorizedException {
        if (authDAO.getAuthData(authToken) == null) {
            throw new UnauthorizedException();  // Return 401 error
        }

        try {
            return gameDAO.getAllGames();
        } catch (Exception e) {
            throw new DataAccessException("Error: " + e.getMessage());  // Return 500 error on failure
        }
    }

    public int createGame(String authToken, String gameName) throws DataAccessException, UnauthorizedException {
        // Check if the auth token is valid
        if (authDAO.getAuthData(authToken) == null) {
            throw new UnauthorizedException();  // Return 401 error
        }

        if (gameName == null || gameName.isEmpty()) {
            throw new DataAccessException("Error: bad request");  // Return 400 error
        }
        List<GameData> allGames = gameDAO.getAllGames();
        int maxGameId = allGames.stream()
                .mapToInt(GameData::gameID)
                .max()
                .orElse(0); // If no games exist, max is 0

        // The new game ID will be the highest existing game ID + 1
        int newGameId = maxGameId + 1;


        try {
            // Create a new GameData object
            ChessGame game = new ChessGame();
            ChessBoard board = new ChessBoard();
            board.resetBoard();
            game.setBoard(board);
            GameData gameData = new GameData(newGameId, null, null, gameName, game);


            return gameDAO.insertGame(gameData);  // Return the generated game ID
        } catch (DataAccessException e) {
            throw new UnauthorizedException();  // Return 401
        }
    }

    public boolean joinGame(String authToken, String playerColor, int gameID) throws DataAccessException, UnauthorizedException, BadRequestException {
        AuthData authData = authDAO.getAuthData(authToken);
        GameData gameData = gameDAO.getGame(gameID);


        if (gameData == null) {
            throw new BadRequestException("Error: bad request");
        }
        if (authData == null) {
            throw new UnauthorizedException(); // Return 401 error
        }

        if (playerColor == null || playerColor.isEmpty()) {
            throw new BadRequestException("Error: bad request");
        }

        if (playerColor == "obs") {
            return true;
        }

        String whiteUser = gameData.whiteUsername();
        String blackUser = gameData.blackUsername();
        String intendedColor;

        try {
            if (playerColor.equalsIgnoreCase("WHITE")) {
                intendedColor = "WHITE";
            } else if (playerColor.equalsIgnoreCase("BLACK")) {
                intendedColor = "BLACK";
            } else {
                // Handle case where input is "WHITE/BLACK"
                intendedColor = decideColor(authData.username(), whiteUser, blackUser);
            }

            if (intendedColor.equals("WHITE")) {
                if (whiteUser == null || whiteUser.equals(authData.username())) {
                    whiteUser = authData.username();
                } else {
                    return false; // Spot taken by someone else
                }
            } else if (intendedColor.equals("BLACK")) {
                if (blackUser == null || blackUser.equals(authData.username())) {
                    blackUser = authData.username();
                } else {
                    return false; // Spot taken by someone else
                }
            }
        } catch (Exception e) {
            throw new BadRequestException("Bad team color");
        }
        GameData updatedGameData = new GameData(gameData.gameID(), whiteUser, blackUser, gameData.gameName(), gameData.game());

        gameDAO.updateGame(updatedGameData);

        return true;  // Return true if successfully joined
    }

    private String decideColor(String username, String whiteUser, String blackUser) {
        if (whiteUser == null) {
            return "WHITE";
        } else if (blackUser == null) {
            return "BLACK";
        } else {
            return ""; // No spots available
        }
    }

    public GameData getGameData(int gameID) throws DataAccessException {
        return gameDAO.getGame(gameID);
    }

    public void updateGame(String authToken, GameData gameData) throws UnauthorizedException, BadRequestException {
        try {
            authDAO.getAuthData(authToken);
        } catch (DataAccessException e) {
            throw new UnauthorizedException();
        }

        try {
            gameDAO.updateGame(gameData);
        } catch (DataAccessException e) {
            throw new BadRequestException(e.getMessage());
        }
    }
}