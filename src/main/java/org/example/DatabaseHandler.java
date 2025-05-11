package org.example;

import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {
    private static final String URL = "jdbc:postgresql://localhost:5432/keep";
    private static final String USER = "postgres";  // Замените на свой логин
    private static final String PASSWORD = "admin"; // Замените на свой пароль

    // Метод для получения соединения с базой данных
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver"); // Подгружаем драйвер
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Хэширование пароля
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12)); // Используйте разумную сложность соли, например, 12
    }


    // Проверка пароля
    public static boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword); // Сравнение пароля с хешем
    }

    // Метод для проверки наличия пользователя в базе данных
    public static boolean authenticateUser(String username, String password) throws SQLException, ClassNotFoundException {
        String sql = "SELECT password_hash FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                return checkPassword(password, storedHash); // Сравниваем введенный пароль с хешом
            }
        }
        return false; // Если пользователь не найден
    }

    // Метод для добавления нового пользователя в базу данных
    public static void registerUser(String username, String password, String email) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO users (username, password_hash, email) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, hashPassword(password)); // Хэшируем пароль перед сохранением
            stmt.setString(3, email);
            stmt.executeUpdate();
        }
    }

    // Метод для добавления новой заметки с учетом типа заметки
    public static void addNote(int userId, String title, String content, String noteType) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO notes (user_id, title, content, created_at, note_type) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
            stmt.setInt(1, userId);
            stmt.setString(2, title);
            stmt.setString(3, content);
            stmt.setTimestamp(4, currentTimestamp);
            stmt.setString(5, noteType);  // Добавляем тип заметки в базу
            stmt.executeUpdate();
        }
    }





    public static List<Note> getNotes(int userId) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM notes WHERE user_id = ?";
        List<Note> notes = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String content = rs.getString("content");
                Timestamp createdAt = rs.getTimestamp("created_at");
                String noteType = rs.getString("note_type"); // получаем тип заметки
                notes.add(new Note(id, userId, title, content, createdAt, noteType)); // передаем тип в конструктор
            }
        }
        return notes;
    }



    public static void updateNote(int noteId, String newTitle, String newContent) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE notes SET title = ?, content = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Логирование для диагностики
            System.out.println("Updating note with ID: " + noteId);
            System.out.println("New Title: " + newTitle);
            System.out.println("New Content: " + newContent);

            stmt.setString(1, newTitle);
            stmt.setString(2, newContent);
            stmt.setInt(3, noteId);

            int rowsAffected = stmt.executeUpdate();

            // Логирование количества затронутых строк
            System.out.println("Rows affected: " + rowsAffected);
        }
    }


    public static void deleteNote(int noteId) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM notes WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, noteId);
            stmt.executeUpdate();
        }
    }


    public static List<Note> searchNotes(int userId, String query) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM notes WHERE user_id = ? AND title LIKE ?";
        List<Note> notes = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, query + "%"); // Ищем только если заголовок начинается с query
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String content = rs.getString("content");
                Timestamp createdAt = rs.getTimestamp("created_at");
                String noteType = rs.getString("note_type"); // Добавляем получение noteType из ResultSet
                notes.add(new Note(id, userId, title, content, createdAt, noteType)); // Передаем noteType
            }
        }
        return notes;
    }



}
