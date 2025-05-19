package org.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.List;

public class CalendarPanel extends JPanel {
    private JLabel monthYearLabel;
    private JPanel calendarGrid;
    private YearMonth currentYearMonth;
    private Map<LocalDate, List<Reminder>> reminderMap;
    private MainWindowController controller;
    private Color highlightColor = new Color(255, 200, 200); // Светло-красный цвет для выделения
    private JLabel[] dayLabels;
    private JButton[][] dayButtons;
    private final String[] DAYS_OF_WEEK = {"Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"};

    public CalendarPanel(MainWindowController controller) {
        this.controller = controller;
        this.currentYearMonth = YearMonth.now();
        this.reminderMap = new HashMap<>();

        setLayout(new BorderLayout(0, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);

        // Заголовок календаря
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Календарь", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(new Color(0, 102, 204)); // Синий цвет для заголовка
        headerPanel.add(titleLabel, BorderLayout.NORTH);

        // Панель с месяцем/годом и кнопками навигации
        JPanel navigationPanel = new JPanel(new BorderLayout());
        navigationPanel.setBackground(Color.WHITE);

        JButton prevButton = new JButton("◀");
        styleNavigationButton(prevButton);
        prevButton.addActionListener(e -> {
            currentYearMonth = currentYearMonth.minusMonths(1);
            updateCalendar();
        });

        JButton nextButton = new JButton("▶");
        styleNavigationButton(nextButton);
        nextButton.addActionListener(e -> {
            currentYearMonth = currentYearMonth.plusMonths(1);
            updateCalendar();
        });

        monthYearLabel = new JLabel("", SwingConstants.CENTER);
        monthYearLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        monthYearLabel.setForeground(new Color(0, 102, 204)); // Синий цвет для месяца/года

        navigationPanel.add(prevButton, BorderLayout.WEST);
        navigationPanel.add(monthYearLabel, BorderLayout.CENTER);
        navigationPanel.add(nextButton, BorderLayout.EAST);

        headerPanel.add(navigationPanel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // Сетка календаря
        calendarGrid = new JPanel(new GridLayout(7, 7, 5, 5));
        calendarGrid.setBackground(Color.WHITE);

        // Инициализация массивов для дней недели и кнопок дней
        dayLabels = new JLabel[7];
        dayButtons = new JButton[6][7];

        // Добавление меток дней недели
        for (int i = 0; i < 7; i++) {
            dayLabels[i] = new JLabel(DAYS_OF_WEEK[i], SwingConstants.CENTER);
            dayLabels[i].setFont(new Font("SansSerif", Font.BOLD, 12));

            // Выделяем выходные дни другим цветом
            if (i >= 5) { // Суббота и воскресенье
                dayLabels[i].setForeground(new Color(0, 150, 0)); // Зеленый цвет для выходных
            } else {
                dayLabels[i].setForeground(Color.BLACK);
            }

            calendarGrid.add(dayLabels[i]);
        }

        // Инициализация кнопок дней
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                dayButtons[row][col] = new JButton();
                dayButtons[row][col].setFont(new Font("SansSerif", Font.PLAIN, 12));
                dayButtons[row][col].setFocusPainted(false);
                dayButtons[row][col].setBorderPainted(false);
                dayButtons[row][col].setContentAreaFilled(false);
                dayButtons[row][col].setOpaque(true);
                dayButtons[row][col].setBackground(Color.WHITE);

                final int finalRow = row;
                final int finalCol = col;

                dayButtons[row][col].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (!dayButtons[finalRow][finalCol].getText().isEmpty()) {
                            dayButtons[finalRow][finalCol].setBorderPainted(true);
                            dayButtons[finalRow][finalCol].setBorder(new LineBorder(Color.GRAY));
                        }
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        dayButtons[finalRow][finalCol].setBorderPainted(false);
                    }
                });

                dayButtons[row][col].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JButton source = (JButton) e.getSource();
                        if (!source.getText().isEmpty()) {
                            int day = Integer.parseInt(source.getText());
                            LocalDate selectedDate = currentYearMonth.atDay(day);
                            controller.openRemindersForDate(selectedDate);
                        }
                    }
                });

                calendarGrid.add(dayButtons[row][col]);
            }
        }

        add(calendarGrid, BorderLayout.CENTER);

        // Обновляем календарь с текущим месяцем
        updateCalendar();
    }

    // Стилизация кнопок навигации
    private void styleNavigationButton(JButton button) {
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setForeground(new Color(0, 102, 204)); // Синий цвет для кнопок
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // Обновление календаря
    public void updateCalendar() {
        // Обновляем метку месяца и года
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("ru"));
        monthYearLabel.setText(currentYearMonth.format(formatter));

        // Получаем напоминания для текущего месяца
        try {
            reminderMap = DatabaseHandlerReminders.getRemindersForMonth(controller.getUserId(), currentYearMonth);
        } catch (SQLException e) {
            e.printStackTrace();
            reminderMap = new HashMap<>();
        }

        // Очищаем все кнопки дней
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 7; col++) {
                dayButtons[row][col].setText("");
                dayButtons[row][col].setBackground(Color.WHITE);
                dayButtons[row][col].setForeground(Color.BLACK);
            }
        }

        // Получаем первый день месяца и его день недели
        LocalDate firstDayOfMonth = currentYearMonth.atDay(1);
        DayOfWeek firstDayOfWeek = firstDayOfMonth.getDayOfWeek();

        // Корректируем индекс, так как в России неделя начинается с понедельника (1)
        int dayOfWeekIndex = firstDayOfWeek.getValue() - 1; // 0 для понедельника, 6 для воскресенья

        // Заполняем календарь
        int daysInMonth = currentYearMonth.lengthOfMonth();
        int day = 1;

        for (int row = 0; row < 6 && day <= daysInMonth; row++) {
            for (int col = 0; col < 7 && day <= daysInMonth; col++) {
                if (row == 0 && col < dayOfWeekIndex) {
                    // Пустые ячейки до начала месяца
                    continue;
                }

                dayButtons[row][col].setText(String.valueOf(day));

                // Проверяем, есть ли напоминания на этот день
                LocalDate currentDate = currentYearMonth.atDay(day);
                if (reminderMap.containsKey(currentDate) && !reminderMap.get(currentDate).isEmpty()) {
                    dayButtons[row][col].setBackground(highlightColor);
                }

                // Выделяем выходные дни
                if (col >= 5) { // Суббота и воскресенье
                    dayButtons[row][col].setForeground(new Color(0, 150, 0)); // Зеленый цвет для выходных
                }

                // Выделяем текущий день
                if (currentDate.equals(LocalDate.now())) {
                    dayButtons[row][col].setBorder(new LineBorder(Color.BLUE, 1));
                    dayButtons[row][col].setBorderPainted(true);
                    dayButtons[row][col].setFont(new Font("SansSerif", Font.BOLD, 12));
                }

                day++;
            }
        }

        revalidate();
        repaint();
    }
}