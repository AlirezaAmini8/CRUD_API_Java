package com.example.controller;

import com.example.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDaoHandler {
    public User addUser(User user) throws SQLException {
        Connection connect = DatabaseConnection.getConnection();

        PreparedStatement preparedStatement
                = connect.prepareStatement(
                "insert into User(username,password) values (?,?)");

        preparedStatement.setString(1, user.getUsername());
        preparedStatement.setString(2, user.getPassword());

        int affectedRows = preparedStatement.executeUpdate();
        if (affectedRows == 0) {
            throw new SQLException("creating user failed, no rows affected.");
        }

        System.out.println("user inserted");

        connect.close();

        return user;
    }
    public User updateUser(int id, User user) throws SQLException {
        Connection connect = DatabaseConnection.getConnection();

        PreparedStatement preparedStatement
                = connect.prepareStatement(
                "update User set username=?,password=? where id=?");

        preparedStatement.setString(1, user.getUsername());
        preparedStatement.setString(2, user.getPassword());
        preparedStatement.setInt(311, id);

        int affectedRows = preparedStatement.executeUpdate();
        if (affectedRows == 0) {
            throw new SQLException("updating user failed, no rows affected.");
        }

        System.out.println("user updated");

        connect.close();

        return user;
    }
    public void deleteUser(int id) throws SQLException
    {
        Connection connect = DatabaseConnection.getConnection();

        PreparedStatement preparedStatement
                = connect.prepareStatement(
                "delete from User where id =?");

        preparedStatement.setInt(1, id);

        int affectedRows = preparedStatement.executeUpdate();
        if (affectedRows == 0) {
            throw new SQLException("Deleting user failed, no rows affected.");
        }

        System.out.printf("user with %s deleted \n",id);

        connect.close();
    }
    public User getUserById(int id) throws SQLException {
        User user = new User();

        Connection connect = DatabaseConnection.getConnection();

        PreparedStatement preparedStatement
                = connect.prepareStatement(
                "select * from User where id=?");

        preparedStatement.setInt(1, id);

        ResultSet resultSet
                = preparedStatement.executeQuery();

        if (resultSet.next()) {
            user.setId(resultSet.getInt(1));
            user.setUsername(resultSet.getString(2));
            user.setPassword(resultSet.getString(3));
        }
        connect.close();

        return user;
    }
    public List<User> getAllUsers() throws SQLException {

        List<User> users = new ArrayList<User>();

        Connection connect = DatabaseConnection.getConnection();

        PreparedStatement preparedStatement
                = connect.prepareStatement(
                "select * from User");
        ResultSet resultSet
                = preparedStatement.executeQuery();

        while (resultSet.next()) {
            User user = new User();
            user.setId(resultSet.getInt(1));
            user.setUsername(resultSet.getString(2));
            user.setPassword(resultSet.getString(3));
            // store the values into the list
            users.add(user);
        }

        connect.close();

        return users;
    }
}
