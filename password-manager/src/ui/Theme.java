package ui;

import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Font;

public class Theme {
    public static void setupDarkTheme() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Keep default look and feel
        }
        
        Color bg = new Color(30,30,30);
        Color fg = new Color(230,230,230);
        Color accent = new Color(70, 130, 180);

        UIManager.put("nimbusBase", bg);
        UIManager.put("nimbusBlueGrey", new Color(45,45,45));
        UIManager.put("control", bg);
        UIManager.put("nimbusLightBackground", new Color(40,40,40));
        UIManager.put("text", fg);
        UIManager.put("nimbusSelectionBackground", accent);
        UIManager.put("nimbusSelectedText", Color.WHITE);
        UIManager.put("nimbusFocus", accent);
        
        Font font = new Font("Segoe UI", Font.PLAIN, 14);
        UIManager.put("defaultFont", font);
        
        UIManager.put("Button.background", new Color(60,60,60));
        UIManager.put("Button.foreground", fg);
        UIManager.put("Panel.background", bg);
        UIManager.put("Label.foreground", fg);
        UIManager.put("Table.background", new Color(40,40,40));
        UIManager.put("Table.foreground", fg);
        UIManager.put("TableHeader.background", new Color(50,50,50));
        UIManager.put("TableHeader.foreground", fg);
        UIManager.put("ScrollPane.background", bg);
    }
}
