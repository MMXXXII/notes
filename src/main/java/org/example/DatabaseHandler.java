package org.example; // Определяет пакет, в котором находится класс

// Импорт необходимых библиотек
import org.mindrot.jbcrypt.BCrypt; // Библиотека для хеширования паролей
import java.sql.*; // Библиотеки для работы с SQL
import java.util.ArrayList; // Коллекция для хранения списков
import java.util.List; // Интерфейс для работы со списками

// Основной класс для работы с базой данных
public class DatabaseHandler {
    // Константы для подключения к базе данных
    private static final String URL = "jdbc:postgresql://localhost:5432/keep"; // URL базы данных PostgreSQL
    private static final String USER = "postgres"; // Имя пользователя базы данных
    private static final String PASSWORD = "admin"; // Пароль для доступа к базе данных

    // Метод для установки соединения с базой данных
    public static Connection getConnection() {
        try {
            Class.forName("org.postgresql.Driver"); // Загрузка драйвера PostgreSQL
            return DriverManager.getConnection(URL, USER, PASSWORD); // Создание соединения с базой данных
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Драйвер базы данных не найден.", e); // Обработка ошибки, если драйвер не найден
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка подключения к базе данных.", e); // Обработка ошибки соединения с БД
        }
    }

    // Метод для хеширования пароля с использованием BCrypt
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12)); // Хеширование пароля с солью (12 - фактор стоимости)
    }

    // Метод для проверки соответствия пароля его хешу
    public static boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword); // Сравнение введенного пароля с хешированным
    }

    // Метод для аутентификации пользователя
    public static boolean authenticateUser(String username, String password) throws SQLException, ClassNotFoundException {
        String sql = "SELECT password_hash FROM users WHERE username = ?"; // SQL-запрос для получения хеша пароля
        try (Connection conn = getConnection(); // Получение соединения с БД
             PreparedStatement stmt = conn.prepareStatement(sql)) { // Подготовка SQL-запроса
            stmt.setString(1, username); // Установка параметра username в запрос
            ResultSet rs = stmt.executeQuery(); // Выполнение запроса и получение результата
            return rs.next() && checkPassword(password, rs.getString("password_hash")); // Проверка пароля, если пользователь найден
        }
    }

    // Метод для регистрации нового пользователя
    public static void registerUser(String username, String password, String email) throws SQLException, ClassNotFoundException {
        try (Connection conn = getConnection(); // Получение соединения с БД
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO users (username, password_hash, email) VALUES (?, ?, ?)")) { // SQL-запрос для вставки
            stmt.setString(1, username); // Установка параметра username
            stmt.setString(2, hashPassword(password)); // Хеширование и установка пароля
            stmt.setString(3, email); // Установка параметра email
            stmt.executeUpdate(); // Выполнение запроса на вставку
        }
    }

    // Метод для добавления новой заметки
    public static void addNote(int userId, String title, String content, String noteType) throws SQLException, ClassNotFoundException {
        try (Connection conn = getConnection(); // Получение соединения с БД
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO notes (user_id, title, content, created_at, note_type) VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?)")) { // SQL-запрос
            stmt.setInt(1, userId); // Установка ID пользователя
            stmt.setString(2, title); // Установка заголовка заметки
            stmt.setString(3, content); // Установка содержимого заметки
            stmt.setString(4, noteType); // Установка типа заметки
            stmt.executeUpdate(); // Выполнение запроса на вставку
        }
    }

    // Метод для получения всех заметок пользователя (не удаленных)
    public static List<Note> getNotes(int userId) throws SQLException {
        List<Note> notes = new ArrayList<>(); // Создание списка для хранения заметок
        String query = "SELECT * FROM notes WHERE user_id = ? AND (is_deleted = FALSE OR is_deleted IS NULL) ORDER BY created_at DESC"; // SQL-запрос

        try (Connection conn = getConnection(); // Получение соединения с БД
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
                    notes.add(note); // Добавление заметки в список
                }
            }
        }

        return notes; // Возврат списка заметок
    }

    // Метод для обновления существующей заметки
    public static void updateNote(int noteId, String newTitle, String newContent, String newNoteType) throws SQLException, ClassNotFoundException {
        try (Connection conn = getConnection(); // Получение соединения с БД
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE notes SET title = ?, content = ?, note_type = ? WHERE id = ?")) { // SQL-запрос на обновление
            stmt.setString(1, newTitle); // Установка нового заголовка
            stmt.setString(2, newContent); // Установка нового содержимого
            stmt.setString(3, newNoteType); // Установка нового типа
            stmt.setInt(4, noteId); // Установка ID заметки для обновления
            stmt.executeUpdate(); // Выполнение запроса на обновление
        }
    }

    // Метод для "удаления" заметки (перемещение в корзину)
    public static void deleteNote(int noteId) throws SQLException {
        // Вместо физического удаления, перемещаем заметку в корзину
        moveNoteToTrash(noteId); // Вызов метода для перемещения в корзину
    }

    // Метод для поиска заметок по ключевому слову
    public static List<Note> searchNotes(int userId, String query) throws SQLException {
        List<Note> notes = new ArrayList<>(); // Создание списка для результатов поиска
        String sql = "SELECT * FROM notes WHERE user_id = ? AND (is_deleted = FALSE OR is_deleted IS NULL) AND " +
                "(LOWER(title) LIKE LOWER(?) OR LOWER(content) LIKE LOWER(?)) ORDER BY created_at DESC"; // SQL-запрос с поиском

        try (Connection conn = getConnection(); // Получение соединения с БД
             PreparedStatement pstmt = conn.prepareStatement(sql)) { // Подготовка запроса

            pstmt.setInt(1, userId); // Установка ID пользователя
            pstmt.setString(2, "%" + query + "%"); // Установка параметра поиска для заголовка (с wildcards)
            pstmt.setString(3, "%" + query + "%"); // Установка параметра поиска для содержимого (с wildcards)

            try (ResultSet rs = pstmt.executeQuery()) { // Выполнение запроса
                while (rs.next()) { // Перебор результатов
                    Note note = new Note( // Создание объекта заметки из результатов
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("title"),
                            rs.getString("content"),
                            rs.getString("note_type"),
                            rs.getTimestamp("created_at").toString()
                    );
                    notes.add(note); // Добавление заметки в список результатов
                }
            }
        }

        return notes; // Возврат списка найденных заметок
    }

    // Метод для перемещения заметки в корзину
    public static void moveNoteToTrash(int noteId) throws SQLException {
        String query = "UPDATE notes SET is_deleted = TRUE, deleted_at = ? WHERE id = ?"; // SQL-запрос для пометки как удаленной

        try (Connection conn = getConnection(); // Получение соединения с БД
             PreparedStatement pstmt = conn.prepareStatement(query)) { // Подготовка запроса

            // Текущее время как время удаления
            Timestamp now = new Timestamp(System.currentTimeMillis()); // Получение текущего времени
            pstmt.setTimestamp(1, now); // Установка времени удаления
            pstmt.setInt(2, noteId); // Установка ID заметки

            pstmt.executeUpdate(); // Выполнение запроса на обновление
        }
    }

    // Метод для восстановления заметки из корзины
    public static void restoreNoteFromTrash(int noteId) throws SQLException {
        String query = "UPDATE notes SET is_deleted = FALSE, deleted_at = NULL WHERE id = ?"; // SQL-запрос для восстановления

        try (Connection conn = getConnection(); // Получение соединения с БД
             PreparedStatement pstmt = conn.prepareStatement(query)) { // Подготовка запроса

            pstmt.setInt(1, noteId); // Установка ID заметки
            pstmt.executeUpdate(); // Выполнение запроса на обновление
        }
    }

    // Метод для окончательного удаления заметки из БД
    public static void permanentlyDeleteNote(int noteId) throws SQLException {
        String query = "DELETE FROM notes WHERE id = ?"; // SQL-запрос для физического удаления

        try (Connection conn = getConnection(); // Получение соединения с БД
             PreparedStatement pstmt = conn.prepareStatement(query)) { // Подготовка запроса

            pstmt.setInt(1, noteId); // Установка ID заметки
            pstmt.executeUpdate(); // Выполнение запроса на удаление
        }
    }

    // Метод для очистки корзины (удаление всех заметок в корзине)
    public static void emptyTrash(int userId) throws SQLException {
        String query = "DELETE FROM notes WHERE user_id = ? AND is_deleted = TRUE"; // SQL-запрос для удаления всех заметок в корзине

        try (Connection conn = getConnection(); // Получение соединения с БД
             PreparedStatement pstmt = conn.prepareStatement(query)) { // Подготовка запроса

            pstmt.setInt(1, userId); // Установка ID пользователя
            pstmt.executeUpdate(); // Выполнение запроса на удаление
        }
    }

    // Метод для получения заметок из корзины
    public static List<Note> getTrashNotes(int userId) throws SQLException {
        List<Note> trashNotes = new ArrayList<>(); // Создание списка для заметок из корзины
        String query = "SELECT * FROM notes WHERE user_id = ? AND is_deleted = TRUE ORDER BY deleted_at DESC"; // SQL-запрос

        try (Connection conn = getConnection(); // Получение соединения с БД
             PreparedStatement pstmt = conn.prepareStatement(query)) { // Подготовка запроса

            pstmt.setInt(1, userId); // Установка ID пользователя

            try (ResultSet rs = pstmt.executeQuery()) { // Выполнение запроса
                while (rs.next()) { // Перебор результатов
                    Note note = new Note( // Создание объекта заметки
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("title"),
                            rs.getString("content"),
                            rs.getString("note_type"),
                            rs.getTimestamp("created_at").toString()
                    );

                    // Добавляем информацию о времени удаления
                    if (rs.getTimestamp("deleted_at") != null) { // Проверка наличия времени удаления
                        note.setDeletedAt(rs.getTimestamp("deleted_at").toString()); // Установка времени удаления
                    }
                    note.setDeleted(true); // Пометка заметки как удаленной
                    trashNotes.add(note); // Добавление заметки в список
                }
            }
        }

        return trashNotes; // Возврат списка заметок из корзины
    }

    // Метод для расчета количества дней с момента удаления заметки
    public static int getDaysSinceDeletion(String deletedAtStr) {
        try {
            // Преобразуем строку времени удаления в Timestamp
            Timestamp deletedAt = Timestamp.valueOf(deletedAtStr); // Преобразование строки в Timestamp

            // Получаем текущее время
            Timestamp now = new Timestamp(System.currentTimeMillis()); // Получение текущего времени

            // Вычисляем разницу в миллисекундах
            long diffInMillis = now.getTime() - deletedAt.getTime(); // Расчет разницы во времени

            // Преобразуем миллисекунды в дни
            return (int) (diffInMillis / (1000 * 60 * 60 * 24)); // Преобразование миллисекунд в дни
        } catch (Exception e) {
            e.printStackTrace(); // Вывод ошибки в консоль
            return 0; // Возврат 0 в случае ошибки
        }
    }
}