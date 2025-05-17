package org.example;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class RegisterWindow extends JFrame {
    private JTextField usernameField = new JTextField();
    private JPasswordField passwordField = new JPasswordField();
    private JTextField emailField = new JTextField();

    public RegisterWindow() {
        setTitle("Регистрация");
        setSize(360, 320);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        var mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(mainPanel);

        var titleLabel = new JLabel("Регистрация");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        var formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        formPanel.add(createLabel("Логин:"));
        setupField(usernameField);
        formPanel.add(usernameField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        formPanel.add(createLabel("Пароль:"));
        setupField(passwordField);
        formPanel.add(passwordField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        formPanel.add(createLabel("Email:"));
        setupField(emailField);
        formPanel.add(emailField);

        mainPanel.add(formPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        var buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);

        var registerButton = createStyledButton("Зарегистрироваться");
        registerButton.addActionListener(e -> register());
        buttonPanel.add(registerButton);

        var backButton = createStyledButton("Назад");
        backButton.addActionListener(e -> goBack());
        buttonPanel.add(backButton);

        mainPanel.add(buttonPanel);

        setVisible(true);
    }

    private JLabel createLabel(String text) {
        var label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 16));
        return label;
    }

    private void setupField(JComponent field) {
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        if (field instanceof JTextField) {
            field.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
        }
    }

    private JButton createStyledButton(String text) {
        var button = new JButton(text);
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
                button.setFont(button.getFont().deriveFont(Font.BOLD));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setFont(button.getFont().deriveFont(Font.PLAIN));
            }
        });

        return button;
    }

    private void register() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String email = emailField.getText();

        String passwordRegex = "^(?=.*[A-Z])(?=.*[\\W_]).{8,}$";
        if (!Pattern.matches(passwordRegex, password)) {
            JOptionPane.showMessageDialog(this,
                    "Пароль должен содержать хотя бы одну заглавную букву, один специальный символ и быть не менее 8 символов.",
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            DatabaseHandler.registerUser(username, password, email);
            new MessageWindow(this, "Регистрация успешна!", "Успех");
            dispose();
            new LoginWindow();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            new MessageWindow(this, "Ошибка регистрации. Попробуйте позже.", "Ошибка");
        }
    }

    private void goBack() {
        dispose();
        new LoginWindow();
    }
}
