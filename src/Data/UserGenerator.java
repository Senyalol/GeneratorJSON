package Data;

import ORM.PostgreSQLUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class UserGenerator {

    //Сгенерировать данные транзакций
    public static Data GenerateData(String url, String dbUsername, String dbPassword, String dbName) {

            User randomU = PostgreSQLUtils.getRandomUser(url,dbUsername,dbPassword,dbName);

            if (randomU == null) {
                System.err.println("Не удалось получить пользователя из Базы данных, пропуск генерации данных");
                return null;
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

            dataPart.setSum(sum);

            dataPart.setEvent_time(System.currentTimeMillis());

            return dataPart;

    }


}