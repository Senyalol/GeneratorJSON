public static void main(String[] args) {

    String path = "D:\\GeneratorJSON\\GeneratorJSON\\UserData.xslx";

    File data = new File(path);

    if(!data.exists()){
        System.err.println("Файл не найден: " + data.getAbsolutePath());
        System.err.println("Текущая директория: " + System.getProperty("user.dir"));
        return;
    }

    if (!data.canRead()) {
        System.err.println("Нет прав на чтение файла: " + path);
        return;
    }

    

}