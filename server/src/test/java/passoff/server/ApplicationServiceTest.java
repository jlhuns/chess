package passoff.server;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.Test;
import service.ApplicationService;
import static org.junit.jupiter.api.Assertions.*;

public class ApplicationServiceTest {
    @Test
    public void testClearedDB() throws DataAccessException {
        ApplicationService ap = ApplicationService.getInstance();
        UserDAO userDAO = MemoryUserDao.getInstance();

        userDAO.insertUser(new UserData("test", "password", "null"));

        ap.clearDatabase();

        assertNull(userDAO.getUser("test"));
    }
    @Test
    public void testUnclearedDB() throws DataAccessException {
        ApplicationService ap = ApplicationService.getInstance();
        UserDAO userDAO = MemoryUserDao.getInstance();

        userDAO.insertUser(new UserData("test", "password", "null"));

        assertNotNull(userDAO.getUser("test"));
    }
}

