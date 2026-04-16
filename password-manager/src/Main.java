import ui.LoginUI;
import ui.Theme;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Apply our custom Dark Theme before creating any UI
        Theme.setupDarkTheme();
        
        SwingUtilities.invokeLater(() -> {
            LoginUI login = new LoginUI();
            login.setVisible(true);
        });
    }
}
