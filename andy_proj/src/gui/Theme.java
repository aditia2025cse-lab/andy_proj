package gui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Font;

// Keeps all colors, fonts and small styling helpers in one place.
public class Theme {

    public static final Color PAGE_BACKGROUND = new Color(0xF5F0E6);
    public static final Color CARD_BACKGROUND = new Color(0xFBF8F2);
    public static final Color PRIMARY = new Color(0xC9A57B);
    public static final Color SECONDARY = new Color(0xE6D5BE);
    public static final Color TEXT_DARK = new Color(0x5B4636);
    public static final Color ACCENT = new Color(0xA67C52);
    public static final Color TABLE_HEADER = new Color(0xE6D5BE);
    public static final Color TABLE_GRID = new Color(0xD9C7AE);
    public static final Color OVERDUE_TEXT = new Color(0xB5533C);

    public static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 22);
    public static final Font HEADING_FONT = new Font("SansSerif", Font.BOLD, 16);
    public static final Font BODY_FONT = new Font("SansSerif", Font.PLAIN, 14);

    // This class only holds constants and helpers, so nobody needs to create it.
    private Theme() {
    }

    // Styles a main button with the light-brown theme color.
    public static void styleButton(JButton button) {
        button.setFont(BODY_FONT);
        button.setBackground(PRIMARY);
        button.setForeground(TEXT_DARK);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
    }

    // Styles a secondary button with the lighter hover color.
    public static void styleSecondaryButton(JButton button) {
        button.setFont(BODY_FONT);
        button.setBackground(SECONDARY);
        button.setForeground(TEXT_DARK);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
    }

    // Styles a button that looks like a plain text link.
    public static void styleLinkButton(JButton button) {
        button.setFont(BODY_FONT);
        button.setForeground(ACCENT);
        button.setBackground(CARD_BACKGROUND);
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    }

    // Gives a text field the theme font, colors and some inside padding.
    public static void styleTextField(JTextField field) {
        field.setFont(BODY_FONT);
        field.setForeground(TEXT_DARK);
        field.setCaretColor(TEXT_DARK);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TABLE_GRID),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
    }

    // Gives a panel the cream card color, a thin border and inside padding.
    public static void makeCard(JPanel panel) {
        panel.setBackground(CARD_BACKGROUND);
        panel.setOpaque(true);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TABLE_GRID),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));
    }
}
