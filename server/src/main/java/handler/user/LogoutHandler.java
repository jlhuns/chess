package handler.user;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
import org.eclipse.jetty.server.Authentication;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class LogoutHandler implements Route {
    UserService userService = UserService.getInstance();
    private final Gson gson = new Gson();

    @Override
    public Object handle(Request request, Response response) throws DataAccessException {
        String authToken = request.headers("authorization");

        userService.logout(authToken);

        response.status(200);
        return "{}";
    }
}

