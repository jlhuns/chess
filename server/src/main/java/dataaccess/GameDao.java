package dataaccess;

import model.GameData;

import java.util.HashMap;
import java.util.Map;

public class GameDao {
    private final Map<Integer, GameData> gameDatabase = new HashMap<>();
    private int currentGameId = 1;

    public void insertGame(GameData game) throws DataAccessException {
        gameDatabase.put(currentGameId, game);
        currentGameId++;
    }

    public GameData getGame(int gameID) throws DataAccessException {
        GameData game = gameDatabase.get(gameID);
        if (game == null) {
            throw new DataAccessException("Game not found.");
        }
        return game;
    }

    public void updateGame(GameData game) throws DataAccessException {
        if (!gameDatabase.containsKey(game.gameID())) {
            throw new DataAccessException("Game not found.");
        }
        gameDatabase.put(game.gameID(), game);
    }

    public void deleteGame(int gameID) throws DataAccessException {
        if (gameDatabase.remove(gameID) == null) {
            throw new DataAccessException("Game not found.");
        }
    }
}
