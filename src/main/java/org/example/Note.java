package org.example;

import java.sql.Timestamp;

public class Note {
    private int id;
    private int userId;
    private String title;
    private String content;
    private Timestamp createdAt;
    private String noteType; // Новое поле для типа заметки

    // Конструктор
    public Note(int id, int userId, String title, String content, Timestamp createdAt, String noteType) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.noteType = noteType;
    }


    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public String getNoteType() {
        return noteType;
    }

}
