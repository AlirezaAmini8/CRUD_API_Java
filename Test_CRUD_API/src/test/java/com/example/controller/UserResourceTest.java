package com.example.controller;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.models.User;
import com.example.repository.UserDao;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserResourceTest {

    @Mock
    private UserDao userDao;

    @Mock
    private ObjectMapper mapper;

    @InjectMocks
    private UserResource userResource;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetAllUsers() throws SQLException, JsonProcessingException {
        // Mock the behavior of userDao.getAllUsers()
        List<User> users = new ArrayList<>();
        users.add(new User(1, "user1", "password1"));
        when(userDao.getAllUsers()).thenReturn(users);

        // Mock the behavior of mapper.writeValueAsString()
        String userJson = "{\"id\":1,\"username\":\"user1\",\"password\":\"password1\"}";
        when(mapper.writeValueAsString(users)).thenReturn(userJson);

        // Test the getAllUsers method
        Response response = userResource.getAllUsers();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(userJson, response.getEntity());
    }

    @Test
    public void testGetAllUsersNoUsersFound() throws SQLException {
        // Mock the behavior of userDao.getAllUsers()
        when(userDao.getAllUsers()).thenReturn(new ArrayList<>());

        // Test the getAllUsers method when no users are found
        Response response = userResource.getAllUsers();
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGetUserById() throws SQLException, JsonProcessingException {
        int userId = 1;
        User user = new User(userId, "user1", "password1");

        // Mock the behavior of userDao.getUserById()
        when(userDao.getUserById(userId)).thenReturn(user);

        // Mock the behavior of mapper.writeValueAsString()
        String userJson = "{\"id\":1,\"username\":\"user1\",\"password\":\"password1\"}";
        when(mapper.writeValueAsString(user)).thenReturn(userJson);

        // Test the getUserById method
        Response response = userResource.getUserById(userId);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(userJson, response.getEntity());
    }

    @Test
    public void testGetUserByIdNotFound() throws SQLException {
        int userId = 1;

        // Mock the behavior of userDao.getUserById() when user is not found
        when(userDao.getUserById(userId)).thenReturn(null);

        // Test the getUserById method when user is not found
        Response response = userResource.getUserById(userId);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    // Similar tests can be written for other methods in UserResource
}
