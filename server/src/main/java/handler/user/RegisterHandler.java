package handler.user;

import com.google.gson.Gson;
import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class RegisterHandler implements Route {
    private final Gson gson = new Gson();
    UserService userService = UserService.getInstance();

    @Override
    public Object handle(Request request, Response response) throws DataAccessException, BadRequestException {

        response.type("application/json");
        RegisterRequest registrationRequest;
        registrationRequest = gson.fromJson(request.body(), RegisterRequest.class);
        UserData newUser = new UserData(registrationRequest.username(), registrationRequest.password(), registrationRequest.email());
        AuthData authToken = userService.register(newUser);
        RegisterResult result = new RegisterResult(authToken.username(), authToken.authToken());

        return gson.toJson(result);
    }

    public record RegisterRequest(String username, String password, String email) {
    }

    public record RegisterResult(String username, String authToken) {
    }
}