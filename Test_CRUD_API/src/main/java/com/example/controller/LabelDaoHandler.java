package com.example.controller;

import com.example.models.Label;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LabelDaoHandler {
    public Label addLabel(Label label) {
        try (Connection connect = DatabaseConnection.getConnection()){
            PreparedStatement preparedStatement
                    = connect.prepareStatement(
                    "insert into \"Label\"(user_id,content) values (?,?)");

            preparedStatement.setInt(1, label.getUser_id());
            preparedStatement.setString(2, label.getContent());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("creating label failed, no rows affected.");
            }
            System.out.println("label inserted");
        }catch (SQLException e) {
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
                return null;
            }

            System.out.println("label updated");

            // todo: you may need to return updated label not previous label -> you need query

        }catch (SQLException e) {
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

            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                return null;
            }
            label.setId(resultSet.getInt(1));
            label.setUser_id(resultSet.getInt(2));
            label.setContent(resultSet.getString(3));
            System.out.printf("label with id = %s deleted \n", id);

        }catch (SQLException e) {
            e.printStackTrace();
        }
        return label;
    }
    public Label getLabelById(int id, int userId) {
        Label label = null;
        try (Connection connect = DatabaseConnection.getConnection()) {
            PreparedStatement preparedStatement
                    = connect.prepareStatement(
                    "select * from \"Label\" where id = ? and user_id = ?");

            preparedStatement.setInt(1, id);
            preparedStatement.setInt(2, userId);

            ResultSet resultSet
                    = preparedStatement.executeQuery();

            if (resultSet.next()) {
                label = new Label();
                label.setId(resultSet.getInt(1));
                label.setUser_id(resultSet.getInt(2));
                label.setContent(resultSet.getString(3));
            }
        }catch (SQLException e) {
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
        }catch (SQLException e) {
            e.printStackTrace();
        }

        return labels;
    }
}

