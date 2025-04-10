package org.example;

import javax.swing.*;
import java.awt.*;

public class MessageWindow extends JDialog {
    public MessageWindow(JFrame parent, String message, String title) {
        super(parent, title, true);

        // Позиционируем окно по центру экрана, относительно родительского окна
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Убирает любые закругленные углы

        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(messageLabel);

        mainPanel.add(Box.createVerticalStrut(10));  // Отступ между сообщением и кнопкой

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));

        JButton okButton = new JButton("Oк");
        okButton.setBackground(Color.WHITE);
        okButton.setForeground(Color.DARK_GRAY);
        okButton.setFocusPainted(false);
        okButton.setBorderPainted(false);
        okButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        okButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        okButton.setOpaque(true);
        okButton.setContentAreaFilled(false);

        okButton.addActionListener(e -> dispose()); // Закрытие окна

        buttonPanel.add(okButton);
        mainPanel.add(buttonPanel);

        // Убираем JScrollPane
        add(mainPanel, BorderLayout.CENTER);

        pack();  // Окно автоматически подстраивается по размеру содержимого
        setVisible(true);
    }
}
