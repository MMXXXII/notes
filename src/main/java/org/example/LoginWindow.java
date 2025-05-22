package org.example; // Указываем пакет, к которому принадлежит данный класс

// Импортируем необходимые классы из библиотеки Swing и AWT
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*; // Для графических компонентов
import java.sql.*; // Для работы с базой данных

// Объявляем класс LoginWindow, который расширяет JFrame (окно)
public class LoginWindow extends JFrame {

    // Поле для ввода логина, с предзаполненным значением
    private final JTextField usernameField = new JTextField("mmxxxii");
    // Поле для ввода пароля, с предзаполненным значением
    private final JPasswordField passwordField = new JPasswordField("Ctvty2005@");
    // Кнопка для отображения/скрытия пароля
    private final JButton togglePasswordVisibilityButton = new JButton("Показать");

    // Конструктор окна
    public LoginWindow() {
        setTitle("Вход в систему"); // Устанавливаем заголовок окна
        setSize(360, 320); // Размер окна
        setDefaultCloseOperation(EXIT_ON_CLOSE); // При закрытии — завершить приложение
        setLocationRelativeTo(null); // Центрировать окно на экране

        // Создаем основную панель
        var mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE); // Белый фон
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS)); // Вертикальное расположение элементов
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Внутренние отступы
        add(mainPanel); // Добавляем панель в окно

        // Заголовок окна
        JLabel titleLabel = new JLabel("Добро пожаловать");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24)); // Жирный шрифт, размер 24
        titleLabel.setAlignmentX(CENTER_ALIGNMENT); // Центрирование
        mainPanel.add(titleLabel); // Добавляем заголовок
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Промежуток

        // Панель формы ввода
        var formPanel = new JPanel();
        formPanel.setOpaque(false); // Прозрачный фон
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS)); // Вертикальное размещение

        // Метка и поле логина
        formPanel.add(createLabel("Логин:"));
        setupTextField(usernameField); // Настройка поля логина
        formPanel.add(usernameField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Промежуток

        // Метка и поле пароля
        formPanel.add(createLabel("Пароль:"));
        setupPasswordField(passwordField); // Настройка поля пароля
        formPanel.add(passwordField);

        // Кнопка показа/скрытия пароля
        setupTogglePasswordButton(); // Настройка кнопки
        formPanel.add(togglePasswordVisibilityButton); // Добавляем кнопку

        mainPanel.add(formPanel); // Добавляем форму на главную панель
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Промежуток

        // Панель с кнопками
        var buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0)); // Горизонтальное выравнивание
        buttonPanel.setOpaque(false); // Прозрачный фон

        // Создаем кнопки входа и регистрации
        var loginButton = createStyledButton("Войти");
        var registerButton = createStyledButton("Регистрация");

        // Обработчики кнопок
        loginButton.addActionListener(e -> login()); // Вход
        registerButton.addActionListener(e -> openRegisterWindow()); // Регистрация

        // Добавляем кнопки на панель
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        // Добавляем панель кнопок в основную панель
        mainPanel.add(buttonPanel);

        setVisible(true); // Отображаем окно
    }

    // Метод настройки текстового поля
    private void setupTextField(JTextField field) {
        field.setPreferredSize(new Dimension(300, 30)); // Размер поля
        field.setBorder(new LineBorder(new Color(200, 200, 200), 1, true)); // Серая рамка с закруглением
    }

    // Метод настройки поля пароля
    private void setupPasswordField(JPasswordField field) {
        setupTextField(field); // Общая настройка как для текстового поля
        field.setFont(new Font("SansSerif", Font.PLAIN, 12)); // Шрифт
        field.setEchoChar('*'); // Символ маскирования
    }

    // Метод настройки кнопки показа/скрытия пароля
    private void setupTogglePasswordButton() {
        togglePasswordVisibilityButton.setFont(new Font("SansSerif", Font.PLAIN, 12)); // Шрифт
        togglePasswordVisibilityButton.setPreferredSize(new Dimension(90, 25)); // Размер
        togglePasswordVisibilityButton.setBackground(Color.WHITE); // Белый фон
        togglePasswordVisibilityButton.setForeground(Color.DARK_GRAY); // Цвет текста
        togglePasswordVisibilityButton.setFocusPainted(false); // Без рамки при фокусе
        togglePasswordVisibilityButton.setBorderPainted(false); // Без границы

        // Добавляем действие по нажатию
        togglePasswordVisibilityButton.addActionListener(e -> {
            if (passwordField.getEchoChar() == '*') { // Если сейчас скрыт
                passwordField.setEchoChar((char) 0); // Показываем символы
                togglePasswordVisibilityButton.setText("Скрыть"); // Меняем текст кнопки
            } else {
                passwordField.setEchoChar('*'); // Снова скрываем
                togglePasswordVisibilityButton.setText("Показать");
            }
        });
    }

    // Метод создания метки (label)
    private JLabel createLabel(String text) {
        var label = new JLabel(text); // Создаем метку
        label.setFont(new Font("SansSerif", Font.PLAIN, 16)); // Шрифт
        return label;
    }

    // Метод создания стилизованной кнопки
    private JButton createStyledButton(String text) {
        var button = new JButton(text); // Создаем кнопку
        button.setBackground(Color.WHITE);
        button.setForeground(Color.DARK_GRAY);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Рука при наведении
        button.setOpaque(true);
        button.setContentAreaFilled(false);

        // Добавляем эффект при наведении
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setFont(button.getFont().deriveFont(Font.BOLD)); // Жирный при наведении
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setFont(button.getFont().deriveFont(Font.PLAIN)); // Обычный при уходе
            }
        });

        return button;
    }

    // Метод авторизации
    private void login() {
        String username = usernameField.getText(); // Получаем логин
        String password = new String(passwordField.getPassword()); // Получаем пароль

        try {
            // Проверка пользователя через базу данных
            if (DatabaseHandler.authenticateUser(username, password)) {
                new MessageWindow(this, "Вход успешен! Добро пожаловать, " + username, "Успех");

                // Получаем ID и email пользователя
                int userId = getUserData(username, "id", -1);
                String email = getUserData(username, "email", null);

                // Открываем главное окно
                new MainWindow(userId, username, email).setVisible(true);
                dispose(); // Закрываем окно входа
            } else {
                new MessageWindow(this, "Неверный логин или пароль.", "Ошибка"); // Ошибка
            }
        } catch (Exception e) {
            e.printStackTrace(); // Вывод ошибки в консоль
            new MessageWindow(this, "Ошибка подключения к базе данных.", "Ошибка");
        }
    }

    // Универсальный метод получения данных по логину
    private <T> T getUserData(String username, String column, T defaultValue) throws SQLException, ClassNotFoundException {
        String sql = "SELECT " + column + " FROM users WHERE username = ?"; // SQL-запрос
        try (Connection conn = DatabaseHandler.getConnection(); // Получаем соединение
             PreparedStatement stmt = conn.prepareStatement(sql)) { // Подготавливаем запрос
            stmt.setString(1, username); // Подставляем логин
            ResultSet rs = stmt.executeQuery(); // Выполняем запрос

            if (rs.next()) { // Если есть результат
                Object val = rs.getObject(column); // Получаем значение
                if (val != null) return (T) val; // Приводим и возвращаем
            }
        }
        return defaultValue; // Возвращаем значение по умолчанию
    }

    // Метод для открытия окна регистрации
    private void openRegisterWindow() {
        dispose(); // Закрываем текущее окно
        new RegisterWindow(); // Открываем окно регистрации
    }
}
