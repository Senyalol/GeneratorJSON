package ORM;

import Data.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgreSQLUtils {

    public static void createDatabase(String url, String dbName,
                                      String adminUser, String adminPassword) {

        try (Connection conn = DriverManager.getConnection(url, adminUser, adminPassword);
             Statement stmt = conn.createStatement()) {

            String quotedDbName = quoteIdentifier(dbName);

            if (!databaseExists(conn, dbName)) {
                String createDbSql = "CREATE DATABASE " + quotedDbName;
                stmt.executeUpdate(createDbSql);
                System.out.printf("База данных '%s' создана%n", dbName);
            } else {
                System.out.printf("База данных '%s' уже существует%n", dbName);
            }

        } catch (SQLException e) {
            System.err.printf("Ошибка при создании БД '%s': %s%n", dbName, e.getMessage());
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
            System.err.printf("Ошибка при создании БД '%s': %s%n", dbName, e.getMessage());
            throw new RuntimeException("Failed to create database", e);
        }


    }

}