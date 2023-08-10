package com.example.view;

import com.example.controller.UserDaoHandler;
import com.example.models.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.List;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
    private UserDaoHandler userDao = new UserDaoHandler();

    @GET
    public List<User> getAllUsers() throws SQLException {
        return userDao.getAllUsers();
    }

    @GET
    @Path("/{id}")
    public User getUserById(@PathParam("id") int id) throws SQLException {
        return userDao.getUserById(id);
    }

    @POST
    public User createUser(User user) throws SQLException {
        return userDao.addUser(user);
    }

    @PUT
    @Path("/{id}")
    public User updateUser(@PathParam("id") int id, User user) throws SQLException {
        return userDao.updateUser(id, user);
    }

    @DELETE
    @Path("/{id}")
    public void deleteUser(@PathParam("id") int id) throws SQLException {
        userDao.deleteUser(id);
    }

}