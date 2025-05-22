package org.example;

// Импортируем необходимые классы для работы с базой данных, датами и коллекциями
import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Класс для взаимодействия с таблицей reminders в базе данных
public class DatabaseHandlerReminders {

    // Метод для добавления нового напоминания в базу данных
    public static void addReminder(int userId, String title, String description, LocalDate reminderDate) throws SQLException {
        String query = "INSERT INTO reminders (user_id, title, description, reminder_date) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseHandler.getConnection(); // Получаем соединение
             PreparedStatement pstmt = conn.prepareStatement(query)) { // Подготавливаем SQL-запрос

            pstmt.setInt(1, userId); // Устанавливаем ID пользователя
            pstmt.setString(2, title); // Заголовок напоминания
            pstmt.setString(3, description); // Описание
            pstmt.setDate(4, java.sql.Date.valueOf(reminderDate)); // Дата напоминания

            pstmt.executeUpdate(); // Выполняем запрос
        }
    }

    // Метод для получения всех напоминаний пользователя
    public static List<Reminder> getReminders(int userId) throws SQLException {
        List<Reminder> reminders = new ArrayList<>(); // Список для хранения напоминаний
        String query = "SELECT * FROM reminders WHERE user_id = ? ORDER BY reminder_date";

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId); // Задаем ID пользователя

            try (ResultSet rs = pstmt.executeQuery()) { // Выполняем запрос и обрабатываем результат
                while (rs.next()) {
                    Reminder reminder = new Reminder( // Создаем объект напоминания
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getDate("reminder_date").toLocalDate(), // Преобразуем SQL-даты в LocalDate
                            rs.getBoolean("is_completed"),
                            rs.getTimestamp("created_at").toString() // Преобразуем временную метку в строку
                    );
                    reminders.add(reminder); // Добавляем в список
                }
            }
        }

        return reminders; // Возвращаем список
    }

    // Метод для получения всех напоминаний пользователя за конкретный месяц
    public static Map<LocalDate, List<Reminder>> getRemindersForMonth(int userId, YearMonth yearMonth) throws SQLException {
        Map<LocalDate, List<Reminder>> reminderMap = new HashMap<>(); // Карта: дата → список напоминаний

        LocalDate startDate = yearMonth.atDay(1); // Первый день месяца
        LocalDate endDate = yearMonth.atEndOfMonth(); // Последний день месяца

        String query = "SELECT * FROM reminders WHERE user_id = ? AND reminder_date BETWEEN ? AND ? ORDER BY reminder_date";

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            pstmt.setDate(2, java.sql.Date.valueOf(startDate));
            pstmt.setDate(3, java.sql.Date.valueOf(endDate));

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    LocalDate date = rs.getDate("reminder_date").toLocalDate(); // Дата напоминания

                    Reminder reminder = new Reminder( // Создаем объект напоминания
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            date,
                            rs.getBoolean("is_completed"),
                            rs.getTimestamp("created_at").toString()
                    );

                    // Если даты ещё нет в карте, создаем новую запись
                    if (!reminderMap.containsKey(date)) {
                        reminderMap.put(date, new ArrayList<>());
                    }

                    // Добавляем напоминание к соответствующей дате
                    reminderMap.get(date).add(reminder);
                }
            }
        }

        return reminderMap; // Возвращаем карту напоминаний
    }

    // Метод для обновления существующего напоминания
    public static void updateReminder(int reminderId, String title, String description, LocalDate reminderDate, boolean completed) throws SQLException {
        String query = "UPDATE reminders SET title = ?, description = ?, reminder_date = ?, is_completed = ? WHERE id = ?";

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, title);
            pstmt.setString(2, description);
            pstmt.setDate(3, java.sql.Date.valueOf(reminderDate));
            pstmt.setBoolean(4, completed); // Устанавливаем статус выполнения
            pstmt.setInt(5, reminderId); // ID напоминания для обновления

            pstmt.executeUpdate(); // Выполняем обновление
        }
    }

    // Метод для удаления напоминания по ID
    public static void deleteReminder(int reminderId) throws SQLException {
        String query = "DELETE FROM reminders WHERE id = ?";

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, reminderId); // Устанавливаем ID напоминания
            pstmt.executeUpdate(); // Выполняем удаление
        }
    }
}
