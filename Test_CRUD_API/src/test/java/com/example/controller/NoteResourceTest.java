package com.example.controller;

import com.example.models.Note;
import com.example.models.NoteLabel;
import com.example.repository.NoteDao;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javassist.NotFoundException;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.mockito.Mockito;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.nio.file.AccessDeniedException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class NoteResourceTest extends JerseyTest {

    private static final NoteDao mockNoteDao = Mockito.mock(NoteDao.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected Application configure() {
        return new ResourceConfig()
                .packages("com.example.controller")
                .register(new NoteResource(mockNoteDao, objectMapper));
    }

    @Test
    public void testGetAllNotes() throws SQLException, JsonProcessingException {
        int userId = 1;
        List<Note> notes = new ArrayList<>();
        Date now = new Date(System.currentTimeMillis());
        notes.add(new Note(1, userId,"Note1", "Content1", now, now));
        notes.add(new Note(2, userId, "Note2", "Content2", now, now));

        Mockito.when(mockNoteDao.getAllNotes(userId)).thenReturn(notes);

        Response response = target("/notes/user/" + userId).request().get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        assertEquals(objectMapper.writeValueAsString(notes), response.readEntity(String.class));
    }

    @Test
    public void testGetAllNotesNoNotesFound() throws SQLException {
        int userId = 1;
        Mockito.when(mockNoteDao.getAllNotes(userId)).thenReturn(new ArrayList<>());

        Response response = target("/notes/user/" + userId).request().get();

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGetAllNotesServerError() throws SQLException {
        int userId = 1;
        Mockito.doThrow(SQLException.class).when(mockNoteDao).getAllNotes(userId);

        Response response = target("/notes/user/" + userId).request().get();

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGetNoteById() throws SQLException, JsonProcessingException, AccessDeniedException {
        int userId = 1;
        int noteId = 1;
        Date now = new Date(System.currentTimeMillis());
        Note note = new Note(noteId, userId,"Note1", "Content1", now, now);

        Mockito.when(mockNoteDao.getNoteById(noteId, userId)).thenReturn(note);

        Response response = target("/notes/" + noteId + "/user/" + userId).request().get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        assertEquals(objectMapper.writeValueAsString(note), response.readEntity(String.class));
    }

    @Test
    public void testGetNoteByIdNotFound() throws SQLException, AccessDeniedException {
        int userId = 1;
        int noteId = 1;
        Mockito.when(mockNoteDao.getNoteById(noteId, userId)).thenReturn(null);

        Response response = target("/notes/" + noteId + "/user/" + userId).request().get();

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGetNoteByIdServerError() throws SQLException, AccessDeniedException {
        int userId = 1;
        int noteId = 1;
        Mockito.doThrow(SQLException.class).when(mockNoteDao).getNoteById(noteId, userId);

        Response response = target("/notes/" + noteId + "/user/" + userId).request().get();

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGetNoteByIdAccessError() throws SQLException, AccessDeniedException {
        int userId = 1;
        int noteId = 1;
        Mockito.doThrow(AccessDeniedException.class).when(mockNoteDao).getNoteById(noteId, userId);

        Response response = target("/notes/" + noteId + "/user/" + userId).request().get();

        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
    }

    @Test
    public void testCreateNote() throws SQLException, JsonProcessingException {
        int userId = 1;
        Date now = new Date(System.currentTimeMillis());
        Note note = new Note(1, userId,"Note1", "Content1", now, now);
        String inputJson = objectMapper.writeValueAsString(note);

        Mockito.when(mockNoteDao.addNote(Mockito.any(Note.class))).thenReturn(note);

        Response response = target("/notes").request().post(Entity.json(inputJson));

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

        Note responseNote = response.readEntity(Note.class);
        assertEquals(note, responseNote);
    }

    @Test
    public void testCreateNoteWithInvalidInput() {
        String invalidInput = "{}";

        Response response = target("/notes").request().post(Entity.json(invalidInput));

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void testCreateNoteServerError() throws SQLException, JsonProcessingException {
        int userId = 1;
        Date now = new Date(System.currentTimeMillis());
        Note note = new Note(1, userId,"Note1", "Content1", now, now);

        Mockito.doThrow(SQLException.class).when(mockNoteDao).addNote(note);

        Response response = target("/notes").request().post(Entity.json(objectMapper.writeValueAsString(note)));

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }
    @Test
    public void testUpdateNote() throws SQLException, JsonProcessingException {
        int userId = 1;
        int noteId = 1;
        Date now = new Date(System.currentTimeMillis());
        Note updatedNote = new Note(noteId, userId,"Updated Note", "Updated Content", now, now);
        String inputJson = objectMapper.writeValueAsString(updatedNote);

        Mockito.when(mockNoteDao.updateNote(noteId, updatedNote)).thenReturn(updatedNote);

        Response response = target("/notes/" + noteId).request().put(Entity.json(inputJson));

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        Note responseNote = response.readEntity(Note.class);
        assertEquals(updatedNote, responseNote);
    }

    @Test
    public void testUpdateNoteNotFound() throws SQLException {
        int userId = 1;
        int noteId = 1;
        Date now = new Date(System.currentTimeMillis());
        Note updatedNote = new Note(noteId, userId,"Updated Note", "Updated Content", now, now);

        Mockito.when(mockNoteDao.updateNote(noteId, updatedNote)).thenReturn(null);

        Response response = target("/notes/" + noteId).request().put(Entity.json(updatedNote));

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void testUpdateNoteWithInvalidInput() {
        int noteId = 1;
        String invalidInput = "{}";

        Response response = target("/notes/" + noteId).request().put(Entity.json(invalidInput));

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void testUpdateNoteServerError() throws SQLException {
        int userId = 1;
        int noteId = 1;
        Date now = new Date(System.currentTimeMillis());
        Note updatedNote = new Note(noteId, userId,"Updated Note", "Updated Content", now, now);

        Mockito.doThrow(SQLException.class).when(mockNoteDao).updateNote(noteId, updatedNote);

        Response response = target("/notes/" + noteId).request().put(Entity.json(updatedNote));

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void testDeleteNote() {
        int userId = 1;
        int noteId = 1;

        Response response = target("/notes/" + noteId + "/user/" + userId).request().delete();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testDeleteNoteNotFound() throws SQLException, AccessDeniedException {
        int userId = 1;
        int noteId = 1;

        Mockito.doThrow(NotFoundException.class).when(mockNoteDao).deleteNote(noteId, userId);

        Response response = target("/notes/" + noteId + "/user/" + userId).request().delete();

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void testDeleteNoteServerError() throws SQLException, AccessDeniedException {
        int userId = 1;
        int noteId = 1;

        Mockito.doThrow(SQLException.class).when(mockNoteDao).deleteNote(noteId, userId);

        Response response = target("/notes/" + noteId + "/user/" + userId).request().delete();

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void testDeleteNoteAccessError() throws SQLException, AccessDeniedException {
        int userId = 1;
        int noteId = 1;

        Mockito.doThrow(AccessDeniedException.class).when(mockNoteDao).deleteNote(noteId, userId);

        Response response = target("/notes/" + noteId + "/user/" + userId).request().delete();

        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
    }

    @Test
    public void testAddLabel() throws SQLException, JsonProcessingException {
        int labelId = 1;
        int noteId = 1;
        NoteLabel noteLabel = new NoteLabel(noteId, labelId);
        String inputJson = objectMapper.writeValueAsString(noteLabel);

        Mockito.when(mockNoteDao.addLabel(Mockito.any(NoteLabel.class))).thenReturn(noteLabel);

        Response response = target("/notes/label").request().post(Entity.json(inputJson));

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

        NoteLabel responseLabel = response.readEntity(NoteLabel.class);
        assertEquals(noteLabel, responseLabel);
    }

    @Test
    public void testAddLabelAccessDeny() throws SQLException, JsonProcessingException {
        int labelId = 1;
        int noteId = 1;
        NoteLabel noteLabel = new NoteLabel(noteId, labelId);
        String inputJson = objectMapper.writeValueAsString(noteLabel);

        Mockito.when(mockNoteDao.addLabel(Mockito.any(NoteLabel.class))).thenReturn(null);

        Response response = target("/notes/label").request().post(Entity.json(inputJson));

        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());

    }

    @Test
    public void testAddLabelWithInvalidInput() {
        String invalidInput = "{}";

        Response response = target("/notes/label").request().post(Entity.json(invalidInput));

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void testAddLabelServerError() throws SQLException, JsonProcessingException {
        int labelId = 1;
        int noteId = 1;
        NoteLabel noteLabel = new NoteLabel(noteId, labelId);

        Mockito.doThrow(SQLException.class).when(mockNoteDao).addLabel(noteLabel);

        Response response = target("/notes/label").request().post(Entity.json(objectMapper.writeValueAsString(noteLabel)));

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGetLabels() throws SQLException, JsonProcessingException, AccessDeniedException {
        int userId = 1;
        int noteId = 1;
        List<NoteLabel> labels = new ArrayList<>();
        labels.add(new NoteLabel(noteId, 1));
        labels.add(new NoteLabel(noteId, 2));

        Mockito.when(mockNoteDao.getLabels(noteId, userId)).thenReturn(labels);

        Response response = target("/notes/" + noteId + "/labels/user/" + userId).request().get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        assertEquals(objectMapper.writeValueAsString(labels), response.readEntity(String.class));
    }

    @Test
    public void testGetLabelsNoLabelsFound() throws SQLException, AccessDeniedException {
        int userId = 1;
        int noteId = 1;
        Mockito.when(mockNoteDao.getLabels(noteId, userId)).thenReturn(new ArrayList<>());

        Response response = target("/notes/" + noteId + "/labels/user/" + userId).request().get();

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGetLabelsServerError() throws SQLException, AccessDeniedException {
        int userId = 1;
        int noteId = 1;
        Mockito.doThrow(SQLException.class).when(mockNoteDao).getLabels(noteId, userId);

        Response response = target("/notes/" + noteId + "/labels/user/" + userId).request().get();

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGetLabelsAccessError() throws SQLException, AccessDeniedException {
        int userId = 1;
        int noteId = 1;
        Mockito.doThrow(AccessDeniedException.class).when(mockNoteDao).getLabels(noteId, userId);

        Response response = target("/notes/" + noteId + "/labels/user/" + userId).request().get();

        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
    }

    @Test
    public void testDeleteLabel() {
        int userId = 1;
        int noteId = 1;
        int labelId = 1;

        Response response = target("/notes/" + noteId + "/label/" + labelId + "/user/" + userId).request().delete();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testDeleteLabelNotFound() throws SQLException, AccessDeniedException {
        int userId = 1;
        int noteId = 1;
        int labelId = 1;

        Mockito.doThrow(NotFoundException.class).when(mockNoteDao).deleteLabel(noteId, labelId, userId);

        Response response = target("/notes/" + noteId + "/label/" + labelId + "/user/" + userId).request().delete();

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void testDeleteLabelServerError() throws SQLException, AccessDeniedException {
        int userId = 1;
        int noteId = 1;
        int labelId = 1;

        Mockito.doThrow(SQLException.class).when(mockNoteDao).deleteLabel(noteId, labelId, userId);

        Response response = target("/notes/" + noteId + "/label/" + labelId + "/user/" + userId).request().delete();

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void testDeleteLabelAccessError() throws SQLException, AccessDeniedException {
        int userId = 1;
        int noteId = 1;
        int labelId = 1;

        Mockito.doThrow(AccessDeniedException.class).when(mockNoteDao).deleteLabel(noteId, labelId, userId);

        Response response = target("/notes/" + noteId + "/label/" + labelId + "/user/" + userId).request().delete();

        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
    }


}

