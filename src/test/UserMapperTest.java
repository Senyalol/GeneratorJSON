package test;

import DTO.UserDTO;
import DTO.UserMapper;
import data.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("UserMapper Unit Tests")
class UserMapperTest {

    @Nested
    @DisplayName("toDTO() tests")
    class ToDTOTests {

        @Test
        @DisplayName("Should convert User to UserDTO successfully")
        void shouldConvertUserToDTO() {
            // given
            User user = new User();
            user.setUser_id(1);
            user.setFirstname("John");
            user.setLastname("Doe");

            // when
            UserDTO result = UserMapper.toDTO(user);

            // then
            assertNotNull(result);
            assertEquals("John", result.getFirstname());
            assertEquals("Doe", result.getLastname());
            // Note: user_id is commented out in mapper
            assertNull(result.getUser_id());
        }

        @Test
        @DisplayName("Should handle null user gracefully")
        void shouldHandleNullUser() {
            // when & then
            assertThrows(NullPointerException.class, () -> UserMapper.toDTO(null));
        }
    }
}