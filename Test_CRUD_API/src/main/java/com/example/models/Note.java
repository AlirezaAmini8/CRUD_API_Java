package com.example.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Date;

public class Note {
    private int id;
    private int user_id;
    private String title;
    private String content;
    private Date created_at;
    private Date modified_at;

    public Note(@JsonProperty("id") int id,
                @JsonProperty("user_id") int user_id,
                @JsonProperty("title") String title,
                @JsonProperty("content") String content,
                @JsonProperty("created_at") Date created_at,
                @JsonProperty("modified_at") Date modified_at) {
        this.id = id;
        this.user_id = user_id;
        this.title = title;
        this.content = content;
        this.created_at = created_at;
        this.modified_at = modified_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public Date getModified_at() {
        return modified_at;
    }

    public void setModified_at(Date modified_at) {
        this.modified_at = modified_at;
    }
}

