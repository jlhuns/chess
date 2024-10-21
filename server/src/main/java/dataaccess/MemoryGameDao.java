package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoryGameDao implements GameDAO {
    private final Map<Integer, GameData> gameDatabase = new HashMap<>();
    private int currentGameId = 1;

    @Override
    public void insertGame(GameData game) throws DataAccessException {
        gameDatabase.put(currentGameId, game);
        currentGameId++;
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
    public void deleteGame(int gameID) throws DataAccessException {
        if (gameDatabase.remove(gameID) == null) {
            throw new DataAccessException("Game not found.");
        }
    }
    @Override
    public List<GameData> getAllGames() throws DataAccessException {
        return new ArrayList<>(gameDatabase.values());
    }

    @Override
    public void setTeamColor(int gameID, ChessGame.TeamColor teamColor, String username) throws DataAccessException {
        // Retrieve the game data from the database
        GameData game = gameDatabase.get(gameID);

        if (game == null) {
            throw new DataAccessException("Error: game not found.");  // Handle case where game does not exist
        }

        GameData updatedGame = switch (teamColor) {
            case WHITE -> {
                if (game.whiteUsername() != null) {
                    throw new DataAccessException("Error: white team is already occupied."); // Handle case where white team is already taken
                }
                yield new GameData(game.gameID(), username, game.blackUsername(), game.game());
            }
            case BLACK -> {
                if (game.blackUsername() != null) {
                    throw new DataAccessException("Error: black team is already occupied."); // Handle case where black team is already taken
                }
                yield new GameData(game.gameID(), game.whiteUsername(), username, game.game());
            }
            default -> throw new DataAccessException("Error: invalid team color."); // Handle invalid team color
        };
        gameDatabase.put(updatedGame.gameID(), updatedGame);
    }
    @Override
    public void clearGameData(){
        gameDatabase.clear();
    }
}
