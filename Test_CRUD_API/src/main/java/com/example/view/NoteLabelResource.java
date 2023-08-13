package com.example.view;

import com.example.controller.NoteLabelDaoHandler;
import com.example.models.NoteLabel;
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

@Tag(name = "Note Label Operations")
@Path("/notelabels")
public class NoteLabelResource {
    private NoteLabelDaoHandler noteLabelDao = new NoteLabelDaoHandler();
    ObjectMapper mapper = new ObjectMapper();

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create a new note label")
    @ApiResponse(responseCode = "201", content = @Content(schema = @Schema(implementation = NoteLabel.class)))
    public Response createNoteLabel(String input) throws JsonProcessingException {
        NoteLabel noteLabel = mapper.readValue(input, NoteLabel.class);
        NoteLabel createdNoteLabel = noteLabelDao.addNoteLabel(noteLabel);
        return Response.status(Response.Status.CREATED)
                .entity(createdNoteLabel)
                .build();
    }

    @GET
    @Path("/note/{noteId}")
    @Operation(summary = "Get note labels for a specific note")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = NoteLabel.class, type = "array")))
    public Response getNoteLabelsForNote(@PathParam("noteId") int noteId) {
        List<NoteLabel> noteLabels = noteLabelDao.getNoteLabelsForNote(noteId);
        return Response.status(Response.Status.OK)
                .entity(noteLabels)
                .build();
    }

    @GET
    @Path("/label/{labelId}")
    @Operation(summary = "Get note labels for a specific label")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = NoteLabel.class, type = "array")))
    public Response getNoteLabelsForLabel(@PathParam("labelId") int labelId) {
        List<NoteLabel> noteLabels = noteLabelDao.getNoteLabelsForLabel(labelId);
        return Response.status(Response.Status.OK)
                .entity(noteLabels)
                .build();
    }

    @DELETE
    @Path("/note/{noteId}/label/{labelId}")
    @Operation(summary = "Delete a note label")
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = NoteLabel.class)))
    public Response deleteNoteLabel(@PathParam("noteId") int noteId, @PathParam("labelId") int labelId) {
        NoteLabel noteLabel = noteLabelDao.deleteNoteLabel(noteId, labelId);
        return Response.status(Response.Status.OK)
                .entity(noteLabel)
                .build();
    }
}
