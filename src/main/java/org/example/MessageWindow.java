package org.example;

import javax.swing.*;
import java.awt.*;

public class MessageWindow extends JDialog {
    public MessageWindow(JFrame parent, String message, String title) {
        super(parent, title, true);
        setLocationRelativeTo(null);

        var mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        var messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(messageLabel);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        var buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);

        var okButton = new JButton("Ок");
        styleButton(okButton);
        okButton.addActionListener(e -> dispose());

        buttonPanel.add(okButton);
        mainPanel.add(buttonPanel);

        add(mainPanel);
        pack();
        setVisible(true);
    }

    private void styleButton(JButton button) {
        button.setBackground(Color.WHITE);
        button.setForeground(Color.DARK_GRAY);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setContentAreaFilled(false);
    }
}
