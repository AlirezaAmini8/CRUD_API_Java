package com.example.controller;

import com.example.models.Note;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NoteDaoHandler {

    public Note addNote(Note note) {

        try(Connection connect = DatabaseConnection.getConnection()) {

            PreparedStatement preparedStatement
                    = connect.prepareStatement(
                    "insert into \"Note\"(user_id,title,content,created_at,modified_at) values (?,?,?,?,?)");

            preparedStatement.setInt(1, note.getUser_id());
            preparedStatement.setString(2, note.getTitle());
            preparedStatement.setString(3, note.getContent());
            Date now = new Date(System.currentTimeMillis());
            note.setCreated_at(now);
            note.setModified_at(now);
            preparedStatement.setDate(4, now);
            preparedStatement.setDate(5, now);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating note failed, no rows affected.");
            }

            System.out.println("note inserted");
        }catch (SQLException e) {
            e.printStackTrace();
        }

        return note;
    }
    public Note updateNote(int id, Note note) {
        try (Connection connect = DatabaseConnection.getConnection()) {

            PreparedStatement preparedStatement
                    = connect.prepareStatement(
                    "update \"Note\" set title=?,content=?,modified_at=? where id=?");

            preparedStatement.setString(1, note.getTitle());
            preparedStatement.setString(2, note.getContent());
            Date now = new Date(System.currentTimeMillis());
            note.setModified_at(now);
            preparedStatement.setDate(3, now);
            preparedStatement.setInt(4, id);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                return null;
            }

            System.out.println("note updated");

            // todo: you may need to return updated note not previous note -> you need query
        }catch (SQLException e) {
            e.printStackTrace();
        }

        return note;
    }
    public Note deleteNote(int id) {
        Note note = new Note();
        try (Connection connect = DatabaseConnection.getConnection()) {

            PreparedStatement preparedStatement
                    = connect.prepareStatement(
                    "delete from \"Note\" where id =?");

            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                return null;
            }else {
                note.setId(resultSet.getInt(1));
                note.setUser_id(resultSet.getInt(2));
                note.setTitle(resultSet.getString(3));
                note.setContent(resultSet.getString(4));
                note.setCreated_at(resultSet.getDate(5));
                note.setModified_at(resultSet.getDate(6));
                System.out.printf("note with id = %s deleted \n", id);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return note;
    }
    public  Note getNoteById(int id, int userId) {
        Note note = null;

        try(Connection connect = DatabaseConnection.getConnection()) {

            PreparedStatement preparedStatement
                    = connect.prepareStatement(
                    "select * from \"Note\" where id=? and user_id = ?");

            preparedStatement.setInt(1, id);
            preparedStatement.setInt(2, userId);

            ResultSet resultSet
                    = preparedStatement.executeQuery();

            if (resultSet.next()) {
                note = new Note();
                note.setId(resultSet.getInt(1));
                note.setUser_id(resultSet.getInt(2));
                note.setTitle(resultSet.getString(3));
                note.setContent(resultSet.getString(4));
                note.setCreated_at(resultSet.getDate(5));
                note.setModified_at(resultSet.getDate(6));
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }

        return note;
    }
    public List<Note> getAllNotes(int userId) {

        List<Note> notes = new ArrayList<Note>();

        try(Connection connect = DatabaseConnection.getConnection()) {

            PreparedStatement preparedStatement
                    = connect.prepareStatement(
                    "select * from \"Note\" where user_id = ?");

            preparedStatement.setInt(1, userId);

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
        }catch (SQLException e) {
            e.printStackTrace();
        }

        return notes;
    }
}

