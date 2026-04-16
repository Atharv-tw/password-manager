package ui;

import data.DataManager;
import security.HashingUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginUI extends JFrame {
    private DataManager dataManager;
    private boolean isFirstTime;

    public LoginUI() {
        dataManager = new DataManager();
        isFirstTime = dataManager.isFirstTimeUser();

        setTitle("Vault Login");
        setSize(480, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(Theme.BG_MAIN);
        
        Theme.setupTheme(); // Ensure theme applies
        initUI();
    }

    private void initUI() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(Theme.BG_MAIN);
        
        // Card Panel (White box with padding)
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Theme.BG_PANEL);
        card.setBorder(new EmptyBorder(50, 50, 50, 50));
        
        // Header Icon & Title
        JLabel iconLabel = new JLabel("🔐", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel headerLabel = new JLabel(isFirstTime ? "Welcome to Vault" : "Welcome Back!");
        headerLabel.setFont(Theme.FONT_XL_BOLD);
        headerLabel.setForeground(Theme.TEXT_PRIMARY);
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subLabel = new JLabel(isFirstTime ? "Create a master password" : "Enter master password to unlock");
        subLabel.setFont(Theme.FONT_REGULAR);
        subLabel.setForeground(Theme.TEXT_MUTED);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Input
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        Theme.styleTextField(passwordField);

        // Login Button
        JButton actionButton = Theme.createButton(isFirstTime ? "Initialize Vault" : "Unlock Vault", Theme.PRIMARY, Theme.PRIMARY_HOVER, Color.WHITE);
        actionButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        actionButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        
        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(Theme.DANGER);
        errorLabel.setFont(Theme.FONT_BOLD);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        actionButton.addActionListener(e -> {
            String pwd = new String(passwordField.getPassword());
            if (pwd.trim().isEmpty()) {
                errorLabel.setText("Password cannot be empty.");
                return;
            }
            try {
                if (isFirstTime) {
                    dataManager.saveMasterHash(HashingUtil.hashPassword(pwd));
                    openDashboard(pwd);
                } else {
                    String storedHash = dataManager.loadMasterHash();
                    if (storedHash.equals(HashingUtil.hashPassword(pwd))) {
                        openDashboard(pwd);
                    } else {
                        errorLabel.setText("Incorrect master password!");
                    }
                }
            } catch (Exception ex) {
                errorLabel.setText("Error accessing vault.");
            }
        });

        this.getRootPane().setDefaultButton(actionButton);

        card.add(iconLabel);
        card.add(Box.createVerticalStrut(15));
        card.add(headerLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(subLabel);
        card.add(Box.createVerticalStrut(40));
        card.add(passwordField);
        card.add(Box.createVerticalStrut(25));
        card.add(actionButton);
        card.add(Box.createVerticalStrut(15));
        card.add(errorLabel);

        wrapper.add(card);
        add(wrapper);
    }

    private void openDashboard(String masterPassword) {
        SwingUtilities.invokeLater(() -> {
            DashboardUI dashboard = new DashboardUI(masterPassword, dataManager);
            dashboard.setVisible(true);
        });
        this.dispose();
    }
}
