package org.example; // Определяет пакет, в котором находится класс

// Импорт необходимых библиотек
import javax.swing.*; // Основные компоненты Swing
import javax.swing.border.EmptyBorder; // Для создания отступов в компонентах
import javax.swing.border.LineBorder; // Для создания линейных границ
import java.awt.*; // Базовые классы AWT
import java.awt.event.MouseAdapter; // Для обработки событий мыши
import java.awt.event.MouseEvent; // Для обработки событий мыши
import java.sql.SQLException; // Для обработки исключений SQL
import java.util.List; // Для работы со списками

// Класс диалогового окна для управления корзиной заметок
public class TrashDialog extends JDialog {
    private MainWindowController controller; // Контроллер главного окна для взаимодействия с данными
    private JPanel trashPanel; // Панель для отображения списка удаленных заметок

    // Конструктор диалогового окна
    public TrashDialog(JFrame parent, MainWindowController controller) {
        super(parent, "Корзина", true); // Вызов конструктора родительского класса с заголовком и модальностью
        this.controller = controller; // Сохранение ссылки на контроллер

        setSize(1200, 600); // Установка размеров окна
        setLocationRelativeTo(parent); // Центрирование окна относительно родительского

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)); // Создание основной панели с отступами
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Установка отступов от краев
        mainPanel.setBackground(Color.WHITE); // Установка белого фона

        // Заголовок
        JLabel titleLabel = new JLabel("Корзина", SwingConstants.CENTER); // Создание метки с заголовком по центру
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24)); // Установка шрифта для заголовка
        titleLabel.setForeground(Color.DARK_GRAY); // Установка цвета текста
        mainPanel.add(titleLabel, BorderLayout.NORTH); // Добавление заголовка в верхнюю часть панели

        // Панель для списка удаленных заметок
        trashPanel = new JPanel(); // Создание панели для списка удаленных заметок
        trashPanel.setLayout(new BoxLayout(trashPanel, BoxLayout.Y_AXIS)); // Установка вертикального расположения элементов
        trashPanel.setBackground(Color.WHITE); // Установка белого фона

        JScrollPane scrollPane = new JScrollPane(trashPanel); // Создание прокручиваемой панели для списка заметок
        scrollPane.setBorder(null); // Удаление границы у прокручиваемой панели
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Установка шага прокрутки
        mainPanel.add(scrollPane, BorderLayout.CENTER); // Добавление прокручиваемой панели в центр основной панели

        // Кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Создание панели для кнопок с выравниванием вправо
        buttonPanel.setBackground(Color.WHITE); // Установка белого фона

        JButton emptyTrashButton = createStyledButton("Очистить корзину"); // Создание стилизованной кнопки очистки корзины
        emptyTrashButton.addActionListener(e -> { // Добавление обработчика события нажатия
            int confirm = JOptionPane.showConfirmDialog(this, // Отображение диалога подтверждения
                    "Вы уверены, что хотите очистить корзину? Все заметки будут удалены безвозвратно.",
                    "Подтверждение",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) { // Если пользователь подтвердил очистку
                try {
                    controller.emptyTrash(); // Вызов метода контроллера для очистки корзины
                } catch (SQLException ex) {
                    throw new RuntimeException(ex); // Обработка исключения
                }
                loadTrashNotes(); // Перезагрузка списка удаленных заметок
            }
        });

        JButton closeButton = createStyledButton("Закрыть"); // Создание стилизованной кнопки закрытия
        closeButton.addActionListener(e -> dispose()); // Добавление обработчика события нажатия для закрытия окна

        buttonPanel.add(emptyTrashButton); // Добавление кнопки очистки корзины на панель кнопок
        buttonPanel.add(closeButton); // Добавление кнопки закрытия на панель кнопок
        mainPanel.add(buttonPanel, BorderLayout.SOUTH); // Добавление панели кнопок в нижнюю часть основной панели

        add(mainPanel); // Добавление основной панели в диалоговое окно

        // Загружаем удаленные заметки
        loadTrashNotes(); // Вызов метода загрузки удаленных заметок
    }

    // Метод для загрузки и отображения удаленных заметок
    private void loadTrashNotes() {
        trashPanel.removeAll(); // Удаление всех компонентов с панели удаленных заметок

        try {
            List<Note> trashNotes = controller.getTrashNotes(); // Получение списка удаленных заметок

            if (trashNotes.isEmpty()) { // Проверка, есть ли удаленные заметки
                JLabel emptyLabel = new JLabel("Корзина пуста", SwingConstants.CENTER); // Создание метки с сообщением
                emptyLabel.setFont(new Font("SansSerif", Font.ITALIC, 16)); // Установка шрифта
                emptyLabel.setForeground(Color.GRAY); // Установка цвета текста
                emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Выравнивание по центру
                trashPanel.add(Box.createVerticalStrut(50)); // Добавление вертикального отступа
                trashPanel.add(emptyLabel); // Добавление метки на панель
            } else {
                for (Note note : trashNotes) { // Перебор всех удаленных заметок
                    JPanel notePanel = createNotePanel(note); // Создание панели для отображения заметки
                    trashPanel.add(notePanel); // Добавление панели заметки на основную панель
                    trashPanel.add(Box.createVerticalStrut(10)); // Добавление вертикального отступа между заметками
                }
            }

            trashPanel.revalidate(); // Перерисовка компонентов панели
            trashPanel.repaint(); // Обновление отображения панели
        } catch (Exception e) {
            e.printStackTrace(); // Вывод информации об ошибке в консоль
            JOptionPane.showMessageDialog(this, "Ошибка при загрузке удаленных заметок: " + e.getMessage()); // Отображение сообщения об ошибке
        }
    }

    // Метод для создания панели отображения одной удаленной заметки
    private JPanel createNotePanel(Note note) {
        JPanel panel = new JPanel(new BorderLayout(5, 5)); // Создание панели с отступами
        panel.setBackground(new Color(250, 250, 250)); // Установка светло-серого фона
        panel.setBorder(BorderFactory.createCompoundBorder( // Создание составной границы
                new LineBorder(new Color(220, 220, 220), 1), // Внешняя граница - тонкая линия
                new EmptyBorder(10, 10, 10, 10) // Внутренние отступы
        ));

        // Заголовок заметки и информация о времени удаления
        int daysSinceDeletion = note.getDaysSinceDeletion(); // Получение количества дней с момента удаления
        String daysText = daysSinceDeletion == 0 ? "сегодня" : // Форматирование текста о времени удаления
                daysSinceDeletion == 1 ? "вчера" :
                        daysSinceDeletion + " дн. назад";

        JLabel titleLabel = new JLabel( // Создание метки с заголовком и информацией о времени удаления
                "<html><b>" + note.getTitle() + "</b> (" + note.getNoteType() + ")<br><i>" +
                        "Удалено: " + daysText + "</i></html>"
        );
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14)); // Установка шрифта
        titleLabel.setForeground(Color.DARK_GRAY); // Установка цвета текста
        panel.add(titleLabel, BorderLayout.NORTH); // Добавление заголовка в верхнюю часть панели

        // Содержимое заметки
        JTextArea contentArea = new JTextArea(note.getContent()); // Создание текстовой области с содержимым заметки
        contentArea.setEditable(false); // Запрет редактирования
        contentArea.setWrapStyleWord(true); // Перенос по словам
        contentArea.setLineWrap(true); // Включение переноса строк
        contentArea.setFont(new Font("SansSerif", Font.PLAIN, 14)); // Установка шрифта
        contentArea.setForeground(Color.DARK_GRAY); // Установка цвета текста
        contentArea.setBackground(new Color(250, 250, 250)); // Установка фона, совпадающего с фоном панели
        contentArea.setBorder(null); // Удаление границы

        // Ограничиваем высоту текстовой области
        int rows = Math.min(5, contentArea.getText().split("\n").length); // Расчет количества строк для отображения
        contentArea.setRows(rows); // Установка количества строк

        panel.add(contentArea, BorderLayout.CENTER); // Добавление содержимого в центр панели

        // Кнопки управления
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0)); // Создание панели для кнопок с выравниванием вправо
        buttonPanel.setBackground(new Color(250, 250, 250)); // Установка фона, совпадающего с фоном панели

        JButton restoreButton = createStyledButton("Восстановить"); // Создание стилизованной кнопки восстановления
        restoreButton.addActionListener(e -> { // Добавление обработчика события нажатия
            try {
                controller.restoreNoteFromTrash(note.getId()); // Вызов метода контроллера для восстановления заметки
                loadTrashNotes(); // Перезагрузка списка удаленных заметок
                JOptionPane.showMessageDialog(this, "Заметка восстановлена!"); // Отображение сообщения об успешном восстановлении
            } catch (Exception ex) {
                ex.printStackTrace(); // Вывод информации об ошибке в консоль
                JOptionPane.showMessageDialog(this, "Ошибка при восстановлении заметки: " + ex.getMessage()); // Отображение сообщения об ошибке
            }
        });

        JButton deleteButton = createStyledButton("Удалить навсегда"); // Создание стилизованной кнопки удаления
        deleteButton.addActionListener(e -> { // Добавление обработчика события нажатия
            int confirm = JOptionPane.showConfirmDialog(this, // Отображение диалога подтверждения
                    "Вы уверены, что хотите удалить эту заметку навсегда?",
                    "Подтверждение",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) { // Если пользователь подтвердил удаление
                try {
                    controller.permanentlyDeleteNote(note.getId()); // Вызов метода контроллера для окончательного удаления заметки
                    loadTrashNotes(); // Перезагрузка списка удаленных заметок
                } catch (Exception ex) {
                    ex.printStackTrace(); // Вывод информации об ошибке в консоль
                    JOptionPane.showMessageDialog(this, "Ошибка при удалении заметки: " + ex.getMessage()); // Отображение сообщения об ошибке
                }
            }
        });

        buttonPanel.add(restoreButton); // Добавление кнопки восстановления на панель кнопок
        buttonPanel.add(deleteButton); // Добавление кнопки удаления на панель кнопок
        panel.add(buttonPanel, BorderLayout.SOUTH); // Добавление панели кнопок в нижнюю часть панели заметки

        return panel; // Возврат созданной панели заметки
    }

    // Метод для создания стилизованной кнопки
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text); // Создание кнопки с указанным текстом
        button.setBackground(Color.WHITE); // Установка белого фона
        button.setForeground(Color.DARK_GRAY); // Установка темно-серого цвета текста
        button.setFocusPainted(false); // Отключение отображения фокуса
        button.setBorderPainted(false); // Отключение отображения границы
        button.setFont(new Font("SansSerif", Font.PLAIN, 14)); // Установка шрифта
        button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Установка курсора в виде руки при наведении
        button.setOpaque(true); // Включение непрозрачности
        button.setContentAreaFilled(false); // Отключение заливки области содержимого

        // Добавление обработчика событий мыши для эффекта при наведении
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) { // При наведении мыши
                button.setFont(new Font("SansSerif", Font.BOLD, 14)); // Изменение шрифта на жирный
            }
            public void mouseExited(MouseEvent evt) { // При уходе мыши
                button.setFont(new Font("SansSerif", Font.PLAIN, 14)); // Возврат обычного шрифта
            }
        });

        return button; // Возврат созданной кнопки
    }
}