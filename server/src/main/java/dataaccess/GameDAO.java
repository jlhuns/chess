package dataaccess;

import model.GameData;

import java.util.List;

public interface GameDAO {
    int insertGame(GameData game) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    void updateGame(GameData game) throws DataAccessException;

    List<GameData> getAllGames() throws DataAccessException;

    void clearGameData() throws DataAccessException;
}
