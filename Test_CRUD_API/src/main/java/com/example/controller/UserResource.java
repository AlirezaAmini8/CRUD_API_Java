package com.example.controller;

import com.example.repository.UserDao;
import com.example.models.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Path("/users")
@Api(value = "User Operations", description = "Web Services for users")
public class UserResource {
    private final UserDao userDao;
    private final ObjectMapper mapper;

    private static final Logger logger = LoggerFactory.getLogger(UserResource.class);

    public UserResource(UserDao userDao, ObjectMapper mapper) {
        this.userDao = userDao;
        this.mapper = mapper;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Return all users", notes = "Returns all users exist in db", response = User.class )
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "not find any user"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Response getAllUsers() {
        try {
            List<User> users = userDao.getAllUsers();
            if (!users.isEmpty()) {
                logger.info("Retrieved all users successfully.");
                return Response.status(Response.Status.OK)
                        .entity(users)
                        .build();
            } else {
                logger.warn("No users found.");
                return Response.status(Response.Status.NOT_FOUND)
                        .build();
            }
        }catch (SQLException e) {
            logger.error("Error retrieving users: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving users: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    @ApiOperation(value = "Return a user", notes = "Returns a user with specific id", response = User.class )
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Response getUserById(@PathParam("id") int id) {
        try {
            User foundedUser = userDao.getUserById(id);
            if (foundedUser != null) {
                logger.info("Retrieved user with ID {}: {}", id, foundedUser);
                return Response.status(Response.Status.OK)
                        .entity(foundedUser)
                        .build();
            } else {
                logger.warn("User with ID {} not found", id);
                return Response.status(Response.Status.NOT_FOUND)
                        .build();
            }
        }catch (SQLException e) {
            logger.error("Error retrieving user: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving user: " + e.getMessage())
                    .build();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "create a user", notes = "create a user based on information you enter", response = User.class)
    @ApiResponses({
            @ApiResponse(code = 201, message = "created"),
            @ApiResponse(code = 400, message = "Bad request."),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Response createUser(String input) {
        try {
            User user = mapper.readValue(input, User.class);
            User createdUser = userDao.addUser(user);

            if (createdUser != null) {
                logger.info("User created: {}", createdUser);
                return Response.status(Response.Status.CREATED)
                        .entity(createdUser)
                        .build();
            } else {
                logger.warn("Failed to add user.");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Failed to add user.")
                        .build();
            }
        } catch (IOException e) {
            logger.error("Invalid input format for User: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid input format for User")
                    .build();
        } catch (SQLException e) {
            logger.error("Error creating user: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error creating user: " + e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "update an existing user which his/her id is in url", notes = "update a user with specific id and information you give", response = User.class )
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Response updateUser(@PathParam("id") int id, String input) {

        try {
            User user = mapper.readValue(input, User.class);

            User updatedUser = userDao.updateUser(id, user);

            if (updatedUser != null) {
                logger.info("User with ID {} updated: {}", id, updatedUser);
                return Response.status(Response.Status.OK)
                        .entity(updatedUser)
                        .build();
            } else {
                logger.warn("User with ID {} not found for update", id);
                return Response.status(Response.Status.NOT_FOUND)
                        .build();
            }

        }catch (IOException e) {
            logger.error("Invalid input format for User: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid input format for User")
                    .build();
        }catch (SQLException e) {
            logger.error("Error updating user: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error updating user: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    @ApiOperation(value = "delete a user", notes = "Deleting a user with specific id", response = User.class )
    @ApiResponses({
        @ApiResponse(code = 200, message = "ok"),
        @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Response deleteUser(@PathParam("id") int id) {
        try{
            userDao.deleteUser(id);

            logger.info("User with ID {} deleted.", id);
            return Response.status(Response.Status.OK)
                    .build();

        }catch (SQLException e) {
            logger.error("Error deleting user: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error deleting user: " + e.getMessage())
                    .build();
        }catch (NotFoundException e){
            logger.error("User with ID {} not found for delete", id);
            return Response.status(Response.Status.NOT_FOUND)
                    .build();
        }

    }

}