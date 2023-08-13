package com.example.view;

import com.example.controller.UserDaoHandler;
import com.example.models.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Tag(name = "User Operations")
@Path("/users")
public class UserResource {
    private UserDaoHandler userDao = new UserDaoHandler();
    ObjectMapper mapper = new ObjectMapper();

    @GET
    @Operation(summary = "Get all users")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = User.class, type = "array")))
    public Response getAllUsers() {
        List<User> users = userDao.getAllUsers();
        return Response.status(Response.Status.OK)
                .entity(users)
                .build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get a user by ID")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "404", description = "User not found")
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
    @Operation(summary = "Create a new user")
    @ApiResponse(responseCode = "201", content = @Content(schema = @Schema(implementation = User.class)))
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
    @Operation(summary = "Update an existing user")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "404", description = "User not found")
    public Response updateUser(@PathParam("id") int id, String input) throws JsonProcessingException {
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
    @Operation(summary = "Delete a user")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = User.class)))
    @ApiResponse(responseCode = "404", description = "User not found")
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