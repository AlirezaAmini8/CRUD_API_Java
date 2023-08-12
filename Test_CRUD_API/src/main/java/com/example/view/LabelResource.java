package com.example.view;

import com.example.controller.LabelDaoHandler;
import com.example.models.Label;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.List;

@Path("/labels")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LabelResource {
    private LabelDaoHandler labelDao = new LabelDaoHandler();

    @GET
    public List<Label> getAllLabels() {
        return labelDao.getAllLabels();
    }

    @GET
    @Path("/{id}")
    public Label getLabelById(@PathParam("id") int id) {
        return labelDao.getLabelById(id);
    }

    @POST
    public Label createLabel(Label label) {
        return labelDao.addLabel(label);
    }

    @PUT
    @Path("/{id}")
    public Label updateLabel(@PathParam("id") int id, Label label) {
        return labelDao.updateLabel(id, label);
    }

    @DELETE
    @Path("/{id}")
    public void deleteLabel(@PathParam("id") int id) {
        labelDao.deleteLabel(id);
    }
}
