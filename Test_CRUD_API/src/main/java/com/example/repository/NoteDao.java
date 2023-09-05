package com.example.repository;

import com.example.models.Note;
import com.example.models.NoteLabel;

import javassist.NotFoundException;
import java.nio.file.AccessDeniedException;
import java.sql.SQLException;
import java.util.List;

public interface NoteDao {
    Note addNote(Note note) throws SQLException;
    Note updateNote(int id, Note note) throws SQLException;
    void deleteNote(int id, int userId) throws SQLException, AccessDeniedException, NotFoundException;
    Note getNoteById(int id, int userId) throws SQLException, AccessDeniedException;
    List<Note> getAllNotes(int userId) throws SQLException;
    NoteLabel addLabel(NoteLabel noteLabel) throws SQLException;
    List<NoteLabel> getLabels(int noteId, int userId) throws SQLException, AccessDeniedException;
    void deleteLabel(int noteId, int labelId, int userId) throws SQLException, AccessDeniedException, NotFoundException;
}
