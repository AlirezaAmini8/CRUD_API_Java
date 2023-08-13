package com.example.view;

import com.example.controller.NoteLabelDaoHandler;
import com.example.models.NoteLabel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/notelabels")
public class NoteLabelResource {
    private NoteLabelDaoHandler noteLabelDao = new NoteLabelDaoHandler();
    ObjectMapper mapper = new ObjectMapper();

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createNoteLabel(String input) throws JsonProcessingException {
        NoteLabel noteLabel = mapper.readValue(input, NoteLabel.class);
        NoteLabel createdNoteLabel = noteLabelDao.addNoteLabel(noteLabel);
        return Response.status(Response.Status.CREATED)
                .entity(createdNoteLabel)
                .build();
    }

    @GET
    @Path("/note/{noteId}")
    public Response getNoteLabelsForNote(@PathParam("noteId") int noteId) {
        List<NoteLabel> noteLabels = noteLabelDao.getNoteLabelsForNote(noteId);
        return Response.status(Response.Status.FOUND)
                .entity(noteLabels)
                .build();
    }

    @GET
    @Path("/label/{labelId}")
    public Response getNoteLabelsForLabel(@PathParam("labelId") int labelId) {
        List<NoteLabel> noteLabels = noteLabelDao.getNoteLabelsForLabel(labelId);
        return Response.status(Response.Status.FOUND)
                .entity(noteLabels)
                .build();
    }

    @DELETE
    @Path("/note/{noteId}/label/{labelId}")
    public Response deleteNoteLabel(@PathParam("noteId") int noteId, @PathParam("labelId") int labelId) {
        NoteLabel noteLabel = noteLabelDao.deleteNoteLabel(noteId, labelId);
        return Response.status(Response.Status.OK)
                .entity(noteLabel)
                .build();
    }
}
