package ui;

import data.DataManager;
import model.Credential;
import security.PasswordGenerator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
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

        setTitle("Vault Dashboard - Password Manager");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BG_MAIN);
        
        loadData();
        initUI();
    }

    private void loadData() {
        try {
            vault = dataManager.loadVault(masterPassword);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load vault.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }
    
    private void saveData() {
        try {
            dataManager.saveVault(vault, masterPassword);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to save vault.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(Theme.BG_MAIN);
        mainPanel.setBorder(new EmptyBorder(25, 30, 25, 30));
        
        // --- Header Panel ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Theme.BG_MAIN);
        topPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("My Passwords");
        titleLabel.setFont(Theme.FONT_XL_BOLD);
        titleLabel.setForeground(Theme.TEXT_PRIMARY);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setBackground(Theme.BG_MAIN);
        
        JTextField searchField = new JTextField(20);
        Theme.styleTextField(searchField);
        searchField.setText("Search credentials...");
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Search credentials...")) {
                    searchField.setText("");
                }
            }
        });
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = searchField.getText();
                if (text.trim().isEmpty() || text.equals("Search credentials...")) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        JButton generateBtn = new JButton("🎲 Generate Password");
        Theme.styleButton(generateBtn, Theme.BG_PANEL, Theme.BG_FIELD, Theme.TEXT_PRIMARY);
        generateBtn.addActionListener(e -> openGenerateDialog());
        
        searchPanel.add(searchField);
        searchPanel.add(generateBtn);
        
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.EAST);

        // --- Table Styling ---
        String[] columns = {"Website / App", "Username", "Password"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        for (Credential cred : vault) {
            tableModel.addRow(new Object[]{cred.getWebsite(), cred.getUsername(), "••••••••••••"});
        }
        
        table = new JTable(tableModel);
        table.setRowHeight(45);
        table.setFont(Theme.FONT_REGULAR);
        table.setBackground(Theme.BG_PANEL);
        table.setForeground(Theme.TEXT_PRIMARY);
        table.setSelectionBackground(Theme.ACCENT);
        table.setSelectionForeground(Color.WHITE);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        
        // Custom Table Header
        JTableHeader header = table.getTableHeader();
        header.setFont(Theme.FONT_L_BOLD);
        header.setBackground(Theme.BG_FIELD);
        header.setForeground(Theme.TEXT_PRIMARY);
        header.setPreferredSize(new Dimension(100, 45));
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        // Padding for rows
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setBorder(new EmptyBorder(0, 15, 0, 15));
        table.setDefaultRenderer(Object.class, cellRenderer);
        
        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.BG_FIELD, 1));
        scrollPane.getViewport().setBackground(Theme.BG_MAIN);
        
        // --- Bottom Actions Panel ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        bottomPanel.setBackground(Theme.BG_MAIN);
        bottomPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        JButton copyUserBtn = new JButton("Copy Username");
        JButton copyPwdBtn = new JButton("Copy Password");
        JButton deleteBtn = new JButton("Delete");
        JButton addBtn = new JButton("➕ Add New");
        
        Theme.styleButton(copyUserBtn, Theme.BG_PANEL, Theme.BG_FIELD, Theme.TEXT_PRIMARY);
        Theme.styleButton(copyPwdBtn, Theme.BG_PANEL, Theme.BG_FIELD, Theme.TEXT_PRIMARY);
        Theme.styleButton(deleteBtn, Theme.DANGER, Theme.DANGER_HOVER, Color.WHITE);
        Theme.styleButton(addBtn, Theme.ACCENT, Theme.ACCENT_HOVER, Color.WHITE);
        
        addBtn.addActionListener(e -> openAddDialog());
        deleteBtn.addActionListener(e -> deleteSelected());
        copyPwdBtn.addActionListener(e -> copySelected(true));
        copyUserBtn.addActionListener(e -> copySelected(false));
        
        bottomPanel.add(copyUserBtn);
        bottomPanel.add(copyPwdBtn);
        bottomPanel.add(deleteBtn);
        bottomPanel.add(addBtn);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void openAddDialog() {
        JDialog dialog = new JDialog(this, "Add New Credential", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(Theme.BG_MAIN);
        
        JPanel p = new JPanel(new GridLayout(6, 1, 10, 10));
        p.setBackground(Theme.BG_MAIN);
        p.setBorder(new EmptyBorder(20, 25, 20, 25));
        
        JLabel l1 = new JLabel("Website / App Name"); l1.setForeground(Theme.TEXT_PRIMARY); l1.setFont(Theme.FONT_BOLD);
        JTextField siteField = new JTextField(); Theme.styleTextField(siteField);
        
        JLabel l2 = new JLabel("Username / Email"); l2.setForeground(Theme.TEXT_PRIMARY); l2.setFont(Theme.FONT_BOLD);
        JTextField userField = new JTextField(); Theme.styleTextField(userField);
        
        JLabel l3 = new JLabel("Password"); l3.setForeground(Theme.TEXT_PRIMARY); l3.setFont(Theme.FONT_BOLD);
        JPasswordField passField = new JPasswordField(); Theme.styleTextField(passField);
        
        p.add(l1); p.add(siteField);
        p.add(l2); p.add(userField);
        p.add(l3); p.add(passField);
        
        JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bp.setBackground(Theme.BG_MAIN);
        JButton saveBtn = new JButton("Save Password");
        Theme.styleButton(saveBtn, Theme.ACCENT, Theme.ACCENT_HOVER, Color.WHITE);
        
        saveBtn.addActionListener(e -> {
            String s = siteField.getText().trim(), u = userField.getText().trim(), pw = new String(passField.getPassword()).trim();
            if (s.isEmpty()||u.isEmpty()||pw.isEmpty()) return;
            vault.add(new Credential(s, u, pw));
            saveData();
            tableModel.addRow(new Object[]{s, u, "••••••••••••"});
            dialog.dispose();
        });
        bp.add(saveBtn);
        
        dialog.add(p, BorderLayout.CENTER);
        dialog.add(bp, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void deleteSelected() {
        int r = table.getSelectedRow();
        if (r == -1) return;
        int mRow = table.convertRowIndexToModel(r);
        vault.remove(mRow);
        tableModel.removeRow(mRow);
        saveData();
    }
    
    private void copySelected(boolean isPass) {
        int r = table.getSelectedRow();
        if (r == -1) return;
        Credential c = vault.get(table.convertRowIndexToModel(r));
        String txt = isPass ? c.getPassword() : c.getUsername();
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(txt), null);
    }
    
    private void openGenerateDialog() {
        JDialog dialog = new JDialog(this, "Generator", true);
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(Theme.BG_MAIN);
        
        JPanel p = new JPanel(new GridLayout(3, 1, 10, 10));
        p.setBackground(Theme.BG_MAIN);
        p.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JSpinner lengthSpinner = new JSpinner(new SpinnerNumberModel(16, 8, 64, 1));
        JCheckBox sym = new JCheckBox("Symbols"); sym.setBackground(Theme.BG_MAIN); sym.setForeground(Theme.TEXT_PRIMARY);
        sym.setSelected(true);
        
        JCheckBox num = new JCheckBox("Numbers"); num.setBackground(Theme.BG_MAIN); num.setForeground(Theme.TEXT_PRIMARY);
        num.setSelected(true);
        
        JPanel opts = new JPanel(new FlowLayout()); opts.setBackground(Theme.BG_MAIN);
        opts.add(new JLabel("Length:")); opts.add(lengthSpinner); opts.add(sym); opts.add(num);
        
        JButton gen = new JButton("Generate & Copy");
        Theme.styleButton(gen, Theme.ACCENT, Theme.ACCENT_HOVER, Color.WHITE);
        
        gen.addActionListener(e -> {
            String pw = PasswordGenerator.generatePassword((Integer)lengthSpinner.getValue(), sym.isSelected(), num.isSelected());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(pw), null);
            dialog.dispose();
        });
        
        p.add(opts);
        p.add(gen);
        
        dialog.add(p);
        dialog.setVisible(true);
    }
}
