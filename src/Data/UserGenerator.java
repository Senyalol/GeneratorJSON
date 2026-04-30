package Data;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UserGenerator {

    //Извлечь данные из файла и вернуть случайного пользователя
    private static User processExcelFile(File file) {
        List<Integer> idList = new ArrayList<>();
        List<String> firstNameList = new ArrayList<>();
        List<String> lastNameList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

            for (Row row : sheet) {

                if (row == null) continue;

                if (row.getRowNum() == 0) {
                    Cell firstCell = row.getCell(0);
                    if (firstCell != null &&
                            firstCell.getCellType() == CellType.STRING &&
                            firstCell.getStringCellValue().equalsIgnoreCase("id")) {
                        continue;
                    }
                }

                try {
                    Cell idCell = row.getCell(0);
                    if (idCell == null) continue;

                    int id = getIntValue(idCell, evaluator);

                    Cell firstNameCell = row.getCell(1);
                    String firstName = getStringValue(firstNameCell, evaluator);

                    Cell lastNameCell = row.getCell(2);
                    String lastName = getStringValue(lastNameCell, evaluator);

                    if (id > 0 && !firstName.isEmpty() && !lastName.isEmpty()) {
                        idList.add(id);
                        firstNameList.add(firstName);
                        lastNameList.add(lastName);
                    }

                }
                catch (Exception e) {
                    System.err.println("Ошибка в строке " + (row.getRowNum() + 1) +
                            ": " + e.getMessage());
                }
            }

            return processData(idList, firstNameList, lastNameList);

        } catch (IOException e) {
            System.err.println("Ошибка чтения Excel файла: " + e.getMessage());
            e.printStackTrace();
        }

        //Добавить кастомный глобальный Exception
        return null;
    }

    //Извлечь данные из файла и вернуть всех пользователей
    private static List<User> processExtractUsers(File file) {
        List<Integer> idList = new ArrayList<>();
        List<String> firstNameList = new ArrayList<>();
        List<String> lastNameList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

            for (Row row : sheet) {

                if (row == null) continue;

                if (row.getRowNum() == 0) {
                    Cell firstCell = row.getCell(0);
                    if (firstCell != null &&
                            firstCell.getCellType() == CellType.STRING &&
                            firstCell.getStringCellValue().equalsIgnoreCase("id")) {
                        continue;
                    }
                }

                try {
                    Cell idCell = row.getCell(0);
                    if (idCell == null) continue;

                    int id = getIntValue(idCell, evaluator);

                    Cell firstNameCell = row.getCell(1);
                    String firstName = getStringValue(firstNameCell, evaluator);

                    Cell lastNameCell = row.getCell(2);
                    String lastName = getStringValue(lastNameCell, evaluator);

                    if (id > 0 && !firstName.isEmpty() && !lastName.isEmpty()) {
                        idList.add(id);
                        firstNameList.add(firstName);
                        lastNameList.add(lastName);
                    }

                }
                catch (Exception e) {
                    System.err.println("Ошибка в строке " + (row.getRowNum() + 1) +
                            ": " + e.getMessage());
                }
            }

            return getUsersList(idList, firstNameList, lastNameList);

        } catch (IOException e) {
            System.err.println("Ошибка чтения Excel файла: " + e.getMessage());
            e.printStackTrace();
        }

        //Добавить кастомный глобальный Exception
        return null;
    }

    //Финальный для отправки метод
    public static List<User> prepareToSend(){

        File file = getUserFile();

        return processExtractUsers(file);
    }

    private static int getIntValue(Cell cell, FormulaEvaluator evaluator) {
        if (cell == null) return 0;

        if (cell.getCellType() == CellType.FORMULA) {
            cell = evaluator.evaluateInCell(cell);
        }

        switch (cell.getCellType()) {
            case NUMERIC:
                return (int) cell.getNumericCellValue();
            case STRING:
                try {
                    return Integer.parseInt(cell.getStringCellValue().trim());
                } catch (NumberFormatException e) {
                    return 0;
                }
            default:
                return 0;
        }
    }

    private static String getStringValue(Cell cell, FormulaEvaluator evaluator) {
        if (cell == null) return "";

        if (cell.getCellType() == CellType.FORMULA) {
            cell = evaluator.evaluateInCell(cell);
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((int) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }

    //Вернуть случайного пользователя
    private static User processData(List<Integer> idList,
                                    List<String> firstNameList,
                                    List<String> lastNameList) {

        if (idList.isEmpty()) {
            System.err.println("Нет данных для обработки");
            return null;
        }

        int[] ids = new int[idList.size()];
        String[] firstNames = new String[firstNameList.size()];
        String[] lastNames = new String[lastNameList.size()];

        for (int i = 0; i < idList.size(); i++) {
            ids[i] = idList.get(i);
            firstNames[i] = firstNameList.get(i);
            lastNames[i] = lastNameList.get(i);
        }

        Random random = new Random();
        int randomID = random.nextInt(ids.length);

        return new User(ids[randomID], firstNames[randomID], lastNames[randomID]);

    }

    //Получить путь к файлу с пользователями
    private static File getUserFile(){

        String path = System.getenv().getOrDefault("USER_DATA_FILE", "UserData.xlsx");

        File data = new File(path);

        if (!data.exists()) {
            System.err.println("Файл не найден: " + data.getAbsolutePath());
            System.err.println("Текущая директория: " + System.getProperty("user.dir"));
            return null;
        }

        if (!data.canRead()) {
            System.err.println("Нет прав на чтение файла: " + path);
            return null;
        }

        return data;
    }

    //Сгенерировать данные транзакций
    public static Data GenerateData() {

//        String path = System.getenv().getOrDefault("USER_DATA_FILE", "UserData.xlsx");
//
//        File data = new File(path);
//
//        if (!data.exists()) {
//            System.err.println("Файл не найден: " + data.getAbsolutePath());
//            System.err.println("Текущая директория: " + System.getProperty("user.dir"));
//            return null;
//        }
//
//        if (!data.canRead()) {
//            System.err.println("Нет прав на чтение файла: " + path);
//            return null;
//        }

        File data = getUserFile();
        String fileName = data.getName().toLowerCase();

        if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {

            User randomU = UserGenerator.processExcelFile(data);
            if (randomU == null) {
                System.err.println("Не удалось получить пользователя из Excel, пропуск генерации данных");
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

        return null;
    }

    //Необходимы тесты



    private static List<User> getUsersList(List<Integer> idList,
                                    List<String> firstNameList,
                                    List<String> lastNameList) {

        if (idList.isEmpty()) {
            System.err.println("Нет данных для обработки");
            return null;
        }

        List<User> userList = new ArrayList<User>();

        for(int i = 0; i < idList.size(); i++) {
            userList.add(new User(idList.get(i), firstNameList.get(i), lastNameList.get(i)));
        }

        return userList;

    }


}