package Producer;

import org.apache.kafka.clients.producer.ProducerConfig;
import java.util.Properties;

public class KafkaConfig {

    private static final String DEFAULT_BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String DEFAULT_TOPIC = "user-transactions";

    /**
     * Создает базовую конфигурацию для Kafka продюсера
     */
    public static Properties createProducerConfig(String bootstrapServers) {
        Properties props = new Properties();

        // Базовые настройки
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        // Настройки надежности
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);

        // Настройки производительности
        props.put(ProducerConfig.LINGER_MS_CONFIG, 5);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);

        return props;
    }

    /**
     * Создает конфигурацию с настройками по умолчанию
     */
    public static Properties createDefaultConfig() {
        return createProducerConfig(DEFAULT_BOOTSTRAP_SERVERS);
    }

    /**
     * Получает настройки из переменных окружения
     */
    public static String getBootstrapServersFromEnv() {
        String env = System.getenv("KAFKA_BOOTSTRAP_SERVERS");
        return env != null ? env : DEFAULT_BOOTSTRAP_SERVERS;
    }

    public static String getTopicFromEnv() {
        String env = System.getenv("KAFKA_TOPIC");
        return env != null ? env : DEFAULT_TOPIC;
    }

    /**
     * Проверка доступности Kafka (можно использовать для тестов)
     */
    public static String getConfigInfo() {
        return String.format("Kafka Config: bootstrap=%s, topic=%s",
                getBootstrapServersFromEnv(), getTopicFromEnv());
    }
}