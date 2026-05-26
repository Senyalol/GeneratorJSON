package DataBase;

import lombok.Data;
import org.flywaydb.core.Flyway;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Data
public class FlywayApply {

    private static final Logger LOGGER = LogManager.getLogger(FlywayApply.class);

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
            LOGGER.info("========================================");
            LOGGER.info("Применение миграций Flyway v9");
            LOGGER.info("URL: " + URL);
            LOGGER.info("========================================");

            // Конфигурация для Flyway 9
            Flyway flyway = Flyway.configure()
                    .dataSource(URL, USERNAME, PASSWORD)
                    .locations("filesystem:/app/db/migration")
                    .baselineOnMigrate(true)
                    .load();

            flyway.migrate();
            LOGGER.info("Миграции Flyway успешно применены");

        } catch (Exception e) {
            LOGGER.error("Ошибка Flyway: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Flyway migration failed", e);
        }


    }

}