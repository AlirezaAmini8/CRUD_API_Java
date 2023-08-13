package com.example.view;

import com.example.controller.LabelDaoHandler;
import com.example.models.Label;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/labels")
public class LabelResource {
    private LabelDaoHandler labelDao = new LabelDaoHandler();
    ObjectMapper mapper = new ObjectMapper();

    @GET
    public Response getAllLabels() {
        List<Label> labels = labelDao.getAllLabels();
        return Response.status(Response.Status.FOUND)
                .entity(labels)
                .build();
    }

    @GET
    @Path("/{id}")
    public Response getLabelById(@PathParam("id") int id) {
        Label foundedLabel = labelDao.getLabelById(id);
        return Response.status(Response.Status.FOUND)
                .entity(foundedLabel)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
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
    public Response updateLabel(@PathParam("id") int id, String input) throws JsonProcessingException {
        Label label = mapper.readValue(input, Label.class);
        Label updatedLabel = labelDao.updateLabel(id, label);
        return Response.status(Response.Status.OK)
                .entity(updatedLabel)
                .build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteLabel(@PathParam("id") int id) {
        Label label = labelDao.deleteLabel(id);
        return Response.status(Response.Status.OK)
                .entity(label)
                .build();
    }
}
