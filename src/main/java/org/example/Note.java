package org.example;

public class Note {
    private int id;
    private int userId;
    private String title;
    private String content;
    private String noteType;
    private String createdAt;
    private boolean isDeleted;
    private String deletedAt;

    // Конструктор
    public Note(int id, int userId, String title, String content, String noteType, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.noteType = noteType;
        this.createdAt = createdAt;
        this.isDeleted = false;
        this.deletedAt = null;
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

    public String getNoteType() {
        return noteType;
    }

    public void setNoteType(String noteType) {
        this.noteType = noteType;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    // Получить количество дней с момента удаления
    public int getDaysSinceDeletion() {
        if (deletedAt == null) {
            return 0;
        }
        return DatabaseHandlerTrash.getDaysSinceDeletion(deletedAt);
    }
}