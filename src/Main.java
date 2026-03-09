//import org.apache.kafka.clients.producer.KafkaProducer;
//import org.apache.kafka.clients.producer.ProducerRecord;
//import org.apache.kafka.clients.producer.RecordMetadata;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import Data.Data;
//import Data.User;
//import Data.UserGenerator;
//import Data.TransactionType;

import java.io.*;
//import java.util.*;
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import Producer.KafkaConfig;

public static void main(String[] args) throws IOException, InterruptedException {

//    String path = "D:\\GeneratorJSON\\GeneratorJSON\\UserData.xlsx";
//
//    File data = new File(path);
//
//    if (!data.exists()) {
//        System.err.println("Файл не найден: " + data.getAbsolutePath());
//        System.err.println("Текущая директория: " + System.getProperty("user.dir"));
//
//
//        String txtPath = "D:\\GeneratorJSON\\GeneratorJSON\\UserData.txt";
//        File txtFile = new File(txtPath);
//
//
//        return;
//    }
//
//    if (!data.canRead()) {
//        System.err.println("Нет прав на чтение файла: " + path);
//        return;
//    }
//
//    String fileName = data.getName().toLowerCase();
//
//    if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
//
//        UserGenerator userGenerator = new UserGenerator();
//
//        // Конфигурация Kafka
//        String bootstrapServers = KafkaConfig.getBootstrapServersFromEnv();
//        String topic = KafkaConfig.getTopicFromEnv();
//
//        Properties kafkaProps = KafkaConfig.createProducerConfig(bootstrapServers);
//        KafkaProducer<String, String> producer = new KafkaProducer<>(kafkaProps);
//
//        ObjectMapper mapper = new ObjectMapper();
//
//        //Поток генератора
//        while(true) {
//
//            // Thread.sleep(2000);
//            User randomU = userGenerator.processExcelFile(data);
//
//            Random rand = new Random();
//
//            Data dataPart = new Data();
//            dataPart.setUser_id(randomU.getUser_id());
//            dataPart.setFirstname(randomU.getFirstname());
//            dataPart.setLastname(randomU.getLastname());
//
//            TransactionType[] types = TransactionType.values();
//            TransactionType type = types[rand.nextInt(types.length)];
//            dataPart.setType(type);
//
//            //Сумма транзакции
//            double randomDouble = rand.nextDouble() * 10000;
//            BigDecimal sum = BigDecimal.valueOf(randomDouble)
//                    .setScale(2, RoundingMode.HALF_UP);
//
//            dataPart.setSum(sum);
//
//            try {
//                // Сериализуем объект Data в JSON
//                String jsonValue = mapper.writeValueAsString(dataPart);
//
//                // Формируем и отправляем сообщение в Kafka
//                ProducerRecord<String, String> record =
//                        new ProducerRecord<>(topic, String.valueOf(dataPart.getUser_id()), jsonValue);
//
//                RecordMetadata metadata = producer.send(record).get();
//
//                System.out.printf(
//                        "Отправлено в Kafka: topic=%s partition=%d offset=%d data=%s%n",
//                        metadata.topic(), metadata.partition(), metadata.offset(), dataPart
//                );
//            } catch (Exception e) {
//                System.err.println("Ошибка отправки в Kafka: " + e.getMessage());
//                e.printStackTrace();
//            }
//
//        }
//        //Поток генератора
//
//    }

}