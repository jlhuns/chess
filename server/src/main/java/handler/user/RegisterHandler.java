package handler.user;

import com.google.gson.Gson;
import dataAccess.BadRequestException;
import dataAccess.DataAccessException;
import model.AuthData;
import model.UserData;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

import static spark.Spark.post;

public class RegisterHandler implements Route {
    private final Gson gson = new Gson();
    UserService userService = UserService.getInstance();

    @Override
    public Object handle(Request request, Response response) throws DataAccessException, BadRequestException {

        response.type("application/json");

        RegisterRequest registrationRequest;

        registrationRequest = gson.fromJson(request.body(), RegisterRequest.class);

        AuthData authToken = userService.register(new UserData(registrationRequest.username(), registrationRequest.password(), registrationRequest.email()));

        RegisterResult result = new RegisterResult(authToken.username(), authToken.authToken());

        return gson.toJson(result);
    }

    public static record RegisterRequest(String username, String password, String email) {
    }

    public static record RegisterResult(String username, String authToken) {
    }
}