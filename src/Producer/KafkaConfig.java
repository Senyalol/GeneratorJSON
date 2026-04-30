package Producer;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class KafkaConfig {

    private static final String DEFAULT_BOOTSTRAP_SERVERS = "localhost:9092";
    private static final String DEFAULT_TOPIC = "user-transactions";

    private static final String USER_TOPIC = "users";
    private static final int USER_PARTITIONS = 2;
    private static final short USER_REPLICATION_FACTOR = 1;

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

    public static String getUserTopicFromEnv() {
        String env = System.getenv("KAFKA_USER_TOPIC");
        return env != null ? env : USER_TOPIC;
    }

    public static String getConfigInfo() {
        return String.format("Kafka Config: bootstrap=%s, topic=%s",
                getBootstrapServersFromEnv(), getTopicFromEnv());
    }

    public static void createTopic(String topicName, int partitions, short replicationFactor) {
        // Используем существующий метод для создания пропсов
        Properties props = createProducerConfig(getBootstrapServersFromEnv());

        try (AdminClient adminClient = AdminClient.create(props)) {
            NewTopic newTopic = new NewTopic(topicName, partitions, replicationFactor);

            CreateTopicsResult result = adminClient.createTopics(Collections.singleton(newTopic));

            // Ждем создания топика (максимум 10 секунд)
            result.all().get(10, TimeUnit.SECONDS);

           // log.info("Топик '{}' успешно создан с {} партициями и репликацией {}",
                    //topicName, partitions, replicationFactor);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
           // log.error("Прерывание потока при создании топика '{}': {}", topicName, e.getMessage());
        } catch (ExecutionException e) {
            if (e.getCause().getMessage().contains("already exists")) {
               // log.warn("Топик '{}' уже существует", topicName);
            } else {
                //log.error("Ошибка при создании топика '{}': {}", topicName, e.getMessage());
            }
        } catch (TimeoutException e) {
            //log.error("Таймаут при создании топика '{}': {}", topicName, e.getMessage());
        }
    }

    public static void createTopicWithTwoPartitions(String topicName){
        createTopic(topicName, 2, (short) 1);
    }

    public static void createDefaultNewTopic(){
        createTopic(USER_TOPIC, USER_PARTITIONS, USER_REPLICATION_FACTOR);
    }

}