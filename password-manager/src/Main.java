import ui.LoginUI;
import ui.Theme;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Apply our custom Light Theme before creating any UI
        Theme.setupTheme();
        
        SwingUtilities.invokeLater(() -> {
            LoginUI login = new LoginUI();
            login.setVisible(true);
        });
    }
}
