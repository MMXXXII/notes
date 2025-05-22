package org.example; // Определяет пакет, в котором находится класс

// Импорт необходимых библиотек
import javax.swing.*; // Основные компоненты Swing
import javax.swing.border.EmptyBorder; // Для создания пустых границ (отступов)
import javax.swing.border.LineBorder; // Для создания линейных границ
import java.awt.*; // Базовые компоненты AWT
import java.awt.event.ActionEvent; // Для обработки событий действия
import java.awt.event.ActionListener; // Интерфейс слушателя событий действия
import java.awt.event.MouseAdapter; // Адаптер для обработки событий мыши
import java.awt.event.MouseEvent; // Для обработки событий мыши
import java.sql.SQLException; // Для обработки SQL исключений
import java.time.DayOfWeek; // Для работы с днями недели
import java.time.LocalDate; // Для работы с датами
import java.time.YearMonth; // Для работы с месяцами и годами
import java.time.format.DateTimeFormatter; // Для форматирования дат
import java.time.format.TextStyle; // Для получения текстового представления элементов даты
import java.util.*; // Основные утилиты Java
import java.util.List; // Для работы со списками

// Основной класс панели календаря, наследуется от JPanel
public class CalendarPanel extends JPanel {
    private JLabel monthYearLabel; // Метка для отображения текущего месяца и года
    private JPanel calendarGrid; // Панель для сетки календаря
    private YearMonth currentYearMonth; // Текущий отображаемый месяц и год
    private Map<LocalDate, List<Reminder>> reminderMap; // Карта для хранения напоминаний по датам
    private MainWindowController controller; // Контроллер главного окна
    private Color highlightColor = new Color(255, 200, 200); // Светло-красный цвет для выделения дней с напоминаниями
    private JLabel[] dayLabels; // Массив меток для дней недели
    private JButton[][] dayButtons; // Двумерный массив кнопок для дней месяца
    private final String[] DAYS_OF_WEEK = {"Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"}; // Массив названий дней недели

