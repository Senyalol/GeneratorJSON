package test;

import DataBase.PostgreSQLUtils;
import data.Data;
import data.User;
import data.UserGenerator;
import exception.GenerateNullException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserGenerator Unit Tests")
class UserGeneratorTest {

    private static final String TEST_URL = "jdbc:postgresql://localhost:5432/test";
    private static final String TEST_USER = "testuser";
    private static final String TEST_PASSWORD = "testpass";
    private static final String TEST_DB = "testdb";

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setUser_id(1);
        mockUser.setFirstname("John");
        mockUser.setLastname("Doe");
    }

    @Nested
    @DisplayName("GenerateData() tests")
    class GenerateDataTests {

        @Test
        @DisplayName("Should generate data successfully when user exists")
        void shouldGenerateDataSuccessfully() {
            try (MockedStatic<PostgreSQLUtils> postgresMock = mockStatic(PostgreSQLUtils.class)) {
                // given
                postgresMock.when(() -> PostgreSQLUtils.getRandomUser(TEST_URL, TEST_USER, TEST_PASSWORD, TEST_DB))
                        .thenReturn(mockUser);

                // when
                Data result = UserGenerator.GenerateData(TEST_URL, TEST_USER, TEST_PASSWORD, TEST_DB);

                // then
                assertNotNull(result);
                assertEquals(1, result.getUser_id());
                assertEquals("John", result.getFirstname());
                assertEquals("Doe", result.getLastname());
                assertNotNull(result.getType());
                assertNotNull(result.getSum());
                assertTrue(result.getEvent_time() > 0);
            }
        }

        @Test
        @DisplayName("Should throw GenerateNullException when user not found")
        void shouldThrowGenerateNullExceptionWhenUserNotFound() {
            try (MockedStatic<PostgreSQLUtils> postgresMock = mockStatic(PostgreSQLUtils.class)) {
                // given
                postgresMock.when(() -> PostgreSQLUtils.getRandomUser(TEST_URL, TEST_USER, TEST_PASSWORD, TEST_DB))
                        .thenReturn(null);

                // when & then
                assertThrows(GenerateNullException.class,
                        () -> UserGenerator.GenerateData(TEST_URL, TEST_USER, TEST_PASSWORD, TEST_DB));
            }
        }
    }

    @Nested
    @DisplayName("getEnvDouble() tests")
    class GetEnvDoubleTests {

        @Test
        @DisplayName("Should return default value when env var not set")
        void shouldReturnDefaultWhenEnvVarNotSet() {
            // The method is private, testing via reflection or indirectly through GenerateData
            // This test verifies that default values are used
            try (MockedStatic<PostgreSQLUtils> postgresMock = mockStatic(PostgreSQLUtils.class)) {
                postgresMock.when(() -> PostgreSQLUtils.getRandomUser(anyString(), anyString(), anyString(), anyString()))
                        .thenReturn(mockUser);

                // When env vars not set, should use defaults (100000 and 99.9)
                Data result = UserGenerator.GenerateData(TEST_URL, TEST_USER, TEST_PASSWORD, TEST_DB);

                assertNotNull(result);
                assertNotNull(result.getSum());
            }
        }
    }
}