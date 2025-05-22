package org.example; // Указываем пакет, к которому принадлежит класс

import javax.swing.*; // Импорт компонентов Swing
import java.awt.*;     // Импорт AWT для графики и компоновки

// Класс MessageWindow — модальное диалоговое окно для отображения сообщений
public class MessageWindow extends JDialog {

    // Конструктор принимает родительское окно, текст сообщения и заголовок
    public MessageWindow(JFrame parent, String message, String title) {
        // Вызываем конструктор JDialog: parent — родитель, title — заголовок, true — модальное
        super(parent, title, true);

        // Центрируем окно относительно экрана (а не родителя)
        setLocationRelativeTo(null);

        // Создаём основную панель с вертикальной компоновкой
        var mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE); // Белый фон
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS)); // Вертикальная компоновка
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Отступы по краям

        // Создаём и настраиваем метку с сообщением
        var messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Центрируем по горизонтали
        mainPanel.add(messageLabel); // Добавляем на панель

        // Добавляем вертикальный отступ
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Панель для кнопок с выравниванием по центру
        var buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false); // Прозрачный фон

        // Создаём кнопку "Ок" и настраиваем её внешний вид
        var okButton = new JButton("Ок");
        styleButton(okButton); // Применяем стили

        // Добавляем обработчик нажатия — закрываем окно
        okButton.addActionListener(e -> dispose());

        // Добавляем кнопку на панель и панель на основную панель
        buttonPanel.add(okButton);
        mainPanel.add(buttonPanel);

        // Добавляем основную панель в диалоговое окно
        add(mainPanel);

        // Автоматически подгоняем размер окна по содержимому
        pack();

        // Показываем окно
        setVisible(true);
    }

    // Метод для стилизации кнопки
    private void styleButton(JButton button) {
        button.setBackground(Color.WHITE); // Белый фон
        button.setForeground(Color.DARK_GRAY); // Тёмный текст
        button.setFocusPainted(false); // Убираем рамку при фокусе
        button.setBorderPainted(false); // Убираем рамку кнопки
        button.setFont(new Font("SansSerif", Font.PLAIN, 14)); // Шрифт
        button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Курсор-рука
        button.setOpaque(true); // Прозрачность
        button.setContentAreaFilled(false); // Убираем фоновую заливку
    }
}
