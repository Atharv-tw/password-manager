package ui;

import data.DataManager;
import security.HashingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginUI extends JFrame {
    private DataManager dataManager;
    private boolean isFirstTime;

    public LoginUI() {
        dataManager = new DataManager();
        isFirstTime = dataManager.isFirstTimeUser();

        setTitle(isFirstTime ? "Welcome - Setup Master Password" : "Login - Password Manager");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel headerLabel = new JLabel(isFirstTime ? "Create Master Password" : "Enter Master Password");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(headerLabel, gbc);

        JPasswordField passwordField = new JPasswordField(20);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        panel.add(passwordField, gbc);

        JButton actionButton = new JButton(isFirstTime ? "Setup & Enter" : "Unlock Vault");
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        panel.add(actionButton, gbc);
        
        JLabel errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panel.add(errorLabel, gbc);

        actionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
                    errorLabel.setText("Error: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        // Trigger action on enter key
        this.getRootPane().setDefaultButton(actionButton);
        add(panel);
    }

    private void openDashboard(String masterPassword) {
        SwingUtilities.invokeLater(() -> {
            DashboardUI dashboard = new DashboardUI(masterPassword, dataManager);
            dashboard.setVisible(true);
        });
        this.dispose();
    }
}
