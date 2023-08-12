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
    public List<Label> getAllLabels() {
        return labelDao.getAllLabels();
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
    public Label updateLabel(@PathParam("id") int id, String input) throws JsonProcessingException {
        Label label = mapper.readValue(input, Label.class);
        return labelDao.updateLabel(id, label);
    }

    @DELETE
    @Path("/{id}")
    public void deleteLabel(@PathParam("id") int id) {
        labelDao.deleteLabel(id);
    }
}
