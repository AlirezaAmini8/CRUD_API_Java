package com.example.view;

import com.example.controller.NoteDaoHandler;
import com.example.models.Note;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.List;

@Path("/notes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NoteResource {
    private NoteDaoHandler noteDao = new NoteDaoHandler();

    @GET
    public List<Note> getAllNotes() throws SQLException {
        return noteDao.getAllNotes();
    }

    @GET
    @Path("/{id}")
    public Note getNoteById(@PathParam("id") int id) throws SQLException {
        return noteDao.getNoteById(id);
    }

    @POST
    public Note createNote(Note note) throws SQLException {
        return noteDao.addNote(note);
    }

    @PUT
    @Path("/{id}")
    public Note updateNote(@PathParam("id") int id, Note note) throws SQLException {
        return noteDao.updateNote(id, note);
    }

    @DELETE
    @Path("/{id}")
    public void deleteNote(@PathParam("id") int id) throws SQLException {
        noteDao.deleteNote(id);
    }

}
