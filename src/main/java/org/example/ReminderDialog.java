package org.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReminderDialog extends JDialog {
    private MainWindowController controller;
    private LocalDate selectedDate;
    private JPanel remindersPanel;

    public ReminderDialog(JFrame parent, MainWindowController controller, LocalDate selectedDate) {
        super(parent, "Напоминания на " + selectedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), true);
        this.controller = controller;
        this.selectedDate = selectedDate;

        setSize(520, 400);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(Color.WHITE);

        // Заголовок
        JLabel titleLabel = new JLabel("Напоминания на " + selectedDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Панель для списка напоминаний
        remindersPanel = new JPanel();
        remindersPanel.setLayout(new BoxLayout(remindersPanel, BoxLayout.Y_AXIS));
        remindersPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(remindersPanel);
        scrollPane.setBorder(null);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton addButton = createStyledButton("Добавить напоминание");
        addButton.addActionListener(e -> openAddReminderDialog());

        JButton closeButton = createStyledButton("Закрыть");
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(addButton);
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Загружаем напоминания
        loadReminders();
    }

    private void loadReminders() {
        remindersPanel.removeAll();

        try {
            List<Reminder> reminders = controller.getRemindersForDate(selectedDate);

            if (reminders.isEmpty()) {
                JLabel noRemindersLabel = new JLabel("На этот день нет напоминаний", SwingConstants.CENTER);
                noRemindersLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
                noRemindersLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                remindersPanel.add(noRemindersLabel);
            } else {
                for (Reminder reminder : reminders) {
                    JPanel reminderPanel = createReminderPanel(reminder);
                    remindersPanel.add(reminderPanel);
                    remindersPanel.add(Box.createVerticalStrut(10));
                }
            }

            remindersPanel.revalidate();
            remindersPanel.repaint();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ошибка при загрузке напоминаний: " + e.getMessage());
        }
    }

    private JPanel createReminderPanel(Reminder reminder) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(10, 10, 10, 10)
        ));

        // Заголовок напоминания
        JLabel titleLabel = new JLabel(reminder.getTitle());
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Описание напоминания
        JTextArea descriptionArea = new JTextArea(reminder.getDescription());
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("SansSerif", Font.PLAIN, 12));
        descriptionArea.setBackground(new Color(245, 245, 245));
        descriptionArea.setBorder(null);
        panel.add(descriptionArea, BorderLayout.CENTER);

        // Кнопки управления
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setBackground(new Color(245, 245, 245));

        JCheckBox completedCheckBox = new JCheckBox("Выполнено");
        completedCheckBox.setSelected(reminder.isCompleted());
        completedCheckBox.setBackground(new Color(245, 245, 245));
        completedCheckBox.addActionListener(e -> {
            try {
                controller.updateReminderStatus(reminder.getId(), completedCheckBox.isSelected());
                loadReminders();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Ошибка при обновлении статуса: " + ex.getMessage());
            }
        });

        JButton editButton = createStyledButton("Изменить");
        editButton.addActionListener(e -> openEditReminderDialog(reminder));

        JButton deleteButton = createStyledButton("Удалить");
        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Вы уверены, что хотите удалить это напоминание?",
                    "Подтверждение удаления",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    controller.deleteReminder(reminder.getId());
                    loadReminders();
                    controller.updateCalendar();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Ошибка при удалении: " + ex.getMessage());
                }
            }
        });

        buttonPanel.add(completedCheckBox);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void openAddReminderDialog() {
        JDialog dialog = new JDialog(this, "Добавить напоминание", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBackground(Color.WHITE);

        // Заголовок
        JLabel titleLabel = new JLabel("Заголовок");
        JTextField titleField = new JTextField();
        titleField.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // Описание
        JLabel descriptionLabel = new JLabel("Описание");
        JTextArea descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);

        inputPanel.add(titleLabel);
        inputPanel.add(titleField);
        inputPanel.add(Box.createVerticalStrut(10));
        inputPanel.add(descriptionLabel);
        inputPanel.add(descriptionScrollPane);

        panel.add(inputPanel, BorderLayout.CENTER);

        // Кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton saveButton = createStyledButton("Сохранить");
        saveButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String description = descriptionArea.getText().trim();

            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Заголовок не может быть пустым!");
                return;
            }

            try {
                controller.addReminder(title, description, selectedDate);
                dialog.dispose();
                loadReminders();
                controller.updateCalendar();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Ошибка при сохранении: " + ex.getMessage());
            }
        });

        JButton cancelButton = createStyledButton("Отмена");
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void openEditReminderDialog(Reminder reminder) {
        JDialog dialog = new JDialog(this, "Изменить напоминание", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBackground(Color.WHITE);

        // Заголовок
        JLabel titleLabel = new JLabel("Заголовок");
        JTextField titleField = new JTextField(reminder.getTitle());
        titleField.setFont(new Font("SansSerif", Font.PLAIN, 14));

        // Описание
        JLabel descriptionLabel = new JLabel("Описание");
        JTextArea descriptionArea = new JTextArea(reminder.getDescription(), 5, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);

        inputPanel.add(titleLabel);
        inputPanel.add(titleField);
        inputPanel.add(Box.createVerticalStrut(10));
        inputPanel.add(descriptionLabel);
        inputPanel.add(descriptionScrollPane);

        panel.add(inputPanel, BorderLayout.CENTER);

        // Кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton saveButton = createStyledButton("Сохранить");
        saveButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String description = descriptionArea.getText().trim();

            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Заголовок не может быть пустым!");
                return;
            }

            try {
                controller.updateReminder(reminder.getId(), title, description, selectedDate, reminder.isCompleted());
                dialog.dispose();
                loadReminders();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Ошибка при сохранении: " + ex.getMessage());
            }
        });

        JButton cancelButton = createStyledButton("Отмена");
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(Color.WHITE);
        button.setForeground(Color.DARK_GRAY);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setContentAreaFilled(false);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setFont(new Font("SansSerif", Font.BOLD, 14));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setFont(new Font("SansSerif", Font.PLAIN, 14));
            }
        });

        return button;
    }
}