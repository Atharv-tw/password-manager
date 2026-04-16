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

        setTitle("Vault Dashboard");
        setSize(950, 650);
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
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        
        // --- Header Panel ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Theme.BG_MAIN);
        topPanel.setBorder(new EmptyBorder(0, 0, 25, 0));
        
        JLabel titleLabel = new JLabel("Passwords Vault");
        titleLabel.setFont(Theme.FONT_XL_BOLD);
        titleLabel.setForeground(Theme.TEXT_PRIMARY);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        searchPanel.setBackground(Theme.BG_MAIN);
        
        JTextField searchField = new JTextField(25);
        Theme.styleTextField(searchField);
        searchField.setText("Search...");
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Search...")) searchField.setText("");
            }
        });
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String text = searchField.getText();
                if (text.trim().isEmpty() || text.equals("Search...")) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        JButton generateBtn = Theme.createButton("⚙️ Generator", Theme.SUCCESS, Theme.SUCCESS_HOVER, Color.WHITE);
        generateBtn.addActionListener(e -> openGenerateDialog());
        
        searchPanel.add(searchField);
        searchPanel.add(generateBtn);
        
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(searchPanel, BorderLayout.EAST);

        // --- Table Styling ---
        String[] columns = {"Platform / Website", "Username", "Password"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        for (Credential cred : vault) {
            tableModel.addRow(new Object[]{cred.getWebsite(), cred.getUsername(), "••••••••••••"});
        }
        
        table = new JTable(tableModel);
        table.setRowHeight(50); // Spacious tall rows
        table.setFont(Theme.FONT_REGULAR);
        table.setBackground(Theme.BG_PANEL);
        table.setForeground(Theme.TEXT_PRIMARY);
        table.setSelectionBackground(new Color(238, 242, 255)); // Indigo 50 Light selection
        table.setSelectionForeground(Theme.PRIMARY);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        
        // Custom Table Header
        JTableHeader header = table.getTableHeader();
        header.setFont(Theme.FONT_L_BOLD);
        header.setBackground(Theme.BG_PANEL);
        header.setForeground(Theme.TEXT_MUTED);
        header.setPreferredSize(new Dimension(100, 50));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Theme.BORDER_COLOR));
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);
        
        // Padding for cells & subtle bottom border
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.SECONDARY), 
                    new EmptyBorder(0, 20, 0, 20)
                ));
                return comp;
            }
        };
        table.setDefaultRenderer(Object.class, cellRenderer);
        
        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.BORDER_COLOR, 1, true));
        scrollPane.getViewport().setBackground(Theme.BG_PANEL);
        
        // --- Bottom Actions Panel ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        bottomPanel.setBackground(Theme.BG_MAIN);
        bottomPanel.setBorder(new EmptyBorder(25, 0, 0, 0));
        
        JButton copyUserBtn = Theme.createButton("📄 Copy User", Theme.SECONDARY, Theme.SECONDARY_HOVER, Theme.SECONDARY_TEXT);
        JButton copyPwdBtn = Theme.createButton("🔑 Copy Pass", Theme.SECONDARY, Theme.SECONDARY_HOVER, Theme.SECONDARY_TEXT);
        JButton deleteBtn = Theme.createButton("🗑️ Delete", Theme.DANGER, Theme.DANGER_HOVER, Color.WHITE);
        JButton addBtn = Theme.createButton("➕ Add Vault", Theme.PRIMARY, Theme.PRIMARY_HOVER, Color.WHITE);
        
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
        dialog.setSize(420, 420);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(Theme.BG_PANEL);
        
        JPanel p = new JPanel(new GridLayout(6, 1, 10, 5));
        p.setBackground(Theme.BG_PANEL);
        p.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        JLabel l1 = new JLabel("App / Website Name"); l1.setFont(Theme.FONT_BOLD);
        JTextField siteField = new JTextField(); Theme.styleTextField(siteField);
        
        JLabel l2 = new JLabel("Username / Email"); l2.setFont(Theme.FONT_BOLD);
        JTextField userField = new JTextField(); Theme.styleTextField(userField);
        
        JLabel l3 = new JLabel("Password"); l3.setFont(Theme.FONT_BOLD);
        JPasswordField passField = new JPasswordField(); Theme.styleTextField(passField);
        
        p.add(l1); p.add(siteField);
        p.add(l2); p.add(userField);
        p.add(l3); p.add(passField);
        
        JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bp.setBackground(Theme.BG_PANEL);
        bp.setBorder(new EmptyBorder(0, 30, 20, 30));
        
        JButton cancelBtn = Theme.createButton("Cancel", Theme.SECONDARY, Theme.SECONDARY_HOVER, Theme.SECONDARY_TEXT);
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        JButton saveBtn = Theme.createButton("Save Password", Theme.PRIMARY, Theme.PRIMARY_HOVER, Color.WHITE);
        saveBtn.addActionListener(e -> {
            String s = siteField.getText().trim(), u = userField.getText().trim(), pw = new String(passField.getPassword()).trim();
            if (s.isEmpty()||u.isEmpty()||pw.isEmpty()) return;
            vault.add(new Credential(s, u, pw));
            saveData();
            tableModel.addRow(new Object[]{s, u, "••••••••••••"});
            dialog.dispose();
        });
        
        bp.add(cancelBtn);
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
        dialog.setSize(350, 250);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(Theme.BG_PANEL);
        
        JPanel p = new JPanel(new GridLayout(3, 1, 10, 10));
        p.setBackground(Theme.BG_PANEL);
        p.setBorder(new EmptyBorder(20, 25, 20, 25));
        
        JSpinner lengthSpinner = new JSpinner(new SpinnerNumberModel(16, 8, 64, 1));
        lengthSpinner.setFont(Theme.FONT_REGULAR);
        
        JCheckBox sym = new JCheckBox("Symbols"); sym.setBackground(Theme.BG_PANEL); sym.setFont(Theme.FONT_REGULAR);
        sym.setSelected(true);
        
        JCheckBox num = new JCheckBox("Numbers"); num.setBackground(Theme.BG_PANEL); num.setFont(Theme.FONT_REGULAR);
        num.setSelected(true);
        
        JPanel opts = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0)); opts.setBackground(Theme.BG_PANEL);
        JLabel lenLbl = new JLabel("Password Length:"); lenLbl.setFont(Theme.FONT_BOLD);
        opts.add(lenLbl); opts.add(lengthSpinner); 
        
        JPanel checks = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0)); checks.setBackground(Theme.BG_PANEL);
        checks.add(sym); checks.add(num);
        
        JButton gen = Theme.createButton("Generate & Copy", Theme.SUCCESS, Theme.SUCCESS_HOVER, Color.WHITE);
        gen.addActionListener(e -> {
            String pw = PasswordGenerator.generatePassword((Integer)lengthSpinner.getValue(), sym.isSelected(), num.isSelected());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(pw), null);
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "A strong password has been copied to your clipboard!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });
        
        p.add(opts);
        p.add(checks);
        p.add(gen);
        
        dialog.add(p);
        dialog.setVisible(true);
    }
}
