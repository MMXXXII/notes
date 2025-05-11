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

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    // Геттеры и сеттеры для нового поля
    public String getNoteType() {
        return noteType;
    }

    public void setNoteType(String noteType) {
        this.noteType = noteType;
    }
}
