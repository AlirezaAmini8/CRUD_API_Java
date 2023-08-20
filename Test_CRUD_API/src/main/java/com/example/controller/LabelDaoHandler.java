package com.example.controller;

import com.example.models.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LabelDaoHandler {

    private static final Logger logger = LoggerFactory.getLogger(LabelDaoHandler.class);
    public Label addLabel(Label label) {
        try (Connection connect = DatabaseConnection.getConnection()){
            PreparedStatement preparedStatement
                    = connect.prepareStatement(
                    "insert into \"Label\"(user_id,content) values (?,?)");

            preparedStatement.setInt(1, label.getUser_id());
            preparedStatement.setString(2, label.getContent());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("Creating label failed, no rows affected.");
                throw new SQLException("creating label failed, no rows affected.");
            }

            logger.info("Label inserted: {}", label.getContent());

        }catch (SQLException e) {
            logger.error("Error adding a label: {}", e.getMessage());
            e.printStackTrace();
        }
        return label;
    }
    public Label updateLabel(int id, Label label) {
        try(Connection connect = DatabaseConnection.getConnection()) {

            PreparedStatement preparedStatement
                    = connect.prepareStatement(
                    "update \"Label\" set content=? where id=?");

            preparedStatement.setString(1, label.getContent());
            preparedStatement.setInt(2, id);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("Label with id = {} not found for update.", id);
                return null;
            }

            logger.info("Label updated with id = {}", id);


        }catch (SQLException e) {
            logger.error("Error updating label with id = {}: {}", id, e.getMessage());
            e.printStackTrace();
        }
        return label;
    }
    public Label deleteLabel(int id) {
        Label label = new Label();
        try (Connection connect = DatabaseConnection.getConnection()) {
            PreparedStatement preparedStatement
                    = connect.prepareStatement(
                    "delete from \"Label\" where id =?");

            preparedStatement.setInt(1, id);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("Label with id = {} not found for delete.", id);
                return null;
            }

            logger.info("Label deleted with id = {}", id);

        }catch (SQLException e) {
            logger.error("Error deleting note with id = {}: {}", id, e.getMessage());
            e.printStackTrace();
        }
        return label;
    }
    public Label getLabelById(int id) {
        Label label = null;
        try (Connection connect = DatabaseConnection.getConnection()) {
            PreparedStatement preparedStatement
                    = connect.prepareStatement(
                    "select * from \"Label\" where id = ?");

            preparedStatement.setInt(1, id);

            ResultSet resultSet
                    = preparedStatement.executeQuery();

            if (resultSet.next()) {
                label = new Label();
                label.setId(resultSet.getInt(1));
                label.setUser_id(resultSet.getInt(2));
                label.setContent(resultSet.getString(3));
            }

            logger.info("Retrieved label with ID {}: {}", id, label);

        }catch (SQLException e) {
            logger.error("Error retrieving label with id = {}: {}", id, e.getMessage());
            e.printStackTrace();
        }
        return label;
    }
    public List<Label> getAllLabels(int userId) {

        List<Label> labels = new ArrayList<Label>();
        try(Connection connect = DatabaseConnection.getConnection()) {

            PreparedStatement preparedStatement
                    = connect.prepareStatement(
                    "select * from \"Label\" where user_id = ?");

            preparedStatement.setInt(1, userId);

            ResultSet resultSet
                    = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Label label = new Label();
                label.setId(resultSet.getInt(1));
                label.setUser_id(resultSet.getInt(2));
                label.setContent(resultSet.getString(3));
                // store the values into the list
                labels.add(label);
            }
            logger.info("Retrieved all labels with size {} successfully", labels.size());
        }catch (SQLException e) {
            logger.error("Error retrieving all labels: {}", e.getMessage());
            e.printStackTrace();
        }

        return labels;
    }
}

