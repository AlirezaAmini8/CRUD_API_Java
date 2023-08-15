package com.example.resources;

import com.example.controller.UserDaoHandler;
import com.example.models.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

@Path("/users")
@Api(value = "User Operations", description = "Web Services for users")
public class UserResource {
    private UserDaoHandler userDao = new UserDaoHandler();
    ObjectMapper mapper = new ObjectMapper();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Return all users", notes = "Returns all users exist in db", response = User.class )
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok")
    })
    public Response getAllUsers() {
        List<User> users = userDao.getAllUsers();
        return Response.status(Response.Status.OK)
                .entity(users)
                .build();
    }

    @GET
    @Path("/{id}")
    @ApiOperation(value = "Return a user", notes = "Returns a user with specific id", response = User.class )
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "User not found")
    })
    public Response getUserById(@PathParam("id") int id) {
        User foundedUser = userDao.getUserById(id);
        if (foundedUser != null) {
            return Response.status(Response.Status.OK)
                    .entity(foundedUser)
                    .build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .build();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "create a user", notes = "create a user based on information you inter", response = User.class)
    @ApiResponses({
            @ApiResponse(code = 201, message = "created")
    })
    public Response createUser(String input) throws IOException {
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
    @ApiOperation(value = "update an existing user", notes = "update a user with specific id and information you give", response = User.class )
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "User not found")
    })
    public Response updateUser(@PathParam("id") int id, String input) throws IOException {
        User user = mapper.readValue(input, User.class);
        User updatedUser = userDao.updateUser(id, user);
        if (updatedUser != null) {
            return Response.status(Response.Status.OK)
                    .entity(updatedUser)
                    .build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    @ApiOperation(value = "delete a user", notes = "Deleting a user with specific id", response = User.class )
    @ApiResponses({
        @ApiResponse(code = 200, message = "ok"),
        @ApiResponse(code = 404, message = "User not found")
    })
    public Response deleteUser(@PathParam("id") int id) {
        User user = userDao.deleteUser(id);
        if (user != null) {
            return Response.status(Response.Status.OK)
                    .entity(user)
                    .build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .build();
        }
    }

}