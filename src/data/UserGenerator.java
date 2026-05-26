package data;

import DataBase.PostgreSQLUtils;

import exception.GenerateNullException;
import exception.TransactionException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserGenerator {

    private static final Logger LOGGER = LogManager.getLogger(UserGenerator.class);

    //Как аномалия в сервисе спарка
    private static final double MAX_TRANSACTION_AMOUNT = Double.parseDouble(System.getenv("MAX_TRANSACTION_AMOUNT")); //100000.0;
    private static final double MIN_TRANSACTION_AMOUNT = Double.parseDouble(System.getenv("MIN_TRANSACTION_AMOUNT"));

    //Сгенерировать данные транзакций
    public static Data GenerateData(String url, String dbUsername, String dbPassword, String dbName) {

            User randomU = PostgreSQLUtils.getRandomUser(url,dbUsername,dbPassword,dbName);

            if (randomU == null) {
                LOGGER.error("Не удалось получить пользователя из Базы данных, пропуск генерации данных");
                throw new GenerateNullException(randomU);
            }

            Random rand = new Random();

            Data dataPart = new Data();
            dataPart.setUser_id(randomU.getUser_id());
            dataPart.setFirstname(randomU.getFirstname());
            dataPart.setLastname(randomU.getLastname());

            TransactionType[] types = TransactionType.values();
            TransactionType type = types[rand.nextInt(types.length)];
            dataPart.setType(type);

            //Сумма транзакции
            double randomDouble = rand.nextDouble() * 10000;
            BigDecimal sum = BigDecimal.valueOf(randomDouble)
                    .setScale(2, RoundingMode.HALF_UP);

            if(randomDouble < MIN_TRANSACTION_AMOUNT || randomDouble > MAX_TRANSACTION_AMOUNT){
                LOGGER.error("Сумма - " + randomDouble);
                throw new TransactionException();
            }

            dataPart.setSum(sum);

            dataPart.setEvent_time(System.currentTimeMillis());

            return dataPart;

    }


}