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

    public String getTitle() {
        return title;
    }


    public String getDescription() {
        return description;
    }


    public LocalDate getReminderDate() {
        return reminderDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    @Override
    public String toString() {
        return title;
    }
}