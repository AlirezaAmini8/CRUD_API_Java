package com.example.resources;

import com.example.controller.NoteDaoHandler;
import com.example.models.Note;
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

@Api(value = "Note Operations", description = "Web Services for notes")
@Path("/notes")
public class NoteResource {
    private NoteDaoHandler noteDao = new NoteDaoHandler();
    ObjectMapper mapper = new ObjectMapper();

    @GET
    @Path("/user/{userId}")
    @ApiOperation(value = "Get all notes of user", notes = "Returns all notes of user with specific id", response = Note.class )
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "User not found")
    })
    public Response getAllNotes(@PathParam("userId") int userId) {
        List<Note> notes = noteDao.getAllNotes(userId);
        if (!notes.isEmpty()) {
            return Response.status(Response.Status.OK)
                    .entity(notes)
                    .build();
        }else{
            return Response.status(Response.Status.NOT_FOUND)
                    .build();
        }
    }

    @GET
    @Path("/note/{id}/user/{userId}")
    @ApiOperation(value = "Get a note of a user", notes = "Returns a note with specific id for specific user", response = Note.class )
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "Note or User not found")
    })
    public Response getNoteById(@PathParam("id") int id, @PathParam("userId") int userId) {
        Note foundedNote = noteDao.getNoteById(id, userId);
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
    @ApiOperation(value = "create a note", notes = "create a note based on information you inter", response = Note.class)
    @ApiResponses({
            @ApiResponse(code = 201, message = "created"),
            @ApiResponse(code = 400, message = "Bad request")
    })
    public Response createNote(String input) {
        try {
            Note note = mapper.readValue(input, Note.class);
            Note createdNote = noteDao.addNote(note);
            return Response.status(Response.Status.CREATED)
                    .entity(createdNote)
                    .build();
        }catch (IOException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid input format for Note")
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Update an existing note", notes = "update a note with specific id and information you give", response = Note.class )
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "Note not found"),
            @ApiResponse(code = 400, message = "Bad request")
    })
    public Response updateNote(@PathParam("id") int id, String input) {
        try {
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
        }catch (IOException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid input format for Note")
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    @ApiOperation(value = "delete a note", notes = "Deleting a note with specific id", response = Note.class )
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "Note not found")
    })
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
