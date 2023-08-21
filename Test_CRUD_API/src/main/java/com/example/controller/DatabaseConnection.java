package com.example.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "4271";

    public static Connection getConnection() throws SQLException {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            if (connection != null) {
                logger.info("Database connection established successfully.");
            } else {
                logger.error("Failed to establish a database connection.");
                throw new SQLException("Failed to establish a database connection.");
            }
        } catch (SQLException e) {
            logger.error("Failed to establish a database connection: {}", e.getMessage());
            throw e;
        }
        return connection;
    }

}