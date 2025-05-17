package org.example;

import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {
    private static final String URL = "jdbc:postgresql://localhost:5432/keep";
    private static final String USER = "postgres";
    private static final String PASSWORD = "admin";

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
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

    public static List<Note> getNotes(int userId) throws SQLException, ClassNotFoundException {
        List<Note> notes = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM notes WHERE user_id = ?")) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                notes.add(new Note(
                        rs.getInt("id"), userId, rs.getString("title"),
                        rs.getString("content"), rs.getTimestamp("created_at"),
                        rs.getString("note_type")));
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

    public static void deleteNote(int noteId) throws SQLException, ClassNotFoundException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM notes WHERE id = ?")) {
            stmt.setInt(1, noteId);
            stmt.executeUpdate();
        }
    }

    public static List<Note> searchNotes(int userId, String query) throws SQLException, ClassNotFoundException {
        List<Note> notes = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM notes WHERE user_id = ? AND title LIKE ?")) {
            stmt.setInt(1, userId);
            stmt.setString(2, query + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                notes.add(new Note(
                        rs.getInt("id"), userId, rs.getString("title"),
                        rs.getString("content"), rs.getTimestamp("created_at"),
                        rs.getString("note_type")));
            }
        }
        return notes;
    }
}
