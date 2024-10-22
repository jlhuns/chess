package handler.user;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
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
    public Object handle(Request request, Response response) throws DataAccessException {

        response.type("application/json");

        RegisterRequest registrationRequest;

        registrationRequest = gson.fromJson(request.body(), RegisterRequest.class);

        AuthData authToken = userService.register(new UserData(registrationRequest.username(), registrationRequest.password(), registrationRequest.email()));

        RegisterResult result = new RegisterResult(authToken.username(), authToken.authToken());

        return gson.toJson(result);
    }
}