package test;

import data.Data;
import data.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Data Unit Tests")
class DataTest {

    @Nested
    @DisplayName("Data properties tests")
    class DataPropertiesTests {

        @Test
        @DisplayName("Should set and get user_id correctly")
        void shouldSetAndGetUserId() {
            // given
            Data data = new Data();

            // when
            data.setUser_id(123);

            // then
            assertEquals(123, data.getUser_id());
        }

        @Test
        @DisplayName("Should set and get firstname correctly")
        void shouldSetAndGetFirstname() {
            Data data = new Data();
            data.setFirstname("John");
            assertEquals("John", data.getFirstname());
        }

        @Test
        @DisplayName("Should set and get lastname correctly")
        void shouldSetAndGetLastname() {
            Data data = new Data();
            data.setLastname("Doe");
            assertEquals("Doe", data.getLastname());
        }

        @Test
        @DisplayName("Should set and get type correctly")
        void shouldSetAndGetType() {
            Data data = new Data();
            data.setType(TransactionType.Deposit);
            assertEquals(TransactionType.Deposit, data.getType());
        }

        @Test
        @DisplayName("Should set and get sum correctly")
        void shouldSetAndGetSum() {
            Data data = new Data();
            BigDecimal sum = new BigDecimal("1500.50");
            data.setSum(sum);
            assertEquals(sum, data.getSum());
        }

        @Test
        @DisplayName("Should set and get event_time correctly")
        void shouldSetAndGetEventTime() {
            Data data = new Data();
            long eventTime = System.currentTimeMillis();
            data.setEvent_time(eventTime);
            assertEquals(eventTime, data.getEvent_time());
        }
    }
}