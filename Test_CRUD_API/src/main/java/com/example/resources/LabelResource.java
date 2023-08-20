package com.example.resources;

import com.example.controller.LabelDaoHandler;
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
import java.util.List;


@Path("/labels")
@Api(value = "Label Operations", description = "Web Services for labels")
public class LabelResource {
    private LabelDaoHandler labelDao = new LabelDaoHandler();
    ObjectMapper mapper = new ObjectMapper();

    private static final Logger logger = LoggerFactory.getLogger(LabelResource.class);

    @GET
    @Path("/user/{userId}")
    @ApiOperation(value = "Return all Labels of user", notes = "Returns all labels of user with specific id", response = Label.class )
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "User not found")
    })
    public Response getAllLabels(@PathParam("userId") int userId) {
        List<Label> labels = labelDao.getAllLabels(userId);
        if(!labels.isEmpty()) {
            logger.info("Retrieved all labels successfully.");
            return Response.status(Response.Status.OK)
                    .entity(labels)
                    .build();
        }else{
            logger.warn("No labels found for this user");
            return Response.status(Response.Status.NOT_FOUND)
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    @ApiOperation(value = "Get a label of a user", notes = "Returns a label with specific id for specific user", response = Label.class )
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "Label not found")
    })
    public Response getLabelById(@PathParam("id") int id) {
        Label foundedLabel = labelDao.getLabelById(id);
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
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "create a label", notes = "create a label based on information you inter", response = Label.class)
    @ApiResponses({
            @ApiResponse(code = 201, message = "created"),
            @ApiResponse(code = 400, message = "Bad request")
    })
    public Response createLabel(String input) {
        try {
            Label label = mapper.readValue(input, Label.class);
            Label createdLabel = labelDao.addLabel(label);

            logger.info("Label created: {}", createdLabel);

            return Response.status(Response.Status.CREATED)
                    .entity(createdLabel)
                    .build();
        }catch (IOException e) {
            logger.error("Invalid input format for Label: {}", e.getMessage());

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid input format for Label")
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
            @ApiResponse(code = 400, message = "Bad request")
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
        }
    }

    @DELETE
    @Path("/{id}")
    @ApiOperation(value = "delete a label", notes = "Deleting a label with specific id", response = Label.class )
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "Label not found")
    })
    public Response deleteLabel(@PathParam("id") int id) {
        Label label = labelDao.deleteLabel(id);
        if (label != null) {
            logger.info("Label with ID {} deleted.", id);
            return Response.status(Response.Status.OK)
                    .entity(label)
                    .build();
        } else {
            logger.warn("Label with ID {} not found for delete", id);
            return Response.status(Response.Status.NOT_FOUND)
                    .build();
        }
    }
}

