package com.example.repository;

import com.example.config.IDatabaseConnection;
import com.example.models.Note;
import com.example.models.NoteLabel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.NotFoundException;
import java.nio.file.AccessDeniedException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NoteDaoHandler implements NoteDao{

    private static final Logger logger = LoggerFactory.getLogger(NoteDaoHandler.class);
    private final IDatabaseConnection databaseConnection;

    private static NoteDaoHandler instance;

    public NoteDaoHandler(IDatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public static NoteDaoHandler getInstance(IDatabaseConnection databaseConnection){
        if(instance == null)
            instance = new NoteDaoHandler(databaseConnection);
        return instance;
    }

    public Note addNote(Note note) throws SQLException {

        try(Connection connect = databaseConnection.getConnection()) {
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

            logger.info("Note inserted with ID {}", note.getId());

        }catch (SQLException e) {
            logger.error("Error adding a note: {}", e.getMessage());
            throw e;
        }

        return note;
    }
    public Note updateNote(int id, Note note) throws SQLException {
        try (Connection connect = databaseConnection.getConnection()) {
            if (connect == null) {
                throw new SQLException("Failed to establish a database connection.");
            }

            PreparedStatement preparedStatement
                    = connect.prepareStatement(
                    "update \"Note\" set title=?,content=?,modified_at=? where id=? and user_id = ?",
                    Statement.RETURN_GENERATED_KEYS);


            preparedStatement.setString(1, note.getTitle());
            preparedStatement.setString(2, note.getContent());
            Date now = new Date(System.currentTimeMillis());
            note.setModified_at(now);
            preparedStatement.setDate(3, now);
            preparedStatement.setInt(4, id);
            preparedStatement.setInt(5, note.getUser_id());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("Note with id = {} not found for update.", id);
                return null;
            }

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                note.setId(generatedKeys.getInt(1));
                note.setUser_id(generatedKeys.getInt(2));
                note.setTitle(generatedKeys.getString(3));
                note.setContent(generatedKeys.getString(4));
                note.setCreated_at(generatedKeys.getDate(5));
                note.setModified_at(generatedKeys.getDate(6));
            } else {
                logger.warn("Failed to retrieve the generated values for the note.");
            }

            logger.info("Note updated with id = {}", id);

        }catch (SQLException e) {
            logger.error("Error updating note with id = {}: {}", id, e.getMessage());
            throw e;
        }

        return note;
    }
    public void deleteNote(int id, int userId) throws SQLException, AccessDeniedException, NotFoundException {
        try (Connection connect = databaseConnection.getConnection()) {
            if (connect == null) {
                throw new SQLException("Failed to establish a database connection.");
            }

            PreparedStatement preparedStatement
                    = connect.prepareStatement(
                    "select * from \"Note\" where id =? and user_id = ?");

            preparedStatement.setInt(1, id);
            preparedStatement.setInt(2, userId);

            ResultSet resultSet
                    = preparedStatement.executeQuery();
            if(!resultSet.next())
                throw new AccessDeniedException("You don't have access.");

            preparedStatement
                    = connect.prepareStatement(
                    "delete from \"Note\" where id =?");

            preparedStatement.setInt(1, id);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("Note with id = {} not found for delete.", id);
                throw new NotFoundException("Note with id = "+ id + " not found for delete.");
            }

            logger.info("Note deleted with id = {}", id);

        }catch (SQLException e) {
            logger.error("Error deleting note with id = {}: {}", id, e.getMessage());
            throw e;
        } catch (AccessDeniedException e) {
            logger.error("Access denied: {}", e.getMessage());
            throw e;
        } catch (NotFoundException e) {
            logger.error("Not found note: {}", e.getMessage());
            throw e;
        }
    }
    public Note getNoteById(int id, int userId) throws SQLException, AccessDeniedException {
        Note note = null;

        try(Connection connect = databaseConnection.getConnection()) {
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
                note = new Note(resultSet.getInt(1),
                        resultSet.getInt(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getDate(5),
                        resultSet.getDate(6)
                );
                if(note.getUser_id() != userId)
                    throw new AccessDeniedException("You don't have access.");
            }
            logger.info("Retrieved note with ID {}: {}", id, note);

        }catch (SQLException e) {
            logger.error("Error retrieving note with id = {}: {}", id, e.getMessage());
            throw e;
        } catch (AccessDeniedException e) {
            logger.error("Access denied: {}", e.getMessage());
            throw e;
        }

        return note;
    }
    public List<Note> getAllNotes(int userId) throws SQLException {

        List<Note> notes = new ArrayList<Note>();

        try(Connection connect = databaseConnection.getConnection()) {
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
                Note note = new Note(resultSet.getInt(1),
                        resultSet.getInt(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getDate(5),
                        resultSet.getDate(6)
                );
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

    public NoteLabel addLabel(NoteLabel noteLabel) throws SQLException {
        try(Connection connect = databaseConnection.getConnection()) {
            if (connect == null) {
                throw new SQLException("Failed to establish a database connection.");
            }
            int noteUserId = getUserIdOfNote(noteLabel.getNote_id(), connect);

            int labelUserId = getUserIdOfLabel(noteLabel.getLabel_id(), connect);

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
            throw e;
        }

        return noteLabel;
    }
    public List<NoteLabel> getLabels(int noteId, int userId) throws SQLException, AccessDeniedException {
        List<NoteLabel> noteLabels = new ArrayList<>();
        try(Connection connect = databaseConnection.getConnection()) {
            if (connect == null) {
                throw new SQLException("Failed to establish a database connection.");
            }

            int noteUserId = getUserIdOfNote(noteId, connect);
            if(noteUserId != userId)
                throw new AccessDeniedException("You don't have access.");

            PreparedStatement preparedStatement = connect.prepareStatement(
                    "SELECT * FROM \"Note_Label\" WHERE note_id = ?");

            preparedStatement.setInt(1, noteId);

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                NoteLabel noteLabel = new NoteLabel(resultSet.getInt("note_id"),
                        resultSet.getInt("label_id")
                );
                noteLabels.add(noteLabel);
            }
            logger.info("Retrieved all labels for note with id = {} successfully", noteId);
        }
        catch (SQLException e) {
            logger.error("Error retrieving labels for this note: {}", e.getMessage());
            throw e;
        } catch (AccessDeniedException e) {
            logger.error("Access denied: {}", e.getMessage());
            throw e;
        }
        return noteLabels;
    }
    public void deleteLabel(int noteId, int labelId, int userId) throws SQLException, AccessDeniedException, NotFoundException {

        try(Connection connect = databaseConnection.getConnection()) {
            if (connect == null) {
                throw new SQLException("Failed to establish a database connection.");
            }
            int noteUserId = getUserIdOfNote(noteId, connect);
            if(noteUserId != userId)
                throw new AccessDeniedException("You don't have access.");

            PreparedStatement preparedStatement = connect.prepareStatement(
                    "DELETE FROM \"Note_Label\" WHERE note_id = ? AND label_id = ?");
            preparedStatement.setInt(1, noteId);
            preparedStatement.setInt(2, labelId);

            int affectedRows = preparedStatement.executeUpdate();

            if(affectedRows == 0){
                logger.warn("Note Label with note's ID {} and label's ID {} not found.", noteId, labelId);
                throw new NotFoundException("Note Label with note's ID = " + noteId + " and label's ID "+ labelId +" not found.");
            }

            logger.info("Note Label with note's ID {} and label's ID {} deleted.", noteId, labelId);

        }catch (SQLException e) {
            logger.error("Error deleting note label: {}", e.getMessage());
            throw e;
        }  catch (AccessDeniedException e) {
            logger.error("Access denied: {}", e.getMessage());
            throw e;
        } catch (NotFoundException e) {
            logger.error("Not found note: {}", e.getMessage());
            throw e;
        }
    }

    private int getUserIdOfNote(int noteId, Connection connection) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT user_id FROM \"Note\" WHERE id = ?");
        preparedStatement.setInt(1, noteId);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            return resultSet.getInt("user_id");
        }

        return -1;
    }

    private int getUserIdOfLabel(int labelId, Connection connection) throws SQLException {
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

