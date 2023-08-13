package com.example.view;

import com.example.controller.NoteDaoHandler;
import com.example.models.Note;
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

@Tag(name = "Note Operations")
@Path("/notes")
public class NoteResource {
    private NoteDaoHandler noteDao = new NoteDaoHandler();
    ObjectMapper mapper = new ObjectMapper();

    @GET
    @Operation(summary = "Get all notes")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Note.class, type = "array")))
    public Response getAllNotes() {
        List<Note> notes = noteDao.getAllNotes();
        return Response.status(Response.Status.OK)
                .entity(notes)
                .build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get a note by ID")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Note.class)))
    @ApiResponse(responseCode = "404", description = "Note not found")
    public Response getNoteById(@PathParam("id") int id) {
        Note foundedNote = noteDao.getNoteById(id);
        if (foundedNote != null) {
            return Response.status(Response.Status.OK)
                    .entity(foundedNote)
                    .build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .build();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create a new note")
    @ApiResponse(responseCode = "201", content = @Content(schema = @Schema(implementation = Note.class)))
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
    @Operation(summary = "Update an existing note")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Note.class)))
    @ApiResponse(responseCode = "404", description = "Note not found")
    public Response updateNote(@PathParam("id") int id, String input) throws JsonProcessingException {
        Note note = mapper.readValue(input, Note.class);
        Note updatedNote = noteDao.updateNote(id, note);
        if (updatedNote != null) {
            return Response.status(Response.Status.OK)
                    .entity(updatedNote)
                    .build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete a note")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = Note.class)))
    @ApiResponse(responseCode = "404", description = "Note not found")
    public Response deleteNote(@PathParam("id") int id) {
        Note note = noteDao.deleteNote(id);
        if (note != null) {
            return Response.status(Response.Status.OK)
                    .entity(note)
                    .build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .build();
        }
    }

}
