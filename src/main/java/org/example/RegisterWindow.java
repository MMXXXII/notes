package org.example;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.SQLException;
import java.util.regex.Pattern;

// Класс RegisterWindow представляет окно регистрации пользователя
public class RegisterWindow extends JFrame {

    // Поля ввода: логин, пароль и email
    private JTextField usernameField = new JTextField();
    private JPasswordField passwordField = new JPasswordField();
    private JTextField emailField = new JTextField();

    // Конструктор окна
    public RegisterWindow() {
        setTitle("Регистрация"); // Заголовок окна
        setSize(360, 320); // Размер окна
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // Закрытие только этого окна
        setLocationRelativeTo(null); // Центрируем на экране

        // Основная панель с вертикальной компоновкой и отступами
        var mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(mainPanel); // Добавляем панель в окно

        // Заголовок формы
        var titleLabel = new JLabel("Регистрация");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Центрируем заголовок
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Отступ

        // Панель с формой (логин, пароль, email)
        var formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        // Логин
        formPanel.add(createLabel("Логин:"));
        setupField(usernameField);
        formPanel.add(usernameField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Пароль
        formPanel.add(createLabel("Пароль:"));
        setupField(passwordField);
        formPanel.add(passwordField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Email
        formPanel.add(createLabel("Email:"));
        setupField(emailField);
        formPanel.add(emailField);

        mainPanel.add(formPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Панель с кнопками
        var buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);

        // Кнопка "Зарегистрироваться"
        var registerButton = createStyledButton("Зарегистрироваться");
        registerButton.addActionListener(e -> register()); // Обработчик регистрации
        buttonPanel.add(registerButton);

        // Кнопка "Назад"
        var backButton = createStyledButton("Назад");
        backButton.addActionListener(e -> goBack()); // Обработчик возврата
        buttonPanel.add(backButton);

        mainPanel.add(buttonPanel);

        setVisible(true); // Показываем окно
    }

    // Создаёт метку с заданным текстом
    private JLabel createLabel(String text) {
        var label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 16));
        return label;
    }

    // Настраивает поля ввода: максимальный размер и рамка
    private void setupField(JComponent field) {
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30)); // Ширина под родителя
        if (field instanceof JTextField) {
            field.setBorder(new LineBorder(new Color(200, 200, 200), 1, true)); // Светлая рамка с закруглением
        }
    }

    // Создаёт стилизованную кнопку с эффектом наведения
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

        // При наведении — делаем текст жирным
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

    // Метод регистрации
    private void register() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String email = emailField.getText();

        // Проверка пароля по регулярному выражению:
        // минимум 8 символов, одна заглавная буква, один спецсимвол
        String passwordRegex = "^(?=.*[A-Z])(?=.*[\\W_]).{8,}$";
        if (!Pattern.matches(passwordRegex, password)) {
            JOptionPane.showMessageDialog(this,
                    "Пароль должен содержать хотя бы одну заглавную букву, один специальный символ и быть не менее 8 символов.",
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Пытаемся зарегистрировать пользователя
            DatabaseHandler.registerUser(username, password, email);

            // Сообщаем об успехе и переходим к окну входа
            new MessageWindow(this, "Регистрация успешна!", "Успех");
            dispose(); // Закрываем окно регистрации
            new LoginWindow(); // Показываем окно входа
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace(); // Логгируем ошибку
            new MessageWindow(this, "Ошибка регистрации. Попробуйте позже.", "Ошибка");
        }
    }

    // Метод возврата в окно входа
    private void goBack() {
        dispose(); // Закрываем окно регистрации
        new LoginWindow(); // Показываем окно входа
    }
}
