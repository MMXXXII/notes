package org.example; // Определение пакета для класса

// Импорт необходимых библиотек
import javax.swing.*; // Основная библиотека Swing для GUI компонентов
import java.sql.SQLException; // Для обработки исключений SQL
import java.time.LocalDate; // Для работы с датами
import java.time.YearMonth; // Для работы с месяцами и годами
import java.util.List; // Для работы со списками
import java.util.Map; // Для работы с ассоциативными массивами

// Основной класс контроллера главного окна приложения
public class MainWindowController {
    private MainWindow view; // Ссылка на представление (главное окно)
    private int userId; // ID пользователя
    private String username; // Имя пользователя
    private String email; // Email пользователя
    private MainWindowControllerReminders reminderController; // Контроллер для работы с напоминаниями
    private MainWindowControllerTrash trashController; // Контроллер для работы с корзиной
    private CalendarPanel calendarPanel; // Ссылка на панель календаря

    // Конструктор контроллера
    public MainWindowController(MainWindow view, int userId, String username, String email) {
        this.view = view; // Сохраняем ссылку на представление
        this.userId = userId; // Сохраняем ID пользователя
        this.username = username; // Сохраняем имя пользователя
        this.email = email; // Сохраняем email пользователя
        this.reminderController = new MainWindowControllerReminders(this); // Создаем контроллер напоминаний
        this.trashController = new MainWindowControllerTrash(this); // Создаем контроллер корзины
    }

    // Геттер для получения ссылки на представление
    public MainWindow getView() {
        return view; // Возвращаем ссылку на представление
    }

    // Метод для открытия диалога профиля пользователя
    public void openUserProfile() {
        JDialog profileDialog = view.createProfileDialog(); // Создаем диалог профиля через представление
        profileDialog.setVisible(true); // Отображаем диалог
    }

    // Метод для показа диалога подтверждения выхода
    public void showExitConfirmation(JDialog parentDialog) {
        JDialog confirmDialog = view.createExitConfirmationDialog(parentDialog); // Создаем диалог подтверждения
        confirmDialog.setVisible(true); // Отображаем диалог
    }

    // Метод для выхода из аккаунта
    public void logout(JDialog profileDialog) {
        profileDialog.dispose(); // Закрываем диалог профиля
        view.dispose(); // Закрываем главное окно
        new LoginWindow(); // Создаем и отображаем окно входа
    }

    // Методы контроллера для работы с заметками

    // Метод для загрузки заметок пользователя
    public void loadNotes() {
        try {
            List<Note> notes = DatabaseHandler.getNotes(userId); // Получаем заметки из базы данных
            view.displayNotes(notes); // Отображаем заметки в представлении
        } catch (Exception e) {
            e.printStackTrace(); // Выводим информацию об ошибке в консоль
        }
    }

    // Метод для поиска заметок
    public void searchNotes(String query) {
        // Создаем SwingWorker для выполнения поиска в фоновом потоке
        SwingWorker<List<Note>, Void> searchWorker = new SwingWorker<>() {
            @Override
            protected List<Note> doInBackground() throws Exception {
                return DatabaseHandler.searchNotes(userId, query); // Выполняем поиск в базе данных
            }

            @Override
            protected void done() {
                try {
                    List<Note> notes = get(); // Получаем результаты поиска
                    view.displayNotes(notes); // Отображаем результаты в представлении
                } catch (Exception e) {
                    e.printStackTrace(); // Выводим информацию об ошибке в консоль
                }
            }
        };
        searchWorker.execute(); // Запускаем фоновую задачу
    }

    // Метод для открытия диалога добавления заметки
    public void openAddNoteDialog() {
        JDialog dialog = view.createAddNoteDialog(); // Создаем диалог добавления заметки
        dialog.setVisible(true); // Отображаем диалог
    }

    // Метод для открытия заметки для редактирования
    public void openNote(Note note) {
        JFrame noteFrame = view.createNoteEditorFrame(note); // Создаем окно редактирования заметки
        noteFrame.setVisible(true); // Отображаем окно
    }

    // Метод для сохранения изменений в заметке
    public void saveNote(Note note, String title, String content, String noteType, JFrame frame) {
        try {
            // Проверяем, что заголовок и содержание не пустые
            if (title.isEmpty() || content.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Заголовок и содержание не могут быть пустыми!");
                return; // Прерываем выполнение метода
            }
            // Обновляем заметку в базе данных
            DatabaseHandler.updateNote(note.getId(), title.trim(), content.trim(), noteType);
            JOptionPane.showMessageDialog(view, "Заметка сохранена!"); // Показываем сообщение об успехе
            loadNotes(); // Перезагружаем список заметок
            frame.dispose(); // Закрываем окно редактирования
        } catch (Exception ex) {
            // Обрабатываем ошибку
            JOptionPane.showMessageDialog(view, "Ошибка при сохранении заметки");
            ex.printStackTrace(); // Выводим информацию об ошибке в консоль
        }
    }

