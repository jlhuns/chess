package handler.game;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.UnauthorizedException;
import model.GameData;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;



public class CreateGameHandler implements Route {
    private final Gson gson = new Gson();
    GameService gameService = GameService.getInstance();

    @Override
    public Object handle(Request request, Response response) throws DataAccessException, UnauthorizedException {
        GameData gameData = gson.fromJson(request.body(), GameData.class);

        String authToken = request.headers("authorization");

        int gameId = gameService.createGame(authToken, gameData.gameName());

        response.status(200);
        return "{ \"gameID\": %d }".formatted(gameId);
    }
}

