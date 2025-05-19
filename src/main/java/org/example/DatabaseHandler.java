package org.example;

import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {
    private static final String URL = "jdbc:postgresql://localhost:5432/keep";
    private static final String USER = "postgres";
    private static final String PASSWORD = "admin";

    // Подключение к базе данных
    public static Connection getConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Драйвер базы данных не найден.", e);
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка подключения к базе данных.", e);
        }
    }

    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    public static boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }

    public static boolean authenticateUser(String username, String password) throws SQLException, ClassNotFoundException {
        String sql = "SELECT password_hash FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && checkPassword(password, rs.getString("password_hash"));
        }
    }

    public static void registerUser(String username, String password, String email) throws SQLException, ClassNotFoundException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO users (username, password_hash, email) VALUES (?, ?, ?)")) {
            stmt.setString(1, username);
            stmt.setString(2, hashPassword(password));
            stmt.setString(3, email);
            stmt.executeUpdate();
        }
    }

    public static void addNote(int userId, String title, String content, String noteType) throws SQLException, ClassNotFoundException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO notes (user_id, title, content, created_at, note_type) VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?)")) {
            stmt.setInt(1, userId);
            stmt.setString(2, title);
            stmt.setString(3, content);
            stmt.setString(4, noteType);
            stmt.executeUpdate();
        }
    }

    public static List<Note> getNotes(int userId) throws SQLException {
        List<Note> notes = new ArrayList<>();
        String query = "SELECT * FROM notes WHERE user_id = ? AND (is_deleted = FALSE OR is_deleted IS NULL) ORDER BY created_at DESC";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Note note = new Note(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("title"),
                            rs.getString("content"),
                            rs.getString("note_type"),
                            rs.getTimestamp("created_at").toString()
                    );
                    notes.add(note);
                }
            }
        }

        return notes;
    }

    public static void updateNote(int noteId, String newTitle, String newContent, String newNoteType) throws SQLException, ClassNotFoundException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE notes SET title = ?, content = ?, note_type = ? WHERE id = ?")) {
            stmt.setString(1, newTitle);
            stmt.setString(2, newContent);
            stmt.setString(3, newNoteType);
            stmt.setInt(4, noteId);
            stmt.executeUpdate();
        }
    }

    // Обновите существующий метод deleteNote
    public static void deleteNote(int noteId) throws SQLException {
        // Вместо удаления, перемещаем заметку в корзину
        moveNoteToTrash(noteId);
    }

    public static List<Note> searchNotes(int userId, String query) throws SQLException {
        List<Note> notes = new ArrayList<>();
        String sql = "SELECT * FROM notes WHERE user_id = ? AND (is_deleted = FALSE OR is_deleted IS NULL) AND " +
                "(LOWER(title) LIKE LOWER(?) OR LOWER(content) LIKE LOWER(?)) ORDER BY created_at DESC";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, "%" + query + "%");
            pstmt.setString(3, "%" + query + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Note note = new Note(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("title"),
                            rs.getString("content"),
                            rs.getString("note_type"),
                            rs.getTimestamp("created_at").toString()
                    );
                    notes.add(note);
                }
            }
        }

        return notes;
    }

    // Добавьте новые методы для работы с корзиной
    public static void moveNoteToTrash(int noteId) throws SQLException {
        String query = "UPDATE notes SET is_deleted = TRUE, deleted_at = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            // Текущее время как время удаления
            Timestamp now = new Timestamp(System.currentTimeMillis());
            pstmt.setTimestamp(1, now);
            pstmt.setInt(2, noteId);

            pstmt.executeUpdate();
        }
    }

    public static void restoreNoteFromTrash(int noteId) throws SQLException {
        String query = "UPDATE notes SET is_deleted = FALSE, deleted_at = NULL WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, noteId);
            pstmt.executeUpdate();
        }
    }

    public static void permanentlyDeleteNote(int noteId) throws SQLException {
        String query = "DELETE FROM notes WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, noteId);
            pstmt.executeUpdate();
        }
    }

    public static void emptyTrash(int userId) throws SQLException {
        String query = "DELETE FROM notes WHERE user_id = ? AND is_deleted = TRUE";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        }
    }

    public static List<Note> getTrashNotes(int userId) throws SQLException {
        List<Note> trashNotes = new ArrayList<>();
        String query = "SELECT * FROM notes WHERE user_id = ? AND is_deleted = TRUE ORDER BY deleted_at DESC";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Note note = new Note(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("title"),
                            rs.getString("content"),
                            rs.getString("note_type"),
                            rs.getTimestamp("created_at").toString()
                    );

                    // Добавляем информацию о времени удаления
                    if (rs.getTimestamp("deleted_at") != null) {
                        note.setDeletedAt(rs.getTimestamp("deleted_at").toString());
                    }
                    note.setDeleted(true);
                    trashNotes.add(note);
                }
            }
        }

        return trashNotes;
    }

    // Метод для получения количества дней, прошедших с момента удаления
    public static int getDaysSinceDeletion(String deletedAtStr) {
        try {
            // Преобразуем строку времени удаления в Timestamp
            Timestamp deletedAt = Timestamp.valueOf(deletedAtStr);

            // Получаем текущее время
            Timestamp now = new Timestamp(System.currentTimeMillis());

            // Вычисляем разницу в миллисекундах
            long diffInMillis = now.getTime() - deletedAt.getTime();

            // Преобразуем миллисекунды в дни
            return (int) (diffInMillis / (1000 * 60 * 60 * 24));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
