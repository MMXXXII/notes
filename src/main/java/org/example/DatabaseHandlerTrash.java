package org.example;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandlerTrash {

    // Метод для восстановления заметки из корзины
    public static void restoreNoteFromTrash(int noteId) throws SQLException {
        String query = "UPDATE notes SET is_deleted = FALSE, deleted_at = NULL WHERE id = ?";

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, noteId);
            pstmt.executeUpdate();
        }
    }

    // Метод для окончательного удаления заметки из корзины
    public static void permanentlyDeleteNote(int noteId) throws SQLException {
        String query = "DELETE FROM notes WHERE id = ?";

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, noteId);
            pstmt.executeUpdate();
        }
    }

    // Метод для очистки всей корзины пользователя
    public static void emptyTrash(int userId) throws SQLException {
        String query = "DELETE FROM notes WHERE user_id = ? AND is_deleted = TRUE";

        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        }
    }

    // Метод для получения всех удаленных заметок пользователя (содержимое корзины)
    public static List<Note> getTrashNotes(int userId) throws SQLException {
        List<Note> trashNotes = new ArrayList<>();
        String query = "SELECT * FROM notes WHERE user_id = ? AND is_deleted = TRUE ORDER BY deleted_at DESC";

        try (Connection conn = DatabaseHandler.getConnection();
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
                    note.setDeletedAt(rs.getTimestamp("deleted_at").toString());
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