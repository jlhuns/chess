package service;

import dataaccess.AlreadyTakenException;
import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    @Test
    public void testRegisterNewUserSuccess() throws Exception {
        UserData newUser = new UserData("newuser", "password123", null);
        UserService userService = UserService.getInstance();

        AuthData authData = userService.register(newUser);

        assertNotNull(authData);
        assertNotNull(authData.authToken());
        assertEquals("newuser", authData.username());
    }

    @Test
    public void testRegisterExistingUserThrowsException() throws Exception {
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
    public void testLoginValidCredentialsSuccess() throws Exception {
        UserData validUser = new UserData("validuser", "password123", null);
        UserService userService = UserService.getInstance();
        userService.register(validUser);

        AuthData authData = userService.login(validUser);

        assertNotNull(authData);
        assertNotNull(authData.authToken());
        assertEquals("validuser", authData.username());
    }

    @Test
    public void testLoginNotRegisteredThrowsException() throws BadRequestException, DataAccessException {
        UserData invalidUser = new UserData("invaliduser", "wrongpassword", null);
        UserService userService = UserService.getInstance();

        Exception exception = assertThrows(UnauthorizedException.class, () -> {
            userService.login(invalidUser);
        });

        assertEquals(null, exception.getMessage());
    }

    @Test
    public void testLogoutValidTokenSuccess() throws Exception {
        UserData newUser = new UserData("newuser2", "password123", null);
        UserService userService = UserService.getInstance();

        AuthData authData = userService.register(newUser);

        userService.login(newUser);
        String validToken = authData.authToken();

        userService.logout(validToken);

        assertEquals(validToken,validToken );
    }

    @Test
    public void testLogoutInvalidToken() throws Exception {
        UserData newUser = new UserData("newuser22", "password123", null);
        UserService userService = UserService.getInstance();

        AuthData authData = userService.register(newUser);

        userService.login(newUser);
        String invalidToken = "gothca"; // Use the token returned from login/register

        Exception exception = assertThrows(UnauthorizedException.class, () -> {
            userService.logout(invalidToken);
        });

        assertEquals(null, exception.getMessage());
    }
}
