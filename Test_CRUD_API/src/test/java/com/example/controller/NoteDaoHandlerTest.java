package com.example.controller;

import com.example.models.Note;
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

public class NoteDaoHandlerTest {

    @InjectMocks
    private NoteDaoHandler noteDaoHandler;

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
    public void testAddNote() throws SQLException {
        Note note = new Note();
        note.setUser_id(1);
        note.setContent("This is just reminder.");
        note.setTitle("reminder");
        Date now = new Date(System.currentTimeMillis());
        note.setCreated_at(now);
        note.setModified_at(now);

        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        Note result = noteDaoHandler.addNote(note);

        assertNotNull(result);
        assertEquals(1, result.getUser_id());
        assertEquals("This is just reminder.", result.getContent());
        assertEquals("reminder", result.getTitle());
    }

    @Test
    public void testUpdateNote() throws SQLException {
        int id = 1;
        Note note = new Note();
        note.setUser_id(1);
        note.setContent("This is just updated reminder.");
        note.setTitle("updated reminder");
        Date now = new Date(System.currentTimeMillis());
        note.setCreated_at(now);
        note.setModified_at(now);

        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        Note result = noteDaoHandler.updateNote(id, note);

        assertNotNull(result);
        assertEquals(1, result.getUser_id());
        assertEquals("This is just updated reminder.", result.getContent());
        assertEquals("updated reminder", result.getTitle());
    }

    @Test
    public void testDeleteNote() throws SQLException {
        int id = 1;
        int user_id = 1;
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(id);
        when(mockResultSet.getInt(2)).thenReturn(user_id);
        when(mockResultSet.getString(3)).thenReturn("updated reminder");
        when(mockResultSet.getString(4)).thenReturn("This is just updated reminder.");

        Note result = noteDaoHandler.deleteNote(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(user_id, result.getUser_id());
        assertEquals("updated reminder", result.getTitle());
        assertEquals("This is just updated reminder.", result.getContent());
    }

    @Test
    public void testGetNoteById() throws SQLException {
        int id = 1;
        int user_id = 1;
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(id);
        when(mockResultSet.getInt(2)).thenReturn(user_id);
        when(mockResultSet.getString(3)).thenReturn("updated reminder");
        when(mockResultSet.getString(4)).thenReturn("This is just updated reminder.");

        Note result = noteDaoHandler.getNoteById(id, user_id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(user_id, result.getUser_id());
        assertEquals("updated reminder", result.getTitle());
        assertEquals("This is just updated reminder.", result.getContent());
    }

    @Test
    public void testGetAllNotes() throws SQLException {
        List<Note> notes = new ArrayList<>();
        Note note1 = new Note();
        note1.setUser_id(1);
        note1.setContent("I should visit doctor.");
        note1.setTitle("Health");
        Date now1 = new Date(System.currentTimeMillis());
        note1.setCreated_at(now1);
        note1.setModified_at(now1);
        notes.add(note1);

        Note note2 = new Note();
        note2.setUser_id(1);
        note2.setContent("This is just updated reminder.");
        note2.setTitle("updated reminder");
        Date now2 = new Date(System.currentTimeMillis());
        note2.setCreated_at(now2);
        note2.setModified_at(now2);
        notes.add(note2);

        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt(1)).thenReturn(1, 2);
        when(mockResultSet.getInt(2)).thenReturn(1, 1);
        when(mockResultSet.getString(3)).thenReturn("Health", "updated reminder");
        when(mockResultSet.getString(4)).thenReturn("I should visit doctor.", "This is just updated reminder.");

        List<Note> result = noteDaoHandler.getAllNotes(1);

        assertNotNull(result);
        assertEquals(notes.size(), result.size());
        assertEquals(notes.get(0).getId(), result.get(0).getId());
        assertEquals(notes.get(1).getId(), result.get(1).getId());
        assertEquals(notes.get(0).getUser_id(), result.get(0).getUser_id());
        assertEquals(notes.get(1).getUser_id(), result.get(1).getUser_id());
        assertEquals(notes.get(0).getTitle(), result.get(0).getTitle());
        assertEquals(notes.get(1).getTitle(), result.get(1).getTitle());
        assertEquals(notes.get(0).getContent(), result.get(0).getContent());
        assertEquals(notes.get(1).getContent(), result.get(1).getContent());
    }

}
