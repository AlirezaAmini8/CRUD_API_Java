package com.example.controller;

import com.example.models.Label;
import com.example.models.NoteLabel;
import com.example.repository.LabelDao;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;
import org.mockito.Mockito;

import javassist.NotFoundException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import java.nio.file.AccessDeniedException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class LabelResourceTest extends JerseyTest {

    private LabelDao mockLabelDao;
    private ObjectMapper objectMapper;

    @Override
    protected Application configure() {
        mockLabelDao = Mockito.mock(LabelDao.class);
        objectMapper = new ObjectMapper();
        return new ResourceConfig()
                .packages("com.example.controller")
                .register(new LabelResource(mockLabelDao, objectMapper));
    }

    @Test
    public void testGetAllLabels() throws SQLException, JsonProcessingException {
        int userId = 1;
        List<Label> labels = new ArrayList<>();
        labels.add(new Label(1, userId, "Label1"));
        labels.add(new Label(2, userId, "Label2"));

        Mockito.when(mockLabelDao.getAllLabels(Mockito.eq(userId))).thenReturn(labels);

        Response response = target("/labels/user/" + userId).request().get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        assertEquals(objectMapper.writeValueAsString(labels), response.readEntity(String.class));
    }

    @Test
    public void testGetAllLabelsNoLabelsFound() throws SQLException {
        int userId = 1;
        Mockito.when(mockLabelDao.getAllLabels(userId)).thenReturn(new ArrayList<>());

        Response response = target("/labels/user/" + userId).request().get();

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGetAllLabelsServerError() throws SQLException {
        int userId = 1;
        Mockito.doThrow(SQLException.class).when(mockLabelDao).getAllLabels(userId);

        Response response = target("/labels/user/" + userId).request().get();

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGetLabelById() throws SQLException, JsonProcessingException, AccessDeniedException {
        int userId = 1;
        int labelId = 1;
        Label label = new Label(labelId, userId, "Label1");

        Mockito.when(mockLabelDao.getLabelById(Mockito.eq(labelId), Mockito.eq(userId))).thenReturn(label);

        Response response = target("/labels/" + labelId + "/user/" + userId).request().get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        assertEquals(objectMapper.writeValueAsString(label), response.readEntity(String.class));
    }

    @Test
    public void testGetLabelByIdNotFound() throws SQLException, AccessDeniedException {
        int userId = 1;
        int labelId = 1;
        Mockito.when(mockLabelDao.getLabelById(Mockito.eq(labelId), Mockito.eq(userId))).thenReturn(null);

        Response response = target("/labels/" + labelId + "/user/" + userId).request().get();

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }
    @Test
    public void testGetLabelByIdAccessError() throws SQLException, AccessDeniedException {
        int userId = 1;
        int labelId = 1;
        Mockito.doThrow(AccessDeniedException.class).when(mockLabelDao).getLabelById(labelId, userId);

        Response response = target("/labels/" + labelId + "/user/" + userId).request().get();

        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
    }
    @Test
    public void testGetLabelByIdServerError() throws SQLException, AccessDeniedException {
        int userId = 1;
        int labelId = 1;
        Mockito.doThrow(SQLException.class).when(mockLabelDao).getLabelById(labelId, userId);

        Response response = target("/labels/" + labelId + "/user/" + userId).request().get();

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void testCreateLabel() throws SQLException, JsonProcessingException {
        int userId = 1;
        Label label = new Label(1, userId, "Label1");
        String inputJson = objectMapper.writeValueAsString(label);

        Mockito.when(mockLabelDao.addLabel(Mockito.any(Label.class))).thenReturn(label);

        Response response = target("/labels").request().post(Entity.json(inputJson));

        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

        assertEquals(inputJson, response.readEntity(String.class));
    }

    @Test
    public void testCreateLabelWithInvalidInput() {
        String invalidInput = "wrong input";

        Response response = target("/labels").request().post(Entity.json(invalidInput));

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void testCreateLabelServerError() throws SQLException, JsonProcessingException {
        int userId = 1;
        Label label = new Label(1, userId, "Label1");

        Mockito.doThrow(SQLException.class).when(mockLabelDao).addLabel(Mockito.any(Label.class));

        Response response = target("/labels").request().post(Entity.json(objectMapper.writeValueAsString(label)));

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void testUpdateLabel() throws SQLException, JsonProcessingException {
        int userId = 1;
        int labelId = 1;
        Label label = new Label(labelId, userId, "UpdatedLabel");
        String inputJson = objectMapper.writeValueAsString(label);

        Mockito.when(mockLabelDao.updateLabel(Mockito.eq(labelId), Mockito.any(Label.class))).thenReturn(label);

        Response response = target("/labels/" + labelId).request().put(Entity.json(inputJson));

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        assertEquals(inputJson, response.readEntity(String.class));
    }

    @Test
    public void testUpdateLabelNotFound() throws SQLException, JsonProcessingException {
        int userId = 1;
        int labelId = 1;
        Label label = new Label(labelId, userId, "UpdatedLabel");
        String inputJson = objectMapper.writeValueAsString(label);

        Mockito.when(mockLabelDao.updateLabel(Mockito.eq(labelId), Mockito.any(Label.class))).thenReturn(null);

        Response response = target("/labels/" + labelId).request().put(Entity.json(inputJson));

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void testUpdateLabelInvalidInput() {
        int labelId = 1;
        String invalidInput = "wrong input";

        Response response = target("/labels/" + labelId).request().put(Entity.json(invalidInput));

        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void testUpdateLabelSQLException() throws SQLException, JsonProcessingException {
        int userId = 1;
        int labelId = 1;
        Label label = new Label(labelId, userId, "UpdatedLabel");
        String inputJson = objectMapper.writeValueAsString(label);

        Mockito.doThrow(SQLException.class).when(mockLabelDao).updateLabel(Mockito.eq(labelId), Mockito.any(Label.class));

        Response response = target("/labels/" + labelId).request().put(Entity.json(inputJson));

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void testDeleteLabel() {
        int userId = 1;
        int labelId = 1;

        Response response = target("/labels/" + labelId + "/user/" + userId).request().delete();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void testDeleteLabelNotFound() throws SQLException, AccessDeniedException, NotFoundException {
        int userId = 1;
        int labelId = 1;
        Mockito.doThrow(NotFoundException.class).when(mockLabelDao).deleteLabel(labelId, userId);

        Response response = target("/labels/" + labelId + "/user/" + userId).request().delete();

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void testDeleteLabelAccessError() throws SQLException, AccessDeniedException, NotFoundException {
        int userId = 1;
        int labelId = 1;

        Mockito.doThrow(AccessDeniedException.class).when(mockLabelDao).deleteLabel(labelId, userId);

        Response response = target("/labels/" + labelId + "/user/" + userId).request().delete();

        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
    }
    @Test
    public void testDeleteLabelSQLException() throws SQLException, AccessDeniedException, NotFoundException {
        int userId = 1;
        int labelId = 1;

        Mockito.doThrow(SQLException.class).when(mockLabelDao).deleteLabel(labelId, userId);

        Response response = target("/labels/" + labelId + "/user/" + userId).request().delete();

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGetNotes() throws SQLException, JsonProcessingException, AccessDeniedException {
        int userId = 1;
        int labelId = 1;
        List<NoteLabel> noteLabels = new ArrayList<>();
        noteLabels.add(new NoteLabel(1, labelId));
        noteLabels.add(new NoteLabel(2, labelId));

        Mockito.when(mockLabelDao.getNotes(Mockito.eq(labelId), Mockito.eq(userId))).thenReturn(noteLabels);

        Response response = target("/labels/" + labelId + "/notes/user/" + userId).request().get();

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        assertEquals(objectMapper.writeValueAsString(noteLabels), response.readEntity(String.class));
    }

    @Test
    public void testGetNotesNoNotesFound() throws SQLException, AccessDeniedException {
        int userId = 1;
        int labelId = 1;
        Mockito.when(mockLabelDao.getNotes(labelId, userId)).thenReturn(new ArrayList<>());

        Response response = target("/labels/" + labelId + "/notes/user/" + userId).request().get();

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGetNotesServerError() throws SQLException, AccessDeniedException {
        int userId = 1;
        int labelId = 1;
        Mockito.doThrow(SQLException.class).when(mockLabelDao).getNotes(labelId, userId);

        Response response = target("/labels/" + labelId + "/notes/user/" + userId).request().get();

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
    }

    @Test
    public void testGetNotesAccessError() throws SQLException, AccessDeniedException {
        int userId = 1;
        int labelId = 1;
        Mockito.doThrow(AccessDeniedException.class).when(mockLabelDao).getNotes(labelId, userId);

        Response response = target("/labels/" + labelId + "/notes/user/" + userId).request().get();

        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
    }

}
