package com.example.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtils {

    private final Connection connection;

    public DatabaseUtils(Connection connection) {
        this.connection = connection;
    }

    public void createNoteTable(){
        String sql = "CREATE TABLE IF NOT EXISTS \"Note\" (\n" +
                "    id SERIAL PRIMARY KEY,\n" +
                "    user_id INT NOT NULL REFERENCES \"User\"(id) ON DELETE CASCADE,\n" +
                "    title VARCHAR(255),\n" +
                "    content TEXT NOT NULL,\n" +
                "    created_at DATE NOT NULL,\n" +
                "    modified_at DATE NOT NULL\n" +
                ");";
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createLabelTable(){
        String sql = "CREATE TABLE IF NOT EXISTS \"Label\" (\n" +
                "    id SERIAL PRIMARY KEY,\n" +
                "    user_id INT NOT NULL REFERENCES \"User\"(id) ON DELETE CASCADE,\n" +
                "    content VARCHAR(255) NOT NULL\n" +
                ");";
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createNoteLabelTable(){
        String sql = "CREATE TABLE IF NOT EXISTS \"Note_Label\" (\n" +
                "    note_id INT NOT NULL REFERENCES \"Note\"(id) ON DELETE CASCADE,\n" +
                "    label_id INT NOT NULL REFERENCES \"Label\"(id) ON DELETE CASCADE,\n" +
                "    PRIMARY KEY (note_id, label_id)\n" +
                ");";
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void createUserTable(){
        String sql = "CREATE TABLE IF NOT EXISTS \"User\" (\n" +
                "    id SERIAL PRIMARY KEY,\n" +
                "    username VARCHAR(255) NOT NULL,\n" +
                "    password VARCHAR(255) NOT NULL\n" +
                ");";
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
