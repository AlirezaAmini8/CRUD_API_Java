package com.example.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Label {
    private int id;
    private int user_id;
    private String content;

    public Label(@JsonProperty("id") int id,
                 @JsonProperty("user_id") int user_id,
                 @JsonProperty("content") String content) {
        this.id = id;
        this.user_id = user_id;
        this.content = content;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

