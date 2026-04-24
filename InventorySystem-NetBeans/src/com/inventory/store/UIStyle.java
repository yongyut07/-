package com.inventory.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public final class UIStyle {
    public static final Color PRIMARY = new Color(41, 128, 185);
    public static final Color PRIMARY_DARK = new Color(31, 97, 141);
    public static final Color SIDEBAR = new Color(236, 240, 241);
    public static final Color BG = new Color(248, 249, 250);
    public static final Color CARD = Color.WHITE;
    public static final Color SUCCESS = new Color(39, 174, 96);
    public static final Color WARNING = new Color(230, 126, 34);
    public static final Color DANGER = new Color(192, 57, 43);

    private UIStyle() {}

    public static Font font(int style, int size) {
        return new Font("Tahoma", style, size);
    }

    public static void styleFrame(JFrame frame, String title) {
        frame.setTitle(title);
        frame.setSize(1280, 760);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(BG);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        stylePrimaryButton(button);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBackground(new Color(52, 73, 94));
        return button;
    }

    public static void stylePrimaryButton(AbstractButton button) {
        button.setFont(font(Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 16, 10, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void styleDangerButton(AbstractButton button) {
        stylePrimaryButton(button);
        button.setBackground(DANGER);
    }

    public static void styleSuccessButton(AbstractButton button) {
        stylePrimaryButton(button);
        button.setBackground(SUCCESS);
    }

    public static JPanel createCard() {
        JPanel panel = new JPanel();
        panel.setBackground(CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(14, 14, 14, 14)
        ));
        return panel;
    }

    public static void styleTextField(JTextField field) {
        field.setFont(font(Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210)),
                new EmptyBorder(8, 8, 8, 8)
        ));
    }

    public static void styleCombo(JComboBox<?> combo) {
        combo.setFont(font(Font.PLAIN, 14));
        combo.setBackground(Color.WHITE);
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(28);
        table.setFont(font(Font.PLAIN, 13));
        table.setGridColor(new Color(235, 235, 235));
        table.setSelectionBackground(new Color(214, 234, 248));
        table.setSelectionForeground(Color.BLACK);
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(font(Font.BOLD, 13));
        header.setBackground(PRIMARY_DARK);
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }
    }

    public static JLabel pageTitle(String title) {
        JLabel label = new JLabel(title);
        label.setFont(font(Font.BOLD, 24));
        label.setForeground(new Color(44, 62, 80));
        return label;
    }
}
