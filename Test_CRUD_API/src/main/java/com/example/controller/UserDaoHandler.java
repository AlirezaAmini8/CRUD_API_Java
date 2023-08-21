package com.example.controller;

import com.example.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDaoHandler {

    private static final Logger logger = LoggerFactory.getLogger(UserDaoHandler.class);

    public User addUser(User user) {

        try(Connection connect = DatabaseConnection.getConnection()) {
            if (connect == null) {
                throw new SQLException("Failed to establish a database connection.");
            }
            PreparedStatement preparedStatement
                    = connect.prepareStatement(
                    "insert into \"User\"(username,password) values (?,?)");

            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("Creating user failed, no rows affected.");
                throw new SQLException("creating user failed, no rows affected.");
            }

            logger.info("User inserted: {}", user.getUsername());
        }catch (SQLException e) {
            logger.error("Error adding a user: {}", e.getMessage());
            e.printStackTrace();
        }

        return user;
    }
    public User updateUser(int id, User user) {
        try(Connection connect = DatabaseConnection.getConnection()) {
            if (connect == null) {
                throw new SQLException("Failed to establish a database connection.");
            }

            PreparedStatement preparedStatement
                    = connect.prepareStatement(
                    "update \"User\" set username=?,password=? where id=?");

            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setInt(3, id);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("User with id = {} not found for update.", id);
                return null;
            }

            logger.info("User updated with id = {}", id);

        }catch (SQLException e) {
            logger.error("Error updating user with id = {}: {}", id, e.getMessage());
            e.printStackTrace();
        }

        return user;
    }
    public User deleteUser(int id) {
        User user = new User();
        try(Connection connect = DatabaseConnection.getConnection()) {
            if (connect == null) {
                throw new SQLException("Failed to establish a database connection.");
            }

            PreparedStatement preparedStatement
                    = connect.prepareStatement(
                    "delete from \"User\" where id = ?");

            preparedStatement.setInt(1, id);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("User with id = {} not found for delete.", id);
                return null;
            }
            user.setId(0);
            user.setUsername(null);
            user.setPassword(null);
            logger.info("User deleted with id = {}", id);

        }catch (SQLException e) {
            logger.error("Error deleting user with id = {}: {}", id, e.getMessage());
            e.printStackTrace();
        }
        return user;
    }
    public User getUserById(int id) {
        User user = null;

        try(Connection connect = DatabaseConnection.getConnection()) {
            if (connect == null) {
                throw new SQLException("Failed to establish a database connection.");
            }

            PreparedStatement preparedStatement
                    = connect.prepareStatement(
                    "select * from \"User\" where id=?");

            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                user = new User();
                user.setId(resultSet.getInt(1));
                user.setUsername(resultSet.getString(2));
                user.setPassword(resultSet.getString(3));
            }
            logger.info("Retrieved user with ID {}: {}", id, user);

        }catch (SQLException e) {
            logger.error("Error retrieving user with id = {}: {}", id, e.getMessage());
            e.printStackTrace();
        }

        return user;
    }
    public List<User> getAllUsers() {

        List<User> users = new ArrayList<User>();

        try(Connection connect = DatabaseConnection.getConnection()) {
            if (connect == null) {
                throw new SQLException("Failed to establish a database connection.");
            }

            PreparedStatement preparedStatement
                    = connect.prepareStatement(
                    "select * from \"User\"");
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
            logger.info("Retrieved all users with size {} successfully", users.size());

        }catch (SQLException e) {
            logger.error("Error retrieving all users: {}", e.getMessage());
            e.printStackTrace();
        }

        return users;
    }
}

