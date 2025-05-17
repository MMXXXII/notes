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
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(Color.WHITE);
        add(mainPanel);

        setupHeaderPanel(userId, username);
        setupNotesPanel();
        setupResizeListener();
        loadNotes();
    }




    private void setupHeaderPanel(int userId, String username) {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
        headerPanel.setBackground(Color.WHITE);

        // Панель поиска
        headerPanel.add(setupSearchPanel());
        headerPanel.add(Box.createHorizontalStrut(20));

        // Панель пользователя
        JLabel usernameLabel = new JLabel(username);
        usernameLabel.setForeground(Color.BLUE);
        usernameLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        usernameLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openUserProfile(userId);
            }
        });

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        userPanel.setBackground(Color.WHITE);
        userPanel.add(usernameLabel);

        headerPanel.add(userPanel);

        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        mainPanel.add(headerPanel, BorderLayout.NORTH);
    }

    private JPanel setupSearchPanel() {
        JTextField searchField = new JTextField();
        searchField.setForeground(Color.DARK_GRAY);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));

        JButton searchButton = createStyledButton("Поиск");
        searchButton.addActionListener(e -> searchNotes(searchField.getText()));

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);
        panel.add(searchField, BorderLayout.CENTER);
        panel.add(searchButton, BorderLayout.EAST);

        // Добавляем отступ сразу к главной панели
        panel.setBorder(new EmptyBorder(0, 20, 0, 0));
        return panel;
    }

    private void setupNotesPanel() {
        notesPanel = new JPanel();
        notesPanel.setLayout(new BoxLayout(notesPanel, BoxLayout.Y_AXIS));
        notesPanel.setBackground(Color.WHITE);

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

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return new JButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return new JButton();
            }
        });
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(15, Integer.MAX_VALUE));

        // Заголовок "ЗАМЕТКИ"
        JLabel titleLabel = new JLabel("ЗАМЕТКИ", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.DARK_GRAY);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(titleLabel);

        // Основной контейнер
        JPanel notesContainer = new JPanel(new BorderLayout());
        notesContainer.setBackground(Color.WHITE);
        notesContainer.add(titlePanel, BorderLayout.NORTH);
        notesContainer.add(scrollPane, BorderLayout.CENTER);

        // Кнопка добавления заметки
        JButton addNoteButton = createStyledButton("Добавить заметку");
        addNoteButton.addActionListener(e -> openAddNoteDialog());

        JPanel addButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addButtonPanel.setBackground(Color.WHITE);
        addButtonPanel.add(addNoteButton);

        mainPanel.add(notesContainer, BorderLayout.CENTER);
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
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        confirmDialog.add(mainPanel);

        mainPanel.add(new JLabel("Вы точно хотите выйти из аккаунта?", SwingConstants.CENTER));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(buttonPanel);

        buttonPanel.add(createStyledButton("Да", e -> {
            confirmDialog.dispose();
            parentDialog.dispose();
            dispose();
            new LoginWindow();
        }));
        buttonPanel.add(createStyledButton("Нет", e -> confirmDialog.dispose()));

        confirmDialog.setVisible(true);
    }

    // Вспомогательный метод для создания кнопки
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
        saveButton.addActionListener(e -> saveNote(note, titleField.getText(), contentArea.getText(), (String) typeComboBox.getSelectedItem(), noteFrame));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(saveButton);

        notePanel.add(inputPanel, BorderLayout.CENTER);
        notePanel.add(buttonPanel, BorderLayout.SOUTH);
        noteFrame.setVisible(true);
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
                NoteType.НАПОМИНАНИЕ.name(),
                NoteType.ЗАДАЧА.name()
        });
        comboBox.setSelectedItem(selectedType);
        return comboBox;
    }

    // Вспомогательный метод для сохранения заметки
    private void saveNote(Note note, String title, String content, String noteType, JFrame frame) {
        try {
            if (title.isEmpty() || content.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Заголовок и содержание не могут быть пустыми!");
                return;
            }
            DatabaseHandler.updateNote(note.getId(), title.trim(), content.trim(), noteType);
            JOptionPane.showMessageDialog(this, "Заметка сохранена!");
            loadNotes();
            frame.dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ошибка при сохранении заметки");
            ex.printStackTrace();
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
                    e.printStackTrace();  // Не выводим стек вызовов в консоль
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
