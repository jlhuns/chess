package passoff.server;

import dataAccess.BadRequestException;
import dataAccess.DataAccessException;
import dataAccess.UnauthorizedException;
import model.GameData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import passoff.model.TestAuthResult;
import passoff.model.TestUser;
import server.Server;
import service.GameService;

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
    public void testListGames_ValidToken_ReturnsEmptyGames() throws Exception {
        GameService gameService = GameService.getInstance();

        List<GameData> games = gameService.listGames(existingAuth);

        assertNotNull(games);
        assertTrue(games.isEmpty());
    }

    @Test
    public void testListGames_InvalidToken_ThrowsException() {
        GameService gameService = GameService.getInstance();

        assertThrows(UnauthorizedException.class, () -> {
            gameService.listGames("invalidToken");
        });
    }

    @Test
    public void testCreateGame_ValidInputs_ReturnsGameId() throws Exception {
        GameService gameService = GameService.getInstance();

        int gameId = gameService.createGame(existingAuth, "gameName");

        assertTrue(gameId > 0);
    }

    @Test
    public void testCreateGame_EmptyGameName_ThrowsException() {
        GameService gameService = GameService.getInstance();

        assertThrows(DataAccessException.class, () -> {
            gameService.createGame(existingAuth, "");
        });
    }

    @Test
    public void testJoinGame_ValidInputs_Success() throws Exception {
        GameService gameService = GameService.getInstance();
        int gameId = gameService.createGame(existingAuth, "gameName");

        int existingGameId = gameId;
        boolean joined = gameService.joinGame(existingAuth, "WHITE", existingGameId);

        assertTrue(joined);
    }

    @Test
    public void testJoinGame_NonExistingGame_ThrowsException() {
        GameService gameService = GameService.getInstance();

        assertThrows(BadRequestException.class, () -> {
            int nonExistingGameId = 999;
            gameService.joinGame(existingAuth, "WHITE", nonExistingGameId);
        });
    }
}
