package com.example.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NoteLabel {
    private int note_id;
    private int label_id;

    public NoteLabel(@JsonProperty("note_id") int note_id,
                     @JsonProperty("label_id") int label_id) {
        this.note_id = note_id;
        this.label_id = label_id;
    }

    public int getNote_id() {
        return note_id;
    }

    public void setNote_id(int note_id) {
        this.note_id = note_id;
    }

    public int getLabel_id() {
        return label_id;
    }

    public void setLabel_id(int label_id) {
        this.label_id = label_id;
    }
}

