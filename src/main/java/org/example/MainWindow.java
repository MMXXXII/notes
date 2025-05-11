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

        // Создаем JScrollPane для списка заметок
        JScrollPane scrollPane = new JScrollPane(notesPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(null);

        // Убираем закругленные углы скроллбаров
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

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return new JButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return new JButton();
            }
        });

        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setPreferredSize(new Dimension(15, Integer.MAX_VALUE));

        // Создаем заголовок "ЗАМЕТКИ"
        JLabel titleLabel = new JLabel("ЗАМЕТКИ");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.DARK_GRAY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Панель заголовка для размещения сверху
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(titleLabel);

        // Создаем основной контейнер для заголовка и списка заметок
        JPanel notesContainer = new JPanel();
        notesContainer.setLayout(new BorderLayout());
        notesContainer.setBackground(Color.WHITE);
        notesContainer.add(titlePanel, BorderLayout.NORTH);
        notesContainer.add(scrollPane, BorderLayout.CENTER);

        // Размещаем в mainPanel
        mainPanel.add(notesContainer, BorderLayout.CENTER);

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
                JPanel gridPanel = new JPanel();
                gridPanel.setLayout(new GridBagLayout());
                gridPanel.setBackground(Color.WHITE);
                GridBagConstraints gbc = new GridBagConstraints();

                int cardWidth = 730;
                int cardHeight = 200;

                for (int i = 0; i < notes.size(); i++) {
                    Note note = notes.get(i);
                    JPanel noteCard = new JPanel(new BorderLayout(5, 5));
                    noteCard.setBackground(new Color(250, 250, 250));
                    noteCard.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    noteCard.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            openNote(note);
                        }
                    });

                    noteCard.setPreferredSize(new Dimension(cardWidth, cardHeight));
                    noteCard.setMaximumSize(new Dimension(cardWidth, cardHeight));
                    noteCard.setMinimumSize(new Dimension(cardWidth, cardHeight));

                    noteCard.setBorder(BorderFactory.createCompoundBorder(
                            new LineBorder(new Color(220, 220, 220), 1, false),
                            new EmptyBorder(10, 10, 10, 10)
                    ));

                    // Заголовок + тип + время создания
                    JLabel titleLabel = new JLabel(
                            "<html><b>" + note.getTitle() + "</b> (" + note.getNoteType() + ")<br><i>" +
                                    "Создано: " + note.getCreatedAt() + "</i></html>"
                    );
                    titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
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

                    int row = i / 2;
                    int col = i % 2;

                    gbc.gridx = col;
                    gbc.gridy = row;
                    gbc.insets = new Insets(10, 10, 10, 10);

                    gridPanel.add(noteCard, gbc);
                    noteCard.setFocusable(false);
                    contentArea.setFocusable(false);
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
        notePanel.setBackground(Color.WHITE);
        notePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Поле заголовка
        JTextField titleField = new JTextField(note.getTitle());
        titleField.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleField.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        titleField.setBackground(Color.WHITE);
        titleField.setCaretColor(Color.BLACK);

        // Поле содержания
        JTextArea contentArea = new JTextArea(note.getContent());
        contentArea.setWrapStyleWord(true);
        contentArea.setLineWrap(true);
        contentArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        contentArea.setBackground(Color.WHITE);
        contentArea.setCaretColor(Color.BLACK);
        contentArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // Выбор типа заметки
        JLabel typeLabel = new JLabel("Тип заметки");
        JComboBox<String> typeComboBox = new JComboBox<>();
        typeComboBox.addItem(NoteType.ЛИЧНАЯ.name());
        typeComboBox.addItem(NoteType.РАБОЧАЯ.name());
        typeComboBox.addItem(NoteType.ИДЕЯ.name());
        typeComboBox.addItem(NoteType.НАПОМИНАНИЕ.name());
        typeComboBox.addItem(NoteType.ЗАДАЧА.name());

        // Устанавливаем текущий тип заметки
        typeComboBox.setSelectedItem(note.getNoteType());

        // Панель для типа заметки
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        typePanel.setBackground(Color.WHITE);
        typePanel.add(typeLabel);
        typePanel.add(typeComboBox);

        // Добавляем элементы на панель
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.add(titleField);
        inputPanel.add(Box.createVerticalStrut(10)); // Отступ
        inputPanel.add(typePanel);
        inputPanel.add(Box.createVerticalStrut(10)); // Отступ
        inputPanel.add(new JScrollPane(contentArea));

        notePanel.add(inputPanel, BorderLayout.CENTER);

        // Кнопка "Сохранить"
        JButton saveButton = createStyledButton("Сохранить");

        // Обработчик для сохранения
        saveButton.addActionListener(e -> {
            try {
                String title = titleField.getText().trim();
                String content = contentArea.getText().trim();
                String noteType = (String) typeComboBox.getSelectedItem();

                if (title.isEmpty() || content.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Заголовок и содержание не могут быть пустыми!");
                    return;
                }

                // Сохраняем изменения заметки в базе данных
                DatabaseHandler.updateNote(note.getId(), title, content, noteType);
                JOptionPane.showMessageDialog(this, "Заметка сохранена!");
                loadNotes();
                JFrame noteFrame = (JFrame) SwingUtilities.getWindowAncestor(notePanel);
                noteFrame.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ошибка при сохранении заметки");
                ex.printStackTrace();
            }
        });

        // Панель кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(saveButton);

        notePanel.add(buttonPanel, BorderLayout.SOUTH);

        // Окно редактора
        JFrame noteFrame = new JFrame("Редактор");
        noteFrame.add(notePanel);
        noteFrame.setLocationRelativeTo(this);
        noteFrame.setSize(600, 400);
        noteFrame.setVisible(true);
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
                        JPanel gridPanel = new JPanel();
                        gridPanel.setLayout(new GridBagLayout());
                        gridPanel.setBackground(Color.WHITE);
                        GridBagConstraints gbc = new GridBagConstraints();

                        int cardWidth = 730;
                        int cardHeight = 200;

                        for (int i = 0; i < notes.size(); i++) {
                            Note note = notes.get(i);
                            JPanel noteCard = new JPanel(new BorderLayout(5, 5));
                            noteCard.setBackground(new Color(250, 250, 250));
                            noteCard.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                            noteCard.addMouseListener(new MouseAdapter() {
                                @Override
                                public void mouseClicked(MouseEvent e) {
                                    openNote(note);
                                }
                            });

                            noteCard.setPreferredSize(new Dimension(cardWidth, cardHeight));
                            noteCard.setMaximumSize(new Dimension(cardWidth, cardHeight));
                            noteCard.setMinimumSize(new Dimension(cardWidth, cardHeight));

                            noteCard.setBorder(BorderFactory.createCompoundBorder(
                                    new LineBorder(new Color(220, 220, 220), 1, false),
                                    new EmptyBorder(10, 10, 10, 10)
                            ));

                            // Заголовок + тип + время создания
                            JLabel titleLabel = new JLabel(
                                    "<html><b>" + note.getTitle() + "</b> (" + note.getNoteType() + ")<br><i>" +
                                            "Создано: " + note.getCreatedAt() + "</i></html>"
                            );
                            titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
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

                            int row = i / 2;
                            int col = i % 2;

                            gbc.gridx = col;
                            gbc.gridy = row;
                            gbc.insets = new Insets(10, 10, 10, 10);

                            gridPanel.add(noteCard, gbc);
                            noteCard.setFocusable(false);
                            contentArea.setFocusable(false);
                        }

                        notesPanel.setLayout(new BorderLayout());
                        notesPanel.add(gridPanel, BorderLayout.CENTER);
                    }

                    SwingUtilities.invokeLater(() -> {
                        notesPanel.revalidate();  // Обновляем панель
                        notesPanel.repaint();     // Рисуем заново
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
        typeComboBox.addItem(NoteType.НАПОМИНАНИЕ.name());
        typeComboBox.addItem(NoteType.ЗАДАЧА.name());

        inputPanel.add(typeLabel);
        inputPanel.add(typeComboBox);

        dialogPanel.add(inputPanel, BorderLayout.CENTER);

        JButton saveButton = createStyledButton("Сохранить");
        saveButton.addActionListener(e -> {
            try {
                String title = titleField.getText().trim();
                String content = contentArea.getText().trim();
                String noteType = (String) typeComboBox.getSelectedItem();

                if (title.isEmpty() || content.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Заголовок и содержание не могут быть пустыми!");
                    return;
                }

                DatabaseHandler.addNote(userId, title, content, noteType);  // Передаем тип заметки
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