    // Конструктор класса
    public CalendarPanel(MainWindowController controller) {
        this.controller = controller; // Сохраняем ссылку на контроллер
        this.currentYearMonth = YearMonth.now(); // Инициализируем текущим месяцем и годом
        this.reminderMap = new HashMap<>(); // Инициализируем пустую карту напоминаний

        setLayout(new BorderLayout(0, 10)); // Устанавливаем компоновщик BorderLayout с вертикальным отступом 10
        setBorder(new EmptyBorder(10, 10, 10, 10)); // Устанавливаем пустую границу с отступами 10 со всех сторон
        setBackground(Color.WHITE); // Устанавливаем белый фон для панели

        // Заголовок календаря
        JPanel headerPanel = new JPanel(new BorderLayout()); // Создаем панель заголовка с компоновщиком BorderLayout
        headerPanel.setBackground(Color.WHITE); // Устанавливаем белый фон для панели заголовка

        JLabel titleLabel = new JLabel("Календарь", SwingConstants.CENTER); // Создаем метку с текстом "Календарь" по центру
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18)); // Устанавливаем шрифт Sans Serif, жирный, размер 18
        titleLabel.setForeground(new Color(0, 102, 204)); // Устанавливаем синий цвет для заголовка
        headerPanel.add(titleLabel, BorderLayout.NORTH); // Добавляем метку в верхнюю часть панели заголовка

        // Панель с месяцем/годом и кнопками навигации
        JPanel navigationPanel = new JPanel(new BorderLayout()); // Создаем панель навигации с компоновщиком BorderLayout
        navigationPanel.setBackground(Color.WHITE); // Устанавливаем белый фон для панели навигации

        JButton prevButton = new JButton("◀"); // Создаем кнопку для перехода к предыдущему месяцу
        styleNavigationButton(prevButton); // Применяем стиль к кнопке навигации
        prevButton.addActionListener(e -> { // Добавляем обработчик события нажатия
            currentYearMonth = currentYearMonth.minusMonths(1); // Уменьшаем текущий месяц на 1
            updateCalendar(); // Обновляем календарь
        });

        JButton nextButton = new JButton("▶"); // Создаем кнопку для перехода к следующему месяцу
        styleNavigationButton(nextButton); // Применяем стиль к кнопке навигации
        nextButton.addActionListener(e -> { // Добавляем обработчик события нажатия
            currentYearMonth = currentYearMonth.plusMonths(1); // Увеличиваем текущий месяц на 1
            updateCalendar(); // Обновляем календарь
        });

        monthYearLabel = new JLabel("", SwingConstants.CENTER); // Создаем метку для отображения месяца и года по центру
        monthYearLabel.setFont(new Font("SansSerif", Font.BOLD, 16)); // Устанавливаем шрифт Sans Serif, жирный, размер 16
        monthYearLabel.setForeground(new Color(0, 102, 204)); // Устанавливаем синий цвет для метки месяца/года

        navigationPanel.add(prevButton, BorderLayout.WEST); // Добавляем кнопку предыдущего месяца в левую часть панели навигации
        navigationPanel.add(monthYearLabel, BorderLayout.CENTER); // Добавляем метку месяца/года в центр панели навигации
        navigationPanel.add(nextButton, BorderLayout.EAST); // Добавляем кнопку следующего месяца в правую часть панели навигации

        headerPanel.add(navigationPanel, BorderLayout.CENTER); // Добавляем панель навигации в центр панели заголовка
        add(headerPanel, BorderLayout.NORTH); // Добавляем панель заголовка в верхнюю часть основной панели

        // Сетка календаря
        calendarGrid = new JPanel(new GridLayout(7, 7, 5, 5)); // Создаем панель сетки с компоновщиком GridLayout 7x7 с отступами 5
        calendarGrid.setBackground(Color.WHITE); // Устанавливаем белый фон для сетки календаря

        // Инициализация массивов для дней недели и кнопок дней
        dayLabels = new JLabel[7]; // Инициализируем массив меток для 7 дней недели
        dayButtons = new JButton[6][7]; // Инициализируем двумерный массив кнопок для 6 недель по 7 дней

        // Добавление меток дней недели
        for (int i = 0; i < 7; i++) { // Цикл по 7 дням недели
            dayLabels[i] = new JLabel(DAYS_OF_WEEK[i], SwingConstants.CENTER); // Создаем метку с названием дня недели по центру
            dayLabels[i].setFont(new Font("SansSerif", Font.BOLD, 12)); // Устанавливаем шрифт Sans Serif, жирный, размер 12

            // Выделяем выходные дни другим цветом
            if (i >= 5) { // Если это суббота или воскресенье (индексы 5 и 6)
                dayLabels[i].setForeground(new Color(0, 150, 0)); // Устанавливаем зеленый цвет для выходных
            } else {
                dayLabels[i].setForeground(Color.BLACK); // Устанавливаем черный цвет для будних дней
            }

            calendarGrid.add(dayLabels[i]); // Добавляем метку дня недели в сетку календаря
        }

        // Инициализация кнопок дней
        for (int row = 0; row < 6; row++) { // Цикл по 6 неделям
            for (int col = 0; col < 7; col++) { // Цикл по 7 дням в неделе
                dayButtons[row][col] = new JButton(); // Создаем новую кнопку для дня
                dayButtons[row][col].setFont(new Font("SansSerif", Font.PLAIN, 12)); // Устанавливаем шрифт Sans Serif, обычный, размер 12
                dayButtons[row][col].setFocusPainted(false); // Отключаем отрисовку фокуса
                dayButtons[row][col].setBorderPainted(false); // Отключаем отрисовку границы
                dayButtons[row][col].setContentAreaFilled(false); // Отключаем заливку области содержимого
                dayButtons[row][col].setOpaque(true); // Делаем кнопку непрозрачной
                dayButtons[row][col].setBackground(Color.WHITE); // Устанавливаем белый фон для кнопки

                final int finalRow = row; // Создаем финальную переменную для строки для использования в анонимных классах
                final int finalCol = col; // Создаем финальную переменную для столбца для использования в анонимных классах

                dayButtons[row][col].addMouseListener(new MouseAdapter() { // Добавляем слушатель событий мыши
                    @Override
                    public void mouseEntered(MouseEvent e) { // Метод вызывается при наведении курсора на кнопку
                        if (!dayButtons[finalRow][finalCol].getText().isEmpty()) { // Если текст кнопки не пустой (есть число дня)
                            dayButtons[finalRow][finalCol].setBorderPainted(true); // Включаем отрисовку границы
                            dayButtons[finalRow][finalCol].setBorder(new LineBorder(Color.GRAY)); // Устанавливаем серую линейную границу
                        }
                    }

                    @Override
                    public void mouseExited(MouseEvent e) { // Метод вызывается при уходе курсора с кнопки
                        dayButtons[finalRow][finalCol].setBorderPainted(false); // Отключаем отрисовку границы
                    }
                });

                dayButtons[row][col].addActionListener(new ActionListener() { // Добавляем слушатель событий действия
                    @Override
                    public void actionPerformed(ActionEvent e) { // Метод вызывается при нажатии на кнопку
                        JButton source = (JButton) e.getSource(); // Получаем источник события (нажатую кнопку)
                        if (!source.getText().isEmpty()) { // Если текст кнопки не пустой (есть число дня)
                            int day = Integer.parseInt(source.getText()); // Преобразуем текст кнопки в число (день месяца)
                            LocalDate selectedDate = currentYearMonth.atDay(day); // Создаем объект даты из текущего месяца/года и дня
                            controller.openRemindersForDate(selectedDate); // Вызываем метод контроллера для открытия напоминаний на выбранную дату
                        }
                    }
                });

                calendarGrid.add(dayButtons[row][col]); // Добавляем кнопку дня в сетку календаря
            }
        }

        add(calendarGrid, BorderLayout.CENTER); // Добавляем сетку календаря в центр основной панели

        // Обновляем календарь с текущим месяцем
        updateCalendar(); // Вызываем метод обновления календаря
    }

    // Стилизация кнопок навигации
    private void styleNavigationButton(JButton button) {
        button.setFont(new Font("SansSerif", Font.BOLD, 14)); // Устанавливаем шрифт Sans Serif, жирный, размер 14
        button.setForeground(new Color(0, 102, 204)); // Устанавливаем синий цвет для текста кнопки
        button.setFocusPainted(false); // Отключаем отрисовку фокуса
        button.setBorderPainted(false); // Отключаем отрисовку границы
        button.setContentAreaFilled(false); // Отключаем заливку области содержимого
        button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Устанавливаем курсор в виде руки при наведении
    }

    // Обновление календаря
    public void updateCalendar() {
        // Обновляем метку месяца и года
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("ru")); // Создаем форматтер для месяца и года на русском языке
        monthYearLabel.setText(currentYearMonth.format(formatter)); // Устанавливаем текст метки с отформатированным месяцем и годом

        // Получаем напоминания для текущего месяца
        try {
            reminderMap = DatabaseHandlerReminders.getRemindersForMonth(controller.getUserId(), currentYearMonth); // Получаем напоминания из базы данных
        } catch (SQLException e) {
            e.printStackTrace(); // Выводим информацию об ошибке в консоль
            reminderMap = new HashMap<>(); // В случае ошибки инициализируем пустую карту
        }

        // Очищаем все кнопки дней
        for (int row = 0; row < 6; row++) { // Цикл по 6 неделям
            for (int col = 0; col < 7; col++) { // Цикл по 7 дням в неделе
                dayButtons[row][col].setText(""); // Очищаем текст кнопки
                dayButtons[row][col].setBackground(Color.WHITE); // Устанавливаем белый фон
                dayButtons[row][col].setForeground(Color.BLACK); // Устанавливаем черный цвет текста
            }
        }

        // Получаем первый день месяца и его день недели
        LocalDate firstDayOfMonth = currentYearMonth.atDay(1); // Создаем объект даты для первого дня текущего месяца
        DayOfWeek firstDayOfWeek = firstDayOfMonth.getDayOfWeek(); // Получаем день недели для первого дня месяца

        // Корректируем индекс, так как в России неделя начинается с понедельника (1)
        int dayOfWeekIndex = firstDayOfWeek.getValue() - 1; // Преобразуем значение дня недели в индекс (0 для понедельника, 6 для воскресенья)

        // Заполняем календарь
        int daysInMonth = currentYearMonth.lengthOfMonth(); // Получаем количество дней в текущем месяце
        int day = 1; // Начинаем с первого дня месяца

        for (int row = 0; row < 6 && day <= daysInMonth; row++) { // Цикл по 6 неделям или пока не закончатся дни месяца
            for (int col = 0; col < 7 && day <= daysInMonth; col++) { // Цикл по 7 дням в неделе или пока не закончатся дни месяца
                if (row == 0 && col < dayOfWeekIndex) { // Если это первая неделя и день недели меньше начального индекса
                    // Пустые ячейки до начала месяца
                    continue; // Пропускаем эту ячейку
                }

                dayButtons[row][col].setText(String.valueOf(day)); // Устанавливаем текст кнопки равным текущему дню

                // Проверяем, есть ли напоминания на этот день
                LocalDate currentDate = currentYearMonth.atDay(day); // Создаем объект даты для текущего дня
                if (reminderMap.containsKey(currentDate) && !reminderMap.get(currentDate).isEmpty()) { // Если есть напоминания на эту дату
                    dayButtons[row][col].setBackground(highlightColor); // Устанавливаем фон кнопки в цвет выделения
                }

                // Выделяем выходные дни
                if (col >= 5) { // Если это суббота или воскресенье (индексы 5 и 6)
                    dayButtons[row][col].setForeground(new Color(0, 150, 0)); // Устанавливаем зеленый цвет для текста
                }

                // Выделяем текущий день
                if (currentDate.equals(LocalDate.now())) { // Если текущая дата совпадает с сегодняшней
                    dayButtons[row][col].setBorder(new LineBorder(Color.BLUE, 1)); // Устанавливаем синюю линейную границу толщиной 1
                    dayButtons[row][col].setBorderPainted(true); // Включаем отрисовку границы
                    dayButtons[row][col].setFont(new Font("SansSerif", Font.BOLD, 12)); // Устанавливаем жирный шрифт
                }

                day++; // Увеличиваем день на 1
            }
        }

        revalidate(); // Перепроверяем компоновку
        repaint(); // Перерисовываем панель
    }
}