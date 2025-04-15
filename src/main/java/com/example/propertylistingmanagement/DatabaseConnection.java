package com.example.propertylistingmanagement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/property_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "Qaz122334azimbai";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Создаём таблицу пользователей
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
                    "id SERIAL PRIMARY KEY, " +
                    "username VARCHAR(50) UNIQUE NOT NULL, " +
                    "password VARCHAR(100) NOT NULL, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "surname VARCHAR(100) NOT NULL, " +
                    "role VARCHAR(10) NOT NULL CHECK (role IN ('buyer', 'seller')))");

            // Создаём таблицу свойств
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS properties (" +
                    "id SERIAL PRIMARY KEY, " +
                    "type VARCHAR(50) NOT NULL, " +
                    "square_feet INT NOT NULL, " +
                    "price DECIMAL(10,2) NOT NULL, " +
                    "address TEXT NOT NULL, " +
                    "status VARCHAR(10) NOT NULL CHECK (status IN ('For Sale', 'For Rent')), " +
                    "seller_id INT NOT NULL REFERENCES users(id))");

            // Добавляем тестового продавца
            stmt.executeUpdate(
                    "INSERT INTO users (username, password, name, surname, role) " +
                            "SELECT 'admin', 'qwerty12', 'Vito', 'Corleone', 'seller' " +
                            "WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin')"
            );

            System.out.println("Database initialized successfully with test seller and buyer");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}