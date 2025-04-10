package org.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginWindow extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JButton togglePasswordVisibilityButton;

    public LoginWindow() {
        setTitle("Вход в систему");
        setSize(360, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(mainPanel);

        JLabel titleLabel = new JLabel("Добро пожаловать");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);

        mainPanel.add(Box.createVerticalStrut(20));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);

        JLabel userLabel = new JLabel("Логин:");
        userLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        formPanel.add(userLabel);

        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(300, 30));  // Убираем максимальный размер
        usernameField.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
        // Автозаполнение логина
        usernameField.setText("mmxxxii");
        formPanel.add(usernameField);

        formPanel.add(Box.createVerticalStrut(10));

        JLabel passLabel = new JLabel("Пароль:");
        passLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        formPanel.add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(300, 30));  // Убираем максимальный размер
        passwordField.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 12));  // уменьшение шрифта
        passwordField.setEchoChar('*');  // Скрываем пароль с самого начала
        // Автозаполнение пароля
        passwordField.setText("Ctvty2005@");
        formPanel.add(passwordField);

        // Создание кнопки для показа/скрытия пароля
        togglePasswordVisibilityButton = new JButton("Показать");
        togglePasswordVisibilityButton.setFont(new Font("SansSerif", Font.PLAIN, 12)); // Уменьшение шрифта
        togglePasswordVisibilityButton.setPreferredSize(new Dimension(90, 25)); // Уменьшение кнопки
        togglePasswordVisibilityButton.setBackground(Color.WHITE);
        togglePasswordVisibilityButton.setForeground(Color.DARK_GRAY);
        togglePasswordVisibilityButton.setFocusPainted(false);
        togglePasswordVisibilityButton.setBorderPainted(false);
        togglePasswordVisibilityButton.addActionListener(e -> togglePasswordVisibility());

        formPanel.add(togglePasswordVisibilityButton);
        mainPanel.add(formPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));

        loginButton = createStyledButton("Войти");
        registerButton = createStyledButton("Регистрация");

        // Добавляем обработчики событий
        loginButton.addActionListener(e -> login());
        registerButton.addActionListener(e -> openRegisterWindow());

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        mainPanel.add(buttonPanel);

        // Убираем JScrollPane, чтобы не было прокрутки
        setContentPane(mainPanel);

        setVisible(true);
    }

    // Метод для переключения видимости пароля
    private void togglePasswordVisibility() {
        if (passwordField.getEchoChar() == '*') {
            passwordField.setEchoChar((char) 0);  // Показываем пароль
            togglePasswordVisibilityButton.setText("Скрыть");  // Меняем текст на кнопке
        } else {
            passwordField.setEchoChar('*');  // Скрываем пароль
            togglePasswordVisibilityButton.setText("Показать");  // Меняем текст на кнопке
        }
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

    private void login() {
        String username = usernameField.getText(); // Получаем имя пользователя
        String password = new String(passwordField.getPassword()); // Получаем пароль

        try {
            // Проверяем правильность логина и пароля
            if (DatabaseHandler.authenticateUser(username, password)) {
                new MessageWindow(this, "Вход успешен! Добро пожаловать, " + username, "Успех");

                // Получаем userId и email из базы данных
                int userId = getUserId(username);
                String email = getUserEmail(username); // Получаем email

                // Создаем главное окно и передаем userId, username и email
                MainWindow mainWindow = new MainWindow(userId, username, email);

                // Делаем главное окно видимым и закрываем окно входа
                mainWindow.setVisible(true);
                dispose();
            } else {
                // Если логин или пароль неверные
                new MessageWindow(this, "Неверный логин или пароль.", "Ошибка");
            }
        } catch (Exception e) {
            e.printStackTrace();
            new MessageWindow(this, "Ошибка подключения к базе данных.", "Ошибка");
        }
    }

    private String getUserEmail(String username) throws SQLException, ClassNotFoundException {
        // Запрос в базу данных для получения email пользователя по его имени
        String sql = "SELECT email FROM users WHERE username = ?";
        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("email"); // Возвращаем email
            }
        }
        return null; // Если email не найден
    }

    private int getUserId(String username) throws SQLException, ClassNotFoundException {
        // Запрос в базу данных для получения ID пользователя по его имени
        String sql = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id"); // Возвращаем userId
            }
        }
        return -1; // Если не найден пользователь
    }

    private void openRegisterWindow() {
        dispose();  // Закрываем окно входа
        new RegisterWindow();  // Открываем окно регистрации
    }
}
