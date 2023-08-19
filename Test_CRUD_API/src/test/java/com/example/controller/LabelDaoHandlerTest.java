package com.example.controller;

import com.example.models.Label;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class LabelDaoHandlerTest {
    @InjectMocks
    private LabelDaoHandler labelDaoHandler;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    @BeforeEach
    public void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        when(mockConnection.prepareStatement(any(String.class))).thenReturn(mockPreparedStatement);
    }

    @Test
    public void testAddLabel() throws SQLException {
        Label label = new Label();
        label.setUser_id(1);
        label.setContent("Sport");

        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        Label result = labelDaoHandler.addLabel(label);

        assertNotNull(result);
        assertEquals(1, result.getUser_id());
        assertEquals("Sport", result.getContent());
    }

    @Test
    public void testUpdateLabel() throws SQLException {
        int id = 1;
        Label label = new Label();
        label.setUser_id(1);
        label.setContent("updated Sport");

        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        Label result = labelDaoHandler.updateLabel(id, label);

        assertNotNull(result);
        assertEquals(1, result.getUser_id());
        assertEquals("updated Sport", result.getContent());
    }

    @Test
    public void testDeleteLabel() throws SQLException {
        int id = 1;
        int user_id = 1;

        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(id);

        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        Label result = labelDaoHandler.deleteLabel(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(user_id, result.getUser_id());
        assertEquals("updated Sport", result.getContent());
    }

    @Test
    public void testGetLabelById() throws SQLException {
        int id = 1;
        int user_id = 1;
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(id);
        when(mockResultSet.getInt(2)).thenReturn(user_id);
        when(mockResultSet.getString(3)).thenReturn("updated Sport");
        Label result = labelDaoHandler.getLabelById(id, user_id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(user_id, result.getUser_id());
        assertEquals("updated Sport", result.getContent());
    }

    @Test
    public void testGetAllLabels() throws SQLException {
        List<Label> labels = new ArrayList<>();
        Label label1 = new Label();
        label1.setUser_id(1);
        label1.setContent("updated Sport");
        labels.add(label1);

        Label label2 = new Label();
        label2.setUser_id(1);
        label2.setContent("Health");
        labels.add(label2);

        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt(1)).thenReturn(1, 2);
        when(mockResultSet.getInt(2)).thenReturn(1, 1);
        when(mockResultSet.getString(3)).thenReturn("updated Sport", "Health");

        List<Label> result = labelDaoHandler.getAllLabels(1);

        assertNotNull(result);
        assertEquals(labels.size(), result.size());
        assertEquals(labels.get(0).getId(), result.get(0).getId());
        assertEquals(labels.get(1).getId(), result.get(1).getId());
        assertEquals(labels.get(0).getUser_id(), result.get(0).getUser_id());
        assertEquals(labels.get(1).getUser_id(), result.get(1).getUser_id());
        assertEquals(labels.get(0).getContent(), result.get(0).getContent());
        assertEquals(labels.get(1).getContent(), result.get(1).getContent());
    }

}
