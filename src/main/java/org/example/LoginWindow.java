package org.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.*;

public class LoginWindow extends JFrame {
    private final JTextField usernameField = new JTextField("mmxxxii");
    private final JPasswordField passwordField = new JPasswordField("Ctvty2005@");
    private final JButton togglePasswordVisibilityButton = new JButton("Показать");

    public LoginWindow() {
        setTitle("Вход в систему");
        setSize(360, 320);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        var mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(mainPanel);

        JLabel titleLabel = new JLabel("Добро пожаловать");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        var formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.add(createLabel("Логин:"));
        setupTextField(usernameField);
        formPanel.add(usernameField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(createLabel("Пароль:"));
        setupPasswordField(passwordField);
        formPanel.add(passwordField);

        setupTogglePasswordButton();
        formPanel.add(togglePasswordVisibilityButton);
        mainPanel.add(formPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        var buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);

        var loginButton = createStyledButton("Войти");
        var registerButton = createStyledButton("Регистрация");
        loginButton.addActionListener(e -> login());
        registerButton.addActionListener(e -> openRegisterWindow());
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        mainPanel.add(buttonPanel);

        setVisible(true);
    }

    private void setupTextField(JTextField field) {
        field.setPreferredSize(new Dimension(300, 30));
        field.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
    }

    private void setupPasswordField(JPasswordField field) {
        setupTextField(field);
        field.setFont(new Font("SansSerif", Font.PLAIN, 12));
        field.setEchoChar('*');
    }

    private void setupTogglePasswordButton() {
        togglePasswordVisibilityButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        togglePasswordVisibilityButton.setPreferredSize(new Dimension(90, 25));
        togglePasswordVisibilityButton.setBackground(Color.WHITE);
        togglePasswordVisibilityButton.setForeground(Color.DARK_GRAY);
        togglePasswordVisibilityButton.setFocusPainted(false);
        togglePasswordVisibilityButton.setBorderPainted(false);
        togglePasswordVisibilityButton.addActionListener(e -> {
            if (passwordField.getEchoChar() == '*') {
                passwordField.setEchoChar((char) 0);
                togglePasswordVisibilityButton.setText("Скрыть");
            } else {
                passwordField.setEchoChar('*');
                togglePasswordVisibilityButton.setText("Показать");
            }
        });
    }

    private JLabel createLabel(String text) {
        var label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 16));
        return label;
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

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try {
            if (DatabaseHandler.authenticateUser(username, password)) {
                new MessageWindow(this, "Вход успешен! Добро пожаловать, " + username, "Успех");

                int userId = getUserData(username, "id", -1);
                String email = getUserData(username, "email", null);

                new MainWindow(userId, username, email).setVisible(true);
                dispose();
            } else {
                new MessageWindow(this, "Неверный логин или пароль.", "Ошибка");
            }
        } catch (Exception e) {
            e.printStackTrace();
            new MessageWindow(this, "Ошибка подключения к базе данных.", "Ошибка");
        }
    }

    // Универсальный метод для получения одного значения из базы по username
    private <T> T getUserData(String username, String column, T defaultValue) throws SQLException, ClassNotFoundException {
        String sql = "SELECT " + column + " FROM users WHERE username = ?";
        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Object val = rs.getObject(column);
                if (val != null) return (T) val;
            }
        }
        return defaultValue;
    }

    private void openRegisterWindow() {
        dispose();
        new RegisterWindow();
    }
}
