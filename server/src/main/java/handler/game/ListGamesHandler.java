package handler.game;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.UnauthorizedException;
import model.GameData;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Comparator;
import java.util.List;


public class ListGamesHandler implements Route {

    private final Gson gson = new Gson();
    GameService gameService = GameService.getInstance();

    @Override
    public Object handle(Request request, Response response) throws DataAccessException, UnauthorizedException {
        String authToken = request.headers("authorization");
        List<GameData> games = gameService.listGames(authToken);

        // Sort the games by gameID
        games.sort(Comparator.comparingInt(GameData::gameID));

        ListGamesResponse listGamesResponse = new ListGamesResponse(games);

        response.status(200);
        return gson.toJson(listGamesResponse);
    }

    public record ListGamesResponse(List<GameData> games) {
    }
}
