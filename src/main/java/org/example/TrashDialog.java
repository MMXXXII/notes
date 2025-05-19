package org.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.List;

public class TrashDialog extends JDialog {
    private MainWindowController controller;
    private JPanel trashPanel;

    public TrashDialog(JFrame parent, MainWindowController controller) {
        super(parent, "Корзина", true);
        this.controller = controller;

        setSize(1200, 600);
        setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(Color.WHITE);

        // Заголовок
        JLabel titleLabel = new JLabel("Корзина", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(Color.DARK_GRAY);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Панель для списка удаленных заметок
        trashPanel = new JPanel();
        trashPanel.setLayout(new BoxLayout(trashPanel, BoxLayout.Y_AXIS));
        trashPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(trashPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton emptyTrashButton = createStyledButton("Очистить корзину");
        emptyTrashButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Вы уверены, что хотите очистить корзину? Все заметки будут удалены безвозвратно.",
                    "Подтверждение",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    controller.emptyTrash();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                loadTrashNotes();
            }
        });

        JButton closeButton = createStyledButton("Закрыть");
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(emptyTrashButton);
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Загружаем удаленные заметки
        loadTrashNotes();
    }

    private void loadTrashNotes() {
        trashPanel.removeAll();

        try {
            List<Note> trashNotes = controller.getTrashNotes();

            if (trashNotes.isEmpty()) {
                JLabel emptyLabel = new JLabel("Корзина пуста", SwingConstants.CENTER);
                emptyLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
                emptyLabel.setForeground(Color.GRAY);
                emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                trashPanel.add(Box.createVerticalStrut(50));
                trashPanel.add(emptyLabel);
            } else {
                for (Note note : trashNotes) {
                    JPanel notePanel = createNotePanel(note);
                    trashPanel.add(notePanel);
                    trashPanel.add(Box.createVerticalStrut(10));
                }
            }

            trashPanel.revalidate();
            trashPanel.repaint();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ошибка при загрузке удаленных заметок: " + e.getMessage());
        }
    }

    private JPanel createNotePanel(Note note) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(new Color(250, 250, 250));
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(10, 10, 10, 10)
        ));

        // Заголовок заметки и информация о времени удаления
        int daysSinceDeletion = note.getDaysSinceDeletion();
        String daysText = daysSinceDeletion == 0 ? "сегодня" :
                daysSinceDeletion == 1 ? "вчера" :
                        daysSinceDeletion + " дн. назад";

        JLabel titleLabel = new JLabel(
                "<html><b>" + note.getTitle() + "</b> (" + note.getNoteType() + ")<br><i>" +
                        "Удалено: " + daysText + "</i></html>"
        );
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        titleLabel.setForeground(Color.DARK_GRAY);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Содержимое заметки
        JTextArea contentArea = new JTextArea(note.getContent());
        contentArea.setEditable(false);
        contentArea.setWrapStyleWord(true);
        contentArea.setLineWrap(true);
        contentArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        contentArea.setForeground(Color.DARK_GRAY);
        contentArea.setBackground(new Color(250, 250, 250));
        contentArea.setBorder(null);

        // Ограничиваем высоту текстовой области
        int rows = Math.min(5, contentArea.getText().split("\n").length);
        contentArea.setRows(rows);

        panel.add(contentArea, BorderLayout.CENTER);

        // Кнопки управления
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.setBackground(new Color(250, 250, 250));

        JButton restoreButton = createStyledButton("Восстановить");
        restoreButton.addActionListener(e -> {
            try {
                controller.restoreNoteFromTrash(note.getId());
                loadTrashNotes();
                JOptionPane.showMessageDialog(this, "Заметка восстановлена!");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Ошибка при восстановлении заметки: " + ex.getMessage());
            }
        });

        JButton deleteButton = createStyledButton("Удалить навсегда");
        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Вы уверены, что хотите удалить эту заметку навсегда?",
                    "Подтверждение",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    controller.permanentlyDeleteNote(note.getId());
                    loadTrashNotes();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Ошибка при удалении заметки: " + ex.getMessage());
                }
            }
        });

        buttonPanel.add(restoreButton);
        buttonPanel.add(deleteButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
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

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setFont(new Font("SansSerif", Font.BOLD, 14));
            }
            public void mouseExited(MouseEvent evt) {
                button.setFont(new Font("SansSerif", Font.PLAIN, 14));
            }
        });

        return button;
    }
}