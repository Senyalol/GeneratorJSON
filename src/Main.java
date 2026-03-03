import Data.Data;
import Data.User;
import Data.UserGenerator;
import Data.TransactionType;

import java.io.*;
import java.util.*;

public static void main(String[] args) throws IOException, InterruptedException {

    String path = "D:\\GeneratorJSON\\GeneratorJSON\\UserData.xlsx";

    File data = new File(path);

    if (!data.exists()) {
        System.err.println("Файл не найден: " + data.getAbsolutePath());
        System.err.println("Текущая директория: " + System.getProperty("user.dir"));


        String txtPath = "D:\\GeneratorJSON\\GeneratorJSON\\UserData.txt";
        File txtFile = new File(txtPath);


        return;
    }

    if (!data.canRead()) {
        System.err.println("Нет прав на чтение файла: " + path);
        return;
    }

    String fileName = data.getName().toLowerCase();

    if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {

        UserGenerator userGenerator = new UserGenerator();

        //Поток генератора
        while(true) {

            Thread.sleep(2000);
            User randomU = userGenerator.processExcelFile(data);

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
            System.out.println(dataPart.toString());

        }
        //Поток генератора
    }

}