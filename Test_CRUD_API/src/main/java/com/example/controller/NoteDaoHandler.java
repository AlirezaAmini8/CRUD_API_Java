package com.example.controller;

import com.example.models.Note;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NoteDaoHandler {
    public Note addNote(Note note) throws SQLException {
        Connection connect = DatabaseConnection.getConnection();

        PreparedStatement preparedStatement
                = connect.prepareStatement(
                "insert into Note(user_id,title,content,created_at,modified_at) values (?,?,?,?,?)");

        preparedStatement.setInt(1, note.getUser_id());
        preparedStatement.setString(2, note.getTitle());
        preparedStatement.setString(3, note.getContent());
        preparedStatement.setDate(4, note.getCreated_at());
        preparedStatement.setDate(5, note.getModified_at());

        int affectedRows = preparedStatement.executeUpdate();
        if (affectedRows == 0) {
            throw new SQLException("Creating note failed, no rows affected.");
        }

        System.out.println("note inserted");

        connect.close();

        return note;
    }
    public Note updateNote(int id, Note note) throws SQLException {
        Connection connect = DatabaseConnection.getConnection();

        PreparedStatement preparedStatement
                = connect.prepareStatement(
                "update Note set title=?,content=?,modified_at=? where id=?");

        preparedStatement.setString(1, note.getTitle());
        preparedStatement.setString(2, note.getContent());
        preparedStatement.setDate(3, note.getModified_at());
        preparedStatement.setInt(4, id);

        int affectedRows = preparedStatement.executeUpdate();
        if (affectedRows == 0) {
            throw new SQLException("Updating note failed, no rows affected.");
        }

        System.out.println("note updated");

        connect.close();

        return note;
    }
    public void deleteNote(int id) throws SQLException
    {
        Connection connect = DatabaseConnection.getConnection();

        PreparedStatement preparedStatement
                = connect.prepareStatement(
                "delete from Note where id =?");

        preparedStatement.setInt(1, id);

        int affectedRows = preparedStatement.executeUpdate();
        if (affectedRows == 0) {
            throw new SQLException("Deleting note failed, no rows affected.");
        }
        System.out.printf("note with %s deleted \n",id);

        connect.close();
    }
    public  Note getNoteById(int id) throws SQLException {
        Note note = new Note();

        Connection connect = DatabaseConnection.getConnection();

        PreparedStatement preparedStatement
                = connect.prepareStatement(
                "select * from Note where id=?");

        preparedStatement.setInt(1, id);

        ResultSet resultSet
                = preparedStatement.executeQuery();

        if (resultSet.next()) {
            note.setId(resultSet.getInt(1));
            note.setUser_id(resultSet.getInt(2));
            note.setTitle(resultSet.getString(3));
            note.setContent(resultSet.getString(4));
            note.setCreated_at(resultSet.getDate(5));
            note.setModified_at(resultSet.getDate(6));
        }
        connect.close();

        return note;
    }
    public List<Note> getAllNotes() throws SQLException {

        List<Note> notes = new ArrayList<Note>();

        Connection connect = DatabaseConnection.getConnection();

        PreparedStatement preparedStatement
                = connect.prepareStatement(
                "select * from Note");
        ResultSet resultSet
                = preparedStatement.executeQuery();

        while (resultSet.next()) {
            Note note = new Note();
            note.setId(resultSet.getInt(1));
            note.setUser_id(resultSet.getInt(2));
            note.setTitle(resultSet.getString(3));
            note.setContent(resultSet.getString(4));
            note.setCreated_at(resultSet.getDate(5));
            note.setModified_at(resultSet.getDate(6));
            // store the values into the list
            notes.add(note);
        }

        connect.close();

        return notes;
    }
}

