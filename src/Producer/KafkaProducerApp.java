package Producer;

import DTO.UserMapper;
import ORM.FlywayApply;
import ORM.PostgreSQLUtils;
import org.apache.kafka.clients.producer.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Data.UserGenerator;
import java.io.*;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;
import Data.User;
import DTO.UserDTO;

import Data.Data;

public class KafkaProducerApp {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducerApp.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Random random = new Random();

    private static void sendUser(KafkaProducer<String, String> producer, String topic, UserDTO userDTO) {
        try {
            String jsonValue = mapper.writeValueAsString(userDTO);
            ProducerRecord<String, String> record = new ProducerRecord<>(
                    topic,
                    String.valueOf(userDTO.getUser_id()),
                    jsonValue
            );
            RecordMetadata metadata = producer.send(record).get(5, TimeUnit.SECONDS);
            log.info("User отправлен: {} | partition={}, offset={}", userDTO, metadata.partition(), metadata.offset());
        } catch (Exception e) {
            log.error("Ошибка отправки user: {}", e.getMessage());
        }
    }

    public static void main(String[] args) {

        String dbName = System.getenv("POSTGRES_DB");
        String systemUrlDB = System.getenv("POSTGRES_SYSTEM_URL_DB");
        String urlDB = System.getenv("POSTGRES_URL_DB");
        String dbUsername = System.getenv("POSTGRES_USER");
        String dbPassword = System.getenv("POSTGRES_PASSWORD");

        log.info("Создание Базы данных: {}", dbName);
        PostgreSQLUtils.createDatabase(systemUrlDB, dbName, dbUsername, dbPassword);

        log.info("Применение миграций Flyway");
        FlywayApply migrations = new FlywayApply(urlDB, dbUsername, dbPassword);
        migrations.ApplyMigration();

        log.info("Запуск Kafka Producer");

        String bootstrapServers = System.getenv().getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092");
        String topic = System.getenv().getOrDefault("KAFKA_TOPIC", "user-transactions");
       // String messagesCountStr = System.getenv().getOrDefault("MESSAGES_COUNT", "100");
        String intervalMsStr = System.getenv().getOrDefault("INTERVAL_MS", "1000");

        int intervalMs = Integer.parseInt(intervalMsStr);

        log.info("Конфигурация: bootstrap={}, topic={}, interval={}ms",
                bootstrapServers, topic, intervalMs);

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

        loadPropertiesFromFile(props, "/app/config/producer.properties");

        int successCount = 0;
        int errorCount = 0;

        String userTopic = KafkaConfig.getUserTopicFromEnv();
        KafkaConfig.createTopicWithTwoPartitions(userTopic);

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {

            List<User> users = PostgreSQLUtils.getAllUsersFromDB(urlDB,dbUsername,dbPassword,dbName);
            if (users != null && !users.isEmpty()) {
                for(User user : users){
                    UserDTO userDTO = UserMapper.toDTO(user);
                    sendUser(producer, userTopic, userDTO);
                }
            } else {
                log.warn("Нет пользователей для отправки");
            }

            //Потоковая генерация данных
            while (true) {
                try {
                    Data data = generateRandomData(urlDB,dbUsername,dbPassword,dbName);
                    if (data == null) {
                        log.error("Не удалось сгенерировать данные для сообщения , пропуск");
                        errorCount++;
                        Thread.sleep(intervalMs);
                        continue;
                    }

                    String jsonValue = mapper.writeValueAsString(data);

                    ProducerRecord<String, String> record =
                            new ProducerRecord<>(topic, String.valueOf(data.getUser_id()), jsonValue);

                    RecordMetadata metadata = producer.send(record).get(5, TimeUnit.SECONDS);

                    log.info("Сообщение {} отправлено: partition={}, offset={}, data={}",
                             metadata.partition(), metadata.offset(), data);

                    successCount++;
                    Thread.sleep(intervalMs);

                } catch (JsonProcessingException e) {
                    log.error("Ошибка сериализации JSON: {}", e.getMessage());
                    errorCount++;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("Прерывание потока: {}", e.getMessage());
                    errorCount++;
                } catch (ExecutionException e) {
                    log.error("Ошибка выполнения: {}", e.getCause().getMessage());
                    errorCount++;
                } catch (Exception e) {
                    log.error("Неизвестная ошибка: {}", e.getMessage());
                    errorCount++;
                }
            }

            //producer.flush();

        } catch (Exception e) {
            log.error("Критическая ошибка: {}", e.getMessage(), e);
        }

        log.info("Завершено. Успешно: {}, Ошибки: {}", successCount, errorCount);
    }

    private static void loadPropertiesFromFile(Properties props, String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            try (InputStream input = new FileInputStream(file)) {
                props.load(input);
                log.info("Загружены настройки из файла: {}", filePath);
            } catch (IOException e) {
                log.warn("Не удалось загрузить настройки из файла: {}", e.getMessage());
            }
        }
    }

    private static Data generateRandomData(String url, String dbUsername, String dbPassword, String dbName) {

        return UserGenerator.GenerateData(url,dbUsername,dbPassword,dbName);

    }
}