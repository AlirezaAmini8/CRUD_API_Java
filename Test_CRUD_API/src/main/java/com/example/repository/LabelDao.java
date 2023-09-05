package com.example.repository;

import com.example.models.Label;
import com.example.models.NoteLabel;

import javassist.NotFoundException;
import java.nio.file.AccessDeniedException;
import java.sql.SQLException;
import java.util.List;

public interface LabelDao {
    Label addLabel(Label label) throws SQLException;
    Label updateLabel(int id, Label label) throws SQLException;
    void deleteLabel(int id, int userId) throws SQLException, AccessDeniedException, NotFoundException;
    Label getLabelById(int id, int userId) throws SQLException, AccessDeniedException;
    List<Label> getAllLabels(int userId) throws SQLException;
    List<NoteLabel> getNotes(int labelId, int userId) throws SQLException, AccessDeniedException;
}
