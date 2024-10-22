package passoff.server;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.DataAccessException;
import service.ApplicationService;

public class ApplicationServiceTest {

    private ApplicationService applicationService;
    private AuthDAO mockAuthDAO;
    private GameDAO mockGameDAO;
    private UserDAO mockUserDAO;

    @BeforeEach
    public void setUp() {
        mockAuthDAO = Mockito.mock(AuthDAO.class);
        mockGameDAO = Mockito.mock(GameDAO.class);
        mockUserDAO = Mockito.mock(UserDAO.class);
        applicationService = new ApplicationService(mockAuthDAO, mockGameDAO, mockUserDAO);
    }

    @Test
    public void testClearDatabaseSuccessfully() throws DataAccessException {
        // Arrange
        // No specific arrangement needed as we are testing successful execution

        // Act
        applicationService.clearDatabase();

        // Assert
        Mockito.verify(mockAuthDAO).clearAuthData();
        Mockito.verify(mockGameDAO).clearGameData();
        Mockito.verify(mockUserDAO).clearUserData();
    }

    @Test
    public void testClearDatabaseFailure() throws DataAccessException {
        // Arrange
        Mockito.doThrow(new DataAccessException("Error clearing user data")).when(mockUserDAO).clearUserData();

        // Act & Assert
        Exception exception = assertThrows(DataAccessException.class, () -> {
            applicationService.clearDatabase();
        });

        assertEquals("Error clearing user data", exception.getMessage());

        // Verify that the other DAOs were called before the exception was thrown
        Mockito.verify(mockAuthDAO).clearAuthData();
        Mockito.verify(mockGameDAO).clearGameData();
    }
}