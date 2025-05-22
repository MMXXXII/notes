package org.example; // Определяет пакет, в котором находится класс

// Импорт необходимых библиотек
import java.sql.*; // Библиотеки для работы с SQL
import java.time.LocalDateTime; // Класс для работы с датой и временем
import java.util.ArrayList; // Коллекция для хранения списков
import java.util.List; // Интерфейс для работы со списками

// Класс для управления операциями с корзиной заметок
public class DatabaseHandlerTrash {

    // Метод для восстановления заметки из корзины
    public static void restoreNoteFromTrash(int noteId) throws SQLException {
        String query = "UPDATE notes SET is_deleted = FALSE, deleted_at = NULL WHERE id = ?"; // SQL-запрос для восстановления заметки

        try (Connection conn = DatabaseHandler.getConnection(); // Получение соединения с БД через основной обработчик
             PreparedStatement pstmt = conn.prepareStatement(query)) { // Подготовка запроса

            pstmt.setInt(1, noteId); // Установка ID заметки в запрос
            pstmt.executeUpdate(); // Выполнение запроса на обновление
        }
    }

    // Метод для окончательного удаления заметки из корзины
    public static void permanentlyDeleteNote(int noteId) throws SQLException {
        String query = "DELETE FROM notes WHERE id = ?"; // SQL-запрос для физического удаления заметки

        try (Connection conn = DatabaseHandler.getConnection(); // Получение соединения с БД
             PreparedStatement pstmt = conn.prepareStatement(query)) { // Подготовка запроса

            pstmt.setInt(1, noteId); // Установка ID заметки в запрос
            pstmt.executeUpdate(); // Выполнение запроса на удаление
        }
    }

    // Метод для очистки всей корзины пользователя
    public static void emptyTrash(int userId) throws SQLException {
        String query = "DELETE FROM notes WHERE user_id = ? AND is_deleted = TRUE"; // SQL-запрос для удаления всех заметок в корзине

        try (Connection conn = DatabaseHandler.getConnection(); // Получение соединения с БД
             PreparedStatement pstmt = conn.prepareStatement(query)) { // Подготовка запроса

            pstmt.setInt(1, userId); // Установка ID пользователя в запрос
            pstmt.executeUpdate(); // Выполнение запроса на удаление
        }
    }

    // Метод для получения всех удаленных заметок пользователя (содержимое корзины)
    public static List<Note> getTrashNotes(int userId) throws SQLException {
        List<Note> trashNotes = new ArrayList<>(); // Создание списка для хранения удаленных заметок
        String query = "SELECT * FROM notes WHERE user_id = ? AND is_deleted = TRUE ORDER BY deleted_at DESC"; // SQL-запрос с сортировкой по времени удаления

        try (Connection conn = DatabaseHandler.getConnection(); // Получение соединения с БД
             PreparedStatement pstmt = conn.prepareStatement(query)) { // Подготовка запроса

            pstmt.setInt(1, userId); // Установка ID пользователя в запрос

            try (ResultSet rs = pstmt.executeQuery()) { // Выполнение запроса и получение результата
                while (rs.next()) { // Перебор всех строк результата
                    Note note = new Note( // Создание объекта заметки из данных БД
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("title"),
                            rs.getString("content"),
                            rs.getString("note_type"),
                            rs.getTimestamp("created_at").toString()
                    );

                    // Добавляем информацию о времени удаления
                    note.setDeletedAt(rs.getTimestamp("deleted_at").toString()); // Установка времени удаления в объект заметки
                    trashNotes.add(note); // Добавление заметки в список результатов
                }
            }
        }

        return trashNotes; // Возврат списка удаленных заметок
    }

    // Метод для получения количества дней, прошедших с момента удаления
    public static int getDaysSinceDeletion(String deletedAtStr) {
        try {
            // Преобразуем строку времени удаления в Timestamp
            Timestamp deletedAt = Timestamp.valueOf(deletedAtStr); // Преобразование строки в объект Timestamp

            // Получаем текущее время
            Timestamp now = new Timestamp(System.currentTimeMillis()); // Создание объекта Timestamp с текущим временем

            // Вычисляем разницу в миллисекундах
            long diffInMillis = now.getTime() - deletedAt.getTime(); // Расчет разницы во времени в миллисекундах

            // Преобразуем миллисекунды в дни
            return (int) (diffInMillis / (1000 * 60 * 60 * 24)); // Преобразование миллисекунд в дни (1000мс * 60с * 60м * 24ч)
        } catch (Exception e) {
            e.printStackTrace(); // Вывод информации об ошибке в консоль
            return 0; // Возврат 0 в случае ошибки
        }
    }
}