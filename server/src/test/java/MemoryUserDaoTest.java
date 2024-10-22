import dataAccess.DataAccessException;
import dataAccess.MemoryUserDao;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MemoryUserDaoTest {

    private MemoryUserDao userDao;
    private UserData testUser;

    @BeforeEach
    void setUp() {
        userDao = new MemoryUserDao();
        testUser = new UserData("testUser", "password123", "test@example.com");
    }

    @Test
    void testInsertUserPositive() throws DataAccessException {
        // Positive test: Insert a new user
        userDao.insertUser(testUser);
        assertNotNull(userDao.getUser("testUser"), "The user should exist in the database.");
    }

    @Test
    void testInsertUserNegative() throws DataAccessException {
        // Negative test: Attempt to insert a user that already exists
        userDao.insertUser(testUser);
        userDao.insertUser(testUser);
    }

    @Test
    void testPersistenceOfAddedUser() throws DataAccessException {
        // Test to ensure added users stay in the database
        userDao.insertUser(testUser);
        UserData retrievedUser = userDao.getUser("testUser");

        assertNotNull(retrievedUser, "The user should be retrievable from the database.");
        assertEquals("testUser", retrievedUser.username(), "The username should match.");
        assertEquals("password123", retrievedUser.password(), "The password should match.");
        assertEquals("test@example.com", retrievedUser.email(), "The email should match.");
    }
}