package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

public class MainWindowController {
    private MainWindow view;
    private int userId;
    private String username;
    private String email;
    private MainWindowControllerReminders reminderController;
    private MainWindowControllerTrash trashController;
    private CalendarPanel calendarPanel;

    public MainWindowController(MainWindow view, int userId, String username, String email) {
        this.view = view;
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.reminderController = new MainWindowControllerReminders(this);
        this.trashController = new MainWindowControllerTrash(this);
    }

    // Геттер для view
    public MainWindow getView() {
        return view;
    }

    // Controller methods for user profile
    public void openUserProfile() {
        JDialog profileDialog = view.createProfileDialog();
        profileDialog.setVisible(true);
    }

    public void showExitConfirmation(JDialog parentDialog) {
        JDialog confirmDialog = view.createExitConfirmationDialog(parentDialog);
        confirmDialog.setVisible(true);
    }

    public void logout(JDialog profileDialog) {
        profileDialog.dispose();
        view.dispose();
        new LoginWindow();
    }

    // Controller methods for notes
    public void loadNotes() {
        try {
            List<Note> notes = DatabaseHandler.getNotes(userId);
            view.displayNotes(notes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void searchNotes(String query) {
        SwingWorker<List<Note>, Void> searchWorker = new SwingWorker<>() {
            @Override
            protected List<Note> doInBackground() throws Exception {
                return DatabaseHandler.searchNotes(userId, query);
            }

            @Override
            protected void done() {
                try {
                    List<Note> notes = get();
                    view.displayNotes(notes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        searchWorker.execute();
    }

    public void openAddNoteDialog() {
        JDialog dialog = view.createAddNoteDialog();
        dialog.setVisible(true);
    }

    public void openNote(Note note) {
        JFrame noteFrame = view.createNoteEditorFrame(note);
        noteFrame.setVisible(true);
    }

    public void saveNote(Note note, String title, String content, String noteType, JFrame frame) {
        try {
            if (title.isEmpty() || content.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Заголовок и содержание не могут быть пустыми!");
                return;
            }
            DatabaseHandler.updateNote(note.getId(), title.trim(), content.trim(), noteType);
            JOptionPane.showMessageDialog(view, "Заметка сохранена!");
            loadNotes();
            frame.dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Ошибка при сохранении заметки");
            ex.printStackTrace();
        }
    }

    public void addNote(String title, String content, String noteType) {
        try {
            if (title.isEmpty() || content.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Заголовок и содержание не могут быть пустыми!");
                return;
            }

            DatabaseHandler.addNote(userId, title, content, noteType);
            JOptionPane.showMessageDialog(view, "Заметка добавлена!");
            loadNotes();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Ошибка при добавлении заметки");
            ex.printStackTrace();
        }
    }

    public void deleteNote(Note note) {
        int confirmation = JOptionPane.showConfirmDialog(view,
                "Переместить заметку в корзину?",
                "Удаление заметки",
                JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                DatabaseHandler.deleteNote(note.getId());
                loadNotes();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Методы для работы с календарем и напоминаниями
    public void setCalendarPanel(CalendarPanel calendarPanel) {
        this.calendarPanel = calendarPanel;
        this.reminderController.setCalendarPanel(calendarPanel);
    }

    public void openRemindersForDate(LocalDate date) {
        reminderController.openRemindersForDate(date);
    }

    public List<Reminder> getRemindersForDate(LocalDate date) throws SQLException {
        return reminderController.getRemindersForDate(date);
    }

    public void addReminder(String title, String description, LocalDate date) throws SQLException {
        reminderController.addReminder(title, description, date);
    }

    public void updateReminder(int reminderId, String title, String description, LocalDate date, boolean completed) throws SQLException {
        reminderController.updateReminder(reminderId, title, description, date, completed);
    }

    public void updateReminderStatus(int reminderId, boolean completed) throws SQLException {
        reminderController.updateReminderStatus(reminderId, completed);
    }

    public void deleteReminder(int reminderId) throws SQLException {
        reminderController.deleteReminder(reminderId);
    }

    public void updateCalendar() {
        reminderController.updateCalendar();
    }

    public Map<LocalDate, List<Reminder>> getRemindersForMonth(YearMonth yearMonth) throws SQLException {
        return reminderController.getRemindersForMonth(yearMonth);
    }

    // Методы для работы с корзиной
    public void openTrashDialog() {
        trashController.openTrashDialog();
    }

    public List<Note> getTrashNotes() throws SQLException {
        return trashController.getTrashNotes();
    }

    public void restoreNoteFromTrash(int noteId) throws SQLException {
        trashController.restoreNoteFromTrash(noteId);
    }

    public void permanentlyDeleteNote(int noteId) throws SQLException {
        trashController.permanentlyDeleteNote(noteId);
    }

    public void emptyTrash() throws SQLException {
        trashController.emptyTrash();
    }

    // Getters for model data
    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }
}