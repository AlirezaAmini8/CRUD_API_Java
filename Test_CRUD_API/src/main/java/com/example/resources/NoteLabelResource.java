package com.example.resources;

import com.example.controller.NoteLabelDaoHandler;
import com.example.models.NoteLabel;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;


@Path("/notelabels")
@Api(value = "Note Label Operations", description = "Web Services for note Labels")
public class NoteLabelResource {
    private NoteLabelDaoHandler noteLabelDao = new NoteLabelDaoHandler();
    ObjectMapper mapper = new ObjectMapper();

    private static final Logger logger = LoggerFactory.getLogger(NoteLabelResource.class);

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Create a new note label", notes = "add a label for a note based on ids given", response = NoteLabel.class )
    @ApiResponses({
            @ApiResponse(code = 201, message = "created"),
            @ApiResponse(code = 403, message = "not access"),
            @ApiResponse(code = 400, message = "bad request"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Response createNoteLabel(String input) {
        try {
            NoteLabel noteLabel = mapper.readValue(input, NoteLabel.class);
            NoteLabel createdNoteLabel = noteLabelDao.addNoteLabel(noteLabel);
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
        }
    }

    @GET
    @Path("/note/{noteId}")
    @ApiOperation(value = "Get note labels for a specific note", notes = "Returns all notelabels for a note which are exist in db", response = NoteLabel.class )
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "not find"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Response getNoteLabelsForNote(@PathParam("noteId") int noteId) {
        List<NoteLabel> noteLabels = noteLabelDao.getNoteLabelsForNote(noteId);
        if(!noteLabels.isEmpty()){
            logger.info("Retrieved all labels for note with id = {} successfully", noteId);
            return Response.status(Response.Status.OK)
                    .entity(noteLabels)
                    .build();
        }else{
            logger.warn("No labels found for this note");
            return Response.status(Response.Status.NOT_FOUND)
                    .build();
        }
    }

    @GET
    @Path("/label/{labelId}")
    @ApiOperation(value = "Get note labels for a specific label", notes = "Returns all notelabels for a label which are exist in db", response = NoteLabel.class )
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "not find"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Response getNoteLabelsForLabel(@PathParam("labelId") int labelId) {
        List<NoteLabel> noteLabels = noteLabelDao.getNoteLabelsForLabel(labelId);
        if(!noteLabels.isEmpty()) {
            logger.info("Retrieved all notes for label with id = {} successfully", labelId);
            return Response.status(Response.Status.OK)
                    .entity(noteLabels)
                    .build();
        }else{
            logger.warn("This label didn't attach to any note");
            return Response.status(Response.Status.NOT_FOUND)
                    .build();
        }
    }

    @DELETE
    @Path("/note/{noteId}/label/{labelId}")
    @ApiOperation(value = "Delete a note label", notes = "Deleting a note label with specific id", response = NoteLabel.class )
    @ApiResponses({
            @ApiResponse(code = 200, message = "ok"),
            @ApiResponse(code = 404, message = "note label not found"),
            @ApiResponse(code = 500, message = "Internal Server Error")
    })
    public Response deleteNoteLabel(@PathParam("noteId") int noteId, @PathParam("labelId") int labelId) {
        NoteLabel noteLabel = noteLabelDao.deleteNoteLabel(noteId, labelId);
        if (noteLabel != null) {
            logger.info("Note Label with note's ID {} and label's ID {} deleted.", noteId, labelId);
            return Response.status(Response.Status.OK)
                    .build();
        } else {
            logger.warn("Note Label with note's ID {} and label's ID {} not found.", noteId, labelId);
            return Response.status(Response.Status.NOT_FOUND)
                    .build();
        }
    }
}

