package org.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterWindow extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField emailField;
    private JButton registerButton;
    private JButton backButton;

    public RegisterWindow() {
        setTitle("Регистрация");
        setSize(360, 320);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        add(mainPanel);

        JLabel titleLabel = new JLabel("Регистрация");
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
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        usernameField.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
        formPanel.add(usernameField);

        formPanel.add(Box.createVerticalStrut(10));

        JLabel passLabel = new JLabel("Пароль:");
        passLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        formPanel.add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        passwordField.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
        formPanel.add(passwordField);

        formPanel.add(Box.createVerticalStrut(10));

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        formPanel.add(emailLabel);

        emailField = new JTextField();
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        emailField.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
        formPanel.add(emailField);

        mainPanel.add(formPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));

        registerButton = createStyledButton("Зарегистрироваться");
        registerButton.addActionListener(e -> register());
        buttonPanel.add(registerButton);

        buttonPanel.add(Box.createVerticalStrut(10)); // Добавляем отступ между кнопками

        backButton = createStyledButton("Назад");
        backButton.addActionListener(e -> goBack());
        buttonPanel.add(backButton);

        mainPanel.add(buttonPanel);

        setVisible(true);
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

    // Метод для регистрации нового пользователя
    private void register() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String email = emailField.getText();

        // Регулярное выражение для проверки пароля
        String passwordRegex = "^(?=.*[A-Z])(?=.*[\\W_]).{8,}$";
        Pattern pattern = Pattern.compile(passwordRegex);
        Matcher matcher = pattern.matcher(password);

        if (!matcher.matches()) {
            JOptionPane.showMessageDialog(this, "Пароль должен содержать хотя бы одну заглавную букву, один специальный символ и быть не менее 8 символов.", "Ошибка", JOptionPane.ERROR_MESSAGE);
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

    // Метод для возврата к окну логина
    private void goBack() {
        dispose(); // Закрыть окно регистрации
        new LoginWindow(); // Показать окно логина
    }

}
