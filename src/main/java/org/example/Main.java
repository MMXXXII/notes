package org.example;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Вместо создания MainWindow, создаем LoginWindow
            new LoginWindow();  // Открываем окно логина
        });
    }
}
