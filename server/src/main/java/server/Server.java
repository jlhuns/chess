package server;

import dataAccess.AlreadyTakenException;
import dataAccess.BadRequestException;
import dataAccess.UnauthorizedException;
import handler.game.CreateGameHandler;
import handler.game.DBHandler;
import handler.game.JoinGameHandler;
import handler.game.ListGamesHandler;
import handler.user.LoginHandler;
import handler.user.LogoutHandler;
import handler.user.RegisterHandler;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        // Register the login handler
        Spark.post("/user", new RegisterHandler());
        Spark.post("/session", new LoginHandler());
        Spark.delete("/session", new LogoutHandler());

        Spark.get("/game", new ListGamesHandler());
        Spark.post("/game", new CreateGameHandler());
        Spark.put("/game", new JoinGameHandler());

        Spark.delete("/db", new DBHandler());

        //This line initializes the server and can be removed once you have a functioning endpoint 
//        Spark.init();

        Spark.exception(BadRequestException.class, this::badRequestExceptionHandler);
        Spark.exception(UnauthorizedException.class, this::unauthorizedExceptionHandler);
        Spark.exception(Exception.class, this::genericExceptionHandler);
        Spark.exception(AlreadyTakenException.class, this::alreadyTakenExceptionHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void badRequestExceptionHandler(BadRequestException ex, Request req, Response resp) {
        resp.status(400);
        resp.body("{ \"message\": \"Error: bad request\" }");
    }

    private void unauthorizedExceptionHandler(UnauthorizedException ex, Request req, Response resp) {
        resp.status(401);
        resp.body("{ \"message\": \"Error: unauthorized\" }");
    }

    private void genericExceptionHandler(Exception ex, Request req, Response resp) {
        resp.status(500);
        resp.body("{ \"message\": \"Error: %s\" }".formatted(ex.getMessage()));
    }
    private void alreadyTakenExceptionHandler(AlreadyTakenException ex, Request req, Response resp) {
        resp.status(403);
        resp.body("{ \"message\": \"Error: already taken\" }");
    }
}
