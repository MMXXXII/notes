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
        // Устанавливаем заголовок окна
        setTitle("Вход в систему");
        // Задаем размер окна
        setSize(360, 320);
        // Завершаем программу при закрытии окна
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // Центрируем окно на экране
        setLocationRelativeTo(null);

        // Создаем основную панель с белым фоном и отступами
        var mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        // Добавляем основную панель в окно
        add(mainPanel);

        // Заголовок окна с приветственным сообщением
        JLabel titleLabel = new JLabel("Добро пожаловать");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        // Добавляем вертикальный промежуток после заголовка
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Создаем панель для формы ввода
        var formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        // Добавляем поле для ввода логина
        formPanel.add(createLabel("Логин:"));
        setupTextField(usernameField);
        formPanel.add(usernameField);
        // Добавляем промежуток между полями ввода
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Добавляем поле для ввода пароля
        formPanel.add(createLabel("Пароль:"));
        setupPasswordField(passwordField);
        formPanel.add(passwordField);

        // Добавляем кнопку для отображения/скрытия пароля
        setupTogglePasswordButton();
        formPanel.add(togglePasswordVisibilityButton);
        // Добавляем форму на основную панель
        mainPanel.add(formPanel);
        // Добавляем вертикальный промежуток после формы
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Создаем панель с кнопками входа и регистрации
        var buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);

        // Создаем и настраиваем кнопки "Войти" и "Регистрация"
        var loginButton = createStyledButton("Войти");
        var registerButton = createStyledButton("Регистрация");

        // Добавляем обработчики событий на кнопки
        loginButton.addActionListener(e -> login());
        registerButton.addActionListener(e -> openRegisterWindow());

        // Добавляем кнопки на панель
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        // Добавляем панель с кнопками на основную панель
        mainPanel.add(buttonPanel);

        // Делаем окно видимым
        setVisible(true);
    }

    // Метод настройки текстового поля (логин)
    private void setupTextField(JTextField field) {
        // Устанавливаем предпочтительный размер и границу текстового поля
        field.setPreferredSize(new Dimension(300, 30));
        field.setBorder(new LineBorder(new Color(200, 200, 200), 1, true));
    }

    // Метод настройки поля пароля (маскируется символом '*')
    private void setupPasswordField(JPasswordField field) {
        // Используем общие настройки для текстового поля
        setupTextField(field);
        // Устанавливаем шрифт и символ маскирования пароля
        field.setFont(new Font("SansSerif", Font.PLAIN, 12));
        field.setEchoChar('*');
    }

    // Метод настройки кнопки для отображения/скрытия пароля
    private void setupTogglePasswordButton() {
        togglePasswordVisibilityButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        togglePasswordVisibilityButton.setPreferredSize(new Dimension(90, 25));
        togglePasswordVisibilityButton.setBackground(Color.WHITE);
        togglePasswordVisibilityButton.setForeground(Color.DARK_GRAY);
        togglePasswordVisibilityButton.setFocusPainted(false);
        togglePasswordVisibilityButton.setBorderPainted(false);

        // Обработчик клика по кнопке (показ/скрытие пароля)
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

    // Метод создания и настройки метки (лейбла) для формы
    private JLabel createLabel(String text) {
        var label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 16));
        return label;
    }

    // Метод создания стилизованной кнопки (например, Войти или Регистрация)
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

        // Обработчики изменения шрифта при наведении курсора
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

    // Метод авторизации пользователя
    private void login() {
        // Получаем введенные логин и пароль
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try {
            // Проверяем учетные данные через DatabaseHandler
            if (DatabaseHandler.authenticateUser(username, password)) {
                // Вход успешен, отображаем приветственное сообщение
                new MessageWindow(this, "Вход успешен! Добро пожаловать, " + username, "Успех");

                // Получаем данные пользователя (ID и email)
                int userId = getUserData(username, "id", -1);
                String email = getUserData(username, "email", null);

                // Открываем главное окно приложения
                new MainWindow(userId, username, email).setVisible(true);
                dispose();
            } else {
                // Неверные учетные данные, отображаем сообщение об ошибке
                new MessageWindow(this, "Неверный логин или пароль.", "Ошибка");
            }
        } catch (Exception e) {
            // Обрабатываем ошибки базы данных
            e.printStackTrace();
            new MessageWindow(this, "Ошибка подключения к базе данных.", "Ошибка");
        }
    }

    // Универсальный метод для получения одного значения из базы данных по username
    private <T> T getUserData(String username, String column, T defaultValue) throws SQLException, ClassNotFoundException {
        String sql = "SELECT " + column + " FROM users WHERE username = ?";
        try (Connection conn = DatabaseHandler.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            // Возвращаем значение, если оно найдено
            if (rs.next()) {
                Object val = rs.getObject(column);
                if (val != null) return (T) val;
            }
        }
        // Возвращаем значение по умолчанию, если данных нет
        return defaultValue;
    }

    // Метод открытия окна регистрации
    private void openRegisterWindow() {
        // Закрываем текущее окно и открываем окно регистрации
        dispose();
        new RegisterWindow();
    }
}
