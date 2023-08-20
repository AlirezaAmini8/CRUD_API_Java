package com.example.resources;

import com.example.controller.UserDaoHandler;
import com.example.models.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger logger = LoggerFactory.getLogger(UserResource.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Return all users", notes = "Returns all users exist in db", response = User.class )
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "not find any user")
    })
    public Response getAllUsers() {
        List<User> users = userDao.getAllUsers();
        if(!users.isEmpty()) {
            logger.info("Retrieved all users successfully.");
            return Response.status(Response.Status.OK)
                    .entity(users)
                    .build();
        }else {
            logger.warn("No users found.");
            return Response.status(Response.Status.NOT_FOUND)
                    .build();
        }
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
            logger.info("Retrieved user with ID {}: {}", id, foundedUser);
            return Response.status(Response.Status.OK)
                    .entity(foundedUser)
                    .build();
        } else {
            logger.warn("User with ID {} not found", id);
            return Response.status(Response.Status.NOT_FOUND)
                    .build();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "create a user", notes = "create a user based on information you inter", response = User.class)
    @ApiResponses({
            @ApiResponse(code = 201, message = "created"),
            @ApiResponse(code = 400, message = "Bad request.")
    })
    public Response createUser(String input) {
        try {
            User user = mapper.readValue(input, User.class);
            User createdUser = userDao.addUser(user);
            logger.info("User created: {}", createdUser);
            return Response.status(Response.Status.CREATED)
                    .entity(createdUser)
                    .build();
        }catch (IOException e) {
            logger.error("Invalid input format for User: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid input format for User")
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "update an existing user", notes = "update a user with specific id and information you give", response = User.class )
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 400, message = "Bad request")
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
            logger.info("User with ID {} deleted.", id);
            return Response.status(Response.Status.OK)
                    .entity(user)
                    .build();
        } else {
            logger.warn("User with ID {} not found for delete", id);
            return Response.status(Response.Status.NOT_FOUND)
                    .build();
        }
    }

}