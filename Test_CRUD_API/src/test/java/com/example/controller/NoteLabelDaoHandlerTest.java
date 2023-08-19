package com.example.controller;


import com.example.models.NoteLabel;
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

public class NoteLabelDaoHandlerTest {
    @InjectMocks
    private NoteLabelDaoHandler noteLabelDaoHandler;

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
    public void testAddNoteLabel() throws SQLException {
        NoteLabel noteLabel = new NoteLabel();
        noteLabel.setNote_id(1);
        noteLabel.setLabel_id(1);


        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        NoteLabel result = noteLabelDaoHandler.addNoteLabel(noteLabel);

        assertNotNull(result);
        assertEquals(1, result.getNote_id());
        assertEquals(1, result.getLabel_id());
    }

    @Test
    public void testDeleteNoteLabel() throws SQLException {
        int note_id = 1;
        int label_id = 1;
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(note_id);
        when(mockResultSet.getInt(2)).thenReturn(label_id);

        NoteLabel result = noteLabelDaoHandler.deleteNoteLabel(note_id, label_id);

        assertNotNull(result);
        assertEquals(note_id, result.getNote_id());
        assertEquals(label_id, result.getLabel_id());

    }

    @Test
    public void testGetNoteLabelsForNote() throws SQLException {
        int note_id = 1;
        List<NoteLabel> noteLabels = new ArrayList<>();
        NoteLabel noteLabel1 = new NoteLabel();
        noteLabel1.setNote_id(1);
        noteLabel1.setLabel_id(1);
        noteLabels.add(noteLabel1);

        NoteLabel noteLabel2 = new NoteLabel();
        noteLabel2.setNote_id(1);
        noteLabel2.setLabel_id(2);
        noteLabels.add(noteLabel2);

        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt(1)).thenReturn(1, 1);
        when(mockResultSet.getInt(2)).thenReturn(1, 2);

        List<NoteLabel> result = noteLabelDaoHandler.getNoteLabelsForNote(note_id);

        assertNotNull(result);
        assertEquals(noteLabels.size(), result.size());
        assertEquals(noteLabels.get(0).getNote_id(), result.get(0).getNote_id());
        assertEquals(noteLabels.get(1).getNote_id(), result.get(1).getNote_id());
        assertEquals(noteLabels.get(0).getLabel_id(), result.get(0).getLabel_id());
        assertEquals(noteLabels.get(1).getLabel_id(), result.get(1).getLabel_id());
    }

    @Test
    public void testGetNoteLabelsForLabel() throws SQLException {
        int label_id = 1;
        List<NoteLabel> noteLabels = new ArrayList<>();
        NoteLabel noteLabel1 = new NoteLabel();
        noteLabel1.setNote_id(1);
        noteLabel1.setLabel_id(1);
        noteLabels.add(noteLabel1);

        NoteLabel noteLabel2 = new NoteLabel();
        noteLabel2.setNote_id(2);
        noteLabel2.setLabel_id(1);
        noteLabels.add(noteLabel2);

        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt(1)).thenReturn(1, 2);
        when(mockResultSet.getInt(2)).thenReturn(1, 1);

        List<NoteLabel> result = noteLabelDaoHandler.getNoteLabelsForLabel(label_id);

        assertNotNull(result);
        assertEquals(noteLabels.size(), result.size());
        assertEquals(noteLabels.get(0).getNote_id(), result.get(0).getNote_id());
        assertEquals(noteLabels.get(1).getNote_id(), result.get(1).getNote_id());
        assertEquals(noteLabels.get(0).getLabel_id(), result.get(0).getLabel_id());
        assertEquals(noteLabels.get(1).getLabel_id(), result.get(1).getLabel_id());
    }
}
