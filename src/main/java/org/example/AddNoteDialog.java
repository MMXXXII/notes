package org.example;

import javax.swing.*;
import java.awt.*;

public class AddNoteDialog extends JDialog {
    private JTextField titleField;
    private JTextArea contentArea;
    private boolean submitted = false;

    public AddNoteDialog(JFrame parent) {
        super(parent, "Новая заметка", true);
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        // Панель ввода
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());

        titleField = new JTextField();
        titleField.setBorder(BorderFactory.createTitledBorder("Заголовок"));
        inputPanel.add(titleField, BorderLayout.NORTH);

        contentArea = new JTextArea(8, 30);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(contentArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Содержание"));
        inputPanel.add(scrollPane, BorderLayout.CENTER);

        add(inputPanel, BorderLayout.CENTER);

        // Кнопки
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Добавить");
        JButton cancelButton = new JButton("Отмена");

        addButton.addActionListener(e -> {
            if (!titleField.getText().trim().isEmpty() && !contentArea.getText().trim().isEmpty()) {
                submitted = true;
                setVisible(false);
            } else {
                JOptionPane.showMessageDialog(this, "Заполните заголовок и содержание");
            }
        });

        cancelButton.addActionListener(e -> {
            submitted = false;
            setVisible(false);
        });

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public String getTitleText() {
        return titleField.getText().trim();
    }

    public String getContentText() {
        return contentArea.getText().trim();
    }

    public boolean isSubmitted() {
        return submitted;
    }
}
