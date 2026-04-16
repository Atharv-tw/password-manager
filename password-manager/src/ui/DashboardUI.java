package ui;

import data.DataManager;
import model.Credential;
import security.PasswordGenerator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class DashboardUI extends JFrame {
    private String masterPassword;
    private DataManager dataManager;
    private List<Credential> vault;

    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    
    public DashboardUI(String masterPassword, DataManager dataManager) {
        this.masterPassword = masterPassword;
        this.dataManager = dataManager;
        this.vault = new ArrayList<>();

        setTitle("Password Manager Vault");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        loadData();
        initUI();
    }

    private void loadData() {
        try {
            vault = dataManager.loadVault(masterPassword);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to decrypt vault. Incorrect master password or corrupted file.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }
    
    private void saveData() {
        try {
            dataManager.saveVault(vault, masterPassword);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to save vault: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // --- Top Panel (Search and Generate) ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        
        JLabel searchLabel = new JLabel("Search:");
        JTextField searchField = new JTextField(20);
        
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = searchField.getText();
                if (text.trim().length() == 0) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        JButton generateBtn = new JButton("Generate Password");
        generateBtn.addActionListener(e -> openGenerateDialog());
        
        topPanel.add(searchLabel);
        topPanel.add(searchField);
        topPanel.add(Box.createHorizontalStrut(200));
        topPanel.add(generateBtn);

        // --- Table ---
        String[] columns = {"Website", "Username", "Password"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; } // Read-only
        };
        
        for (Credential cred : vault) {
            tableModel.addRow(new Object[]{cred.getWebsite(), cred.getUsername(), "*****"});
        }
        
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.getTableHeader().setReorderingAllowed(false);
        
        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        JScrollPane scrollPane = new JScrollPane(table);
        
        // --- Bottom Panel (Actions) ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        JButton addBtn = new JButton("Add New");
        JButton deleteBtn = new JButton("Delete Selected");
        JButton copyPwdBtn = new JButton("Copy Password");
        JButton copyUserBtn = new JButton("Copy Username");
        
        addBtn.addActionListener(e -> openAddDialog());
        deleteBtn.addActionListener(e -> deleteSelected());
        copyPwdBtn.addActionListener(e -> copySelected(true));
        copyUserBtn.addActionListener(e -> copySelected(false));
        
        bottomPanel.add(addBtn);
        bottomPanel.add(deleteBtn);
        bottomPanel.add(copyUserBtn);
        bottomPanel.add(copyPwdBtn);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void openAddDialog() {
        JDialog dialog = new JDialog(this, "Add Credential", true);
        dialog.setSize(350, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField siteField = new JTextField(15);
        JTextField userField = new JTextField(15);
        JPasswordField passField = new JPasswordField(15);
        
        gbc.gridx = 0; gbc.gridy = 0; dialog.add(new JLabel("Website:"), gbc);
        gbc.gridx = 1; dialog.add(siteField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; dialog.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; dialog.add(userField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; dialog.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; dialog.add(passField, gbc);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");
        
        saveBtn.addActionListener(e -> {
            String site = siteField.getText().trim();
            String user = userField.getText().trim();
            String pass = new String(passField.getPassword()).trim();
            
            if (site.isEmpty() || user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Credential cred = new Credential(site, user, pass);
            vault.add(cred);
            saveData();
            
            tableModel.addRow(new Object[]{site, user, "*****"});
            dialog.dispose();
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        dialog.add(btnPanel, gbc);
        
        dialog.setVisible(true);
    }
    
    private void deleteSelected() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete.");
            return;
        }
        
        int modelRow = table.convertRowIndexToModel(selectedRow);
        
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this credential?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            vault.remove(modelRow);
            tableModel.removeRow(modelRow);
            saveData();
        }
    }
    
    private void copySelected(boolean isPassword) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to copy.");
            return;
        }
        
        int modelRow = table.convertRowIndexToModel(selectedRow);
        Credential cred = vault.get(modelRow);
        String data = isPassword ? cred.getPassword() : cred.getUsername();
        
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(data), null);
        JOptionPane.showMessageDialog(this, (isPassword ? "Password" : "Username") + " copied to clipboard!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void openGenerateDialog() {
        JDialog dialog = new JDialog(this, "Password Generator", true);
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridLayout(4, 1, 10, 10));
        
        JPanel lengthPanel = new JPanel(new FlowLayout());
        lengthPanel.add(new JLabel("Length:"));
        JSpinner lengthSpinner = new JSpinner(new SpinnerNumberModel(12, 6, 64, 1));
        lengthPanel.add(lengthSpinner);
        
        JCheckBox symbolsBox = new JCheckBox("Include Symbols", true);
        JCheckBox numbersBox = new JCheckBox("Include Numbers", true);
        
        JPanel checksPanel = new JPanel(new FlowLayout());
        checksPanel.add(symbolsBox);
        checksPanel.add(numbersBox);
        
        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton genBtn = new JButton("Generate & Copy");
        
        genBtn.addActionListener(e -> {
            int length = (Integer) lengthSpinner.getValue();
            boolean symbols = symbolsBox.isSelected();
            boolean numbers = numbersBox.isSelected();
            
            String pwd = PasswordGenerator.generatePassword(length, symbols, numbers);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(pwd), null);
            JOptionPane.showMessageDialog(dialog, "Generated Password: " + pwd + "\nCopied to clipboard!", "Generated", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });
        
        btnPanel.add(genBtn);
        
        dialog.add(lengthPanel);
        dialog.add(checksPanel);
        dialog.add(btnPanel);
        
        dialog.setVisible(true);
    }
}
