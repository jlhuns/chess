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

        //one user already logged in
        TestAuthResult regResult = serverFacade.register(existingUser);
        existingAuth = regResult.getAuthToken();
    }

    @Test
    public void testListGamesValidTokenReturnsEmptyGames() throws Exception {
        GameService gameService = GameService.getInstance();

        List<GameData> games = gameService.listGames(existingAuth);

        assertNotNull(games);
        assertTrue(games.isEmpty());
    }

    @Test
    public void testListGamesInvalidTokenThrowsException() {
        GameService gameService = GameService.getInstance();

        assertThrows(UnauthorizedException.class, () -> {
            gameService.listGames("invalidToken");
        });
    }

    @Test
    public void testCreateGameValidInputsReturnsGameId() throws Exception {
        GameService gameService = GameService.getInstance();

        int gameId = gameService.createGame(existingAuth, "gameName");

        assertTrue(gameId > 0);
    }

    @Test
    public void testCreateGameEmptyGameNameThrowsException() {
        GameService gameService = GameService.getInstance();

        assertThrows(DataAccessException.class, () -> {
            gameService.createGame(existingAuth, "");
        });
    }

    @Test
    public void testJoinGameValidInputsSuccess() throws Exception {
        GameService gameService = GameService.getInstance();
        int gameId = gameService.createGame(existingAuth, "gameName");

        int existingGameId = gameId;
        boolean joined = gameService.joinGame(existingAuth, "WHITE", existingGameId);

        assertTrue(joined);
    }

    @Test
    public void testJoinGameNonExistingGameThrowsException() {
        GameService gameService = GameService.getInstance();

        assertThrows(BadRequestException.class, () -> {
            int nonExistingGameId = 999;
            gameService.joinGame(existingAuth, "WHITE", nonExistingGameId);
        });
    }
}
