package com.example.resources;

import com.example.controller.UserDaoHandler;
import com.example.models.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UserResourceTest {

    @InjectMocks
    private UserResource userResource;

    @Mock
    private UserDaoHandler mockUserDaoHandler;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        users.add(new User());
        when(mockUserDaoHandler.getAllUsers()).thenReturn(users);

        Response response = userResource.getAllUsers();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(users, response.getEntity());
    }

    @Test
    public void testGetUserById() throws SQLException {
        int userId = 1;
        User user = new User();
        user.setId(userId);
        when(mockUserDaoHandler.getUserById(userId)).thenReturn(user);

        Response response = userResource.getUserById(userId);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(user, response.getEntity());
    }

    @Test
    public void testCreateUser() throws IOException, SQLException {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("testpassword");
        String input = objectMapper.writeValueAsString(user);

        when(mockUserDaoHandler.addUser(any(User.class))).thenReturn(user);

        Response response = userResource.createUser(input);

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(user, response.getEntity());
    }

    @Test
    public void testUpdateUser() throws IOException, SQLException {
        int userId = 1;
        User user = new User();
        user.setUsername("updateduser");
        user.setPassword("updatedpassword");
        String input = objectMapper.writeValueAsString(user);

        when(mockUserDaoHandler.updateUser(eq(userId), any(User.class))).thenReturn(user);

        Response response = userResource.updateUser(userId, input);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(user, response.getEntity());
    }

    @Test
    public void testDeleteUser() throws SQLException {
        int userId = 1;
        User user = new User();
        user.setId(userId);
        when(mockUserDaoHandler.deleteUser(userId)).thenReturn(user);

        Response response = userResource.deleteUser(userId);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(user, response.getEntity());
    }
    
}
