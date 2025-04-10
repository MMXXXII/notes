package org.example;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class MainWindow extends JFrame {
    private int userId;
    private String username;
    private String email;
    private JPanel mainPanel;
    private JPanel notesPanel;
    private boolean isResizing = false;  // Флаг для отслеживания изменения размера окна

    public MainWindow(int userId, String username, String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;

        setTitle("Notes App");
        setResizable(true);  // Окно теперь будет изменяемым
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // Окно будет по центру

        // Делаем окно полноэкранным
        setExtendedState(JFrame.MAXIMIZED_BOTH);  // Максимизируем окно на весь экран

        mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(Color.WHITE);
        add(mainPanel);

        setupHeaderPanel(userId, username);
        setupNotesPanel();
        setupResizeListener();

        loadNotes();
    }



    private void setupHeaderPanel(int userId, String username) {
        // Панель для поиска
        JPanel searchPanel = setupSearchPanel();

        // Панель для никнейма
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Используем FlowLayout с выравниванием по левому краю
        userPanel.setBackground(Color.WHITE);

        JLabel usernameLabel = new JLabel(username);
        usernameLabel.setForeground(Color.BLUE);
        usernameLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        usernameLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openUserProfile(userId);
            }
        });

        userPanel.add(usernameLabel);

        // Панель для заголовка с BoxLayout для правильной адаптации компонентов
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS)); // Горизонтальное расположение компонентов
        headerPanel.setBackground(Color.WHITE);

        // Добавляем панели в заголовок
        headerPanel.add(searchPanel);
        headerPanel.add(Box.createHorizontalStrut(20)); // Добавляем горизонтальный отступ
        headerPanel.add(userPanel);

        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Помещаем все в mainPanel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
    }



    private JPanel setupSearchPanel() {
        JTextField searchField = new JTextField();
        searchField.setForeground(Color.DARK_GRAY);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchField.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));

        JButton searchButton = createStyledButton("Поиск");
        searchButton.addActionListener(e -> searchNotes(searchField.getText()));

        JPanel panel = new JPanel(new BorderLayout(5, 5)); // Используем BorderLayout для управления компонентами
        panel.setBackground(Color.WHITE);

        // Устанавливаем отступ слева для строки поиска, но не меняем ее ширину
        panel.add(searchField, BorderLayout.CENTER); // Строка поиска занимает оставшееся пространство
        panel.add(searchButton, BorderLayout.EAST); // Кнопка справа

        // Добавляем отступ с левого края на весь панель
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(Color.WHITE);
        wrapperPanel.add(panel, BorderLayout.CENTER);
        wrapperPanel.setBorder(new EmptyBorder(0, 20, 0, 0)); // Добавляем отступ слева

        return wrapperPanel;
    }





    private void setupNotesPanel() {
        notesPanel = new JPanel();
        notesPanel.setLayout(new BoxLayout(notesPanel, BoxLayout.Y_AXIS));
        notesPanel.setBackground(Color.WHITE);

        // Убираем жесткое ограничение по размерам, чтобы панель подстраивалась под содержимое
        JScrollPane scrollPane = new JScrollPane(notesPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); // Горизонтальная прокрутка, если нужно
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(null);

        // Убираем закругленные углы скроллбаров
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
                g.setColor(Color.GRAY);  // Цвет бегунка
                g.fillRect(r.x, r.y, r.width, r.height);  // Рисуем бегунок с прямыми углами
            }

            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
                g.setColor(Color.WHITE);  // Цвет фона полосы прокрутки
                g.fillRect(r.x, r.y, r.width, r.height);  // Рисуем фон полосы прокрутки
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                JButton button = super.createDecreaseButton(orientation);
                button.setBorder(BorderFactory.createEmptyBorder()); // Без рамки у кнопок
                return button;
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                JButton button = super.createIncreaseButton(orientation);
                button.setBorder(BorderFactory.createEmptyBorder()); // Без рамки у кнопок
                return button;
            }
        });

        // Устанавливаем ширину вертикального скроллбара
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setPreferredSize(new Dimension(15, Integer.MAX_VALUE)); // Устанавливаем ширину скроллбара (например, 15 пикселей)

        // Размещаем JScrollPane в центральной части mainPanel
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Создаем кнопку для добавления заметки
        JButton addNoteButton = createStyledButton("Добавить заметку");
        addNoteButton.addActionListener(e -> openAddNoteDialog());

        // Панель с кнопкой, размещаем ее в нижней части
        JPanel addButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addButtonPanel.setBackground(Color.WHITE);
        addButtonPanel.add(addNoteButton);
        mainPanel.add(addButtonPanel, BorderLayout.SOUTH);
    }


    private void setupResizeListener() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (!isResizing) {
                    isResizing = true;
                    SwingUtilities.invokeLater(() -> {
                        loadNotes();
                        isResizing = false;
                    });
                }
            }
        });
    }


    private void openUserProfile(int userId) {
        JDialog profileDialog = new JDialog(this, "Профиль пользователя", true);
        profileDialog.setResizable(true);  // Окно теперь будет изменяемым
        profileDialog.setLocationRelativeTo(this);

        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.setBackground(Color.WHITE);
        profilePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel userLabel = new JLabel("Ник: " + username);
        userLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel emailLabel = new JLabel("Почта: " + email);
        emailLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton closeButton = createStyledButton("Закрыть");
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.addActionListener(e -> profileDialog.dispose());

        JButton logoutButton = createStyledButton("Выйти из аккаунта");
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.addActionListener(e -> showExitConfirmation(profileDialog));

        profilePanel.add(userLabel);
        profilePanel.add(Box.createVerticalStrut(10));
        profilePanel.add(emailLabel);
        profilePanel.add(Box.createVerticalStrut(30));
        profilePanel.add(closeButton);
        profilePanel.add(Box.createVerticalStrut(10));
        profilePanel.add(logoutButton);

        profileDialog.add(profilePanel);
        profileDialog.pack();  // Используем pack() для автоматического подбора размеров
        profileDialog.setVisible(true);
    }


    private void showExitConfirmation(JDialog parentDialog) {
        JDialog confirmDialog = new JDialog(parentDialog, "Подтверждение выхода", true);
        confirmDialog.setSize(400, 150);
        confirmDialog.setLocationRelativeTo(parentDialog);
        confirmDialog.setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        confirmDialog.add(mainPanel);

        JLabel messageLabel = new JLabel("Вы точно хотите выйти из аккаунта?");
        messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(messageLabel);

        mainPanel.add(Box.createVerticalStrut(10));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));

        JButton yesButton = createStyledButton("Да");
        JButton noButton = createStyledButton("Нет");

        yesButton.addActionListener(e -> {
            confirmDialog.dispose();
            parentDialog.dispose();
            dispose();
            new LoginWindow();
        });

        noButton.addActionListener(e -> confirmDialog.dispose());

        buttonPanel.add(yesButton);
        buttonPanel.add(noButton);
        mainPanel.add(buttonPanel);

        confirmDialog.setVisible(true);
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

    private void loadNotes() {
        try {
            List<Note> notes = DatabaseHandler.getNotes(userId);
            notesPanel.removeAll();

            if (notes.isEmpty()) {
                JPanel messagePanel = new JPanel();
                messagePanel.setBackground(Color.WHITE);
                JLabel noResultsLabel = new JLabel("Заметок пока нет... \uD83D\uDE22");
                noResultsLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
                noResultsLabel.setForeground(Color.GRAY);

                messagePanel.add(noResultsLabel);
                messagePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
                notesPanel.add(messagePanel);
            } else {
                int columns = 2; // Количество столбцов
                int rows = (int) Math.ceil((double) notes.size() / columns); // Количество строк

                JPanel gridPanel = new JPanel(new GridLayout(rows, columns, 10, 10)); // Сетка из карточек
                gridPanel.setBackground(Color.WHITE);

                for (Note note : notes) {
                    JPanel noteCard = new JPanel(new BorderLayout(5, 5));
                    noteCard.setBackground(new Color(250, 250, 250));
                    noteCard.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Курсор для всей области карточки
                    noteCard.setBorder(BorderFactory.createCompoundBorder(
                            new LineBorder(new Color(220, 220, 220), 1, false),
                            new EmptyBorder(10, 10, 10, 10)
                    ));

                    // Ограничиваем размер карточки, чтобы она не растягивалась
                    noteCard.setPreferredSize(new Dimension(250, 300));  // Размер карточки
                    noteCard.setMaximumSize(new Dimension(250, 300));   // Максимальный размер
                    noteCard.setMinimumSize(new Dimension(250, 300));   // Минимальный размер

                    noteCard.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            openNote(note); // Открываем содержимое заметки вместо редактора
                        }
                    });

                    // Создаем компоненты для карточки
                    JLabel titleLabel = new JLabel(note.getTitle());
                    titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
                    titleLabel.setForeground(Color.DARK_GRAY);
                    titleLabel.setPreferredSize(new Dimension(240, 40)); // Ограничение по размеру для заголовка
                    noteCard.add(titleLabel, BorderLayout.NORTH);

                    JTextArea contentArea = new JTextArea(note.getContent());
                    contentArea.setEditable(false);
                    contentArea.setWrapStyleWord(true);
                    contentArea.setLineWrap(true);
                    contentArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
                    contentArea.setForeground(Color.DARK_GRAY);
                    contentArea.setBackground(new Color(250, 250, 250));
                    contentArea.setBorder(null);
                    contentArea.setPreferredSize(new Dimension(240, 150)); // Размер для текстовой области
                    noteCard.add(contentArea, BorderLayout.CENTER);

                    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
                    buttonPanel.setBackground(new Color(250, 250, 250));

                    JButton editButton = createStyledButton("Редактировать");
                    editButton.addActionListener(e -> openNote(note));
                    JButton deleteButton = createStyledButton("Удалить");
                    deleteButton.addActionListener(e -> deleteNote(note));
                    buttonPanel.add(editButton);
                    buttonPanel.add(deleteButton);
                    noteCard.add(buttonPanel, BorderLayout.SOUTH);

                    gridPanel.add(noteCard);
                }

                notesPanel.setLayout(new BorderLayout());
                notesPanel.add(gridPanel, BorderLayout.CENTER);
            }

            notesPanel.revalidate();
            notesPanel.repaint();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private void openNote(Note note) {
        JPanel notePanel = new JPanel(new BorderLayout(5, 5));
        notePanel.setBackground(Color.WHITE); // Белый фон для панели
        notePanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Убираем лишние отступы вокруг панели

        // Заголовок с тонкой серой линией и закругленными углами
        JTextField titleField = new JTextField(note.getTitle());
        titleField.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1)); // Тонкая серая линия
        titleField.setBackground(Color.WHITE); // Белый фон для заголовка
        titleField.setCaretColor(Color.BLACK); // Цвет курсора (черный)

        // Содержимое с тонкой серой линией и закругленными углами
        JTextArea contentArea = new JTextArea(note.getContent());
        contentArea.setWrapStyleWord(true);
        contentArea.setLineWrap(true);
        contentArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        contentArea.setBackground(Color.WHITE); // Белый фон для содержимого
        contentArea.setCaretColor(Color.BLACK); // Цвет курсора (черный)

        // Добавляем бордер с закругленными углами и серой линией
        contentArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),  // Тонкая серая линия
                BorderFactory.createEmptyBorder(5, 5, 5, 5)   // Отступы внутри
        ));

        // Добавляем элементы на панель
        notePanel.add(titleField, BorderLayout.NORTH);
        notePanel.add(new JScrollPane(contentArea), BorderLayout.CENTER);

        // Кнопка "Сохранить"
        JButton saveButton = createStyledButton("Сохранить");

        // Обработчик для сохранения
        saveButton.addActionListener(e -> {
            saveEditedNote(note, titleField.getText(), contentArea.getText());
            JFrame noteFrame = (JFrame) SwingUtilities.getWindowAncestor(notePanel);
            noteFrame.dispose();  // Закрытие окна редактора
        });

        // Создаём меню с троеточием
        JMenuBar menuBar = new JMenuBar();
        JMenu optionsMenu = new JMenu("⋮");  // Троеточие как меню
        JMenuItem deleteMenuItem = new JMenuItem("Удалить запись");
        deleteMenuItem.addActionListener(e -> {
            int confirmation = JOptionPane.showConfirmDialog(null,
                    "Вы уверены, что хотите удалить эту запись?",
                    "Удалить запись", JOptionPane.YES_NO_OPTION);
            if (confirmation == JOptionPane.YES_OPTION) {
                deleteNote(note); // Реализуйте метод для удаления заметки
                JFrame noteFrame = (JFrame) SwingUtilities.getWindowAncestor(notePanel);
                noteFrame.dispose();
            }
        });
        optionsMenu.add(deleteMenuItem);
        menuBar.add(optionsMenu); // Добавляем меню в меню-бар

        // Окно для редактирования
        JFrame noteFrame = new JFrame("Редактор");
        noteFrame.setJMenuBar(menuBar);  // Устанавливаем меню-бар в окно
        noteFrame.add(notePanel);
        noteFrame.setLocationRelativeTo(this);
        noteFrame.setSize(600, 400);  // Устанавливаем размер окна
        noteFrame.setVisible(true);
    }






    /// Сохранение изменений в заметке
    private void saveEditedNote(Note note, String newTitle, String newContent) {
        try {
            note.setTitle(newTitle);
            note.setContent(newContent);
            DatabaseHandler.updateNote(note.getId(), newTitle, newContent);
            loadNotes();  // Загружаем обновленные заметки
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void searchNotes(String query) {
        SwingWorker<List<Note>, Void> searchWorker = new SwingWorker<>() {
            @Override
            protected List<Note> doInBackground() throws Exception {
                return DatabaseHandler.searchNotes(userId, query);
            }

            @Override
            protected void done() {
                try {
                    List<Note> notes = get();
                    notesPanel.removeAll();  // Очищаем панель перед добавлением новых данных
                    notesPanel.setBackground(Color.WHITE);

                    if (notes.isEmpty()) {
                        JPanel messagePanel = new JPanel();
                        messagePanel.setBackground(Color.WHITE);
                        JLabel noResultsLabel = new JLabel("Заметки не найдены");
                        noResultsLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
                        noResultsLabel.setForeground(Color.GRAY);

                        messagePanel.add(noResultsLabel);
                        messagePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
                        notesPanel.add(messagePanel);
                    } else {
                        int cardSpacing = 10;
                        int cardWidth = getWidth() - 40;
                        if (cardWidth < 200) {
                            cardWidth = 200;
                        }

                        for (Note note : notes) {
                            JPanel noteCardWrapper = new JPanel(new BorderLayout());
                            noteCardWrapper.setBackground(Color.WHITE);
                            noteCardWrapper.setBorder(new EmptyBorder(0, 20, 0, 20));

                            JPanel noteCard = new JPanel(new BorderLayout(5, 5));
                            noteCard.setBackground(new Color(250, 250, 250));
                            noteCard.setBorder(BorderFactory.createCompoundBorder(
                                    new LineBorder(new Color(220, 220, 220), 1, false),
                                    new EmptyBorder(10, 10, 10, 10)
                            ));

                            JLabel titleLabel = new JLabel(note.getTitle());
                            titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
                            titleLabel.setForeground(Color.DARK_GRAY);
                            noteCard.add(titleLabel, BorderLayout.NORTH);

                            JTextArea contentArea = new JTextArea(note.getContent());
                            contentArea.setEditable(false);
                            contentArea.setWrapStyleWord(true);
                            contentArea.setLineWrap(true);
                            contentArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
                            contentArea.setForeground(Color.DARK_GRAY);
                            contentArea.setBackground(new Color(250, 250, 250));
                            contentArea.setBorder(null);
                            noteCard.add(contentArea, BorderLayout.CENTER);

                            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
                            buttonPanel.setBackground(new Color(250, 250, 250));
                            JButton editButton = createStyledButton("Редактировать");
                            editButton.addActionListener(e -> openNote(note));
                            JButton deleteButton = createStyledButton("Удалить");
                            deleteButton.addActionListener(e -> deleteNote(note));
                            buttonPanel.add(editButton);
                            buttonPanel.add(deleteButton);
                            noteCard.add(buttonPanel, BorderLayout.SOUTH);

                            noteCardWrapper.add(noteCard, BorderLayout.CENTER);
                            notesPanel.add(noteCardWrapper);
                            notesPanel.add(Box.createVerticalStrut(cardSpacing));
                        }
                    }

                    SwingUtilities.invokeLater(() -> {
                        notesPanel.revalidate();
                        notesPanel.repaint();
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        searchWorker.execute();
    }


    // Удаление заметки
    private void deleteNote(Note note) {
        int confirmation = JOptionPane.showConfirmDialog(this, "Вы уверены, что хотите удалить эту заметку?", "Удалить заметку", JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                DatabaseHandler.deleteNote(note.getId());
                loadNotes();  // Загружаем обновленные заметки после удаления
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        notesPanel.revalidate();  // Обновляем панель
        notesPanel.repaint();     // Перерисовка панели
    }


    // Диалог для добавления новой заметки
    private void openAddNoteDialog() {
        JDialog dialog = new JDialog(this, "Добавить заметку", true);
        dialog.setSize(500, 400); // Увеличенное окно
        dialog.setLocationRelativeTo(this);

        JPanel dialogPanel = new JPanel(new BorderLayout(10, 10));
        dialogPanel.setBackground(Color.WHITE);
        dialogPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Панель для ввода заголовка и содержания
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBackground(Color.WHITE);

        // Заголовок
        JLabel titleLabel = new JLabel("Заголовок");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Центрируем метку
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER); // Центрируем текст
        JTextField titleField = new JTextField();
        titleField.setFont(new Font("SansSerif", Font.PLAIN, 14)); // Уменьшен шрифт
        titleField.setMaximumSize(new Dimension(Short.MAX_VALUE, titleField.getPreferredSize().height)); // Ограничение ширины
        inputPanel.add(titleLabel);
        inputPanel.add(Box.createVerticalStrut(5)); // Немного отступа между заголовком и полем
        inputPanel.add(titleField);

        // Содержание
        JLabel contentLabel = new JLabel("Содержание");
        contentLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Центрируем метку
        contentLabel.setHorizontalAlignment(SwingConstants.CENTER); // Центрируем текст
        JTextArea contentArea = new JTextArea(10, 30); // Увеличен размер для содержания
        contentArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        contentArea.setWrapStyleWord(true);
        contentArea.setLineWrap(true);
        JScrollPane contentScrollPane = new JScrollPane(contentArea);
        inputPanel.add(contentLabel);
        inputPanel.add(Box.createVerticalStrut(5)); // Отступ
        inputPanel.add(contentScrollPane);

        dialogPanel.add(inputPanel, BorderLayout.CENTER);

        JButton saveButton = createStyledButton("Сохранить");
        saveButton.addActionListener(e -> {
            try {
                String title = titleField.getText().trim();
                String content = contentArea.getText().trim();
                if (title.isEmpty() || content.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Заголовок и содержание не могут быть пустыми!");
                    return;
                }
                DatabaseHandler.addNote(userId, title, content);
                JOptionPane.showMessageDialog(this, "Заметка добавлена!");
                loadNotes();
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ошибка при добавлении заметки ");
                ex.printStackTrace();
            }
        });

        dialogPanel.add(saveButton, BorderLayout.SOUTH);
        dialog.add(dialogPanel);
        dialog.setVisible(true);
    }

}
