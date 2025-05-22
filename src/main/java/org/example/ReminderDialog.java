package org.example; // Определяет пакет, в котором находится класс

// Импорт необходимых библиотек Swing и AWT для создания графического интерфейса
import javax.swing.*; // Основные компоненты Swing
import javax.swing.border.EmptyBorder; // Для создания отступов в компонентах
import java.awt.*; // Базовые классы AWT
import java.time.LocalDate; // Для работы с датами
import java.time.format.DateTimeFormatter; // Для форматирования дат
import java.util.List; // Для работы со списками

// Класс диалогового окна для управления напоминаниями
public class ReminderDialog extends JDialog {
    private MainWindowController controller; // Контроллер главного окна для взаимодействия с данными
    private LocalDate selectedDate; // Выбранная дата, для которой отображаются напоминания
    private JPanel remindersPanel; // Панель для отображения списка напоминаний

    // Конструктор диалогового окна
    public ReminderDialog(JFrame parent, MainWindowController controller, LocalDate selectedDate) {
        super(parent, "Напоминания на " + selectedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), true); // Вызов конструктора родительского класса с заголовком
        this.controller = controller; // Сохранение ссылки на контроллер
        this.selectedDate = selectedDate; // Сохранение выбранной даты

        setSize(520, 400); // Установка размеров окна
        setLocationRelativeTo(parent); // Центрирование окна относительно родительского

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)); // Создание основной панели с отступами
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Установка отступов от краев
        mainPanel.setBackground(Color.WHITE); // Установка белого фона

        // Заголовок
        JLabel titleLabel = new JLabel("Напоминания на " + selectedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), SwingConstants.CENTER); // Создание метки с заголовком
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16)); // Установка шрифта для заголовка
        mainPanel.add(titleLabel, BorderLayout.NORTH); // Добавление заголовка в верхнюю часть панели

        // Панель для списка напоминаний
        remindersPanel = new JPanel(); // Создание панели для списка напоминаний
        remindersPanel.setLayout(new BoxLayout(remindersPanel, BoxLayout.Y_AXIS)); // Установка вертикального расположения элементов
        remindersPanel.setBackground(Color.WHITE); // Установка белого фона

        JScrollPane scrollPane = new JScrollPane(remindersPanel); // Создание прокручиваемой панели для списка напоминаний
        scrollPane.setBorder(null); // Удаление границы у прокручиваемой панели
        mainPanel.add(scrollPane, BorderLayout.CENTER); // Добавление прокручиваемой панели в центр основной панели

        // Кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Создание панели для кнопок с выравниванием вправо
        buttonPanel.setBackground(Color.WHITE); // Установка белого фона

        JButton addButton = createStyledButton("Добавить напоминание"); // Создание стилизованной кнопки добавления
        addButton.addActionListener(e -> openAddReminderDialog()); // Добавление обработчика события нажатия

        JButton closeButton = createStyledButton("Закрыть"); // Создание стилизованной кнопки закрытия
        closeButton.addActionListener(e -> dispose()); // Добавление обработчика события нажатия для закрытия окна

        buttonPanel.add(addButton); // Добавление кнопки добавления на панель кнопок
        buttonPanel.add(closeButton); // Добавление кнопки закрытия на панель кнопок
        mainPanel.add(buttonPanel, BorderLayout.SOUTH); // Добавление панели кнопок в нижнюю часть основной панели

        add(mainPanel); // Добавление основной панели в диалоговое окно

        // Загружаем напоминания
        loadReminders(); // Вызов метода загрузки напоминаний
    }

    // Метод для загрузки и отображения напоминаний
    private void loadReminders() {
        remindersPanel.removeAll(); // Удаление всех компонентов с панели напоминаний

        try {
            List<Reminder> reminders = controller.getRemindersForDate(selectedDate); // Получение списка напоминаний на выбранную дату

            if (reminders.isEmpty()) { // Проверка, есть ли напоминания
                JLabel noRemindersLabel = new JLabel("На этот день нет напоминаний", SwingConstants.CENTER); // Создание метки с сообщением
                noRemindersLabel.setFont(new Font("SansSerif", Font.ITALIC, 14)); // Установка шрифта
                noRemindersLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Выравнивание по центру
                remindersPanel.add(noRemindersLabel); // Добавление метки на панель
            } else {
                for (Reminder reminder : reminders) { // Перебор всех напоминаний
                    JPanel reminderPanel = createReminderPanel(reminder); // Создание панели для отображения напоминания
                    remindersPanel.add(reminderPanel); // Добавление панели напоминания на основную панель
                    remindersPanel.add(Box.createVerticalStrut(10)); // Добавление вертикального отступа между напоминаниями
                }
            }

            remindersPanel.revalidate(); // Перерисовка компонентов панели
            remindersPanel.repaint(); // Обновление отображения панели
        } catch (Exception e) {
            e.printStackTrace(); // Вывод информации об ошибке в консоль
            JOptionPane.showMessageDialog(this, "Ошибка при загрузке напоминаний: " + e.getMessage()); // Отображение сообщения об ошибке
        }
    }

    // Метод для создания панели отображения одного напоминания
    private JPanel createReminderPanel(Reminder reminder) {
        JPanel panel = new JPanel(new BorderLayout(5, 5)); // Создание панели с отступами
        panel.setBackground(new Color(245, 245, 245)); // Установка светло-серого фона
        panel.setBorder(BorderFactory.createCompoundBorder( // Создание составной границы
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1), // Внешняя граница - тонкая линия
                new EmptyBorder(10, 10, 10, 10) // Внутренние отступы
        ));

        // Заголовок напоминания
        JLabel titleLabel = new JLabel(reminder.getTitle()); // Создание метки с заголовком напоминания
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14)); // Установка шрифта
        panel.add(titleLabel, BorderLayout.NORTH); // Добавление заголовка в верхнюю часть панели

        // Описание напоминания
        JTextArea descriptionArea = new JTextArea(reminder.getDescription()); // Создание текстовой области с описанием
        descriptionArea.setEditable(false); // Запрет редактирования
        descriptionArea.setLineWrap(true); // Включение переноса строк
        descriptionArea.setWrapStyleWord(true); // Перенос по словам
        descriptionArea.setFont(new Font("SansSerif", Font.PLAIN, 12)); // Установка шрифта
        descriptionArea.setBackground(new Color(245, 245, 245)); // Установка фона, совпадающего с фоном панели
        descriptionArea.setBorder(null); // Удаление границы
        panel.add(descriptionArea, BorderLayout.CENTER); // Добавление описания в центр панели

        // Кнопки управления
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0)); // Создание панели для кнопок с выравниванием вправо
        buttonPanel.setBackground(new Color(245, 245, 245)); // Установка фона, совпадающего с фоном панели

        JCheckBox completedCheckBox = new JCheckBox("Выполнено"); // Создание флажка для отметки выполнения
        completedCheckBox.setSelected(reminder.isCompleted()); // Установка начального состояния флажка
        completedCheckBox.setBackground(new Color(245, 245, 245)); // Установка фона
        completedCheckBox.addActionListener(e -> { // Добавление обработчика события изменения состояния
            try {
                controller.updateReminderStatus(reminder.getId(), completedCheckBox.isSelected()); // Обновление статуса напоминания
                loadReminders(); // Перезагрузка списка напоминаний
            } catch (Exception ex) {
                ex.printStackTrace(); // Вывод информации об ошибке в консоль
                JOptionPane.showMessageDialog(this, "Ошибка при обновлении статуса: " + ex.getMessage()); // Отображение сообщения об ошибке
            }
        });

        JButton editButton = createStyledButton("Изменить"); // Создание стилизованной кнопки редактирования
        editButton.addActionListener(e -> openEditReminderDialog(reminder)); // Добавление обработчика события нажатия

        JButton deleteButton = createStyledButton("Удалить"); // Создание стилизованной кнопки удаления
        deleteButton.addActionListener(e -> { // Добавление обработчика события нажатия
            int confirm = JOptionPane.showConfirmDialog(this, // Отображение диалога подтверждения
                    "Вы уверены, что хотите удалить это напоминание?",
                    "Подтверждение удаления",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) { // Если пользователь подтвердил удаление
                try {
                    controller.deleteReminder(reminder.getId()); // Удаление напоминания
                    loadReminders(); // Перезагрузка списка напоминаний
                    controller.updateCalendar(); // Обновление календаря
                } catch (Exception ex) {
                    ex.printStackTrace(); // Вывод информации об ошибке в консоль
                    JOptionPane.showMessageDialog(this, "Ошибка при удалении: " + ex.getMessage()); // Отображение сообщения об ошибке
                }
            }
        });

        buttonPanel.add(completedCheckBox); // Добавление флажка на панель кнопок
        buttonPanel.add(editButton); // Добавление кнопки редактирования на панель кнопок
        buttonPanel.add(deleteButton); // Добавление кнопки удаления на панель кнопок
        panel.add(buttonPanel, BorderLayout.SOUTH); // Добавление панели кнопок в нижнюю часть панели напоминания

        return panel; // Возврат созданной панели напоминания
    }

    // Метод для открытия диалога добавления нового напоминания
    private void openAddReminderDialog() {
        JDialog dialog = new JDialog(this, "Добавить напоминание", true); // Создание модального диалогового окна
        dialog.setSize(400, 300); // Установка размеров окна
        dialog.setLocationRelativeTo(this); // Центрирование окна относительно родительского

        JPanel panel = new JPanel(new BorderLayout(10, 10)); // Создание основной панели с отступами
        panel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Установка отступов от краев
        panel.setBackground(Color.WHITE); // Установка белого фона

        JPanel inputPanel = new JPanel(); // Создание панели для полей ввода
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS)); // Установка вертикального расположения элементов
        inputPanel.setBackground(Color.WHITE); // Установка белого фона

        // Заголовок
        JLabel titleLabel = new JLabel("Заголовок"); // Создание метки для поля заголовка
        JTextField titleField = new JTextField(); // Создание текстового поля для ввода заголовка
        titleField.setFont(new Font("SansSerif", Font.PLAIN, 14)); // Установка шрифта

        // Описание
        JLabel descriptionLabel = new JLabel("Описание"); // Создание метки для поля описания
        JTextArea descriptionArea = new JTextArea(5, 20); // Создание текстовой области для ввода описания
        descriptionArea.setLineWrap(true); // Включение переноса строк
        descriptionArea.setWrapStyleWord(true); // Перенос по словам
        descriptionArea.setFont(new Font("SansSerif", Font.PLAIN, 14)); // Установка шрифта
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea); // Создание прокручиваемой панели для текстовой области

        inputPanel.add(titleLabel); // Добавление метки заголовка на панель ввода
        inputPanel.add(titleField); // Добавление поля заголовка на панель ввода
        inputPanel.add(Box.createVerticalStrut(10)); // Добавление вертикального отступа
        inputPanel.add(descriptionLabel); // Добавление метки описания на панель ввода
        inputPanel.add(descriptionScrollPane); // Добавление прокручиваемой панели с полем описания

        panel.add(inputPanel, BorderLayout.CENTER); // Добавление панели ввода в центр основной панели

        // Кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Создание панели для кнопок с выравниванием вправо
        buttonPanel.setBackground(Color.WHITE); // Установка белого фона

        JButton saveButton = createStyledButton("Сохранить"); // Создание стилизованной кнопки сохранения
        saveButton.addActionListener(e -> { // Добавление обработчика события нажатия
            String title = titleField.getText().trim(); // Получение введенного заголовка
            String description = descriptionArea.getText().trim(); // Получение введенного описания

            if (title.isEmpty()) { // Проверка, что заголовок не пустой
                JOptionPane.showMessageDialog(dialog, "Заголовок не может быть пустым!"); // Отображение сообщения об ошибке
                return; // Прерывание выполнения метода
            }

            try {
                controller.addReminder(title, description, selectedDate); // Добавление нового напоминания
                dialog.dispose(); // Закрытие диалогового окна
                loadReminders(); // Перезагрузка списка напоминаний
                controller.updateCalendar(); // Обновление календаря
            } catch (Exception ex) {
                ex.printStackTrace(); // Вывод информации об ошибке в консоль
                JOptionPane.showMessageDialog(dialog, "Ошибка при сохранении: " + ex.getMessage()); // Отображение сообщения об ошибке
            }
        });

        JButton cancelButton = createStyledButton("Отмена"); // Создание стилизованной кнопки отмены
        cancelButton.addActionListener(e -> dialog.dispose()); // Добавление обработчика события нажатия для закрытия окна

        buttonPanel.add(saveButton); // Добавление кнопки сохранения на панель кнопок
        buttonPanel.add(cancelButton); // Добавление кнопки отмены на панель кнопок
        panel.add(buttonPanel, BorderLayout.SOUTH); // Добавление панели кнопок в нижнюю часть основной панели

        dialog.add(panel); // Добавление основной панели в диалоговое окно
        dialog.setVisible(true); // Отображение диалогового окна
    }

    // Метод для открытия диалога редактирования существующего напоминания
    private void openEditReminderDialog(Reminder reminder) {
        JDialog dialog = new JDialog(this, "Изменить напоминание", true); // Создание модального диалогового окна
        dialog.setSize(400, 300); // Установка размеров окна
        dialog.setLocationRelativeTo(this); // Центрирование окна относительно родительского

        JPanel panel = new JPanel(new BorderLayout(10, 10)); // Создание основной панели с отступами
        panel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Установка отступов от краев
        panel.setBackground(Color.WHITE); // Установка белого фона

        JPanel inputPanel = new JPanel(); // Создание панели для полей ввода
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS)); // Установка вертикального расположения элементов
        inputPanel.setBackground(Color.WHITE); // Установка белого фона

        // Заголовок
        JLabel titleLabel = new JLabel("Заголовок"); // Создание метки для поля заголовка
        JTextField titleField = new JTextField(reminder.getTitle()); // Создание текстового поля с текущим заголовком
        titleField.setFont(new Font("SansSerif", Font.PLAIN, 14)); // Установка шрифта

        // Описание
        JLabel descriptionLabel = new JLabel("Описание"); // Создание метки для поля описания
        JTextArea descriptionArea = new JTextArea(reminder.getDescription(), 5, 20); // Создание текстовой области с текущим описанием
        descriptionArea.setLineWrap(true); // Включение переноса строк
        descriptionArea.setWrapStyleWord(true); // Перенос по словам
        descriptionArea.setFont(new Font("SansSerif", Font.PLAIN, 14)); // Установка шрифта
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea); // Создание прокручиваемой панели для текстовой области

        inputPanel.add(titleLabel); // Добавление метки заголовка на панель ввода
        inputPanel.add(titleField); // Добавление поля заголовка на панель ввода
        inputPanel.add(Box.createVerticalStrut(10)); // Добавление вертикального отступа
        inputPanel.add(descriptionLabel); // Добавление метки описания на панель ввода
        inputPanel.add(descriptionScrollPane); // Добавление прокручиваемой панели с полем описания

        panel.add(inputPanel, BorderLayout.CENTER); // Добавление панели ввода в центр основной панели

        // Кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Создание панели для кнопок с выравниванием вправо
        buttonPanel.setBackground(Color.WHITE); // Установка белого фона

        JButton saveButton = createStyledButton("Сохранить"); // Создание стилизованной кнопки сохранения
        saveButton.addActionListener(e -> { // Добавление обработчика события нажатия
            String title = titleField.getText().trim(); // Получение введенного заголовка
            String description = descriptionArea.getText().trim(); // Получение введенного описания

            if (title.isEmpty()) { // Проверка, что заголовок не пустой
                JOptionPane.showMessageDialog(dialog, "Заголовок не может быть пустым!"); // Отображение сообщения об ошибке
                return; // Прерывание выполнения метода
            }

            try {
                controller.updateReminder(reminder.getId(), title, description, selectedDate, reminder.isCompleted()); // Обновление напоминания
                dialog.dispose(); // Закрытие диалогового окна
                loadReminders(); // Перезагрузка списка напоминаний
            } catch (Exception ex) {
                ex.printStackTrace(); // Вывод информации об ошибке в консоль
                JOptionPane.showMessageDialog(dialog, "Ошибка при сохранении: " + ex.getMessage()); // Отображение сообщения об ошибке
            }
        });

        JButton cancelButton = createStyledButton("Отмена"); // Создание стилизованной кнопки отмены
        cancelButton.addActionListener(e -> dialog.dispose()); // Добавление обработчика события нажатия для закрытия окна

        buttonPanel.add(saveButton); // Добавление кнопки сохранения на панель кнопок
        buttonPanel.add(cancelButton); // Добавление кнопки отмены на панель кнопок
        panel.add(buttonPanel, BorderLayout.SOUTH); // Добавление панели кнопок в нижнюю часть основной панели

        dialog.add(panel); // Добавление основной панели в диалоговое окно
        dialog.setVisible(true); // Отображение диалогового окна
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
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { // При наведении мыши
                button.setFont(new Font("SansSerif", Font.BOLD, 14)); // Изменение шрифта на жирный
            }
            public void mouseExited(java.awt.event.MouseEvent evt) { // При уходе мыши
                button.setFont(new Font("SansSerif", Font.PLAIN, 14)); // Возврат обычного шрифта
            }
        });

        return button; // Возврат созданной кнопки
    }
}