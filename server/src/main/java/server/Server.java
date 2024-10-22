package server;

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

        //This line initializes the server and can be removed once you have a functioning endpoint 
//        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
