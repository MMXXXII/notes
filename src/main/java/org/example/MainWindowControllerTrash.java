package org.example; // Указываем, что класс принадлежит пакету org.example

// Импортируем исключение SQL и коллекции
import java.sql.SQLException;
import java.util.List;

// Класс для управления корзиной в главном окне приложения
public class MainWindowControllerTrash {
    // Ссылка на основной контроллер главного окна
    private MainWindowController controller;

    // Конструктор: получаем ссылку на основной контроллер
    public MainWindowControllerTrash(MainWindowController controller) {
        this.controller = controller;
    }

    // Метод для открытия окна с удалёнными заметками (корзины)
    public void openTrashDialog() {
        // Создаём диалоговое окно корзины, передаём в него представление и контроллер
        TrashDialog dialog = new TrashDialog(controller.getView(), controller);
        dialog.setVisible(true); // Показываем диалог
    }

    // Метод для получения списка удалённых заметок из базы данных
    public List<Note> getTrashNotes() throws SQLException {
        // Вызываем соответствующий метод у обработчика базы данных
        return DatabaseHandlerTrash.getTrashNotes(controller.getUserId());
    }

    // Метод для восстановления заметки из корзины по её ID
    public void restoreNoteFromTrash(int noteId) throws SQLException {
        // Восстанавливаем заметку
        DatabaseHandlerTrash.restoreNoteFromTrash(noteId);
        controller.loadNotes(); // Обновляем список заметок в интерфейсе
    }

    // Метод для полного удаления заметки из корзины по её ID
    public void permanentlyDeleteNote(int noteId) throws SQLException {
        DatabaseHandlerTrash.permanentlyDeleteNote(noteId); // Удаляем из базы
    }

    // Метод для полной очистки корзины пользователя
    public void emptyTrash() throws SQLException {
        DatabaseHandlerTrash.emptyTrash(controller.getUserId()); // Удаляем все удалённые заметки пользователя
    }
}
