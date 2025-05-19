package org.example;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MainWindowControllerReminders {
    private MainWindowController controller;
    private CalendarPanel calendarPanel;

    public MainWindowControllerReminders(MainWindowController controller) {
        this.controller = controller;
    }

    public void setCalendarPanel(CalendarPanel calendarPanel) {
        this.calendarPanel = calendarPanel;
    }

    // Открыть диалог с напоминаниями на выбранную дату
    public void openRemindersForDate(LocalDate date) {
        ReminderDialog dialog = new ReminderDialog(controller.getView(), controller, date);
        dialog.setVisible(true);
    }

    // Получить напоминания на конкретную дату
    public List<Reminder> getRemindersForDate(LocalDate date) throws SQLException {
        List<Reminder> allReminders = DatabaseHandlerReminders.getReminders(controller.getUserId());
        return allReminders.stream()
                .filter(r -> r.getReminderDate().equals(date))
                .collect(Collectors.toList());
    }

    // Добавить новое напоминание
    public void addReminder(String title, String description, LocalDate date) throws SQLException {
        DatabaseHandlerReminders.addReminder(controller.getUserId(), title, description, date);
    }

    // Обновить напоминание
    public void updateReminder(int reminderId, String title, String description, LocalDate date, boolean completed) throws SQLException {
        DatabaseHandlerReminders.updateReminder(reminderId, title, description, date, completed);
    }

    // Обновить статус напоминания (выполнено/не выполнено)
    public void updateReminderStatus(int reminderId, boolean completed) throws SQLException {
        Reminder reminder = null;

        // Находим напоминание по ID
        List<Reminder> allReminders = DatabaseHandlerReminders.getReminders(controller.getUserId());
        for (Reminder r : allReminders) {
            if (r.getId() == reminderId) {
                reminder = r;
                break;
            }
        }

        if (reminder != null) {
            DatabaseHandlerReminders.updateReminder(
                    reminderId,
                    reminder.getTitle(),
                    reminder.getDescription(),
                    reminder.getReminderDate(),
                    completed
            );
        }
    }

    // Удалить напоминание
    public void deleteReminder(int reminderId) throws SQLException {
        DatabaseHandlerReminders.deleteReminder(reminderId);
    }

    // Обновить календарь
    public void updateCalendar() {
        if (calendarPanel != null) {
            calendarPanel.updateCalendar();
        }
    }

    // Получить напоминания для месяца
    public Map<LocalDate, List<Reminder>> getRemindersForMonth(YearMonth yearMonth) throws SQLException {
        return DatabaseHandlerReminders.getRemindersForMonth(controller.getUserId(), yearMonth);
    }
}