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
        int user_id = 2;
        Label label = new Label();
        label.setUser_id(user_id);
        label.setContent("Outside");

        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        Label result = labelDaoHandler.addLabel(label);

        assertNotNull(result);
        assertEquals(user_id, result.getUser_id());
        assertEquals("Outside", result.getContent());
    }

    @Test
    public void testUpdateLabel() throws SQLException {
        int id = 1;
        int user_id = 3;
        Label label = new Label();
        label.setUser_id(user_id);
        label.setContent("updated Sport");

        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        Label result = labelDaoHandler.updateLabel(id, label);

        assertNotNull(result);
        assertEquals(user_id, result.getUser_id());
        assertEquals("updated Sport", result.getContent());
    }

    @Test
    public void testDeleteLabel() throws SQLException {
        int id = 3;

        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(id);

        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        Label result = labelDaoHandler.deleteLabel(id);

        assertNotNull(result);
    }

    @Test
    public void testGetLabelById() throws SQLException {
        int id = 1;
        int user_id = 3;
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(id);
        when(mockResultSet.getInt(2)).thenReturn(user_id);
        when(mockResultSet.getString(3)).thenReturn("updated Sport");
        Label result = labelDaoHandler.getLabelById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(user_id, result.getUser_id());
        assertEquals("updated Sport", result.getContent());
    }

    @Test
    public void testGetAllLabels() throws SQLException {
        int user_id = 2;
        List<Label> labels = new ArrayList<>();
        /*Label label1 = new Label();
        label1.setId(1);
        label1.setUser_id(user_id);
        label1.setContent("updated Sport");
        labels.add(label1);*/

        Label label2 = new Label();
        label2.setId(2);
        label2.setUser_id(user_id);
        label2.setContent("Shop");
        labels.add(label2);

        Label label3 = new Label();
        label3.setId(4);
        label3.setUser_id(user_id);
        label3.setContent("Outside");
        labels.add(label3);

        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt(1)).thenReturn(2, 4);
        when(mockResultSet.getInt(2)).thenReturn( 2, 2);
        when(mockResultSet.getString(3)).thenReturn("Shop", "Outside");

        List<Label> result = labelDaoHandler.getAllLabels(user_id);

        assertNotNull(result);
        assertEquals(labels.size(), result.size());
        assertEquals(labels.get(0).getId(), result.get(0).getId());
        assertEquals(labels.get(1).getId(), result.get(1).getId());
        //assertEquals(labels.get(2).getId(), result.get(2).getId());
        assertEquals(labels.get(0).getUser_id(), result.get(0).getUser_id());
        assertEquals(labels.get(1).getUser_id(), result.get(1).getUser_id());
        //assertEquals(labels.get(2).getUser_id(), result.get(2).getUser_id());
        assertEquals(labels.get(0).getContent(), result.get(0).getContent());
        assertEquals(labels.get(1).getContent(), result.get(1).getContent());
        //assertEquals(labels.get(2).getContent(), result.get(2).getContent());
    }

}
