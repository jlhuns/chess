package handler.user;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import dataAccess.UnauthorizedException;
import model.AuthData;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class LogoutHandler implements Route {
    UserService userService = UserService.getInstance();

    @Override
    public Object handle(Request request, Response response) throws DataAccessException, UnauthorizedException {
        String authToken = request.headers("authorization");

        userService.logout(authToken);

        response.status(200);
        return "{}";
    }
}

