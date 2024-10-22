package dataAccess;

import chess.ChessGame;
import model.GameData;

import java.util.List;

public interface GameDAO {
    int insertGame(GameData game) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    void updateGame(GameData game) throws DataAccessException;

    void deleteGame(int gameID) throws DataAccessException;

    List<GameData> getAllGames() throws DataAccessException;

    void setTeamColor(int gameID, ChessGame.TeamColor teamColor, String username) throws DataAccessException;

    void clearGameData() throws DataAccessException;
}
