package com.example.view;

import com.example.controller.UserDaoHandler;
import com.example.models.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/users")
public class UserResource {
    private UserDaoHandler userDao = new UserDaoHandler();
    ObjectMapper mapper = new ObjectMapper();

    @GET
    public List<User> getAllUsers() {
        return userDao.getAllUsers();
    }

    @GET
    @Path("/{id}")
    public Response getUserById(@PathParam("id") int id) {
        User foundedUser = userDao.getUserById(id);
        return Response.status(Response.Status.FOUND)
                .entity(foundedUser)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser(String input) throws JsonProcessingException {
        User user = mapper.readValue(input, User.class);
        User createdUser = userDao.addUser(user);
        return Response.status(Response.Status.CREATED)
                .entity(createdUser)
                .build();
    }

    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public User updateUser(@PathParam("id") int id, String input) throws JsonProcessingException {
        User user = mapper.readValue(input, User.class);
        return userDao.updateUser(id, user);
    }

    @DELETE
    @Path("/{id}")
    public void deleteUser(@PathParam("id") int id) {
        userDao.deleteUser(id);
    }

}