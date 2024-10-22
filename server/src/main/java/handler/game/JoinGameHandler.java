package handler.game;
import com.google.gson.Gson;
import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

public class JoinGameHandler implements Route {
    private final Gson gson = new Gson();
    GameService gameService = GameService.getInstance();

    @Override
    public Object handle(Request request, Response response) throws DataAccessException, UnauthorizedException, BadRequestException {
        String authToken = request.headers("authorization");
        record JoinGameData(String playerColor, int gameID) {}
        JoinGameData joinData = new Gson().fromJson(request.body(), JoinGameData.class);
        boolean joinSuccess =  gameService.joinGame(authToken, joinData.playerColor(), joinData.gameID());

        if (!joinSuccess) {
            response.status(403);
            return "{ \"message\": \"Error: already taken\" }";
        }

        response.status(200);
        return "{}";
    }
}
