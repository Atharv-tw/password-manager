package ui;

import data.DataManager;
import security.HashingUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginUI extends JFrame {
    private DataManager dataManager;
    private boolean isFirstTime;

    public LoginUI() {
        dataManager = new DataManager();
        isFirstTime = dataManager.isFirstTimeUser();

        setTitle("Vault Login");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(Theme.BG_MAIN);
        
        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Theme.BG_MAIN);
        mainPanel.setBorder(new EmptyBorder(40, 50, 40, 50));

        // Header Icon & Title
        JLabel iconLabel = new JLabel("🛡️", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel headerLabel = new JLabel(isFirstTime ? "Create Master Password" : "Login to Your Vault");
        headerLabel.setFont(Theme.FONT_XL_BOLD);
        headerLabel.setForeground(Theme.TEXT_PRIMARY);
        headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subLabel = new JLabel(isFirstTime ? "Secure your passwords forever." : "Enter your master password to continue.");
        subLabel.setFont(Theme.FONT_REGULAR);
        subLabel.setForeground(Theme.TEXT_MUTED);
        subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Input
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        Theme.styleTextField(passwordField);

        // Login Button
        JButton actionButton = new JButton(isFirstTime ? "Initialize Vault" : "Unlock Vault");
        actionButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        actionButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        Theme.styleButton(actionButton, Theme.ACCENT, Theme.ACCENT_HOVER, Color.WHITE);
        
        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(Theme.DANGER);
        errorLabel.setFont(Theme.FONT_REGULAR);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        actionButton.addActionListener((ActionEvent e) -> {
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
                errorLabel.setText("Error occurred accessing vault.");
            }
        });

        this.getRootPane().setDefaultButton(actionButton);

        mainPanel.add(iconLabel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(headerLabel);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(subLabel);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(passwordField);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(actionButton);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(errorLabel);

        add(mainPanel);
    }

    private void openDashboard(String masterPassword) {
        SwingUtilities.invokeLater(() -> {
            DashboardUI dashboard = new DashboardUI(masterPassword, dataManager);
            dashboard.setVisible(true);
        });
        this.dispose();
    }
}
