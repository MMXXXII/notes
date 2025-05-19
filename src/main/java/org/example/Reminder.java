package org.example;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Reminder {
    private int id;
    private int userId;
    private String title;
    private String description;
    private LocalDate reminderDate;
    private boolean completed;
    private String createdAt;

    // Конструктор
    public Reminder(int id, int userId, String title, String description, LocalDate reminderDate, boolean completed, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.reminderDate = reminderDate;
        this.completed = completed;
        this.createdAt = createdAt;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(LocalDate reminderDate) {
        this.reminderDate = reminderDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    // Форматированная дата для отображения
    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return reminderDate.format(formatter);
    }

    @Override
    public String toString() {
        return title;
    }
}