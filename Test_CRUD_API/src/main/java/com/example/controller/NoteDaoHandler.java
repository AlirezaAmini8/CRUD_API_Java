package com.example.controller;

import com.example.models.Note;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NoteDaoHandler {

    private static final Logger logger = LoggerFactory.getLogger(NoteDaoHandler.class);
    public Note addNote(Note note) throws SQLException {

        try(Connection connect = DatabaseConnection.getConnection()) {
            if (connect == null) {
                throw new SQLException("Failed to establish a database connection.");
            }

            PreparedStatement preparedStatement
                    = connect.prepareStatement(
                    "insert into \"Note\"(user_id,title,content,created_at,modified_at) values (?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setInt(1, note.getUser_id());
            preparedStatement.setString(2, note.getTitle());
            preparedStatement.setString(3, note.getContent());
            Date now = new Date(System.currentTimeMillis());
            note.setCreated_at(now);
            note.setModified_at(now);
            preparedStatement.setDate(4, now);
            preparedStatement.setDate(5, now);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("Creating note failed, no rows affected.");
                throw new SQLException("Creating note failed, no rows affected.");
            }

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                note.setId(generatedKeys.getInt(1));
            } else {
                logger.warn("Failed to retrieve the generated ID for the note.");
            }

            logger.info("Note inserted with ID {}", note.getTitle());

        }catch (SQLException e) {
            logger.error("Error adding a note: {}", e.getMessage());
            throw e;
        }

        return note;
    }
    public Note updateNote(int id, Note note) throws SQLException {
        try (Connection connect = DatabaseConnection.getConnection()) {
            if (connect == null) {
                throw new SQLException("Failed to establish a database connection.");
            }

            PreparedStatement preparedStatement
                    = connect.prepareStatement(
                    "update \"Note\" set title=?,content=?,modified_at=? where id=?");

            preparedStatement.setString(1, note.getTitle());
            preparedStatement.setString(2, note.getContent());
            Date now = new Date(System.currentTimeMillis());
            note.setModified_at(now);
            preparedStatement.setDate(3, now);
            preparedStatement.setInt(4, id);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("Note with id = {} not found for update.", id);
                return null;
            }
            note.setId(id);
            logger.info("Note updated with id = {}", id);

        }catch (SQLException e) {
            logger.error("Error updating note with id = {}: {}", id, e.getMessage());
            throw e;
        }

        return note;
    }
    public Note deleteNote(int id) throws SQLException {
        Note note = new Note();
        try (Connection connect = DatabaseConnection.getConnection()) {
            if (connect == null) {
                throw new SQLException("Failed to establish a database connection.");
            }

            PreparedStatement preparedStatement
                    = connect.prepareStatement(
                    "delete from \"Note\" where id =?");

            preparedStatement.setInt(1, id);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("Note with id = {} not found for delete.", id);
                return null;
            }

            logger.info("Note deleted with id = {}", id);

        }catch (SQLException e) {
            logger.error("Error deleting note with id = {}: {}", id, e.getMessage());
            throw e;
        }
        return note;
    }
    public  Note getNoteById(int id) throws SQLException {
        Note note = null;

        try(Connection connect = DatabaseConnection.getConnection()) {
            if (connect == null) {
                throw new SQLException("Failed to establish a database connection.");
            }

            PreparedStatement preparedStatement
                    = connect.prepareStatement(
                    "select * from \"Note\" where id = ? ");

            preparedStatement.setInt(1, id);

            ResultSet resultSet
                    = preparedStatement.executeQuery();

            if (resultSet.next()) {
                note = new Note();
                note.setId(resultSet.getInt(1));
                note.setUser_id(resultSet.getInt(2));
                note.setTitle(resultSet.getString(3));
                note.setContent(resultSet.getString(4));
                note.setCreated_at(resultSet.getDate(5));
                note.setModified_at(resultSet.getDate(6));
            }
            logger.info("Retrieved note with ID {}: {}", id, note);

        }catch (SQLException e) {
            logger.error("Error retrieving note with id = {}: {}", id, e.getMessage());
            throw e;
        }

        return note;
    }
    public List<Note> getAllNotes(int userId) throws SQLException {

        List<Note> notes = new ArrayList<Note>();

        try(Connection connect = DatabaseConnection.getConnection()) {
            if (connect == null) {
                throw new SQLException("Failed to establish a database connection.");
            }

            PreparedStatement preparedStatement
                    = connect.prepareStatement(
                    "select * from \"Note\" where user_id = ?");

            preparedStatement.setInt(1, userId);

            ResultSet resultSet
                    = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Note note = new Note();
                note.setId(resultSet.getInt(1));
                note.setUser_id(resultSet.getInt(2));
                note.setTitle(resultSet.getString(3));
                note.setContent(resultSet.getString(4));
                note.setCreated_at(resultSet.getDate(5));
                note.setModified_at(resultSet.getDate(6));
                // store the values into the list
                notes.add(note);
            }
            logger.info("Retrieved all notes with size {} successfully", notes.size());
        }catch (SQLException e) {
            logger.error("Error retrieving all notes: {}", e.getMessage());
            throw e;
        }

        return notes;
    }
}