    // Метод для добавления новой заметки
    public void addNote(String title, String content, String noteType) {
        try {
            // Проверяем, что заголовок и содержание не пустые
            if (title.isEmpty() || content.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Заголовок и содержание не могут быть пустыми!");
                return; // Прерываем выполнение метода
            }

            // Добавляем заметку в базу данных
            DatabaseHandler.addNote(userId, title, content, noteType);
            JOptionPane.showMessageDialog(view, "Заметка добавлена!"); // Показываем сообщение об успехе
            loadNotes(); // Перезагружаем список заметок
        } catch (Exception ex) {
            // Обрабатываем ошибку
            JOptionPane.showMessageDialog(view, "Ошибка при добавлении заметки");
            ex.printStackTrace(); // Выводим информацию об ошибке в консоль
        }
    }

    // Метод для удаления заметки (перемещения в корзину)
    public void deleteNote(Note note) {
        // Показываем диалог подтверждения
        int confirmation = JOptionPane.showConfirmDialog(view,
                "Переместить заметку в корзину?",
                "Удаление заметки",
                JOptionPane.YES_NO_OPTION);

        // Если пользователь подтвердил удаление
        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                DatabaseHandler.deleteNote(note.getId()); // Удаляем заметку (перемещаем в корзину)
                loadNotes(); // Перезагружаем список заметок
            } catch (Exception e) {
                e.printStackTrace(); // Выводим информацию об ошибке в консоль
            }
        }
    }

    // Методы для работы с календарем и напоминаниями

    // Метод для установки ссылки на панель календаря
    public void setCalendarPanel(CalendarPanel calendarPanel) {
        this.calendarPanel = calendarPanel; // Сохраняем ссылку на панель календаря
        this.reminderController.setCalendarPanel(calendarPanel); // Передаем ссылку контроллеру напоминаний
    }

    // Метод для открытия диалога напоминаний для выбранной даты
    public void openRemindersForDate(LocalDate date) {
        reminderController.openRemindersForDate(date); // Делегируем вызов контроллеру напоминаний
    }

    // Метод для получения напоминаний на выбранную дату
    public List<Reminder> getRemindersForDate(LocalDate date) throws SQLException {
        return reminderController.getRemindersForDate(date); // Делегируем вызов контроллеру напоминаний
    }

    // Метод для добавления нового напоминания
    public void addReminder(String title, String description, LocalDate date) throws SQLException {
        reminderController.addReminder(title, description, date); // Делегируем вызов контроллеру напоминаний
    }

    // Метод для обновления существующего напоминания
    public void updateReminder(int reminderId, String title, String description, LocalDate date, boolean completed) throws SQLException {
        reminderController.updateReminder(reminderId, title, description, date, completed); // Делегируем вызов контроллеру напоминаний
    }

    // Метод для обновления статуса напоминания (выполнено/не выполнено)
    public void updateReminderStatus(int reminderId, boolean completed) throws SQLException {
        reminderController.updateReminderStatus(reminderId, completed); // Делегируем вызов контроллеру напоминаний
    }

    // Метод для удаления напоминания
    public void deleteReminder(int reminderId) throws SQLException {
        reminderController.deleteReminder(reminderId); // Делегируем вызов контроллеру напоминаний
    }

    // Метод для обновления отображения календаря
    public void updateCalendar() {
        reminderController.updateCalendar(); // Делегируем вызов контроллеру напоминаний
    }

    // Метод для получения напоминаний на весь месяц
    public Map<LocalDate, List<Reminder>> getRemindersForMonth(YearMonth yearMonth) throws SQLException {
        return reminderController.getRemindersForMonth(yearMonth); // Делегируем вызов контроллеру напоминаний
    }

    // Методы для работы с корзиной

    // Метод для открытия диалога корзины
    public void openTrashDialog() {
        trashController.openTrashDialog(); // Делегируем вызов контроллеру корзины
    }

    // Метод для получения заметок из корзины
    public List<Note> getTrashNotes() throws SQLException {
        return trashController.getTrashNotes(); // Делегируем вызов контроллеру корзины
    }

    // Метод для восстановления заметки из корзины
    public void restoreNoteFromTrash(int noteId) throws SQLException {
        trashController.restoreNoteFromTrash(noteId); // Делегируем вызов контроллеру корзины
    }

    // Метод для окончательного удаления заметки из корзины
    public void permanentlyDeleteNote(int noteId) throws SQLException {
        trashController.permanentlyDeleteNote(noteId); // Делегируем вызов контроллеру корзины
    }

    // Метод для очистки корзины (удаления всех заметок из корзины)
    public void emptyTrash() throws SQLException {
        trashController.emptyTrash(); // Делегируем вызов контроллеру корзины
    }

    // Геттеры для получения данных модели

    // Геттер для получения ID пользователя
    public int getUserId() {
        return userId; // Возвращаем ID пользователя
    }

    // Геттер для получения имени пользователя
    public String getUsername() {
        return username; // Возвращаем имя пользователя
    }

    // Геттер для получения email пользователя
    public String getEmail() {
        return email; // Возвращаем email пользователя
    }
}