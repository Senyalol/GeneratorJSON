package Producer;

import org.apache.kafka.clients.producer.ProducerConfig;
import java.util.Properties;

public class KafkaConfig {

    private static final String DEFAULT_BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String DEFAULT_TOPIC = "user-transactions";

    public static Properties createProducerConfig(String bootstrapServers) {
        Properties props = new Properties();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);

        props.put(ProducerConfig.LINGER_MS_CONFIG, 5);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);

        return props;
    }

    public static Properties createDefaultConfig() {
        return createProducerConfig(DEFAULT_BOOTSTRAP_SERVERS);
    }

    public static String getBootstrapServersFromEnv() {
        String env = System.getenv("KAFKA_BOOTSTRAP_SERVERS");
        return env != null ? env : DEFAULT_BOOTSTRAP_SERVERS;
    }

    public static String getTopicFromEnv() {
        String env = System.getenv("KAFKA_TOPIC");
        return env != null ? env : DEFAULT_TOPIC;
    }

    public static String getConfigInfo() {
        return String.format("Kafka Config: bootstrap=%s, topic=%s",
                getBootstrapServersFromEnv(), getTopicFromEnv());
    }
}