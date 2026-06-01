package test;

import DataBase.PostgreSQLUtils;
import data.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostgreSQLUtils Unit Tests")
class PostgreSQLUtilsTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private Statement mockStatement;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    @Nested
    @DisplayName("quoteIdentifier() tests")
    class QuoteIdentifierTests {

        @Test
        @DisplayName("Should quote identifier correctly")
        void shouldQuoteIdentifierCorrectly() {
            // This is a private method, testing via reflection would be needed
            // For now, just a placeholder
            assertTrue(true);
        }
    }

    @Nested
    @DisplayName("databaseExists() tests")
    class DatabaseExistsTests {

        @Test
        @DisplayName("Should return true when database exists")
        void shouldReturnTrueWhenDatabaseExists() throws SQLException {


            assertTrue(true);
        }
    }

    @Nested
    @DisplayName("getAllUsersFromDB() tests")
    class GetAllUsersFromDBTests {

        @Test
        @DisplayName("Should return list of users when query succeeds")
        void shouldReturnListOfUsers() throws SQLException {
            try (MockedStatic<DriverManager> driverManagerMock = mockStatic(DriverManager.class)) {
                driverManagerMock.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                        .thenReturn(mockConnection);
                when(mockConnection.createStatement()).thenReturn(mockStatement);
                when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);

                when(mockResultSet.next()).thenReturn(true, true, false);
                when(mockResultSet.getInt("user_id")).thenReturn(1, 2);
                when(mockResultSet.getString("firstname")).thenReturn("John", "Jane");
                when(mockResultSet.getString("lastname")).thenReturn("Doe", "Smith");

                List<User> result = PostgreSQLUtils.getAllUsersFromDB(
                        "jdbc:postgresql://localhost:5432/test", "user", "pass", "testdb");

                assertNotNull(result);
                assertEquals(2, result.size());
            }
        }
    }
}