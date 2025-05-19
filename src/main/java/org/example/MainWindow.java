package org.example;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class MainWindow extends JFrame {
    private JPanel mainPanel;
    private JPanel notesPanel;
    private boolean isResizing = false;  // Флаг для отслеживания изменения размера окна
    private MainWindowController controller;
    private CalendarPanel calendarPanel;

    // Конструктор основного окна приложения (Notes App)
    public MainWindow(int userId, String username, String email) {
        // Настраиваем основные параметры окна
        setTitle("Notes App");                // Заголовок окна
        setResizable(true);                   // Возможность изменения размеров окна
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Закрытие программы при закрытии окна
        setExtendedState(JFrame.MAXIMIZED_BOTH);        // Окно открывается в полноэкранном режиме
        setLocationRelativeTo(null);          // Центрируем окно на экране

        // Создаем контроллер
        controller = new MainWindowController(this, userId, username, email);

        // Создаем основную панель с менеджером компоновки BorderLayout
        mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(Color.WHITE); // Устанавливаем белый фон основной панели
        add(mainPanel);                       // Добавляем основную панель в окно

        // Настраиваем верхнюю панель с информацией о пользователе
        setupHeaderPanel(userId, username);

        // Создаем панель с разделением на заметки и календарь
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(0.7); // 70% для заметок, 30% для календаря
        splitPane.setResizeWeight(0.7);
        splitPane.setBorder(null);
        splitPane.setDividerSize(5);
        splitPane.setContinuousLayout(true);

        // Левая панель - заметки
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.WHITE);

        // Настраиваем панель для отображения заметок
        setupNotesPanel();
        leftPanel.add(createNotesContainer(), BorderLayout.CENTER);

        // Кнопка корзины
        JButton trashButton = createStyledButton("Корзина");
        trashButton.addActionListener(e -> controller.openTrashDialog());

// Кнопка добавления заметки
        JButton addNoteButton = createStyledButton("Добавить заметку");
        addNoteButton.addActionListener(e -> controller.openAddNoteDialog());

        JPanel addButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addButtonPanel.setBackground(Color.WHITE);
        addButtonPanel.add(trashButton);     // Добавляем кнопку корзины
        addButtonPanel.add(addNoteButton);   // Добавляем кнопку добавления заметки
        leftPanel.add(addButtonPanel, BorderLayout.SOUTH);

        // Правая панель - календарь
        calendarPanel = new CalendarPanel(controller);
        controller.setCalendarPanel(calendarPanel);

        // Добавляем панели в разделитель
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(calendarPanel);

        mainPanel.add(splitPane, BorderLayout.CENTER);

        // Добавляем слушатель изменения размеров окна
        setupResizeListener();

        // Загружаем заметки пользователя
        controller.loadNotes();
    }

    // Метод настройки верхней панели (хедера) приложения
    private void setupHeaderPanel(int userId, String username) {
        // Создаем панель хедера с горизонтальным расположением компонентов
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
        headerPanel.setBackground(Color.WHITE); // Белый фон для хедера

        // Добавляем панель поиска на хедер
        headerPanel.add(setupSearchPanel());
        // Добавляем горизонтальный промежуток между панелью поиска и панелью пользователя
        headerPanel.add(Box.createHorizontalStrut(20));

        // Создаем метку с именем пользователя (кликабельная)
        JLabel usernameLabel = new JLabel(username);
        usernameLabel.setForeground(Color.BLUE); // Синий цвет текста (стиль ссылки)
        usernameLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Курсор "рука"

        // Обработчик клика на имя пользователя (открывает профиль)
        usernameLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                controller.openUserProfile(); // Открытие профиля пользователя через контроллер
            }
        });

        // Создаем панель пользователя (имя пользователя)
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        userPanel.setBackground(Color.WHITE); // Белый фон панели
        userPanel.add(usernameLabel);         // Добавляем имя пользователя на панель

        // Добавляем панель пользователя в хедер
        headerPanel.add(userPanel);

        // Устанавливаем отступ сверху для главной панели
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        // Добавляем хедер в верхнюю часть основной панели
        mainPanel.add(headerPanel, BorderLayout.NORTH);
    }

    // Метод настройки панели поиска заметок
    private JPanel setupSearchPanel() {
        // Создаем текстовое поле для ввода поискового запроса
        JTextField searchField = new JTextField();
        searchField.setForeground(Color.DARK_GRAY); // Цвет текста (темно-серый)
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14)); // Шрифт текста

        // Настраиваем границу текстового поля с внутренними отступами
        searchField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true), // Внешняя рамка (светло-серая)
                new EmptyBorder(5, 10, 5, 10) // Внутренний отступ (поля)
        ));

        // Создаем кнопку поиска
        JButton searchButton = createStyledButton("Поиск");
        // Добавляем обработчик нажатия кнопки поиска через контроллер
        searchButton.addActionListener(e -> controller.searchNotes(searchField.getText()));

        // Создаем панель поиска с компоновкой BorderLayout
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE); // Белый фон панели
        panel.add(searchField, BorderLayout.CENTER); // Поле поиска занимает центр
        panel.add(searchButton, BorderLayout.EAST);  // Кнопка поиска справа

        // Добавляем отступ к левой стороне панели
        panel.setBorder(new EmptyBorder(0, 20, 0, 0));
        return panel; // Возвращаем настроенную панель поиска
    }

    private void setupNotesPanel() {
        notesPanel = new JPanel();
        notesPanel.setLayout(new BoxLayout(notesPanel, BoxLayout.Y_AXIS));
        notesPanel.setBackground(Color.WHITE);
    }

    private JPanel createNotesContainer() {
        // Заголовок "ЗАМЕТКИ"
        JLabel titleLabel = new JLabel("ЗАМЕТКИ", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.DARK_GRAY);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(titleLabel);

        JScrollPane scrollPane = new JScrollPane(notesPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(null);

        // Кастомизация скроллбара
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
                g.setColor(Color.GRAY);
                g.fillRect(r.x, r.y, r.width, r.height);
            }

            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
                g.setColor(Color.WHITE);
                g.fillRect(r.x, r.y, r.width, r.height);
            }
        });
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(15, Integer.MAX_VALUE));

        // Основной контейнер
        JPanel notesContainer = new JPanel(new BorderLayout());
        notesContainer.setBackground(Color.WHITE);
        notesContainer.add(titlePanel, BorderLayout.NORTH);
        notesContainer.add(scrollPane, BorderLayout.CENTER);

        return notesContainer;
    }

    // Метод настройки слушателя изменения размеров окна
    private void setupResizeListener() {
        // Добавляем слушатель на изменение размеров окна
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // Проверяем, не выполняется ли уже процесс изменения размера
                if (!isResizing) {
                    isResizing = true; // Флаг предотвращает повторные вызовы

                    // Отложенная перезагрузка заметок после изменения размера
                    SwingUtilities.invokeLater(() -> {
                        controller.loadNotes();    // Перезагружаем заметки с учетом нового размера окна
                        isResizing = false; // Снимаем флаг после завершения
                    });
                }
            }
        });
    }

    // Метод для создания диалогового окна профиля пользователя
    public JDialog createProfileDialog() {
        // Создаем диалоговое окно с заголовком и модальностью (блокирует основное окно)
        JDialog profileDialog = new JDialog(this, "Профиль пользователя", true);
        profileDialog.setResizable(true);            // Позволяем изменять размер окна профиля
        profileDialog.setLocationRelativeTo(this);  // Центрируем окно относительно главного окна

        // Создаем панель для содержимого профиля с вертикальным расположением элементов
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.setBackground(Color.WHITE);     // Белый фон панели
        profilePanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Отступы вокруг содержимого

        // Метка с ником пользователя, выравнена по центру
        JLabel userLabel = new JLabel("Ник: " + controller.getUsername());
        userLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Метка с email пользователя, выравнена по центру
        JLabel emailLabel = new JLabel("Почта: " + controller.getEmail());
        emailLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Кнопка "Закрыть" для закрытия окна профиля
        JButton closeButton = createStyledButton("Закрыть");
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.addActionListener(e -> profileDialog.dispose());

        // Кнопка "Выйти из аккаунта" с подтверждением выхода
        JButton logoutButton = createStyledButton("Выйти из аккаунта");
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.addActionListener(e -> controller.showExitConfirmation(profileDialog));

        // Добавляем компоненты на панель с отступами между ними
        profilePanel.add(userLabel);
        profilePanel.add(Box.createVerticalStrut(10));
        profilePanel.add(emailLabel);
        profilePanel.add(Box.createVerticalStrut(30));
        profilePanel.add(closeButton);
        profilePanel.add(Box.createVerticalStrut(10));
        profilePanel.add(logoutButton);

        // Добавляем панель в диалоговое окно
        profileDialog.add(profilePanel);
        profileDialog.pack();           // Автоматический подбор размеров окна под содержимое

        return profileDialog;
    }

    // Метод для создания диалогового окна подтверждения выхода
    public JDialog createExitConfirmationDialog(JDialog parentDialog) {
        JDialog confirmDialog = new JDialog(parentDialog, "Подтверждение выхода", true);
        confirmDialog.setSize(400, 150);
        confirmDialog.setLocationRelativeTo(parentDialog);
        confirmDialog.setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        confirmDialog.add(mainPanel);

        mainPanel.add(new JLabel("Вы точно хотите выйти из аккаунта?", SwingConstants.CENTER));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(buttonPanel);

        buttonPanel.add(createStyledButton("Да", e -> controller.logout(parentDialog)));
        buttonPanel.add(createStyledButton("Нет", e -> confirmDialog.dispose()));

        return confirmDialog;
    }

    // Вспомогательный метод для создания кнопки с обработчиком
    private JButton createStyledButton(String text, ActionListener action) {
        JButton button = createStyledButton(text);
        button.addActionListener(action);
        return button;
    }

    // Метод для создания стилизованной кнопки
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(Color.WHITE);
        button.setForeground(Color.DARK_GRAY);
        button.setFocusPainted(false);
        // Убираем отрисовку обводки
        button.setBorderPainted(false); // либо: button.setBorder(null);
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setContentAreaFilled(false);

        // Эффекты при наведении
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setFont(new Font("SansSerif", Font.BOLD, 14));
            }
            public void mouseExited(MouseEvent evt) {
                button.setFont(new Font("SansSerif", Font.PLAIN, 14));
            }
        });
        return button;
    }

    // Метод для отображения заметок в интерфейсе
    public void displayNotes(List<Note> notes) {
        // Очищаем панель с заметками перед новой отрисовкой
        notesPanel.removeAll();

        // Если заметок нет, показываем сообщение об отсутствии заметок
        if (notes.isEmpty()) {
            JPanel messagePanel = new JPanel();
            messagePanel.setBackground(Color.WHITE);

            JLabel noResultsLabel = new JLabel("Заметок пока нет... \uD83D\uDE22"); // Сообщение с эмодзи
            noResultsLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            noResultsLabel.setForeground(Color.GRAY);

            messagePanel.add(noResultsLabel);
            messagePanel.setLayout(new FlowLayout(FlowLayout.CENTER)); // Центрируем сообщение
            notesPanel.add(messagePanel);

        } else {
            // Если заметки есть, создаем сетку для их отображения
            JPanel gridPanel = new JPanel();
            gridPanel.setLayout(new GridBagLayout()); // Используем GridBagLayout для сетки карточек
            gridPanel.setBackground(Color.WHITE);
            GridBagConstraints gbc = new GridBagConstraints();

            int cardWidth = 530;  // Ширина каждой карточки заметки
            int cardHeight = 200; // Высота каждой карточки заметки

            // Проходим по всем заметкам и создаем для каждой карточку
            for (int i = 0; i < notes.size(); i++) {
                Note note = notes.get(i);

                // Панель карточки заметки с BorderLayout
                JPanel noteCard = new JPanel(new BorderLayout(5, 5));
                noteCard.setBackground(new Color(250, 250, 250)); // Светлый фон карточки
                noteCard.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Курсор "рука" при наведении

                // Открываем заметку по клику на карточку через контроллер
                noteCard.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        controller.openNote(note);
                    }
                });

                // Фиксируем размеры карточки
                noteCard.setPreferredSize(new Dimension(cardWidth, cardHeight));
                noteCard.setMaximumSize(new Dimension(cardWidth, cardHeight));
                noteCard.setMinimumSize(new Dimension(cardWidth, cardHeight));

                // Добавляем рамку с отступом вокруг карточки
                noteCard.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(new Color(220, 220, 220), 1, false),
                        new EmptyBorder(10, 10, 10, 10)
                ));

                // Заголовок заметки: жирный заголовок, тип заметки и дата создания
                JLabel titleLabel = new JLabel(
                        "<html><b>" + note.getTitle() + "</b> (" + note.getNoteType() + ")<br><i>" +
                                "Создано: " + note.getCreatedAt() + "</i></html>"
                );
                titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
                titleLabel.setForeground(Color.DARK_GRAY);
                noteCard.add(titleLabel, BorderLayout.NORTH);

                // Текстовое поле с содержимым заметки (не редактируемое)
                JTextArea contentArea = new JTextArea(note.getContent());
                contentArea.setEditable(false);
                contentArea.setWrapStyleWord(true); // Перенос слов
                contentArea.setLineWrap(true);      // Перенос строк
                contentArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
                contentArea.setForeground(Color.DARK_GRAY);
                contentArea.setBackground(new Color(250, 250, 250)); // Фон совпадает с карточкой
                contentArea.setBorder(null);
                noteCard.add(contentArea, BorderLayout.CENTER);

                // Панель кнопок "Редактировать" и "Удалить"
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
                buttonPanel.setBackground(new Color(250, 250, 250));

                JButton editButton = createStyledButton("Редактировать");
                editButton.addActionListener(e -> controller.openNote(note));

                JButton deleteButton = createStyledButton("Удалить");
                deleteButton.addActionListener(e -> controller.deleteNote(note));

                buttonPanel.add(editButton);
                buttonPanel.add(deleteButton);
                noteCard.add(buttonPanel, BorderLayout.SOUTH);

                // Расчет положения карточки в сетке (2 колонки)
                int row = i / 2;
                int col = i % 2;

                gbc.gridx = col;
                gbc.gridy = row;
                gbc.insets = new Insets(10, 10, 10, 10); // Отступы между карточками

                gridPanel.add(noteCard, gbc);

                // Отключаем фокусировку на карточках и тексте (для удобства UI)
                noteCard.setFocusable(false);
                contentArea.setFocusable(false);
            }

            // Устанавливаем layout и добавляем сетку в основную панель заметок
            notesPanel.setLayout(new BorderLayout());
            notesPanel.add(gridPanel, BorderLayout.CENTER);
        }

        // Обновляем UI после изменений
        notesPanel.revalidate();
        notesPanel.repaint();
    }

    // Метод для создания окна редактирования заметки
    public JFrame createNoteEditorFrame(Note note) {
        JFrame noteFrame = new JFrame("Редактор");
        noteFrame.setSize(600, 400);
        noteFrame.setLocationRelativeTo(this);

        JPanel notePanel = new JPanel(new BorderLayout(5, 5));
        notePanel.setBackground(Color.WHITE);
        notePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        noteFrame.add(notePanel);

        JTextField titleField = createTextField(note.getTitle(), Font.BOLD, 16);
        JTextArea contentArea = createTextArea(note.getContent());
        JComboBox<String> typeComboBox = createTypeComboBox(note.getNoteType());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.add(titleField);
        inputPanel.add(Box.createVerticalStrut(10));
        inputPanel.add(new JLabel("Тип заметки"));
        inputPanel.add(typeComboBox);
        inputPanel.add(Box.createVerticalStrut(10));
        inputPanel.add(new JScrollPane(contentArea));

        JButton saveButton = createStyledButton("Сохранить");
        saveButton.addActionListener(e -> controller.saveNote(note, titleField.getText(), contentArea.getText(), (String) typeComboBox.getSelectedItem(), noteFrame));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(saveButton);

        notePanel.add(inputPanel, BorderLayout.CENTER);
        notePanel.add(buttonPanel, BorderLayout.SOUTH);

        return noteFrame;
    }

    // Вспомогательный метод для создания текстового поля
    private JTextField createTextField(String text, int style, int size) {
        JTextField field = new JTextField(text);
        field.setFont(new Font("SansSerif", style, size));
        field.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        field.setBackground(Color.WHITE);
        field.setCaretColor(Color.BLACK);
        return field;
    }

    // Вспомогательный метод для создания текстовой области
    private JTextArea createTextArea(String text) {
        JTextArea area = new JTextArea(text, 10, 50);
        area.setWrapStyleWord(true);
        area.setLineWrap(true);
        area.setFont(new Font("SansSerif", Font.PLAIN, 14));
        area.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(5, 5, 5, 5)
        ));
        return area;
    }

    // Вспомогательный метод для создания выпадающего списка типов
    private JComboBox<String> createTypeComboBox(String selectedType) {
        JComboBox<String> comboBox = new JComboBox<>(new String[]{
                NoteType.ЛИЧНАЯ.name(),
                NoteType.РАБОЧАЯ.name(),
                NoteType.ИДЕЯ.name(),
                NoteType.ЗАДАЧА.name()
        });
        comboBox.setSelectedItem(selectedType);
        return comboBox;
    }

    // Метод для создания диалога добавления новой заметки
    public JDialog createAddNoteDialog() {
        JDialog dialog = new JDialog(this, "Добавить заметку", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel dialogPanel = new JPanel(new BorderLayout(10, 10));
        dialogPanel.setBackground(Color.WHITE);
        dialogPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBackground(Color.WHITE);

        // Заголовок
        JLabel titleLabel = new JLabel("Заголовок");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextField titleField = new JTextField();
        titleField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        inputPanel.add(titleLabel);
        inputPanel.add(titleField);

        // Содержание
        JLabel contentLabel = new JLabel("Содержание");
        JTextArea contentArea = new JTextArea(10, 30);
        contentArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JScrollPane contentScrollPane = new JScrollPane(contentArea);
        inputPanel.add(contentLabel);
        inputPanel.add(contentScrollPane);

        // Тип заметки
        JLabel typeLabel = new JLabel("Тип заметки");
        JComboBox<String> typeComboBox = new JComboBox<>();
        typeComboBox.addItem(NoteType.ЛИЧНАЯ.name());
        typeComboBox.addItem(NoteType.РАБОЧАЯ.name());
        typeComboBox.addItem(NoteType.ИДЕЯ.name());
        typeComboBox.addItem(NoteType.ЗАДАЧА.name());

        inputPanel.add(typeLabel);
        inputPanel.add(typeComboBox);

        dialogPanel.add(inputPanel, BorderLayout.CENTER);

        JButton saveButton = createStyledButton("Сохранить");
        saveButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String content = contentArea.getText().trim();
            String noteType = (String) typeComboBox.getSelectedItem();
            controller.addNote(title, content, noteType);
            dialog.dispose();
        });

        dialogPanel.add(saveButton, BorderLayout.SOUTH);
        dialog.add(dialogPanel);

        return dialog;
    }
}