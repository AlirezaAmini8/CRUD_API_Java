package com.example.controller;

import com.example.models.Label;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LabelDaoHandler {
    public Label addLabel(Label label) throws SQLException {
        Connection connect = DatabaseConnection.getConnection();

        PreparedStatement preparedStatement
                = connect.prepareStatement(
                "insert into Label(user_id,content) values (?,?)");

        preparedStatement.setInt(1, label.getUser_id());
        preparedStatement.setString(2, label.getContent());

        int affectedRows = preparedStatement.executeUpdate();
        if (affectedRows == 0) {
            throw new SQLException("creating label failed, no rows affected.");
        }
        System.out.println("label inserted");

        connect.close();

        return label;
    }
    public Label updateLabel(int id, Label label) throws SQLException {
        Connection connect = DatabaseConnection.getConnection();

        PreparedStatement preparedStatement
                = connect.prepareStatement(
                "update Label set content=? where id=?");

        preparedStatement.setString(1, label.getContent());
        preparedStatement.setInt(2, id);

        int affectedRows = preparedStatement.executeUpdate();
        if (affectedRows == 0) {
            throw new SQLException("updating label failed, no rows affected.");
        }

        System.out.println("label updated");

        connect.close();

        return label;
    }
    public void deleteLabel(int id) throws SQLException
    {
        Connection connect = DatabaseConnection.getConnection();

        PreparedStatement preparedStatement
                = connect.prepareStatement(
                "delete from Label where id =?");

        preparedStatement.setInt(1, id);

        int affectedRows = preparedStatement.executeUpdate();
        if (affectedRows == 0) {
            throw new SQLException("Deleting label failed, no rows affected.");
        }

        System.out.printf("label with %s deleted \n",id);

        connect.close();
    }
    public Label getLabelById(int id) throws SQLException {
        Label label = new Label();

        Connection connect = DatabaseConnection.getConnection();

        PreparedStatement preparedStatement
                = connect.prepareStatement(
                "select * from Label where id=?");

        preparedStatement.setInt(1, id);

        ResultSet resultSet
                = preparedStatement.executeQuery();

        if (resultSet.next()) {
            label.setId(resultSet.getInt(1));
            label.setUser_id(resultSet.getInt(2));
            label.setContent(resultSet.getString(3));
        }
        connect.close();

        return label;
    }
    public List<Label> getAllLabels() throws SQLException {

        List<Label> labels = new ArrayList<Label>();

        Connection connect = DatabaseConnection.getConnection();

        PreparedStatement preparedStatement
                = connect.prepareStatement(
                "select * from Label");
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

        connect.close();

        return labels;
    }
}
