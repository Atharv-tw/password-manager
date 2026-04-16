package ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class Theme {
    // Modern Light Colors (Inspired by modern web UI / Tailwind)
    public static final Color BG_MAIN = new Color(248, 250, 252);     // Slate 50 (App Background)
    public static final Color BG_PANEL = new Color(255, 255, 255);    // White (Cards & Panels)
    
    public static final Color PRIMARY = new Color(99, 102, 241);      // Indigo 500
    public static final Color PRIMARY_HOVER = new Color(129, 140, 248); // Indigo 400

    public static final Color SUCCESS = new Color(16, 185, 129);      // Emerald 500
    public static final Color SUCCESS_HOVER = new Color(52, 211, 153); // Emerald 400

    public static final Color DANGER = new Color(239, 68, 68);        // Red 500
    public static final Color DANGER_HOVER = new Color(248, 113, 113); // Red 400

    public static final Color SECONDARY = new Color(226, 232, 240);   // Slate 200
    public static final Color SECONDARY_HOVER = new Color(203, 213, 225); // Slate 300
    public static final Color SECONDARY_TEXT = new Color(71, 85, 105);

    public static final Color TEXT_PRIMARY = new Color(15, 23, 42);   // Slate 900
    public static final Color TEXT_MUTED = new Color(100, 116, 139);  // Slate 500
    public static final Color BORDER_COLOR = new Color(203, 213, 225);// Slate 300

    // Typography
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 15);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_XL_BOLD = new Font("Segoe UI", Font.BOLD, 32);
    public static final Font FONT_L_BOLD = new Font("Segoe UI", Font.BOLD, 20);

    public static void setupTheme() {
        try {
            // Drop Nimbus, use Native System which respects flat drawings better
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}
        
        UIManager.put("Panel.background", BG_MAIN);
        UIManager.put("Label.foreground", TEXT_PRIMARY);
        UIManager.put("Label.font", FONT_REGULAR);
        UIManager.put("OptionPane.background", BG_MAIN);
        UIManager.put("OptionPane.messageForeground", TEXT_PRIMARY);
    }

    public static JButton createButton(String text, Color base, Color hover, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(base.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(hover);
                } else {
                    g2.setColor(base);
                }
                // Draw rounded rectangle background
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
                super.paintComponent(g); 
            }
        };
        btn.setFont(FONT_BOLD);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(150, 45));
        return btn;
    }

    public static void styleTextField(JTextField field) {
        field.setBackground(BG_PANEL);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(TEXT_PRIMARY);
        field.setFont(FONT_REGULAR);
        
        Border line = new LineBorder(BORDER_COLOR, 1, true);
        Border empty = new EmptyBorder(12, 15, 12, 15);
        field.setBorder(new CompoundBorder(line, empty));
    }
}
