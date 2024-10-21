package dataaccess;

import model.GameData;

public interface GameDAO {
    void insertGame(GameData game) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    void updateGame(GameData game) throws DataAccessException;

    void deleteGame(int gameID) throws DataAccessException;
}
