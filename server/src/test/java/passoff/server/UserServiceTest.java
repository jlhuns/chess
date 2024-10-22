package passoff.server;

import dataAccess.AlreadyTakenException;
import dataAccess.BadRequestException;
import dataAccess.DataAccessException;
import dataAccess.UnauthorizedException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Test;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    @Test
    public void testRegister_NewUser_Success() throws Exception {
        UserData newUser = new UserData("newuser", "password123", null);
        UserService userService = UserService.getInstance();

        AuthData authData = userService.register(newUser);

        assertNotNull(authData);
        assertNotNull(authData.authToken());
        assertEquals("newuser", authData.username());
    }

    @Test
    public void testRegister_ExistingUser_ThrowsException() throws Exception {
        UserData existingUser = new UserData("existinguser", "password123", null);
        UserService userService = UserService.getInstance();

        // Assume user already exists
        userService.register(existingUser); // First registration

        Exception exception = assertThrows(AlreadyTakenException.class, () -> {
            userService.register(existingUser);
        });

        assertEquals("User already exists.", exception.getMessage());
    }

    @Test
    public void testLogin_ValidCredentials_Success() throws Exception {
        UserData validUser = new UserData("validuser", "password123", null);
        UserService userService = UserService.getInstance();
        userService.register(validUser);

        AuthData authData = userService.login(validUser);

        assertNotNull(authData);
        assertNotNull(authData.authToken());
        assertEquals("validuser", authData.username());
    }

    @Test
    public void testLogin_NotRegistered_ThrowsException() throws BadRequestException, DataAccessException {
        UserData invalidUser = new UserData("invaliduser", "wrongpassword", null);
        UserService userService = UserService.getInstance();

        Exception exception = assertThrows(UnauthorizedException.class, () -> {
            userService.login(invalidUser);
        });

        assertEquals(null, exception.getMessage());
    }

    @Test
    public void testLogout_ValidToken_Success() throws Exception {
        UserData newUser = new UserData("newuser2", "password123", null);
        UserService userService = UserService.getInstance();

        AuthData authData = userService.register(newUser);

        userService.login(newUser);
        String validToken = authData.authToken();

        userService.logout(validToken);

        assertEquals(validToken,validToken );
    }

    @Test
    public void testLogout_InValidToken() throws Exception {
        UserData newUser = new UserData("newuser22", "password123", null);
        UserService userService = UserService.getInstance();

        AuthData authData = userService.register(newUser);

        userService.login(newUser);
        String InvalidToken = "gothca"; // Use the token returned from login/register

        Exception exception = assertThrows(UnauthorizedException.class, () -> {
            userService.logout(InvalidToken);
        });

        assertEquals(null, exception.getMessage());
    }
}
