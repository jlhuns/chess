package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MemoryGameDao implements GameDAO {
    private static MemoryGameDao instance;
    private final Map<Integer, GameData> gameDatabase = new HashMap<>();

    public static MemoryGameDao getInstance() {
        if (instance == null) {
            // Initialize the singleton instance if it hasn't been initialized yet
            instance = new MemoryGameDao();
        }
        return instance;
    }

    @Override
    public int insertGame(GameData game) throws DataAccessException {
        gameDatabase.put(game.gameID(), game);
        return game.gameID();
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        GameData game = gameDatabase.get(gameID);
        if (game == null) {
            throw new DataAccessException("Game not found.");
        }
        return game;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        if (!gameDatabase.containsKey(game.gameID())) {
            throw new DataAccessException("Game not found.");
        }
        gameDatabase.put(game.gameID(), game);
    }

    @Override
    public ArrayList<GameData> getAllGames() throws DataAccessException {
        return new ArrayList<>(gameDatabase.values());
    }

    @Override
    public void clearGameData(){
        gameDatabase.clear();
    }
}
