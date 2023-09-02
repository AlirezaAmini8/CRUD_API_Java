package com.example.controller;

import com.example.models.NoteLabel;
import com.example.repository.LabelDao;
import com.example.models.Label;
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
import java.nio.file.AccessDeniedException;
import java.sql.SQLException;
import java.util.List;


@Path("/labels")
@Api(value = "Label Operations", description = "Web Services for labels")
public class LabelResource {
    private final LabelDao labelDao;
    private final ObjectMapper mapper;

    private static final Logger logger = LoggerFactory.getLogger(LabelResource.class);

    public LabelResource(LabelDao labelDao, ObjectMapper mapper) {
        this.labelDao = labelDao;
        this.mapper = mapper;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/user/{userId}")
    @ApiOperation(value = "Return all Labels of user", notes = "Returns all labels of user with specific id", response = Label.class )
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Response getAllLabels(@PathParam("userId") int userId) {
        try {
            List<Label> labels = labelDao.getAllLabels(userId);
            if (!labels.isEmpty()) {
                logger.info("Retrieved all labels successfully.");
                return Response.status(Response.Status.OK)
                        .entity(labels)
                        .build();
            } else {
                logger.warn("No labels found for this user");
                return Response.status(Response.Status.NOT_FOUND)
                        .build();
            }
        }catch (SQLException e) {
            logger.error("Error retrieving labels: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving labels: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}/user/{userId}")
    @ApiOperation(value = "Get a label of a user", notes = "Returns a label with specific id for specific user", response = Label.class )
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "Label not found"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 403, message = "not access")
    })
    public Response getLabelById(@PathParam("id") int id,@PathParam("userId") int userId) {
        try {
            Label foundedLabel = labelDao.getLabelById(id, userId);
            if (foundedLabel != null) {
                logger.info("Retrieved label with ID {}", id);
                return Response.status(Response.Status.OK)
                        .entity(foundedLabel)
                        .build();
            } else {
                logger.warn("Label with ID {} not found", id);
                return Response.status(Response.Status.NOT_FOUND)
                        .build();
            }
        }catch (AccessDeniedException e){
            logger.error("Access denied: {}", e.getMessage());
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("Access denied: " + e.getMessage())
                    .build();
        } catch (SQLException e) {
            logger.error("Error retrieving label: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving label: " + e.getMessage())
                    .build();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "create a label", notes = "create a label based on information you inter", response = Label.class)
    @ApiResponses({
            @ApiResponse(code = 201, message = "created"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Response createLabel(String input) {
        try {
            Label label = mapper.readValue(input, Label.class);
            Label createdLabel = labelDao.addLabel(label);

            if(label != null) {
                logger.info("Label created: {}", createdLabel);
                return Response.status(Response.Status.CREATED)
                        .entity(createdLabel)
                        .build();
            } else {
                logger.warn("Failed to add label.");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Failed to add label.")
                        .build();
            }
        }catch (IOException e) {
            logger.error("Invalid input format for Label: {}", e.getMessage());

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid input format for Label")
                    .build();
        }catch (SQLException e){
            logger.error("Error creating label: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error creating label: " + e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "update an existing label", notes = "update a label with specific id and information you give", response = Label.class )
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "Label not found"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Response updateLabel(@PathParam("id") int id, String input) {
        try {
            Label label = mapper.readValue(input, Label.class);
            Label updatedLabel = labelDao.updateLabel(id, label);
            if (updatedLabel != null) {
                logger.info("Label with ID {} updated: {}", id, updatedLabel);
                return Response.status(Response.Status.OK)
                        .entity(updatedLabel)
                        .build();
            } else {
                logger.warn("Label with ID {} not found for update", id);
                return Response.status(Response.Status.NOT_FOUND)
                        .build();
            }
        }catch (IOException e) {
            logger.error("Invalid input format for Label: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid input format for Label")
                    .build();
        }catch (SQLException e) {
            logger.error("Error updating label: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error updating label: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}/user/{userId}")
    @ApiOperation(value = "delete a label", notes = "Deleting a label with specific id", response = Label.class )
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "Label not found"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 403, message = "not access")
    })
    public Response deleteLabel(@PathParam("id") int id, @PathParam("userId") int userId) {
        try {
            labelDao.deleteLabel(id, userId);

            logger.info("Label with ID {} deleted.", id);
            return Response.status(Response.Status.OK)
                    .build();

        }catch (AccessDeniedException e){
            logger.error("Access denied: {}", e.getMessage());
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("Access denied: " + e.getMessage())
                    .build();
        }catch(SQLException e) {
            logger.error("Error deleting label: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error deleting label: " + e.getMessage())
                    .build();
        }catch (NotFoundException e){
            logger.error("Label with ID {} not found for delete", id);
            return Response.status(Response.Status.NOT_FOUND)
                    .build();
        }
    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{labelId}/notes/user/{userId}")
    @ApiOperation(value = "Get notes of a specific label", notes = "Returns all notes of a label which attached to those notes", response = NoteLabel.class )
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "not find"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 403, message = "not access")
    })
    public Response getNotes(@PathParam("labelId") int labelId, @PathParam("userId") int userId) {
        try {
            List<NoteLabel> noteLabels = labelDao.getNotes(labelId, userId);
            if (!noteLabels.isEmpty()) {
                logger.info("Retrieved all notes for label with id = {} successfully", labelId);
                return Response.status(Response.Status.OK)
                        .entity(noteLabels)
                        .build();
            } else {
                logger.warn("This label didn't attach to any note");
                return Response.status(Response.Status.NOT_FOUND)
                        .build();
            }
        }catch (SQLException e) {
            logger.error("Error retrieving notes for label: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving notes for label: " + e.getMessage())
                    .build();
        }catch (AccessDeniedException e) {
            logger.error("Access denied: {}", e.getMessage());
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("Access denied: " + e.getMessage())
                    .build();
        }
    }
}

