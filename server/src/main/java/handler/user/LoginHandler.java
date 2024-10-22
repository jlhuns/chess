package handler.user;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import model.AuthData;
import model.UserData;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;


public class LoginHandler implements Route {
    UserService userService = UserService.getInstance();
    private final Gson gson = new Gson();
    @Override
    public Object handle(Request request, Response response) throws DataAccessException, UnauthorizedException {
        response.type("application/json");

        LoginRequest loginRequest = gson.fromJson(request.body(), LoginRequest.class);


        AuthData authToken = userService.login(new UserData(loginRequest.username(), loginRequest.password(), null));

        LoginResult result = new LoginResult(authToken.username(), authToken.authToken());

        return gson.toJson(result);
    }

    public record LoginRequest(String username, String password) {}

    public record LoginResult(String username, String authToken) {}
}
