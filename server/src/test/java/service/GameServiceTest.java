package service;

import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import model.GameData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import passoff.model.TestAuthResult;
import passoff.model.TestUser;
import passoff.server.TestServerFacade;
import server.Server;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {
    private static TestUser existingUser;
    private static TestServerFacade serverFacade;
    private static Server server;
    private String existingAuth;

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        serverFacade = new TestServerFacade("localhost", Integer.toString(port));

        existingUser = new TestUser("ExistingUser", "existingUserPassword", "eu@mail.com");
    }

    @BeforeEach
    public void setup() {
        serverFacade.clear();

        // One user already logged in
        TestAuthResult regResult = serverFacade.register(existingUser);
        existingAuth = regResult.getAuthToken();
    }

    // Test cases

    @Test
    public void testListGamesInvalidTokenThrowsException() {
        GameService gameService = GameService.getInstance();

        assertThrows(UnauthorizedException.class, () -> {
            gameService.listGames("invalidToken");
        });
    }

    @Test
    public void testCreateGameNullAuthTokenThrowsException() {
        GameService gameService = GameService.getInstance();

        assertThrows(UnauthorizedException.class, () -> {
            gameService.createGame(null, "gameName");
        });
    }

    @Test
    public void testCreateGameNullGameNameThrowsException() {
        GameService gameService = GameService.getInstance();

        assertThrows(DataAccessException.class, () -> {
            gameService.createGame(existingAuth, null);
        });
    }

    @Test
    public void testJoinGameNullAuthTokenThrowsException() {
        GameService gameService = GameService.getInstance();

        assertThrows(BadRequestException.class, () -> {
            gameService.joinGame(null, "WHITE", 1);
        });
    }

    @Test
    public void testJoinGameEmptyColorDoesNotThrowException() throws UnauthorizedException, DataAccessException {
        GameService gameService = GameService.getInstance();
        int gameId = gameService.createGame(existingAuth, "gameName");

        // This line should not throw any exception
        assertDoesNotThrow(() -> {
            gameService.joinGame(existingAuth, "", gameId);
        });
    }

    @Test
    public void testCreateGameUnauthorizedUserThrowsException() {
        GameService gameService = GameService.getInstance();
        String unauthorizedAuth = "unauthorizedToken";

        assertThrows(UnauthorizedException.class, () -> {
            gameService.createGame(unauthorizedAuth, "gameName");
        });
    }
}
