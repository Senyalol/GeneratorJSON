package Producer;

import org.apache.kafka.clients.producer.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigDecimal;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutionException;

import Data.Data;
import Data.TransactionType;

public class KafkaProducerApp {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducerApp.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Random random = new Random();

    public static void main(String[] args) {
        log.info("Запуск Kafka Producer приложения...");

        String bootstrapServers = System.getenv().getOrDefault("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092");
        String topic = System.getenv().getOrDefault("KAFKA_TOPIC", "user-transactions");
        String messagesCountStr = System.getenv().getOrDefault("MESSAGES_COUNT", "100");
        String intervalMsStr = System.getenv().getOrDefault("INTERVAL_MS", "1000");

        int messagesCount = Integer.parseInt(messagesCountStr);
        int intervalMs = Integer.parseInt(intervalMsStr);

        log.info("Конфигурация: bootstrap={}, topic={}, messages={}, interval={}ms",
                bootstrapServers, topic, messagesCount, intervalMs);

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

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {

            for (int i = 1; i <= messagesCount; i++) {
                try {
                    Data data = generateRandomData(i);
                    String jsonValue = mapper.writeValueAsString(data);

                    ProducerRecord<String, String> record =
                            new ProducerRecord<>(topic, String.valueOf(data.getUser_id()), jsonValue);

                    RecordMetadata metadata = producer.send(record).get(5, TimeUnit.SECONDS);

                    log.info("✓ Сообщение {} отправлено: partition={}, offset={}, data={}",
                            i, metadata.partition(), metadata.offset(), data);

                    successCount++;
                    Thread.sleep(intervalMs);

                } catch (JsonProcessingException e) {
                    log.error("✗ Ошибка сериализации JSON: {}", e.getMessage());
                    errorCount++;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("✗ Прерывание потока: {}", e.getMessage());
                    errorCount++;
                } catch (ExecutionException e) {
                    log.error("✗ Ошибка выполнения: {}", e.getCause().getMessage());
                    errorCount++;
                } catch (Exception e) {
                    log.error("✗ Неизвестная ошибка: {}", e.getMessage());
                    errorCount++;
                }
            }

            producer.flush();

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

    private static Data generateRandomData(int id) {
        Data data = new Data();
        data.setUser_id(id);

        String[] firstNames = {"Джеймс", "Майкл", "Роберт", "Дэвид", "Александр",
                "Дмитрий", "Иван", "Сергей", "Андрей", "Алексей"};
        String[] lastNames = {"Андерсон", "Браун", "Джонсон", "Миллер", "Иванов",
                "Смирнов", "Кузнецов", "Попов", "Петров", "Соколов"};

        data.setFirstname(firstNames[random.nextInt(firstNames.length)]);
        data.setLastname(lastNames[random.nextInt(lastNames.length)]);

        TransactionType[] types = TransactionType.values();
        if (types.length > 0) {
            data.setType(types[random.nextInt(types.length)]);
        }

        data.setSum(BigDecimal.valueOf(random.nextInt(10000) + random.nextDouble()));

        return data;
    }
}