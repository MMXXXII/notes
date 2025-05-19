package org.example;

import java.sql.SQLException;
import java.util.List;

public class MainWindowControllerTrash {
    private MainWindowController controller;

    public MainWindowControllerTrash(MainWindowController controller) {
        this.controller = controller;
    }

    // Открыть диалог корзины
    public void openTrashDialog() {
        TrashDialog dialog = new TrashDialog(controller.getView(), controller);
        dialog.setVisible(true);
    }

    // Получить удаленные заметки
    public List<Note> getTrashNotes() throws SQLException {
        return DatabaseHandlerTrash.getTrashNotes(controller.getUserId());
    }

    // Восстановить заметку из корзины
    public void restoreNoteFromTrash(int noteId) throws SQLException {
        DatabaseHandlerTrash.restoreNoteFromTrash(noteId);
        controller.loadNotes(); // Обновляем список заметок
    }

    // Окончательно удалить заметку
    public void permanentlyDeleteNote(int noteId) throws SQLException {
        DatabaseHandlerTrash.permanentlyDeleteNote(noteId);
    }

    // Очистить корзину
    public void emptyTrash() throws SQLException {
        DatabaseHandlerTrash.emptyTrash(controller.getUserId());
    }
}