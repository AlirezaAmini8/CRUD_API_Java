package com.example.view;

import com.example.controller.LabelDaoHandler;
import com.example.models.Label;
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

@Tag(name = "Label Operations")
@Path("/labels")
public class LabelResource {
    private LabelDaoHandler labelDao = new LabelDaoHandler();
    ObjectMapper mapper = new ObjectMapper();

    @GET
    @Operation(summary = "Get all labels")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Label.class, type = "array")))
    public Response getAllLabels() {
        List<Label> labels = labelDao.getAllLabels();
        return Response.status(Response.Status.OK)
                .entity(labels)
                .build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get a label by ID")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Label.class)))
    @ApiResponse(responseCode = "404", description = "Label not found")
    public Response getLabelById(@PathParam("id") int id) {
        Label foundedLabel = labelDao.getLabelById(id);
        if (foundedLabel != null) {
            return Response.status(Response.Status.OK)
                    .entity(foundedLabel)
                    .build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .build();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create a new label")
    @ApiResponse(responseCode = "201", content = @Content(schema = @Schema(implementation = Label.class)))
    public Response createLabel(String input) throws JsonProcessingException {
        Label label = mapper.readValue(input, Label.class);
        Label createdLabel = labelDao.addLabel(label);
        return Response.status(Response.Status.CREATED)
                .entity(createdLabel)
                .build();
    }

    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update an existing label")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Label.class)))
    @ApiResponse(responseCode = "404", description = "Label not found")
    public Response updateLabel(@PathParam("id") int id, String input) throws JsonProcessingException {
        Label label = mapper.readValue(input, Label.class);
        Label updatedLabel = labelDao.updateLabel(id, label);
        if (updatedLabel != null) {
            return Response.status(Response.Status.OK)
                    .entity(updatedLabel)
                    .build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete a label")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Label.class)))
    @ApiResponse(responseCode = "404", description = "Label not found")
    public Response deleteLabel(@PathParam("id") int id) {
        Label label = labelDao.deleteLabel(id);
        if (label != null) {
            return Response.status(Response.Status.OK)
                    .entity(label)
                    .build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .build();
        }
    }
}
