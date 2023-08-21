package com.example.controller;

import com.example.models.NoteLabel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NoteLabelDaoHandler {

    private static final Logger logger = LoggerFactory.getLogger(NoteLabelDaoHandler.class);
    public NoteLabel addNoteLabel(NoteLabel noteLabel) {
        try(Connection connect = DatabaseConnection.getConnection()) {
            if (connect == null) {
                throw new SQLException("Failed to establish a database connection.");
            }
            int noteUserId = getNoteUserId(noteLabel.getNote_id(), connect);

            int labelUserId = getLabelUserId(noteLabel.getLabel_id(), connect);

            if (noteUserId == labelUserId) {
                PreparedStatement preparedStatement
                        = connect.prepareStatement(
                        "INSERT INTO \"Note_Label\" (note_id, label_id) VALUES (?, ?)");

                preparedStatement.setInt(1, noteLabel.getNote_id());
                preparedStatement.setInt(2, noteLabel.getLabel_id());

                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows == 0) {
                    logger.warn("creating note_label failed, no rows affected.");
                    throw new SQLException("creating note_label failed, no rows affected.");
                }

                logger.info("note label with note's ID = {} and label's ID = {} inserted.", noteLabel.getNote_id() , noteLabel.getLabel_id());
            }
            else {
                logger.warn("Label doesn't belong to the user of the note.");
                return null;
            }
        }catch (SQLException e) {
            logger.error("Error adding a note label: {}", e.getMessage());
            e.printStackTrace();
        }

        return noteLabel;
    }
    public List<NoteLabel> getNoteLabelsForNote(int noteId) {
        List<NoteLabel> noteLabels = new ArrayList<>();
        try(Connection connect = DatabaseConnection.getConnection()) {
            if (connect == null) {
                throw new SQLException("Failed to establish a database connection.");
            }

            PreparedStatement preparedStatement = connect.prepareStatement(
                    "SELECT * FROM \"Note_Label\" WHERE note_id = ?");

            preparedStatement.setInt(1, noteId);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                NoteLabel noteLabel = new NoteLabel();
                noteLabel.setNote_id(resultSet.getInt("note_id"));
                noteLabel.setLabel_id(resultSet.getInt("label_id"));
                noteLabels.add(noteLabel);
            }
            logger.info("Retrieved all labels for note with id = {} successfully", noteId);
        }
        catch (SQLException e) {
            logger.error("Error retrieving labels for this note: {}", e.getMessage());
            e.printStackTrace();
        }
        return noteLabels;
    }

    public List<NoteLabel> getNoteLabelsForLabel(int labelId) {
        List<NoteLabel> noteLabels = new ArrayList<>();

        try(Connection connect = DatabaseConnection.getConnection()) {
            if (connect == null) {
                throw new SQLException("Failed to establish a database connection.");
            }
            PreparedStatement preparedStatement = connect.prepareStatement(
                    "SELECT * FROM \"Note_Label\" WHERE label_id = ?");

            preparedStatement.setInt(1, labelId);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                NoteLabel noteLabel = new NoteLabel();
                noteLabel.setNote_id(resultSet.getInt("note_id"));
                noteLabel.setLabel_id(resultSet.getInt("label_id"));
                noteLabels.add(noteLabel);
            }

            logger.info("Retrieved all notes for label with id = {} successfully", labelId);

        }
        catch (SQLException e) {
            logger.error("Error retrieving notes for this label: {}", e.getMessage());
            e.printStackTrace();
        }
        return noteLabels;
    }

    public NoteLabel deleteNoteLabel(int noteId, int labelId) {
        NoteLabel noteLabel = new NoteLabel();

        try(Connection connect = DatabaseConnection.getConnection()) {
            if (connect == null) {
                throw new SQLException("Failed to establish a database connection.");
            }

            PreparedStatement preparedStatement = connect.prepareStatement(
                    "DELETE FROM \"Note_Label\" WHERE note_id = ? AND label_id = ?");
            preparedStatement.setInt(1, noteId);
            preparedStatement.setInt(2, labelId);

            int affectedRows = preparedStatement.executeUpdate();

            if(affectedRows == 0){
                logger.warn("Note Label with note's ID {} and label's ID {} not found.", noteId, labelId);
                return null;
            }

            logger.info("Note Label with note's ID {} and label's ID {} deleted.", noteId, labelId);

        }catch (SQLException e) {
            logger.error("Error deleting note label: {}", e.getMessage());
            e.printStackTrace();
        }
        return noteLabel;
    }

    private int getNoteUserId(int noteId, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT user_id FROM \"Note\" WHERE id = ?");
        preparedStatement.setInt(1, noteId);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            return resultSet.getInt("user_id");
        }

        return -1;
    }

    private int getLabelUserId(int labelId, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT user_id FROM \"Label\" WHERE id = ?");
        preparedStatement.setInt(1, labelId);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            return resultSet.getInt("user_id");
        }

        return -1;
    }
}
