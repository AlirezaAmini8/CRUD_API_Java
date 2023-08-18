package com.example.controller;

import com.example.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserDaoHandlerTest {

    @InjectMocks
    private UserDaoHandler userDaoHandler;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    @BeforeEach
    public void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        when(mockConnection.prepareStatement(any(String.class))).thenReturn(mockPreparedStatement);
    }

    @Test
    public void testAddUser() throws SQLException {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("testpassword");

        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        User result = userDaoHandler.addUser(user);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("testpassword", result.getPassword());
    }

    @Test
    public void testUpdateUser() throws SQLException {
        int id = 6;
        User user = new User();
        user.setUsername("updateduser");
        user.setPassword("updatedpassword");

        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        User result = userDaoHandler.updateUser(id, user);

        assertNotNull(result);
        assertEquals("updateduser", result.getUsername());
        assertEquals("updatedpassword", result.getPassword());
    }

    @Test
    public void testDeleteUser() throws SQLException {
        int id = 5;
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(id);
        when(mockResultSet.getString(2)).thenReturn("deleteduser");
        when(mockResultSet.getString(3)).thenReturn("deletedpassword");

        User result = userDaoHandler.deleteUser(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("deleteduser", result.getUsername());
        assertEquals("deletedpassword", result.getPassword());
    }

    @Test
    public void testGetUserById() throws SQLException {
        int id = 3;
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(id);
        when(mockResultSet.getString(2)).thenReturn("farhad");
        when(mockResultSet.getString(3)).thenReturn("fawea5225");

        User result = userDaoHandler.getUserById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("farhad", result.getUsername());
        assertEquals("fawea5225", result.getPassword());
    }

    @Test
    public void testGetAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        User user1 = new User();
        user1.setId(2);
        user1.setUsername("mohammad");
        user1.setPassword("1552s");
        users.add(user1);

        User user2 = new User();
        user2.setId(3);
        user2.setUsername("farhad");
        user2.setPassword("fawea5225");
        users.add(user2);

        User user3 = new User();
        user3.setId(4);
        user3.setUsername("sahel");
        user3.setPassword("daesfa56");
        users.add(user3);

        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, true, false);
        when(mockResultSet.getInt(1)).thenReturn(2, 3, 4);
        when(mockResultSet.getString(2)).thenReturn("mohammad", "farhad", "sahel");
        when(mockResultSet.getString(3)).thenReturn("1552s", "fawea5225", "daesfa56");

        List<User> result = userDaoHandler.getAllUsers();

        assertNotNull(result);
        assertEquals(users.size(), result.size());
        assertEquals(users.get(0).getId(), result.get(0).getId());
        assertEquals(users.get(1).getId(), result.get(1).getId());
        assertEquals(users.get(2).getId(), result.get(2).getId());
        assertEquals(users.get(0).getUsername(), result.get(0).getUsername());
        assertEquals(users.get(1).getUsername(), result.get(1).getUsername());
        assertEquals(users.get(2).getUsername(), result.get(2).getUsername());
        assertEquals(users.get(0).getPassword(), result.get(0).getPassword());
        assertEquals(users.get(1).getPassword(), result.get(1).getPassword());
        assertEquals(users.get(2).getPassword(), result.get(2).getPassword());
    }
}
