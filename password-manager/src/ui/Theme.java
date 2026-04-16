package ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Theme {
    public static final Color ACCENT = new Color(0, 122, 204); // Vibrant Blue
    public static final Color ACCENT_HOVER = new Color(28, 151, 234);
    
    public static final Color DANGER = new Color(229, 83, 83);
    public static final Color DANGER_HOVER = new Color(244, 102, 102);

    public static final Color BG_MAIN = new Color(30, 30, 30);
    public static final Color BG_PANEL = new Color(37, 37, 38);
    public static final Color BG_FIELD = new Color(60, 60, 60);

    public static final Color TEXT_PRIMARY = new Color(240, 240, 240);
    public static final Color TEXT_MUTED = new Color(150, 150, 150);

    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_L_BOLD = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_XL_BOLD = new Font("Segoe UI", Font.BOLD, 24);

    public static void setupDarkTheme() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {}

        UIManager.put("Panel.background", BG_MAIN);
        UIManager.put("OptionPane.background", BG_MAIN);
        UIManager.put("OptionPane.messageForeground", TEXT_PRIMARY);
        UIManager.put("Label.foreground", TEXT_PRIMARY);
        UIManager.put("Label.font", FONT_REGULAR);
    }

    public static void styleButton(JButton btn, Color bgColor, Color bgHover, Color fgColor) {
        btn.setFont(FONT_BOLD);
        btn.setBackground(bgColor);
        btn.setForeground(fgColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bgHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bgColor);
            }
        });
    }

    public static void styleTextField(JTextField field) {
        field.setBackground(BG_FIELD);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(TEXT_PRIMARY);
        field.setFont(FONT_REGULAR);
        Border line = new LineBorder(new Color(80, 80, 80), 1);
        Border empty = new EmptyBorder(10, 10, 10, 10);
        field.setBorder(new CompoundBorder(line, empty));
    }
}
