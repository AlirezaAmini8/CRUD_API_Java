package com.example.controller;

import com.example.models.User;
import com.example.repository.UserDao;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.mockito.Mockito;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class UserResourceTest extends JerseyTest {


    private static final UserDao mockUserDao = Mockito.mock(UserDao.class);
    private final ObjectMapper objectMapper;

    public UserResourceTest() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected Application configure() {
        return new ResourceConfig()
                .packages("com.example.controller")
                .register(new UserResource(mockUserDao, objectMapper));
    }
    @Test
    public void testGetAllUsers() throws SQLException, JsonProcessingException {
        List<User> users = new ArrayList<>();
        users.add(new User(1, "User1", "password1"));
        users.add(new User(2, "User2", "password2"));

        Mockito.when(mockUserDao.getAllUsers()).thenReturn(users);

        Response response = target("/users").request().get();


        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        assertEquals(objectMapper.writeValueAsString(users), response.readEntity(String.class));
    }

    @Test
    public void testGetAllUsersNoUsersFound() throws SQLException {
        Mockito.when(mockUserDao.getAllUsers()).thenReturn(new ArrayList<>());

        Response response = target("/users").request().get();

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGetAllUsersServerError() throws SQLException {
        Mockito.doThrow(SQLException.class).when(mockUserDao).getAllUsers();

        Response response = target("/users").request().get();

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGetUserById() throws SQLException, JsonProcessingException {
        int userId = 2;
        User user = new User(userId, "User2","password2");

        Mockito.when(mockUserDao.getUserById(userId)).thenReturn(user);

        Response response = target("/users/" + userId).request().get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        assertEquals(objectMapper.writeValueAsString(user), response.readEntity(String.class));
    }


    @Test
    public void testGetUserByIdNotFound() throws SQLException {
        int userId = 1;
        Mockito.when(mockUserDao.getUserById(userId)).thenReturn(null);

        Response response = target("/users/" + userId).request().get();

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGetUserByIdServerError() throws SQLException {
        int userId = 1;
        Mockito.doThrow(SQLException.class).when(mockUserDao).getUserById(userId);

        Response response = target("/users/" + userId).request().get();

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void testCreateUser() throws SQLException, JsonProcessingException {
        User user = new User(1, "User1", "password1");
        String inputJson = objectMapper.writeValueAsString(user);

        Mockito.when(mockUserDao.addUser(Mockito.any(User.class))).thenReturn(user);

        Response response = target("/users").request().post(Entity.json(inputJson));

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

        User responseUser = response.readEntity(User.class);
        assertEquals(user, responseUser);
    }

    @Test
    public void testCreateUserWithInvalidInput() {
        String invalidInput = "{}";

        Response response = target("/users").request().post(javax.ws.rs.client.Entity.json(invalidInput));

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void testCreateUserServerError() throws SQLException, JsonProcessingException {
        int userId = 2;
        User user = new User(userId, "User2","password2");

        Mockito.doThrow(SQLException.class).when(mockUserDao).addUser(user);

        Response response = target("/users").request().post(Entity.json(objectMapper.writeValueAsString(user)));

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void testUpdateUser() throws SQLException, JsonProcessingException {
        int userId = 1;
        User user = new User(userId, "UpdatedUser", "updatedPassword");
        String inputJson = objectMapper.writeValueAsString(user);

        Mockito.when(mockUserDao.updateUser(userId, user)).thenReturn(user);

        Response response = target("/users/" + userId).request().put(Entity.json(inputJson));

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        User responseUser = response.readEntity(User.class);
        assertEquals(user, responseUser);
    }

    @Test
    public void testUpdateUserNotFound() throws SQLException, JsonProcessingException {
        int userId = 1;
        User user = new User(userId, "UpdatedUser", "updatedPassword");
        String inputJson = objectMapper.writeValueAsString(user);

        Mockito.when(mockUserDao.updateUser(userId, user)).thenReturn(null);

        Response response = target("/users/" + userId).request().put(Entity.json(inputJson));

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void testUpdateUserInvalidInput() {
        int userId = 1;
        String invalidInput = "{}";

        Response response = target("/users/" + userId).request().put(Entity.json(invalidInput));

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void testUpdateUserSQLException() throws SQLException, JsonProcessingException {
        int userId = 1;
        User user = new User(userId, "UpdatedUser", "updatedPassword");
        String inputJson = objectMapper.writeValueAsString(user);

        Mockito.doThrow(SQLException.class).when(mockUserDao).updateUser(userId, user);

        Response response = target("/users/" + userId).request().put(Entity.json(inputJson));

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void testDeleteUser() {
        int userId = 1;

        Response response = target("/users/" + userId).request().delete();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testDeleteUserNotFound() throws SQLException {
        int userId = 1;
        Mockito.doThrow(NotFoundException.class).when(mockUserDao).deleteUser(userId);

        Response response = target("/users/" + userId).request().delete();

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void testDeleteUserSQLException() throws SQLException {
        int userId = 1;

        Mockito.doThrow(SQLException.class).when(mockUserDao).deleteUser(userId);

        Response response = target("/users/" + userId).request().delete();

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

}
