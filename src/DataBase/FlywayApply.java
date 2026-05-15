package DataBase;

import lombok.Data;
import org.flywaydb.core.Flyway;

@Data
public class FlywayApply {

    private final String URL;
    private final String USERNAME;
    private final String PASSWORD;

    public FlywayApply(String URL, String USERNAME, String PASSWORD) {
        this.URL = URL;
        this.USERNAME = USERNAME;
        this.PASSWORD = PASSWORD;
    }

    public void ApplyMigration() {

        try {
            System.out.println("========================================");
            System.out.println("Применение миграций Flyway v9");
            System.out.println("URL: " + URL);
            System.out.println("========================================");

            // Конфигурация для Flyway 9
            Flyway flyway = Flyway.configure()
                    .dataSource(URL, USERNAME, PASSWORD)
                    .locations("filesystem:/app/db/migration")
                    .baselineOnMigrate(true)
                    .load();

            flyway.migrate();
            System.out.println("Миграции Flyway успешно применены");

        } catch (Exception e) {
            System.err.println("Ошибка Flyway: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Flyway migration failed", e);
        }


    }

}