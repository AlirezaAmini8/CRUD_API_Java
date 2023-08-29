package com.example.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection implements IDatabaseConnection{
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);

    private final DatabaseConfig config;

    public DatabaseConnection(DatabaseConfig config){
        this.config = config;
    }

    public Connection getConnection() throws SQLException {

        try {
            Connection connection = DriverManager.getConnection(config.getDB_URL(), config.getDB_USER(), config.getDB_PASSWORD());
            if (connection != null) {
                logger.info("Database connection established successfully.");
            } else {
                logger.error("Failed to establish a database connection.");
                throw new SQLException("Failed to establish a database connection.");
            }
            return connection;

        } catch (SQLException e) {
            logger.error("Failed to establish a database connection: {}", e.getMessage());
            throw e;
        }

    }

}