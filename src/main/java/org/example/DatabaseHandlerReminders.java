package org.example;

import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHandlerReminders {

    // Добавление нового напоминания
    public static void addReminder(int userId, String title, String description, LocalDate reminderDate) throws SQLException {
        String query = "INSERT INTO reminders (user_id, title, description, reminder_date) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, title);
            pstmt.setString(3, description);
            pstmt.setDate(4, java.sql.Date.valueOf(reminderDate));

            pstmt.executeUpdate();
        }
    }

    // Получение всех напоминаний пользователя
    public static List<Reminder> getReminders(int userId) throws SQLException {
        List<Reminder> reminders = new ArrayList<>();
        String query = "SELECT * FROM reminders WHERE user_id = ? ORDER BY reminder_date";

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Reminder reminder = new Reminder(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getDate("reminder_date").toLocalDate(),
                            rs.getBoolean("is_completed"),
                            rs.getTimestamp("created_at").toString()
                    );
                    reminders.add(reminder);
                }
            }
        }

        return reminders;
    }

    // Получение напоминаний пользователя за определенный месяц
    public static Map<LocalDate, List<Reminder>> getRemindersForMonth(int userId, YearMonth yearMonth) throws SQLException {
        Map<LocalDate, List<Reminder>> reminderMap = new HashMap<>();

        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        String query = "SELECT * FROM reminders WHERE user_id = ? AND reminder_date BETWEEN ? AND ? ORDER BY reminder_date";

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            pstmt.setDate(2, java.sql.Date.valueOf(startDate));
            pstmt.setDate(3, java.sql.Date.valueOf(endDate));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    LocalDate date = rs.getDate("reminder_date").toLocalDate();

                    Reminder reminder = new Reminder(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            date,
                            rs.getBoolean("is_completed"),
                            rs.getTimestamp("created_at").toString()
                    );

                    if (!reminderMap.containsKey(date)) {
                        reminderMap.put(date, new ArrayList<>());
                    }

                    reminderMap.get(date).add(reminder);
                }
            }
        }

        return reminderMap;
    }

    // Обновление напоминания
    public static void updateReminder(int reminderId, String title, String description, LocalDate reminderDate, boolean completed) throws SQLException {
        String query = "UPDATE reminders SET title = ?, description = ?, reminder_date = ?, is_completed = ? WHERE id = ?";

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, title);
            pstmt.setString(2, description);
            pstmt.setDate(3, java.sql.Date.valueOf(reminderDate));
            pstmt.setBoolean(4, completed);
            pstmt.setInt(5, reminderId);

            pstmt.executeUpdate();
        }
    }

    // Удаление напоминания
    public static void deleteReminder(int reminderId) throws SQLException {
        String query = "DELETE FROM reminders WHERE id = ?";

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, reminderId);
            pstmt.executeUpdate();
        }
    }
}