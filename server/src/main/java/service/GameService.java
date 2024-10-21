package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;

import java.util.List;

public class GameService {
    private final AuthDAO authDAO;  // For authentication validation
    private final GameDAO gameDAO;  // For retrieving games

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public List<GameData> listGames(AuthData authData) throws DataAccessException {
        if (authDAO.getAuthData(authData.authToken()) == null) {
            throw new DataAccessException("Error: unauthorized");  // Return 401 error
        }

        try {
            // Return the list of games from the GameDAO
            return gameDAO.getAllGames();
        } catch (Exception e) {
            throw new DataAccessException("Error: " + e.getMessage());  // Return 500 error on failure
        }
    }

    public GameData createGame(AuthData authData, GameData gameData) throws DataAccessException {
        if (authDAO.getAuthData(authData.authToken()) == null) {
            throw new DataAccessException("Error: unauthorized");  // Return 401 error
        }

        // Validate game data
        if (gameData.game() == null) {
            throw new DataAccessException("Error: bad request");  // Return 400 error
        }

        try {
            // Insert the new game and return the created GameData object
            gameDAO.insertGame(gameData);
            return gameData;  // Assuming gameData is populated with gameID after insertion
        } catch (Exception e) {
            throw new DataAccessException("Error: " + e.getMessage());  // Return 500 error
        }
    }

    public void JoinGame(AuthData authData, ChessGame.TeamColor teamColor, int gameID) throws DataAccessException {
        if (authDAO.getAuthData(authData.authToken()) == null) {
            throw new DataAccessException("Error: unauthorized");  // Return 401 error
        }
        GameData gameData;
        try{
            gameData = gameDAO.getGame(gameID);
        } catch (DataAccessException e){
            throw new DataAccessException("Error: game not found");
        }
        try{
            gameDAO.setTeamColor(gameID, teamColor, authData.username());
        } catch (DataAccessException e){
            throw new DataAccessException("Error: already taken");
        }

        gameData = switch (teamColor) {
            case WHITE ->
                    new GameData(gameData.gameID(), authData.username(), gameData.blackUsername(), gameData.game());
            case BLACK ->
                    new GameData(gameData.gameID(), gameData.whiteUsername(), authData.username(), gameData.game());
            default -> throw new DataAccessException("Error: invalid team color"); // Handle unexpected cases
        };

        gameDAO.updateGame(gameData);
    }
}
