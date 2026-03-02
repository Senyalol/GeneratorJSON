import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

public static void main(String[] args) throws IOException {

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
        User randomU = processExcelFile(data);

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

}

public static User processExcelFile(File file) {
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