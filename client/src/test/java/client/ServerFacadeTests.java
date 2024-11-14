package client;

import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() throws Exception {
        server = new Server();
        var port = server.run(0); // Get the random port
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("localhost:" + port); // Initialize facade with the server's port
    }

    @AfterAll
    static void stopServer() {
        if (server != null) {
            server.stop();
        }
    }

    @BeforeEach
    void clearDatabase() {
        // Reset database here if necessary to ensure tests are independent

    }

    @Test
    void testRegisterSuccess() throws Exception {
        var authData = facade.register("player1", "password", "p1@email.com");
        assertTrue(authData, "Registration should be successful");
    }

    // Test for registration failure due to invalid data
    @Test
    void testRegisterFailure() throws Exception {
        var authData = facade.register("", "", "invalidemail");
        assertFalse(authData, "Registration should fail with invalid data");
    }

    // Test for login success
    @Test
    void testLoginSuccess() throws Exception {
        facade.register("player2", "password", "player2@email.com");
        boolean loginSuccess = facade.login("player2", "password");
        assertTrue(loginSuccess, "Login should be successful with correct credentials");
    }

    // Test for login failure with incorrect credentials
    @Test
    void testLoginFailure() throws Exception {
        boolean loginSuccess = facade.login("nonexistentuser", "wrongpassword");
        assertFalse(loginSuccess, "Login should fail with incorrect credentials");
    }

    // Test for successful logout
    @Test
    void testLogout() throws Exception {
        facade.register("player3", "password", "player3@email.com");
        facade.login("player3", "password");
        boolean logoutSuccess = facade.logout();
        assertTrue(logoutSuccess, "Logout should be successful after login");
    }

    // Test for createGame success
    @Test
    void testCreateGameSuccess() throws Exception {
        facade.register("player4", "password", "player4@email.com");
        facade.login("player4", "password");
        int gameId = facade.createGame("Test Game");
        assertTrue(gameId > 0, "Game creation should return a valid game ID");
    }

    // Test for createGame failure (e.g., unauthorized user)
    @Test
    void testCreateGameFailure() throws Exception {
        int gameId = facade.createGame("Test Game");
        assertEquals(-1, gameId, "Game creation should fail for unauthenticated users");
    }

    // Test for listGames success
    @Test
    void testListGames() throws Exception {
        facade.register("player5", "password", "player5@email.com");
        facade.login("player5", "password");
        facade.createGame("Game 1");
        List<GameData> games = facade.listGames();
        assertFalse(games.isEmpty(), "List of games should not be empty after creating a game");
    }

    // Test for joinGame success
    @Test
    void testJoinGameSuccess() throws Exception {
        facade.register("player6", "password", "player6@email.com");
        facade.login("player6", "password");
        int gameId = facade.createGame("Test Game 2");
        boolean joinSuccess = facade.joinGame(gameId, "Red");
        assertTrue(joinSuccess, "Joining the game should be successful");
    }

    // Test for joinGame failure (e.g., game doesn't exist)
    @Test
    void testJoinGameFailure() throws Exception {
        facade.register("player7", "password", "player7@email.com");
        facade.login("player7", "password");
        boolean joinSuccess = facade.joinGame(99999, "Blue");
        assertFalse(joinSuccess, "Joining a non-existent game should fail");
    }

    @Test
    void testRegisterAlreadyTakenUsername() throws Exception {
        facade.register("player9", "password", "player9@email.com");
        boolean registerSuccess = facade.register("player9", "newpassword", "newemail@email.com");
        assertFalse(registerSuccess, "Registration should fail with an already taken username");
    }

    @Test
    void testLoginAfterLogout() throws Exception {
        facade.register("player11", "password", "player11@email.com");
        facade.login("player11", "password");
        facade.logout();
        boolean loginSuccess = facade.login("player11", "password");
        assertTrue(loginSuccess, "Login should be successful after logout");
    }

    @Test
    void testJoinGameWithoutLogin() throws Exception {
        int gameId = facade.createGame("Game 4");
        boolean joinSuccess = facade.joinGame(gameId, "Red");
        assertFalse(joinSuccess, "Joining a game should fail without a logged-in user");
    }

    @Test
    void testCreateGameAndJoinByDifferentPlayer() throws Exception {
        facade.register("player14", "password", "player14@email.com");
        facade.login("player14", "password");
        int gameId = facade.createGame("Game 5");

        facade.register("player15", "password", "player15@email.com");
        facade.login("player15", "password");

        boolean joinSuccess = facade.joinGame(gameId, "Blue");
        assertTrue(joinSuccess, "Second player should be able to join an existing game");
    }

}
