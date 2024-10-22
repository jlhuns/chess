package handler.game;
import com.google.gson.Gson;
import dataAccess.DataAccessException;
import service.ApplicationService;
import spark.Request;
import spark.Response;
import spark.Route;

public class DBHandler implements Route {
    ApplicationService applicationService = ApplicationService.getInstance();
    Gson gson = new Gson();
    public Object handle(Request request, Response response) throws DataAccessException {
        applicationService.clearDatabase();

        response.status(200);
        return "{}";
    }
}
