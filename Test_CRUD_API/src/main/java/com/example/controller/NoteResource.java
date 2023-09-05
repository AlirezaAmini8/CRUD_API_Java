package com.example.controller;

import com.example.models.NoteLabel;
import com.example.repository.NoteDao;
import com.example.models.Note;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javassist.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.sql.SQLException;
import java.util.List;

@Api(value = "Note Operations", description = "Web Services for notes")
@Path("/notes")
public class NoteResource {
    private final NoteDao noteDao;
    private final ObjectMapper mapper;

    private static final Logger logger = LoggerFactory.getLogger(NoteResource.class);

    public NoteResource(NoteDao noteDao, ObjectMapper mapper) {
        this.noteDao = noteDao;
        this.mapper = mapper;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/user/{userId}")
    @ApiOperation(value = "Get all notes of user", notes = "Returns all notes of user with specific id", response = Note.class )
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Response getAllNotes(@PathParam("userId") int userId) {
        try {
            List<Note> notes = noteDao.getAllNotes(userId);
            if (!notes.isEmpty()) {
                logger.info("Retrieved all notes successfully.");
                return Response.status(Response.Status.OK)
                        .entity(notes)
                        .build();
            } else {
                logger.warn("No notes found for this user");
                return Response.status(Response.Status.NOT_FOUND)
                        .build();
            }
        }catch (SQLException e) {
            logger.error("Error retrieving notes: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving notes: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}/user/{userId}")
    @ApiOperation(value = "Get a note of a user", notes = "Returns a note with specific id for specific user", response = Note.class )
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "Note not found"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 403, message = "not access")
    })
    public Response getNoteById(@PathParam("id") int id, @PathParam("userId") int userId) {
        try {
            Note foundedNote = noteDao.getNoteById(id, userId);
            if (foundedNote != null) {
                logger.info("Retrieved note with ID {}", id);
                return Response.status(Response.Status.OK)
                        .entity(foundedNote)
                        .build();
            } else {
                logger.warn("Note with ID {} not found", id);
                return Response.status(Response.Status.NOT_FOUND)
                        .build();
            }
        }catch (AccessDeniedException e){
            logger.error("Access denied: {}", e.getMessage());
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("Access denied: " + e.getMessage())
                    .build();
        }catch (SQLException e) {
            logger.error("Error retrieving note: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving note: " + e.getMessage())
                    .build();
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "create a note", notes = "create a note based on information you inter", response = Note.class)
    @ApiResponses({
            @ApiResponse(code = 201, message = "created"),
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Response createNote(String input) {
        try {
            Note note = mapper.readValue(input, Note.class);
            Note createdNote = noteDao.addNote(note);

            if(createdNote != null) {
                logger.info("Note created: {}", createdNote);
                return Response.status(Response.Status.CREATED)
                        .entity(createdNote)
                        .build();
            }else {
                logger.warn("Failed to add note.");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity("Failed to add note.")
                        .build();
            }
        }catch (IOException e) {
            logger.error("Invalid input format for Note: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid input format for Note")
                    .build();
        }catch (SQLException e){
            logger.error("Error creating note: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error creating note: " + e.getMessage())
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
            @ApiResponse(code = 400, message = "Bad request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Response updateNote(@PathParam("id") int id, String input) {
        try {
            Note note = mapper.readValue(input, Note.class);
            Note updatedNote = noteDao.updateNote(id, note);
            if (updatedNote != null) {
                logger.info("Note with ID {} updated: {}", id, updatedNote);
                return Response.status(Response.Status.OK)
                        .entity(updatedNote)
                        .build();
            } else {
                logger.warn("Note with ID {} not found for update", id);
                return Response.status(Response.Status.NOT_FOUND)
                        .build();
            }
        }catch (IOException e) {
            logger.error("Invalid input format for Note: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid input format for Note")
                    .build();
        }catch (SQLException e) {
            logger.error("Error updating note: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error updating note: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}/user/{userId}")
    @ApiOperation(value = "delete a note", notes = "Deleting a note with specific id", response = Note.class )
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "Note not found"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 403, message = "not access")
    })
    public Response deleteNote(@PathParam("id") int id, @PathParam("userId") int userId) {
        try {
            noteDao.deleteNote(id, userId);

            logger.info("Note with ID {} deleted.", id);
            return Response.status(Response.Status.OK)
                    .build();

        }catch (AccessDeniedException e){
            logger.error("Access denied: {}", e.getMessage());
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("Access denied: " + e.getMessage())
                    .build();
        }catch (NotFoundException e){
            logger.error("Note with ID {} not found for delete", id);
            return Response.status(Response.Status.NOT_FOUND)
                    .build();
        } catch (SQLException e) {
            logger.error("Error deleting note: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error deleting note: " + e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("/label")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "attach a new label to note", notes = "add a label for a note based on ids given", response = NoteLabel.class )
    @ApiResponses({
            @ApiResponse(code = 201, message = "created"),
            @ApiResponse(code = 403, message = "not access"),
            @ApiResponse(code = 400, message = "bad request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Response addLabel(String input) {
        try {
            NoteLabel noteLabel = mapper.readValue(input, NoteLabel.class);
            NoteLabel createdNoteLabel = noteDao.addLabel(noteLabel);
            if(createdNoteLabel != null) {
                logger.info("NoteLabel created: {}", createdNoteLabel);
                return Response.status(Response.Status.CREATED)
                        .entity(createdNoteLabel)
                        .build();
            }else{
                logger.warn("You can't add this label to this note,because their users are different.");
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("You can't add this label to this note,because their users are different.")
                        .build();
            }
        }catch (IOException e) {
            logger.error("Invalid input format for NoteLabel: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid input format for Note Label")
                    .build();
        }catch (SQLException e) {
            logger.error("Error creating noteLabel: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error creating noteLabel: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{noteId}/labels/user/{userId}")
    @ApiOperation(value = "Get labels for a specific note", notes = "Returns all labels for a note which are to this note", response = NoteLabel.class )
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "not find"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 403, message = "not access")
    })
    public Response getLabels(@PathParam("noteId") int noteId, @PathParam("userId") int userId) {
        try {
            List<NoteLabel> noteLabels = noteDao.getLabels(noteId, userId);
            if (!noteLabels.isEmpty()) {
                logger.info("Retrieved all labels for note with id = {} successfully", noteId);
                return Response.status(Response.Status.OK)
                        .entity(noteLabels)
                        .build();
            } else {
                logger.warn("No labels found for this note");
                return Response.status(Response.Status.NOT_FOUND)
                        .build();
            }
        }catch (SQLException e) {
            logger.error("Error retrieving labels for note: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving labels for note: " + e.getMessage())
                    .build();
        }catch (AccessDeniedException e){
            logger.error("Access denied: {}", e.getMessage());
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("Access denied: " + e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{noteId}/label/{labelId}/user/{userId}")
    @ApiOperation(value = "Delete a label from note", notes = "Deleting a label with specific id from a note", response = NoteLabel.class )
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "note label not found"),
            @ApiResponse(code = 500, message = "Internal Server Error"),
            @ApiResponse(code = 403, message = "not access")
    })
    public Response deleteLabel(@PathParam("noteId") int noteId, @PathParam("labelId") int labelId, @PathParam("userId") int userId) {
        try {
            noteDao.deleteLabel(noteId, labelId, userId);

            logger.info("Note Label with note's ID {} and label's ID {} deleted.", noteId, labelId);
            return Response.status(Response.Status.OK)
                    .build();

        }catch (SQLException e) {
            logger.error("Error deleting noteLabel: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error deleting noteLabel: " + e.getMessage())
                    .build();
        }catch (NotFoundException e){
            logger.error("Note Label with note's ID {} and label's ID {} not found.", noteId, labelId);
            return Response.status(Response.Status.NOT_FOUND)
                    .build();

        }catch (AccessDeniedException e){
            logger.error("Access denied: {}", e.getMessage());
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("Access denied: " + e.getMessage())
                    .build();
        }
    }

}
