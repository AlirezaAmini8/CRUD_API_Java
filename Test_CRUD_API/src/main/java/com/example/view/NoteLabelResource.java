package com.example.view;

import com.example.controller.NoteLabelDaoHandler;
import com.example.models.NoteLabel;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.List;

@Path("/notelabels")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NoteLabelResource {
    private NoteLabelDaoHandler noteLabelDao = new NoteLabelDaoHandler();

    @POST
    public void createNoteLabel(NoteLabel noteLabel) {
        noteLabelDao.addNoteLabel(noteLabel);
    }

    @GET
    @Path("/note/{noteId}")
    public List<NoteLabel> getNoteLabelsForNote(@PathParam("noteId") int noteId) {
        return noteLabelDao.getNoteLabelsForNote(noteId);
    }

    @GET
    @Path("/label/{labelId}")
    public List<NoteLabel> getNoteLabelsForLabel(@PathParam("labelId") int labelId) {
        return noteLabelDao.getNoteLabelsForLabel(labelId);
    }

    @DELETE
    @Path("/note/{noteId}/label/{labelId}")
    public void deleteNoteLabel(@PathParam("noteId") int noteId, @PathParam("labelId") int labelId) {
        noteLabelDao.deleteNoteLabel(noteId, labelId);
    }
}
