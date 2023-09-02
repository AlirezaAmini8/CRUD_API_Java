package com.example.repository;

import com.example.config.IDatabaseConnection;
import com.example.models.Label;
import com.example.models.NoteLabel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.NotFoundException;
import java.nio.file.AccessDeniedException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LabelDaoHandler implements LabelDao{

    private static final Logger logger = LoggerFactory.getLogger(LabelDaoHandler.class);
    private final IDatabaseConnection databaseConnection;

    private static LabelDaoHandler instance;

    public LabelDaoHandler(IDatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public static synchronized LabelDaoHandler getInstance(IDatabaseConnection databaseConnection){
        if(instance == null)
            instance = new LabelDaoHandler(databaseConnection);
        return instance;
    }
    public Label addLabel(Label label) throws SQLException {
        try (Connection connect = databaseConnection.getConnection()){
            if (connect == null) {
                throw new SQLException("Failed to establish a database connection.");
            }
            PreparedStatement preparedStatement
                    = connect.prepareStatement(
                    "insert into \"Label\"(user_id,content) values (?,?)",
                    Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setInt(1, label.getUser_id());
            preparedStatement.setString(2, label.getContent());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("Creating label failed, no rows affected.");
                throw new SQLException("creating label failed, no rows affected.");
            }

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                label.setId(generatedKeys.getInt(1));
            } else {
                logger.warn("Failed to retrieve the generated ID for the label.");
            }

            logger.info("Label inserted: {}", label.getId());

        }catch (SQLException e) {
            logger.error("Error adding a label: {}", e.getMessage());
            throw e;

        }
        return label;
    }
    public Label updateLabel(int id, Label label) throws SQLException {
        try(Connection connect = databaseConnection.getConnection()) {
            if (connect == null) {
                throw new SQLException("Failed to establish a database connection.");
            }
            PreparedStatement preparedStatement
                    = connect.prepareStatement(
                    "update \"Label\" set content=? where id=? and user_id = ?",
                    Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, label.getContent());
            preparedStatement.setInt(2, id);
            preparedStatement.setInt(3, label.getUser_id());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("Label with id = {} not found for update or you don't have access", id);
                return null;
            }

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                label.setId(generatedKeys.getInt(1));
                label.setUser_id(generatedKeys.getInt(2));
                label.setContent(generatedKeys.getString(3));
            } else {
                logger.warn("Failed to retrieve the generated values for the label.");
            }

            logger.info("Label updated with id = {}", id);


        }catch (SQLException e) {
            logger.error("Error updating label with id = {}: {}", id, e.getMessage());
            throw e;
        }
        return label;
    }
    public void deleteLabel(int id, int userId) throws SQLException, AccessDeniedException, NotFoundException {
        try (Connection connect = databaseConnection.getConnection()) {
            if (connect == null) {
                throw new SQLException("Failed to establish a database connection.");
            }

            PreparedStatement preparedStatement
                    = connect.prepareStatement(
                            "select * from \"Label\" where id = ? and user_id = ?");

            preparedStatement.setInt(1, id);
            preparedStatement.setInt(2, userId);

            ResultSet resultSet
                    = preparedStatement.executeQuery();
            if(!resultSet.next())
                throw new AccessDeniedException("You don't have acess.");


            preparedStatement
                    = connect.prepareStatement(
                    "delete from \"Label\" where id =?");

            preparedStatement.setInt(1, id);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("Label with id = {} not found for delete.", id);
                throw new NotFoundException("Label with id = " + id + " not found for delete.");
            }

            logger.info("Label deleted with id = {}", id);

        }catch (SQLException e) {
            logger.error("Error deleting note with id = {}: {}", id, e.getMessage());
            throw e;
        } catch (AccessDeniedException e) {
            logger.error("Access denied: {}", e.getMessage());
            throw e;
        } catch (NotFoundException e) {
            logger.error("Not Found Label: {}", e.getMessage());
            throw e;
        }
    }
    public Label getLabelById(int id, int userId) throws SQLException, AccessDeniedException {
        Label label = null;
        try (Connection connect = databaseConnection.getConnection()) {
            if (connect == null) {
                throw new SQLException("Failed to establish a database connection.");
            }
            PreparedStatement preparedStatement
                    = connect.prepareStatement(
                    "select * from \"Label\" where id = ?");

            preparedStatement.setInt(1, id);

            ResultSet resultSet
                    = preparedStatement.executeQuery();

            if (resultSet.next()) {
                label = new Label(resultSet.getInt(1),
                        resultSet.getInt(2),
                        resultSet.getString(3));
                if(label.getUser_id() != userId)
                    throw new AccessDeniedException("You don't have access.");
            }

            logger.info("Retrieved label with ID {}: {}", id, label);

        }catch (SQLException e) {
            logger.error("Error retrieving label with id = {}: {}", id, e.getMessage());
            throw e;
        } catch (AccessDeniedException e) {
            logger.error("Access denied: {}", e.getMessage());
            throw e;
        }
        return label;
    }
    public List<Label> getAllLabels(int userId) throws SQLException {

        List<Label> labels = new ArrayList<Label>();
        try(Connection connect = databaseConnection.getConnection()) {
            if (connect == null) {
                throw new SQLException("Failed to establish a database connection.");
            }

            PreparedStatement preparedStatement
                    = connect.prepareStatement(
                    "select * from \"Label\" where user_id = ?");

            preparedStatement.setInt(1, userId);

            ResultSet resultSet
                    = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Label label = new Label(resultSet.getInt(1),
                        resultSet.getInt(2),
                        resultSet.getString(3)
                );
                // store the values into the list
                labels.add(label);
            }
            logger.info("Retrieved all labels with size {} successfully", labels.size());
        }catch (SQLException e) {
            logger.error("Error retrieving all labels: {}", e.getMessage());
            throw e;
        }

        return labels;
    }

    public List<NoteLabel> getNotes(int labelId, int userId) throws SQLException, AccessDeniedException {
        List<NoteLabel> noteLabels = new ArrayList<>();

        try(Connection connect = databaseConnection.getConnection()) {
            if (connect == null) {
                throw new SQLException("Failed to establish a database connection.");
            }

            PreparedStatement preparedStatement = connect.prepareStatement(
                    "SELECT user_id FROM \"Label\" WHERE id = ?");
            preparedStatement.setInt(1, labelId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                if(resultSet.getInt("user_id") != userId)
                    throw new AccessDeniedException("You don't have access.");
            }

            preparedStatement = connect.prepareStatement(
                    "SELECT * FROM \"Note_Label\" WHERE label_id = ?");

            preparedStatement.setInt(1, labelId);

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                NoteLabel noteLabel = new NoteLabel(resultSet.getInt("note_id"),
                        resultSet.getInt("label_id")
                );
                noteLabels.add(noteLabel);
            }

            logger.info("Retrieved all notes for label with id = {} successfully", labelId);

        }
        catch (SQLException e) {
            logger.error("Error retrieving notes for this label: {}", e.getMessage());
            throw e;
        } catch (AccessDeniedException e) {
            logger.error("Access denied: {}", e.getMessage());
            throw e;
        }
        return noteLabels;
    }
}

