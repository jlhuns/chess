package service;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ApplicationServiceTest {
    @Test
    public void testUnclearedDB() throws DataAccessException {
        ApplicationService ap = ApplicationService.getInstance();
        UserDAO userDAO = MemoryUserDao.getInstance();

        userDAO.insertUser(new UserData("test", "password", "null"));

        assertNotNull(userDAO.getUser("test"));
    }
}

