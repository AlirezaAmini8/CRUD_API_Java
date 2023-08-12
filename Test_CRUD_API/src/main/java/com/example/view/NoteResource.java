package com.example.view;

import com.example.controller.NoteDaoHandler;
import com.example.models.Note;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/notes")

public class NoteResource {
    private NoteDaoHandler noteDao = new NoteDaoHandler();
    ObjectMapper mapper = new ObjectMapper();

    @GET
    public List<Note> getAllNotes() {
        return noteDao.getAllNotes();
    }

    @GET
    @Path("/{id}")
    public Response getNoteById(@PathParam("id") int id) {
        Note foundedNote = noteDao.getNoteById(id);
        return Response.status(Response.Status.FOUND)
                .entity(foundedNote)
                .build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createNote(String input) throws JsonProcessingException {
        Note note = mapper.readValue(input, Note.class);
        Note createdNote = noteDao.addNote(note);
        return Response.status(Response.Status.CREATED)
                .entity(createdNote)
                .build();
    }

    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Note updateNote(@PathParam("id") int id, String input) throws JsonProcessingException {
        Note note = mapper.readValue(input, Note.class);
        return noteDao.updateNote(id, note);
    }

    @DELETE
    @Path("/{id}")
    public void deleteNote(@PathParam("id") int id) {
        noteDao.deleteNote(id);
    }

}
