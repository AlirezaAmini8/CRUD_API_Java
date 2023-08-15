package com.example.resources;

import com.example.controller.LabelDaoHandler;
import com.example.models.Label;
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


@Path("/labels")
@Api(value = "Label Operations", description = "Web Services for labels")
public class LabelResource {
    private LabelDaoHandler labelDao = new LabelDaoHandler();
    ObjectMapper mapper = new ObjectMapper();

    @GET
    @ApiOperation(value = "Return all Labels", notes = "Returns all labels exist in db", response = Label.class )
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok")
    })
    public Response getAllLabels() {
        List<Label> labels = labelDao.getAllLabels();
        return Response.status(Response.Status.OK)
                .entity(labels)
                .build();
    }

    @GET
    @Path("/{id}")
    @ApiOperation(value = "Return a label", notes = "Returns a label with specific id", response = Label.class )
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "Label not found")
    })
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
    @ApiOperation(value = "create a label", notes = "create a label based on information you inter", response = Label.class)
    @ApiResponses({
            @ApiResponse(code = 201, message = "created")
    })
    public Response createLabel(String input) throws IOException {
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
    @ApiOperation(value = "update an existing label", notes = "update a label with specific id and information you give", response = Label.class )
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "Label not found")
    })
    public Response updateLabel(@PathParam("id") int id, String input) throws IOException {
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
    @ApiOperation(value = "delete a label", notes = "Deleting a label with specific id", response = Label.class )
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "Label not found")
    })
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

