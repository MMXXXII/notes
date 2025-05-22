package org.example; // Определяет пакет, в котором находится класс

// Импорт необходимых библиотек
import java.sql.SQLException; // Для обработки исключений SQL
import java.time.LocalDate; // Для работы с датами
import java.time.YearMonth; // Для работы с месяцами
import java.util.ArrayList; // Коллекция для хранения списков
import java.util.List; // Интерфейс для работы со списками
import java.util.Map; // Интерфейс для работы с ассоциативными массивами
import java.util.stream.Collectors; // Для работы с потоками данных

// Класс-контроллер для управления напоминаниями в главном окне приложения
public class MainWindowControllerReminders {
    private MainWindowController controller; // Ссылка на основной контроллер главного окна
    private CalendarPanel calendarPanel; // Ссылка на панель календаря

    // Конструктор класса
    public MainWindowControllerReminders(MainWindowController controller) {
        this.controller = controller; // Инициализация ссылки на основной контроллер
    }

    // Метод для установки панели календаря
    public void setCalendarPanel(CalendarPanel calendarPanel) {
        this.calendarPanel = calendarPanel; // Сохранение ссылки на панель календаря
    }

    // Открыть диалог с напоминаниями на выбранную дату
    public void openRemindersForDate(LocalDate date) {
        ReminderDialog dialog = new ReminderDialog(controller.getView(), controller, date); // Создание диалогового окна для напоминаний
        dialog.setVisible(true); // Отображение диалогового окна
    }

    // Получить напоминания на конкретную дату
    public List<Reminder> getRemindersForDate(LocalDate date) throws SQLException {
        List<Reminder> allReminders = DatabaseHandlerReminders.getReminders(controller.getUserId()); // Получение всех напоминаний пользователя
        return allReminders.stream() // Преобразование списка в поток
                .filter(r -> r.getReminderDate().equals(date)) // Фильтрация напоминаний по дате
                .collect(Collectors.toList()); // Сбор результатов в список
    }

    // Добавить новое напоминание
    public void addReminder(String title, String description, LocalDate date) throws SQLException {
        DatabaseHandlerReminders.addReminder(controller.getUserId(), title, description, date); // Вызов метода БД для добавления напоминания
    }

    // Обновить напоминание
    public void updateReminder(int reminderId, String title, String description, LocalDate date, boolean completed) throws SQLException {
        DatabaseHandlerReminders.updateReminder(reminderId, title, description, date, completed); // Вызов метода БД для обновления напоминания
    }

    // Обновить статус напоминания (выполнено/не выполнено)
    public void updateReminderStatus(int reminderId, boolean completed) throws SQLException {
        Reminder reminder = null; // Инициализация переменной для хранения напоминания

        // Находим напоминание по ID
        List<Reminder> allReminders = DatabaseHandlerReminders.getReminders(controller.getUserId()); // Получение всех напоминаний
        for (Reminder r : allReminders) { // Перебор всех напоминаний
            if (r.getId() == reminderId) { // Проверка совпадения ID
                reminder = r; // Сохранение найденного напоминания
                break; // Выход из цикла
            }
        }

        if (reminder != null) { // Проверка, что напоминание найдено
            DatabaseHandlerReminders.updateReminder( // Вызов метода БД для обновления напоминания
                    reminderId, // ID напоминания
                    reminder.getTitle(), // Сохранение текущего заголовка
                    reminder.getDescription(), // Сохранение текущего описания
                    reminder.getReminderDate(), // Сохранение текущей даты
                    completed // Обновление статуса выполнения
            );
        }
    }

    // Удалить напоминание
    public void deleteReminder(int reminderId) throws SQLException {
        DatabaseHandlerReminders.deleteReminder(reminderId); // Вызов метода БД для удаления напоминания
    }

    // Обновить календарь
    public void updateCalendar() {
        if (calendarPanel != null) { // Проверка, что панель календаря инициализирована
            calendarPanel.updateCalendar(); // Вызов метода обновления календаря
        }
    }

    // Получить напоминания для месяца
    public Map<LocalDate, List<Reminder>> getRemindersForMonth(YearMonth yearMonth) throws SQLException {
        return DatabaseHandlerReminders.getRemindersForMonth(controller.getUserId(), yearMonth); // Вызов метода БД для получения напоминаний за месяц
    }
}