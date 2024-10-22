package handler.game;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.GameData;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.List;


public class ListGamesHandler implements Route {

    private final Gson gson = new Gson();
    GameService gameService = GameService.getInstance();

    @Override
    public Object handle(Request request, Response response) throws DataAccessException{
        String authToken = request.headers("authorization");
        List<GameData> games = gameService.listGames(authToken);
        response.status(200);
        return new Gson().toJson(games);
    }
}
