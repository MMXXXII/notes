package org.example; // Определение пакета для класса

// Импорт необходимых библиотек
import javax.swing.*; // Основная библиотека Swing для создания GUI
import javax.swing.plaf.basic.BasicScrollBarUI; // Для настройки внешнего вида полосы прокрутки
import javax.swing.border.EmptyBorder; // Для создания пустых границ (отступов)
import javax.swing.border.LineBorder; // Для создания линейных границ
import java.awt.*; // Базовые классы AWT для работы с графикой
import java.awt.event.*; // Классы для обработки событий
import java.util.List; // Для работы со списками

// Основной класс главного окна приложения, наследуется от JFrame
public class MainWindow extends JFrame {
    // Объявление приватных полей класса
    private JPanel mainPanel; // Основная панель окна
    private JPanel notesPanel; // Панель для отображения заметок
    private boolean isResizing = false; // Флаг для отслеживания изменения размера окна
    private MainWindowController controller; // Контроллер для обработки бизнес-логики
    private CalendarPanel calendarPanel; // Панель календаря

    // Конструктор основного окна приложения
    public MainWindow(int userId, String username, String email) {
        // Настройка основных параметров окна
        setTitle("Notes App"); // Устанавливаем заголовок окна
        setResizable(true); // Разрешаем изменение размеров окна
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Закрытие программы при закрытии окна
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Окно открывается в полноэкранном режиме
        setLocationRelativeTo(null); // Центрируем окно на экране

        // Создаем контроллер для обработки бизнес-логики
        controller = new MainWindowController(this, userId, username, email);

        // Создаем основную панель с менеджером компоновки BorderLayout
        mainPanel = new JPanel(new BorderLayout(15, 15)); // Отступы между компонентами 15 пикселей
        mainPanel.setBackground(Color.WHITE); // Устанавливаем белый фон основной панели
        add(mainPanel); // Добавляем основную панель в окно

        // Настраиваем верхнюю панель с информацией о пользователе
        setupHeaderPanel(userId, username);

        // Создаем панель с разделением на заметки и календарь
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT); // Горизонтальное разделение
        splitPane.setDividerLocation(0.7); // 70% для заметок, 30% для календаря
        splitPane.setResizeWeight(0.7); // Вес при изменении размера
        splitPane.setBorder(null); // Убираем границу
        splitPane.setDividerSize(5); // Размер разделителя 5 пикселей
        splitPane.setContinuousLayout(true); // Непрерывное обновление при перетаскивании

        // Левая панель - заметки
        JPanel leftPanel = new JPanel(new BorderLayout()); // Панель с компоновкой BorderLayout
        leftPanel.setBackground(Color.WHITE); // Белый фон

        // Настраиваем панель для отображения заметок
        setupNotesPanel(); // Вызов метода настройки панели заметок
        leftPanel.add(createNotesContainer(), BorderLayout.CENTER); // Добавляем контейнер заметок в центр

        // Кнопка корзины
        JButton trashButton = createStyledButton("Корзина"); // Создаем стилизованную кнопку
        trashButton.addActionListener(e -> controller.openTrashDialog()); // Добавляем обработчик нажатия

        // Кнопка добавления заметки
        JButton addNoteButton = createStyledButton("Добавить заметку"); // Создаем стилизованную кнопку
        addNoteButton.addActionListener(e -> controller.openAddNoteDialog()); // Добавляем обработчик нажатия

        // Панель для кнопок внизу левой панели
        JPanel addButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Выравнивание кнопок по правому краю
        addButtonPanel.setBackground(Color.WHITE); // Белый фон
        addButtonPanel.add(trashButton); // Добавляем кнопку корзины
        addButtonPanel.add(addNoteButton); // Добавляем кнопку добавления заметки
        leftPanel.add(addButtonPanel, BorderLayout.SOUTH); // Размещаем панель кнопок внизу

        // Правая панель - календарь
        calendarPanel = new CalendarPanel(controller); // Создаем панель календаря
        controller.setCalendarPanel(calendarPanel); // Передаем ссылку на календарь в контроллер

