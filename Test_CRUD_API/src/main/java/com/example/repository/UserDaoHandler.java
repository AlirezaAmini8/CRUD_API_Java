package com.example.repository;

import com.example.config.IDatabaseConnection;
import com.example.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.NotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDaoHandler implements UserDao{

    private static final Logger logger = LoggerFactory.getLogger(UserDaoHandler.class);
    private final IDatabaseConnection databaseConnection;

    private static UserDaoHandler instance;
    public UserDaoHandler(IDatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public static synchronized UserDaoHandler getInstance(IDatabaseConnection databaseConnection){
        if(instance == null)
            instance = new UserDaoHandler(databaseConnection);
        return instance;
    }

    public User addUser(User user) throws SQLException {
        try(Connection connect = databaseConnection.getConnection()) {
            if (connect == null) {
                throw new SQLException("Failed to establish a database connection.");
            }
            PreparedStatement preparedStatement
                    = connect.prepareStatement(
                    "insert into \"User\"(username,password) values (?,?)",
                    Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                logger.warn("Creating user failed, no rows affected.");
                throw new SQLException("creating user failed, no rows affected.");
            }

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                user.setId(generatedKeys.getInt(1));
            } else {
                logger.warn("Failed to retrieve the generated ID for the user.");
            }

            logger.info("User inserted with ID {}", user.getId());

        }catch (SQLException e) {
            logger.error("Error adding a user: {}", e.getMessage());
            throw e;
        }

        return user;
    }

    public User updateUser(int id, User user) throws SQLException {
        try(Connection connect = databaseConnection.getConnection()) {
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

            user.setId(id);
            logger.info("User updated with id = {}", id);

        }catch (SQLException e) {
            logger.error("Error updating user with id = {}: {}", id, e.getMessage());
            throw e;
        }

        return user;
    }

    public void deleteUser(int id) throws SQLException {
        try(Connection connect = databaseConnection.getConnection()) {
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
                throw new NotFoundException("User with id = " + id  + " not found for delete.");
            }

            logger.info("User deleted with id = {}", id);

        }catch (SQLException e) {
            logger.error("Error deleting user with id = {}: {}", id, e.getMessage());
            throw e;
        }catch (NotFoundException e){
            logger.error("user with id = {} not found", id);
            throw e;
        }
    }
    public User getUserById(int id) throws SQLException {
        User user = null;

        try(Connection connect = databaseConnection.getConnection()) {
            if (connect == null) {
                throw new SQLException("Failed to establish a database connection.");
            }

            PreparedStatement preparedStatement
                    = connect.prepareStatement(
                    "select * from \"User\" where id=?");

            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                user = new User(resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3)
                );
            }
            logger.info("Retrieved user with ID {}: {}", id, user);

        }catch (SQLException e) {
            logger.error("Error retrieving user with id = {}: {}", id, e.getMessage());
            throw e;
        }

        return user;
    }

    public List<User> getAllUsers() throws SQLException {

        List<User> users = new ArrayList<User>();

        try(Connection connect = databaseConnection.getConnection()) {
            if (connect == null) {
                throw new SQLException("Failed to establish a database connection.");
            }

            PreparedStatement preparedStatement
                    = connect.prepareStatement(
                    "select * from \"User\"");
            ResultSet resultSet
                    = preparedStatement.executeQuery();

            while (resultSet.next()) {
                User user = new User(resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3)
                );
                // store the values into the list
                users.add(user);
            }
            logger.info("Retrieved all users with size {} successfully", users.size());

        }catch (SQLException e) {
            logger.error("Error retrieving all users: {}", e.getMessage());
            throw e;
        }

        return users;
    }
}

