package DataBase;

import data.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PostgreSQLUtils {

    private static final Logger LOGGER = LogManager.getLogger(PostgreSQLUtils.class);

    //Создать базу данных
    public static void createDatabase(String url, String dbName,
                                      String adminUser, String adminPassword) {

        try (Connection conn = DriverManager.getConnection(url, adminUser, adminPassword);
             Statement stmt = conn.createStatement()) {

            String quotedDbName = quoteIdentifier(dbName);

            if (!databaseExists(conn, dbName)) {
                String createDbSql = "CREATE DATABASE " + quotedDbName;
                stmt.executeUpdate(createDbSql);
                LOGGER.info("База данных '{}' создана", dbName);
            } else {
                LOGGER.info("База данных '{}' уже существует", dbName);
            }

        } catch (SQLException e) {
            LOGGER.error("Ошибка при создании БД '{}': {}", dbName, e.getMessage());
            throw new RuntimeException("Failed to create database", e);
        }
    }

    private static String quoteIdentifier(String identifier) {
        return "\"" + identifier.replace("\"", "\"\"") + "\"";
    }

    private static boolean databaseExists(Connection conn, String dbName) throws SQLException {
        String sql = "SELECT 1 FROM pg_database WHERE datname = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dbName);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    //Вернуть случайного пользователя
    public static User getRandomUser(String url, String dbUsername, String dbPassword, String dbName){

        String sqlQuery = "SELECT * FROM users ORDER BY RANDOM() LIMIT 1";;

        try(Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(sqlQuery)){

                if (resultSet.next()) {
                    User randomUser = new User();
                    randomUser.setUser_id(resultSet.getInt("user_id"));
                    randomUser.setFirstname(resultSet.getString("firstname"));
                    randomUser.setLastname(resultSet.getString("lastname"));
                    return randomUser;

            }
            throw new RuntimeException("No users found in database");
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    //Получить всех пользователей
    public static List<User> getAllUsersFromDB(String url, String dbUsername, String dbPassword, String dbName) {
        List<User> users = new ArrayList<>();
        String sqlQuery = "SELECT * FROM users ORDER BY user_id";

        try(Connection conn = DriverManager.getConnection(url,dbUsername,dbPassword)){

            try(Statement stmt = conn.createStatement()){
                ResultSet resultSet  = stmt.executeQuery(sqlQuery);

                while (resultSet.next()) {
                    users.add(new User(resultSet.getInt("user_id"),
                                        resultSet.getString("firstname"),
                                        resultSet.getString("lastname")));
                }

            }

            return users;
        }
        catch (SQLException e) {
            LOGGER.error("Ошибка при создании БД '{}': {}", dbName, e.getMessage());
            throw new RuntimeException("Failed to create database", e);
        }


    }

}