        // Добавляем панели в разделитель
        splitPane.setLeftComponent(leftPanel); // Устанавливаем левую панель
        splitPane.setRightComponent(calendarPanel); // Устанавливаем правую панель

        // Добавляем разделитель в основную панель
        mainPanel.add(splitPane, BorderLayout.CENTER);

        // Загружаем заметки пользователя
        controller.loadNotes();
    }

    // Метод настройки верхней панели (хедера) приложения
    private void setupHeaderPanel(int userId, String username) {
        // Создаем панель хедера с горизонтальным расположением компонентов
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS)); // Горизонтальное расположение
        headerPanel.setBackground(Color.WHITE); // Белый фон для хедера

        // Добавляем панель поиска на хедер
        headerPanel.add(setupSearchPanel()); // Вызов метода создания панели поиска
        // Добавляем горизонтальный промежуток между панелью поиска и панелью пользователя
        headerPanel.add(Box.createHorizontalStrut(20)); // Отступ 20 пикселей

        // Создаем метку с именем пользователя (кликабельная)
        JLabel usernameLabel = new JLabel(username); // Метка с именем пользователя
        usernameLabel.setForeground(Color.BLUE); // Синий цвет текста (стиль ссылки)
        usernameLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Курсор "рука"

        // Обработчик клика на имя пользователя (открывает профиль)
        usernameLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                controller.openUserProfile(); // Открытие профиля пользователя через контроллер
            }
        });

        // Создаем панель пользователя (имя пользователя)
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0)); // Центрирование без отступов
        userPanel.setBackground(Color.WHITE); // Белый фон панели
        userPanel.add(usernameLabel); // Добавляем имя пользователя на панель

        // Добавляем панель пользователя в хедер
        headerPanel.add(userPanel);

        // Устанавливаем отступ сверху для главной панели
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0)); // Отступ 10 пикселей сверху
        // Добавляем хедер в верхнюю часть основной панели
        mainPanel.add(headerPanel, BorderLayout.NORTH);
    }

    // Метод настройки панели поиска заметок
    private JPanel setupSearchPanel() {
        // Создаем текстовое поле для ввода поискового запроса
        JTextField searchField = new JTextField();
        searchField.setForeground(Color.DARK_GRAY); // Цвет текста (темно-серый)
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14)); // Шрифт текста

        // Настраиваем границу текстового поля с внутренними отступами
        searchField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true), // Внешняя рамка (светло-серая)
                new EmptyBorder(5, 10, 5, 10) // Внутренний отступ (поля)
        ));

        // Создаем кнопку поиска
        JButton searchButton = createStyledButton("Поиск"); // Создаем стилизованную кнопку
        // Добавляем обработчик нажатия кнопки поиска через контроллер
        searchButton.addActionListener(e -> controller.searchNotes(searchField.getText()));

        // Создаем панель поиска с компоновкой BorderLayout
        JPanel panel = new JPanel(new BorderLayout(5, 5)); // Отступы между компонентами 5 пикселей
        panel.setBackground(Color.WHITE); // Белый фон панели
        panel.add(searchField, BorderLayout.CENTER); // Поле поиска занимает центр
        panel.add(searchButton, BorderLayout.EAST); // Кнопка поиска справа

        // Добавляем отступ к левой стороне панели
        panel.setBorder(new EmptyBorder(0, 20, 0, 0)); // Отступ 20 пикселей слева
        return panel; // Возвращаем настроенную панель поиска
    }

    // Метод настройки панели заметок
    private void setupNotesPanel() {
        notesPanel = new JPanel(); // Создаем новую панель
        notesPanel.setLayout(new BoxLayout(notesPanel, BoxLayout.Y_AXIS)); // Вертикальное расположение
        notesPanel.setBackground(Color.WHITE); // Белый фон
    }

    // Метод создания контейнера для заметок
    private JPanel createNotesContainer() {
        // Заголовок "ЗАМЕТКИ"
        JLabel titleLabel = new JLabel("ЗАМЕТКИ", SwingConstants.CENTER); // Центрированный заголовок
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24)); // Жирный шрифт размером 24
        titleLabel.setForeground(Color.DARK_GRAY); // Темно-серый цвет текста

        // Панель для заголовка
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Центрирование заголовка
        titlePanel.setBackground(Color.WHITE); // Белый фон
        titlePanel.add(titleLabel); // Добавляем заголовок на панель

        // Создаем прокручиваемую панель для заметок
        JScrollPane scrollPane = new JScrollPane(notesPanel); // Панель прокрутки с панелью заметок
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); // Всегда показывать вертикальную полосу прокрутки
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); // Горизонтальную - при необходимости
        scrollPane.getViewport().setBackground(Color.WHITE); // Белый фон области просмотра
        scrollPane.setBorder(null); // Убираем границу

        // Кастомизация скроллбара
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
                g.setColor(Color.GRAY); // Серый цвет ползунка
                g.fillRect(r.x, r.y, r.width, r.height); // Заполняем прямоугольник ползунка
            }

            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
                g.setColor(Color.WHITE); // Белый цвет дорожки
                g.fillRect(r.x, r.y, r.width, r.height); // Заполняем прямоугольник дорожки
            }
        });
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(15, Integer.MAX_VALUE)); // Устанавливаем ширину полосы прокрутки

        // Основной контейнер
        JPanel notesContainer = new JPanel(new BorderLayout()); // Панель с компоновкой BorderLayout
        notesContainer.setBackground(Color.WHITE); // Белый фон
        notesContainer.add(titlePanel, BorderLayout.NORTH); // Заголовок вверху
        notesContainer.add(scrollPane, BorderLayout.CENTER); // Прокручиваемая панель в центре

        return notesContainer; // Возвращаем созданный контейнер
    }

    // Метод для создания диалогового окна профиля пользователя
    public JDialog createProfileDialog() {
        // Создаем диалоговое окно с заголовком и модальностью (блокирует основное окно)
        JDialog profileDialog = new JDialog(this, "Профиль пользователя", true); // Модальное окно
        profileDialog.setResizable(true); // Позволяем изменять размер окна профиля
        profileDialog.setLocationRelativeTo(this); // Центрируем окно относительно главного окна

        // Создаем панель для содержимого профиля с вертикальным расположением элементов
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS)); // Вертикальное расположение
        profilePanel.setBackground(Color.WHITE); // Белый фон панели
        profilePanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Отступы вокруг содержимого

        // Метка с ником пользователя, выравнена по центру
        JLabel userLabel = new JLabel("Ник: " + controller.getUsername()); // Метка с ником
        userLabel.setFont(new Font("SansSerif", Font.PLAIN, 16)); // Шрифт размером 16
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Выравнивание по центру

        // Метка с email пользователя, выравнена по центру
        JLabel emailLabel = new JLabel("Почта: " + controller.getEmail()); // Метка с email
        emailLabel.setFont(new Font("SansSerif", Font.PLAIN, 16)); // Шрифт размером 16
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Выравнивание по центру

        // Кнопка "Закрыть" для закрытия окна профиля
        JButton closeButton = createStyledButton("Закрыть"); // Создаем стилизованную кнопку
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Выравнивание по центру
        closeButton.addActionListener(e -> profileDialog.dispose()); // Закрытие диалога при нажатии

        // Кнопка "Выйти из аккаунта" с подтверждением выхода
        JButton logoutButton = createStyledButton("Выйти из аккаунта"); // Создаем стилизованную кнопку
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Выравнивание по центру
        logoutButton.addActionListener(e -> controller.showExitConfirmation(profileDialog)); // Показ подтверждения выхода

        // Добавляем компоненты на панель с отступами между ними
        profilePanel.add(userLabel); // Добавляем метку с ником
        profilePanel.add(Box.createVerticalStrut(10)); // Отступ 10 пикселей
        profilePanel.add(emailLabel); // Добавляем метку с email
        profilePanel.add(Box.createVerticalStrut(30)); // Отступ 30 пикселей
        profilePanel.add(closeButton); // Добавляем кнопку закрытия
        profilePanel.add(Box.createVerticalStrut(10)); // Отступ 10 пикселей
        profilePanel.add(logoutButton); // Добавляем кнопку выхода

        // Добавляем панель в диалоговое окно
        profileDialog.add(profilePanel);
        profileDialog.pack(); // Автоматический подбор размеров окна под содержимое

        return profileDialog; // Возвращаем созданное диалоговое окно
    }

    // Метод для создания диалогового окна подтверждения выхода
    public JDialog createExitConfirmationDialog(JDialog parentDialog) {
        // Создаем диалоговое окно подтверждения
        JDialog confirmDialog = new JDialog(parentDialog, "Подтверждение выхода", true); // Модальное окно
        confirmDialog.setSize(400, 150); // Размер окна
        confirmDialog.setLocationRelativeTo(parentDialog); // Центрируем относительно родительского окна
        confirmDialog.setResizable(false); // Запрещаем изменение размера

        // Создаем основную панель
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE); // Белый фон
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Отступы
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS)); // Вертикальное расположение
        confirmDialog.add(mainPanel); // Добавляем панель в диалог

        // Добавляем вопрос подтверждения
        mainPanel.add(new JLabel("Вы точно хотите выйти из аккаунта?", SwingConstants.CENTER));

        // Панель для кнопок
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0)); // Центрирование с отступами
        buttonPanel.setOpaque(false); // Прозрачный фон
        mainPanel.add(Box.createVerticalStrut(10)); // Отступ 10 пикселей
        mainPanel.add(buttonPanel); // Добавляем панель кнопок

        // Добавляем кнопки "Да" и "Нет"
        buttonPanel.add(createStyledButton("Да", e -> controller.logout(parentDialog))); // Кнопка "Да" с обработчиком
        buttonPanel.add(createStyledButton("Нет", e -> confirmDialog.dispose())); // Кнопка "Нет" с обработчиком

        return confirmDialog; // Возвращаем созданное диалоговое окно
    }

    // Вспомогательный метод для создания кнопки с обработчиком
    private JButton createStyledButton(String text, ActionListener action) {
        JButton button = createStyledButton(text); // Создаем стилизованную кнопку
        button.addActionListener(action); // Добавляем обработчик
        return button; // Возвращаем кнопку
    }

    // Метод для создания стилизованной кнопки
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text); // Создаем кнопку с текстом
        button.setBackground(Color.WHITE); // Белый фон
        button.setForeground(Color.DARK_GRAY); // Темно-серый текст
        button.setFocusPainted(false); // Убираем отрисовку фокуса
        button.setBorderPainted(false); // Убираем отрисовку границы
        button.setFont(new Font("SansSerif", Font.PLAIN, 14)); // Шрифт размером 14
        button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Курсор "рука"
        button.setOpaque(true); // Делаем кнопку непрозрачной
        button.setContentAreaFilled(false); // Убираем заливку области содержимого

        // Эффекты при наведении
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setFont(new Font("SansSerif", Font.BOLD, 14)); // Жирный шрифт при наведении
            }
            public void mouseExited(MouseEvent evt) {
                button.setFont(new Font("SansSerif", Font.PLAIN, 14)); // Обычный шрифт при уходе курсора
            }
        });
        return button; // Возвращаем стилизованную кнопку
    }

    // Метод для отображения заметок в интерфейсе
    public void displayNotes(List<Note> notes) {
        // Очищаем панель с заметками перед новой отрисовкой
        notesPanel.removeAll();

        // Если заметок нет, показываем сообщение об отсутствии заметок
        if (notes.isEmpty()) {
            JPanel messagePanel = new JPanel(); // Создаем панель для сообщения
            messagePanel.setBackground(Color.WHITE); // Белый фон

            JLabel noResultsLabel = new JLabel("Заметок пока нет... \uD83D\uDE22"); // Сообщение с эмодзи
            noResultsLabel.setFont(new Font("SansSerif", Font.PLAIN, 14)); // Шрифт размером 14
            noResultsLabel.setForeground(Color.GRAY); // Серый цвет текста

            messagePanel.add(noResultsLabel); // Добавляем метку на панель
            messagePanel.setLayout(new FlowLayout(FlowLayout.CENTER)); // Центрируем сообщение
            notesPanel.add(messagePanel); // Добавляем панель сообщения на панель заметок

        } else {
            // Если заметки есть, создаем сетку для их отображения
            JPanel gridPanel = new JPanel();
            gridPanel.setLayout(new GridBagLayout()); // Используем GridBagLayout для сетки карточек
            gridPanel.setBackground(Color.WHITE); // Белый фон
            GridBagConstraints gbc = new GridBagConstraints(); // Ограничения для размещения компонентов

            int cardWidth = 530; // Ширина каждой карточки заметки
            int cardHeight = 200; // Высота каждой карточки заметки

            // Проходим по всем заметкам и создаем для каждой карточку
            for (int i = 0; i < notes.size(); i++) {
                Note note = notes.get(i); // Получаем заметку

                // Панель карточки заметки с BorderLayout
                JPanel noteCard = new JPanel(new BorderLayout(5, 5)); // Отступы между компонентами 5 пикселей
                noteCard.setBackground(new Color(250, 250, 250)); // Светлый фон карточки
                noteCard.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Курсор "рука" при наведении

                // Открываем заметку по клику на карточку через контроллер
                noteCard.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        controller.openNote(note); // Открытие заметки
                    }
                });

                // Фиксируем размеры карточки
                noteCard.setPreferredSize(new Dimension(cardWidth, cardHeight)); // Предпочтительный размер
                noteCard.setMaximumSize(new Dimension(cardWidth, cardHeight)); // Максимальный размер
                noteCard.setMinimumSize(new Dimension(cardWidth, cardHeight)); // Минимальный размер

                // Добавляем рамку с отступом вокруг карточки
                noteCard.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(new Color(220, 220, 220), 1, false), // Внешняя рамка
                        new EmptyBorder(10, 10, 10, 10) // Внутренний отступ
                ));

                // Заголовок заметки: жирный заголовок, тип заметки и дата создания
                JLabel titleLabel = new JLabel(
                        "<html><b>" + note.getTitle() + "</b> (" + note.getNoteType() + ")<br><i>" +
                                "Создано: " + note.getCreatedAt() + "</i></html>"
                );
                titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14)); // Шрифт размером 14
                titleLabel.setForeground(Color.DARK_GRAY); // Темно-серый цвет текста
                noteCard.add(titleLabel, BorderLayout.NORTH); // Добавляем заголовок вверху карточки

                // Текстовое поле с содержимым заметки (не редактируемое)
                JTextArea contentArea = new JTextArea(note.getContent()); // Текстовая область с содержимым
                contentArea.setEditable(false); // Запрещаем редактирование
                contentArea.setWrapStyleWord(true); // Перенос слов
                contentArea.setLineWrap(true); // Перенос строк
                contentArea.setFont(new Font("SansSerif", Font.PLAIN, 14)); // Шрифт размером 14
                contentArea.setForeground(Color.DARK_GRAY); // Темно-серый цвет текста
                contentArea.setBackground(new Color(250, 250, 250)); // Фон совпадает с карточкой
                contentArea.setBorder(null); // Убираем границу
                noteCard.add(contentArea, BorderLayout.CENTER); // Добавляем содержимое в центр карточки

                // Панель кнопок "Редактировать" и "Удалить"
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0)); // Выравнивание по правому краю
                buttonPanel.setBackground(new Color(250, 250, 250)); // Фон совпадает с карточкой

                JButton editButton = createStyledButton("Редактировать"); // Кнопка редактирования
                editButton.addActionListener(e -> controller.openNote(note)); // Обработчик нажатия

                JButton deleteButton = createStyledButton("Удалить"); // Кнопка удаления
                deleteButton.addActionListener(e -> controller.deleteNote(note)); // Обработчик нажатия

                buttonPanel.add(editButton); // Добавляем кнопку редактирования
                buttonPanel.add(deleteButton); // Добавляем кнопку удаления
                noteCard.add(buttonPanel, BorderLayout.SOUTH); // Добавляем панель кнопок внизу карточки

                // Расчет положения карточки в сетке (2 колонки)
                int row = i / 2; // Номер строки
                int col = i % 2; // Номер колонки

                gbc.gridx = col; // Устанавливаем координату x
                gbc.gridy = row; // Устанавливаем координату y
                gbc.insets = new Insets(10, 10, 10, 10); // Отступы между карточками

                gridPanel.add(noteCard, gbc); // Добавляем карточку в сетку

                // Отключаем фокусировку на карточках и тексте (для удобства UI)
                noteCard.setFocusable(false);
                contentArea.setFocusable(false);
            }

            // Устанавливаем layout и добавляем сетку в основную панель заметок
            notesPanel.setLayout(new BorderLayout());
            notesPanel.add(gridPanel, BorderLayout.CENTER);
        }

        // Обновляем UI после изменений
        notesPanel.revalidate(); // Перепроверяем компоновку
        notesPanel.repaint(); // Перерисовываем панель
    }

    // Метод для создания окна редактирования заметки
    public JFrame createNoteEditorFrame(Note note) {
        JFrame noteFrame = new JFrame("Редактор"); // Создаем новое окно с заголовком
        noteFrame.setSize(600, 400); // Устанавливаем размер окна
        noteFrame.setLocationRelativeTo(this); // Центрируем относительно главного окна

        JPanel notePanel = new JPanel(new BorderLayout(5, 5)); // Панель с отступами
        notePanel.setBackground(Color.WHITE); // Белый фон
        notePanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Отступы вокруг содержимого
        noteFrame.add(notePanel); // Добавляем панель в окно

        JTextField titleField = createTextField(note.getTitle(), Font.BOLD, 16); // Поле для заголовка
        JTextArea contentArea = createTextArea(note.getContent()); // Область для содержимого
        JComboBox<String> typeComboBox = createTypeComboBox(note.getNoteType()); // Выпадающий список типов

        JPanel inputPanel = new JPanel(); // Панель для полей ввода
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS)); // Вертикальное расположение
        inputPanel.setBackground(Color.WHITE); // Белый фон
        inputPanel.add(titleField); // Добавляем поле заголовка
        inputPanel.add(Box.createVerticalStrut(10)); // Отступ 10 пикселей
        inputPanel.add(new JLabel("Тип заметки")); // Метка для типа заметки
        inputPanel.add(typeComboBox); // Добавляем выпадающий список типов
        inputPanel.add(Box.createVerticalStrut(10)); // Отступ 10 пикселей
        inputPanel.add(new JScrollPane(contentArea)); // Добавляем прокручиваемую область содержимого

        JButton saveButton = createStyledButton("Сохранить"); // Кнопка сохранения
        saveButton.addActionListener(e -> controller.saveNote(note, titleField.getText(), contentArea.getText(), (String) typeComboBox.getSelectedItem(), noteFrame)); // Обработчик нажатия

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Панель для кнопки с выравниванием вправо
        buttonPanel.setBackground(Color.WHITE); // Белый фон
        buttonPanel.add(saveButton); // Добавляем кнопку сохранения

        notePanel.add(inputPanel, BorderLayout.CENTER); // Добавляем панель ввода в центр
        notePanel.add(buttonPanel, BorderLayout.SOUTH); // Добавляем панель кнопок внизу

        return noteFrame; // Возвращаем созданное окно
    }

    // Вспомогательный метод для создания текстового поля
    private JTextField createTextField(String text, int style, int size) {
        JTextField field = new JTextField(text); // Создаем текстовое поле с текстом
        field.setFont(new Font("SansSerif", style, size)); // Устанавливаем шрифт
        field.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1)); // Устанавливаем границу
        field.setBackground(Color.WHITE); // Белый фон
        field.setCaretColor(Color.BLACK); // Черный цвет курсора
        return field; // Возвращаем созданное поле
    }

    // Вспомогательный метод для создания текстовой области
    private JTextArea createTextArea(String text) {
        JTextArea area = new JTextArea(text, 10, 50); // Создаем текстовую область с текстом и размерами
        area.setWrapStyleWord(true); // Перенос слов
        area.setLineWrap(true); // Перенос строк
        area.setFont(new Font("SansSerif", Font.PLAIN, 14)); // Устанавливаем шрифт
        area.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1), // Внешняя рамка
                new EmptyBorder(5, 5, 5, 5) // Внутренний отступ
        ));
        return area; // Возвращаем созданную область
    }

    // Вспомогательный метод для создания выпадающего списка типов
    private JComboBox<String> createTypeComboBox(String selectedType) {
        JComboBox<String> comboBox = new JComboBox<>(new String[]{
                NoteType.ЛИЧНАЯ.name(), // Тип "ЛИЧНАЯ"
                NoteType.РАБОЧАЯ.name(), // Тип "РАБОЧАЯ"
                NoteType.ИДЕЯ.name(), // Тип "ИДЕЯ"
                NoteType.ЗАДАЧА.name() // Тип "ЗАДАЧА"
        });
        comboBox.setSelectedItem(selectedType); // Устанавливаем выбранный тип
        return comboBox; // Возвращаем созданный выпадающий список
    }

    // Метод для создания диалога добавления новой заметки
    public JDialog createAddNoteDialog() {
        JDialog dialog = new JDialog(this, "Добавить заметку", true); // Создаем модальное диалоговое окно
        dialog.setSize(500, 400); // Устанавливаем размер
        dialog.setLocationRelativeTo(this); // Центрируем относительно главного окна

        JPanel dialogPanel = new JPanel(new BorderLayout(10, 10)); // Панель с отступами
        dialogPanel.setBackground(Color.WHITE); // Белый фон
        dialogPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Отступы вокруг содержимого

        JPanel inputPanel = new JPanel(); // Панель для полей ввода
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS)); // Вертикальное расположение
        inputPanel.setBackground(Color.WHITE); // Белый фон

        // Заголовок
        JLabel titleLabel = new JLabel("Заголовок"); // Метка для заголовка
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Выравнивание по центру
        JTextField titleField = new JTextField(); // Поле для ввода заголовка
        titleField.setFont(new Font("SansSerif", Font.PLAIN, 14)); // Устанавливаем шрифт
        inputPanel.add(titleLabel); // Добавляем метку
        inputPanel.add(titleField); // Добавляем поле

        // Содержание
        JLabel contentLabel = new JLabel("Содержание"); // Метка для содержания
        JTextArea contentArea = new JTextArea(10, 30); // Область для ввода содержания
        contentArea.setFont(new Font("SansSerif", Font.PLAIN, 14)); // Устанавливаем шрифт
        JScrollPane contentScrollPane = new JScrollPane(contentArea); // Прокручиваемая панель для содержания
        inputPanel.add(contentLabel); // Добавляем метку
        inputPanel.add(contentScrollPane); // Добавляем прокручиваемую панель

        // Тип заметки
        JLabel typeLabel = new JLabel("Тип заметки"); // Метка для типа
        JComboBox<String> typeComboBox = new JComboBox<>(); // Выпадающий список типов
        typeComboBox.addItem(NoteType.ЛИЧНАЯ.name()); // Добавляем тип "ЛИЧНАЯ"
        typeComboBox.addItem(NoteType.РАБОЧАЯ.name()); // Добавляем тип "РАБОЧАЯ"
        typeComboBox.addItem(NoteType.ИДЕЯ.name()); // Добавляем тип "ИДЕЯ"
        typeComboBox.addItem(NoteType.ЗАДАЧА.name()); // Добавляем тип "ЗАДАЧА"

        inputPanel.add(typeLabel); // Добавляем метку
        inputPanel.add(typeComboBox); // Добавляем выпадающий список

        dialogPanel.add(inputPanel, BorderLayout.CENTER); // Добавляем панель ввода в центр

        JButton saveButton = createStyledButton("Сохранить"); // Кнопка сохранения
        saveButton.addActionListener(e -> {
            String title = titleField.getText().trim(); // Получаем заголовок
            String content = contentArea.getText().trim(); // Получаем содержание
            String noteType = (String) typeComboBox.getSelectedItem(); // Получаем тип
            controller.addNote(title, content, noteType); // Добавляем заметку через контроллер
            dialog.dispose(); // Закрываем диалог
        });

        dialogPanel.add(saveButton, BorderLayout.SOUTH); // Добавляем кнопку сохранения внизу
        dialog.add(dialogPanel); // Добавляем панель в диалог

        return dialog; // Возвращаем созданный диалог
    }
}