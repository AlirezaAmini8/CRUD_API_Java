package com.example.repository;

import com.example.models.User;

import javassist.NotFoundException;
import java.sql.SQLException;
import java.util.List;

public interface UserDao {
    User addUser(User user) throws SQLException;
    User updateUser(int id, User user) throws SQLException;
    void deleteUser(int id) throws SQLException, NotFoundException;
    User getUserById(int id) throws SQLException;
    List<User> getAllUsers() throws SQLException;
}
