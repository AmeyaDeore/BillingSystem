package com.billing.gui;

import com.billing.database.UserBillStorage;
import com.billing.logic.BillCalculator;
import com.billing.logic.BillCalculator.BillDetails;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

/**
 * The main calculator window for entering customer details,
 * calculating the bill, and saving it to a file.
 * (This version fixes the totalCost variable typo)
 */
public class BillingFrame extends JFrame implements ActionListener {

    // --- GUI Components ---
    private Container container;
    private JLabel titleLabel;
    
    private JLabel nameLabel, meterLabel, addressLabel, unitsLabel, monthLabel, yearLabel;
    private JTextField nameField, meterField, addressField, unitsField, yearField;
    private JComboBox<String> monthComboBox;
    
    private JButton calculateButton, saveButton, backButton;
    
    private JTextArea billTextArea;
    private JScrollPane scrollPane;
    private JLabel lockLabel;

    private JTabbedPane tabbedPane;
    private JPanel calculatorPanel;
    private JPanel historyPanel;
    private JTable historyTable;
    private javax.swing.table.DefaultTableModel historyTableModel;
    private javax.swing.table.TableRowSorter<javax.swing.table.DefaultTableModel> historySorter;
    private JTextField searchField;
    private JComboBox<String> yearFilter;
    private String loggedInUser;

    private static final String[] MONTHS = {
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    };

    public BillingFrame(String username) {
        this.loggedInUser = username;
        // --- Frame Setup ---
        setTitle("Bill Calculator");
        setSize(850, 780);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close only this window
        setLocationRelativeTo(null); // Center the window

        container = getContentPane();
        container.setLayout(null);
        container.setBackground(new Color(245, 245, 245));

        tabbedPane = new JTabbedPane();
        tabbedPane.setBounds(0, 0, 850, 780);
        container.add(tabbedPane);

        // Calculator Tab
        calculatorPanel = new JPanel(null);
        calculatorPanel.setBackground(new Color(245, 245, 245));
        tabbedPane.addTab("Calculator", calculatorPanel);

        titleLabel = new JLabel("Customer Bill Calculator");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBounds(280, 20, 300, 30);
        calculatorPanel.add(titleLabel);

        // --- Input Form ---
        int labelX = 50;
        int fieldX = 200;
        int startY = 80;
        int rowHeight = 45;

        // Billing Period Section
        JLabel periodLabel = new JLabel("--- Billing Period ---");
        periodLabel.setFont(new Font("Arial", Font.BOLD, 14));
        periodLabel.setForeground(new Color(0, 102, 204));
        periodLabel.setBounds(labelX, startY, 300, 25);
        calculatorPanel.add(periodLabel);

        monthLabel = new JLabel("Billing Month:");
        monthLabel.setBounds(labelX, startY + 30, 140, 30);
        calculatorPanel.add(monthLabel);
        
        monthComboBox = new JComboBox<>(MONTHS);
        // Set current month as default
        monthComboBox.setSelectedIndex(Calendar.getInstance().get(Calendar.MONTH));
        monthComboBox.setBounds(fieldX, startY + 30, 200, 30);
        calculatorPanel.add(monthComboBox);

        yearLabel = new JLabel("Billing Year:");
        yearLabel.setBounds(labelX, startY + 30 + rowHeight, 140, 30);
        calculatorPanel.add(yearLabel);
        
        yearField = new JTextField(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));
        yearField.setBounds(fieldX, startY + 30 + rowHeight, 200, 30);
        calculatorPanel.add(yearField);

        // Customer Details Section
        JLabel customerLabel = new JLabel("--- Customer Details ---");
        customerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        customerLabel.setForeground(new Color(0, 102, 204));
        customerLabel.setBounds(labelX, startY + 30 + 2 * rowHeight + 10, 300, 25);
        calculatorPanel.add(customerLabel);

        nameLabel = new JLabel("Customer Name:");
        nameLabel.setBounds(labelX, startY + 30 + 2 * rowHeight + 40, 140, 30);
        calculatorPanel.add(nameLabel);
        nameField = new JTextField();
        nameField.setBounds(fieldX, startY + 30 + 2 * rowHeight + 40, 200, 30);
        calculatorPanel.add(nameField);

        meterLabel = new JLabel("Meter Number:");
        meterLabel.setBounds(labelX, startY + 30 + 3 * rowHeight + 40, 140, 30);
        calculatorPanel.add(meterLabel);
        meterField = new JTextField();
        meterField.setBounds(fieldX, startY + 30 + 3 * rowHeight + 40, 200, 30);
        calculatorPanel.add(meterField);

        addressLabel = new JLabel("Address:");
        addressLabel.setBounds(labelX, startY + 30 + 4 * rowHeight + 40, 140, 30);
        calculatorPanel.add(addressLabel);
        addressField = new JTextField();
        addressField.setBounds(fieldX, startY + 30 + 4 * rowHeight + 40, 200, 30);
        calculatorPanel.add(addressField);

        unitsLabel = new JLabel("Units Consumed:");
        unitsLabel.setBounds(labelX, startY + 30 + 5 * rowHeight + 40, 140, 30);
        calculatorPanel.add(unitsLabel);
        unitsField = new JTextField();
        unitsField.setBounds(fieldX, startY + 30 + 5 * rowHeight + 40, 200, 30);
        calculatorPanel.add(unitsField);

        // Lock message - positioned below the last input field with proper spacing
        lockLabel = new JLabel(" ", SwingConstants.LEFT);
        lockLabel.setForeground(Color.RED);
        lockLabel.setFont(new Font("Arial", Font.BOLD, 13));
        lockLabel.setBounds(50, 440, 700, 20);
        calculatorPanel.add(lockLabel);

        // --- Buttons ---
        calculateButton = new JButton("Calculate Bill");
        calculateButton.setBounds(50, 470, 150, 35);
        calculateButton.setBackground(new Color(0, 102, 204));
        calculateButton.setForeground(Color.WHITE);
        calculateButton.setFocusPainted(false);
        calculateButton.addActionListener(this);
        calculatorPanel.add(calculateButton);

        saveButton = new JButton("Save Bill to File");
        saveButton.setBounds(220, 470, 160, 35);
        saveButton.setBackground(new Color(34, 139, 34));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.addActionListener(this);
        calculatorPanel.add(saveButton);

        backButton = new JButton("Back to Dashboard");
        backButton.setBounds(400, 470, 180, 35);
        backButton.setBackground(new Color(128, 128, 128));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.addActionListener(this);
        calculatorPanel.add(backButton);

        // --- Output Area ---
        billTextArea = new JTextArea();
        billTextArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        billTextArea.setEditable(false);
        scrollPane = new JScrollPane(billTextArea);
        scrollPane.setBounds(50, 520, 750, 180);
        calculatorPanel.add(scrollPane);

        // History Tab
        historyPanel = new JPanel(null);
        historyPanel.setBackground(Color.WHITE);
        tabbedPane.addTab("My Months", historyPanel);

        JLabel historyTitle = new JLabel("Previously Calculated Periods for " + this.loggedInUser);
        historyTitle.setFont(new Font("Arial", Font.BOLD, 16));
        historyTitle.setBounds(30, 20, 600, 25);
        historyPanel.add(historyTitle);

        historyTableModel = new javax.swing.table.DefaultTableModel(
            new Object[] { "Month", "Year", "Units", "Amount (Rs.)" }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        historyTable = new JTable(historyTableModel);
        historyTable.setRowHeight(24);
        // Enable multi-select for export functionality
        historyTable.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        // Enable sorting and filtering
        historySorter = new javax.swing.table.TableRowSorter<>(historyTableModel);
        historyTable.setRowSorter(historySorter);
        // Double-click to export (single selection)
        historyTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && historyTable.getSelectedRow() >= 0) {
                    exportSelectedHistory();
                }
            }
        });
        JScrollPane historyScroll = new JScrollPane(historyTable);
        historyScroll.setBounds(30, 60, 500, 450);
        historyPanel.add(historyScroll);

        // Filters and actions
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setBounds(550, 60, 60, 30);
        historyPanel.add(searchLabel);

        searchField = new JTextField();
        searchField.setBounds(610, 60, 160, 30);
        historyPanel.add(searchField);
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { applyHistoryFilter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { applyHistoryFilter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { applyHistoryFilter(); }
        });

        JLabel yearLabel = new JLabel("Year:");
        yearLabel.setBounds(550, 100, 60, 30);
        historyPanel.add(yearLabel);

        yearFilter = new JComboBox<>(new String[] { "All" });
        yearFilter.setBounds(610, 100, 160, 30);
        historyPanel.add(yearFilter);
        yearFilter.addActionListener(evt -> applyHistoryFilter());

        JButton refreshHistoryBtn = new JButton("Refresh");
        refreshHistoryBtn.setBounds(550, 140, 220, 30);
        historyPanel.add(refreshHistoryBtn);
        refreshHistoryBtn.addActionListener(evt -> refreshHistory());

        JButton exportSelectedBtn = new JButton("Export Selected (Multi)");
        exportSelectedBtn.setBounds(550, 180, 220, 30);
        historyPanel.add(exportSelectedBtn);
        exportSelectedBtn.addActionListener(evt -> exportSelectedHistory());

        refreshHistory();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == calculateButton) {
            calculateBill();
        } else if (e.getSource() == saveButton) {
            saveBillToFile();
        } else if (e.getSource() == backButton) {
            // Just close this window, the dashboard is still open behind it
            dispose(); 
        }
    }

    private void calculateBill() {
        try {
            // 1. Get billing period
            String month = (String) monthComboBox.getSelectedItem();
            String yearStr = yearField.getText().trim();
            int year = Integer.parseInt(yearStr);
            int monthIndex = monthComboBox.getSelectedIndex();
            
            // Validate year
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            if (year < 2000 || year > currentYear) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter a valid year between 2000 and " + currentYear, 
                    "Invalid Year", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Check if this user already calculated this period
            if (UserBillStorage.hasCalculated(loggedInUser, year, monthIndex)) {
                String key = UserBillStorage.toPeriodKey(year, monthIndex);
                lockLabel.setText("Bill already calculated for period " + key + ".");
                JOptionPane.showMessageDialog(this,
                    "Bill already calculated for this month (" + key + ").",
                    "Already Calculated",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 2. Get customer details
            String name = nameField.getText().trim();
            String meter = meterField.getText().trim();
            String address = addressField.getText().trim();
            
            if (name.isEmpty() || meter.isEmpty() || address.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Please fill in all customer details", 
                    "Missing Information", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Check if meter number already exists for another user
            if (UserBillStorage.meterNumberExistsForOtherUser(meter, loggedInUser)) {
                String owner = UserBillStorage.getMeterNumberOwner(meter);
                JOptionPane.showMessageDialog(this,
                    "This meter number has already been used by user '" + owner + "'. Please enter a different meter number.",
                    "Meter Number Already Used",
                    JOptionPane.ERROR_MESSAGE);
                meterField.requestFocus();
                return;
            }
            
            int units = Integer.parseInt(unitsField.getText());
            
            if (units < 0) {
                JOptionPane.showMessageDialog(this, 
                    "Units consumed cannot be negative", 
                    "Invalid Input", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 3. Call the calculation engine
            BillCalculator.BillDetails details = BillCalculator.calculate(units);

            // 4. Build the output string
            StringBuilder billText = new StringBuilder();
            billText.append("========================================\n");
            billText.append("      ELECTRICITY BILL RECEIPT\n");
            billText.append("========================================\n\n");
            billText.append(String.format("%-20s %s %d\n", "Billing Period:", month, year));
            billText.append(String.format("%-20s %s\n", "Customer Name:", name));
            billText.append(String.format("%-20s %s\n", "Meter Number:", meter));
            billText.append(String.format("%-20s %s\n", "Address:", address));
            billText.append(String.format("%-20s %d kWh\n", "Units Consumed:", units));
            billText.append("\n----------------------------------------\n");
            billText.append("           BILL BREAKDOWN\n");
            billText.append("----------------------------------------\n");
            billText.append(String.format("%-25s Rs. %.2f\n", "Fixed Service Charge:", details.serviceCharge));
            billText.append(String.format("%-25s Rs. %.2f\n", "Slab 1 Cost (0-100):", details.slab1Cost));
            billText.append(String.format("%-25s Rs. %.2f\n", "Slab 2 Cost (101-300):", details.slab2Cost));
            billText.append(String.format("%-25s Rs. %.2f\n", "Slab 3 Cost (301-500):", details.slab3Cost));
            billText.append(String.format("%-25s Rs. %.2f\n", "Slab 4 Cost (>500):", details.slab4Cost));
            billText.append("========================================\n");
            billText.append(String.format("%-25s Rs. %.2f\n", "TOTAL AMOUNT DUE:", details.totalAmountDue));
            billText.append("========================================\n");

            // 5. Display in text area
            billTextArea.setText(billText.toString());

            // 6. Mark this period as calculated for the user with details and update history
            UserBillStorage.addCalculated(loggedInUser, year, monthIndex, units, details.totalAmountDue, meter);
            lockLabel.setText("Marked period " + UserBillStorage.toPeriodKey(year, monthIndex) + " as calculated.");
            refreshHistory();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Please enter valid numbers for units and year.", 
                "Input Error", 
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "An error occurred: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void saveBillToFile() {
        String billText = billTextArea.getText();
        if (billText.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please calculate the bill first.", 
                "Warning", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Bill As...");
        
        String month = (String) monthComboBox.getSelectedItem();
        String year = yearField.getText().trim();
        String meter = meterField.getText().trim();
        String suggestedName = meter + "_" + month + "_" + year + "_bill.txt";
        fileChooser.setSelectedFile(new File(suggestedName));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            
            try (FileWriter fw = new FileWriter(fileToSave)) {
                fw.write(billText);
                JOptionPane.showMessageDialog(this, 
                    "Bill saved successfully to:\n" + fileToSave.getAbsolutePath(), 
                    "Save Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error saving file: " + ex.getMessage(), 
                    "Save Error", 
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void refreshHistory() {
        if (historyTableModel == null) return;
        historyTableModel.setRowCount(0);
        java.util.List<UserBillStorage.BillRecord> list = UserBillStorage.getCalculatedRecords(loggedInUser);
        // Sort newest first by periodKey (YYYY-MM) descending
        list.sort((a, b) -> b.periodKey.compareTo(a.periodKey));
        java.util.Set<String> years = new java.util.TreeSet<>((a, b) -> b.compareTo(a));
        for (UserBillStorage.BillRecord r : list) {
            // r.periodKey = YYYY-MM
            String[] parts = r.periodKey.split("-");
            String year = parts.length > 0 ? parts[0] : "";
            if (!year.isEmpty()) years.add(year);
            int monthIndex = 0;
            try { monthIndex = Integer.parseInt(parts[1]) - 1; } catch (Exception ignored) {}
            String monthName = MONTHS[Math.max(0, Math.min(11, monthIndex))];
            String amount = r.amount >= 0 ? String.format("%.2f", r.amount) : "-";
            Object[] row = new Object[] { monthName, year, (r.units >= 0 ? r.units : "-"), amount };
            historyTableModel.addRow(row);
        }
        updateYearFilterOptions(years);
        applyHistoryFilter();
    }

    private void exportSelectedHistory() {
        int[] selectedRows = historyTable.getSelectedRows();
        if (selectedRows.length == 0) {
            JOptionPane.showMessageDialog(this,
                "Please select one or more rows to export.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Convert view indices to model indices
        int[] modelRows = new int[selectedRows.length];
        for (int i = 0; i < selectedRows.length; i++) {
            modelRows[i] = historyTable.convertRowIndexToModel(selectedRows[i]);
        }

        StringBuilder content = new StringBuilder();
        content.append("========================================\n");
        content.append("    ELECTRICITY BILL EXPORT SUMMARY\n");
        content.append("========================================\n\n");
        content.append("User: ").append(loggedInUser).append("\n");
        content.append("Export Date: ").append(java.time.LocalDate.now()).append("\n");
        content.append("Total Records: ").append(selectedRows.length).append("\n\n");

        // Sort by year and month for organized output
        java.util.List<java.util.Map<String, String>> records = new java.util.ArrayList<>();
        for (int modelRow : modelRows) {
            java.util.Map<String, String> record = new java.util.HashMap<>();
            record.put("month", String.valueOf(historyTableModel.getValueAt(modelRow, 0)));
            record.put("year", String.valueOf(historyTableModel.getValueAt(modelRow, 1)));
            record.put("units", String.valueOf(historyTableModel.getValueAt(modelRow, 2)));
            record.put("amount", String.valueOf(historyTableModel.getValueAt(modelRow, 3)));
            records.add(record);
        }

        // Sort by year (descending) then by month
        records.sort((a, b) -> {
            int yearCompare = b.get("year").compareTo(a.get("year"));
            if (yearCompare != 0) return yearCompare;
            return getMonthIndex(b.get("month")) - getMonthIndex(a.get("month"));
        });

        // Generate organized content
        double totalAmount = 0.0;
        int totalUnits = 0;
        
        for (int i = 0; i < records.size(); i++) {
            java.util.Map<String, String> record = records.get(i);
            content.append("----------------------------------------\n");
            content.append("RECORD ").append(i + 1).append(" of ").append(records.size()).append("\n");
            content.append("----------------------------------------\n");
            content.append("Period: ").append(record.get("month")).append(" ").append(record.get("year")).append("\n");
            content.append("Units Consumed: ").append(record.get("units")).append(" kWh\n");
            content.append("Bill Amount: Rs. ").append(record.get("amount")).append("\n");
            
            // Add to totals
            try {
                totalAmount += Double.parseDouble(record.get("amount"));
                totalUnits += Integer.parseInt(record.get("units"));
            } catch (NumberFormatException ignored) {}
            
            content.append("\n");
        }

        // Add summary
        content.append("========================================\n");
        content.append("           EXPORT SUMMARY\n");
        content.append("========================================\n");
        content.append("Total Records Exported: ").append(records.size()).append("\n");
        content.append("Total Units Consumed: ").append(totalUnits).append(" kWh\n");
        content.append("Total Amount: Rs. ").append(String.format("%.2f", totalAmount)).append("\n");
        content.append("Average per Record: Rs. ").append(String.format("%.2f", records.size() > 0 ? totalAmount / records.size() : 0)).append("\n");
        content.append("========================================\n");

        // File chooser with better naming
        JFileChooser chooser = new JFileChooser();
        String suggested = loggedInUser + "_bills_export_" + java.time.LocalDate.now() + ".txt";
        chooser.setSelectedFile(new File(suggested));
        int choice = chooser.showSaveDialog(this);
        if (choice == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (FileWriter fw = new FileWriter(file)) {
                fw.write(content.toString());
                JOptionPane.showMessageDialog(this,
                    "Successfully exported " + selectedRows.length + " record(s) to:\n" + file.getAbsolutePath(),
                    "Export Successful",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                    "Error exporting: " + ex.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private int getMonthIndex(String monthName) {
        for (int i = 0; i < MONTHS.length; i++) {
            if (MONTHS[i].equals(monthName)) {
                return i;
            }
        }
        return 0;
    }

    private void updateYearFilterOptions(java.util.Set<String> years) {
        if (yearFilter == null) return;
        String selected = (String) yearFilter.getSelectedItem();
        yearFilter.removeAllItems();
        yearFilter.addItem("All");
        for (String y : years) yearFilter.addItem(y);
        if (selected != null) yearFilter.setSelectedItem(selected);
    }

    private void applyHistoryFilter() {
        if (historySorter == null) return;
        String text = searchField != null ? searchField.getText().trim().toLowerCase() : "";
        String year = yearFilter != null ? (String) yearFilter.getSelectedItem() : "All";
        java.util.List<RowFilter<javax.swing.table.DefaultTableModel,Object>> filters = new java.util.ArrayList<>();
        if (text != null && !text.isEmpty()) {
            RowFilter<javax.swing.table.DefaultTableModel,Object> textFilter = RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text));
            filters.add(textFilter);
        }
        if (year != null && !"All".equals(year)) {
            RowFilter<javax.swing.table.DefaultTableModel,Object> yFilter = RowFilter.regexFilter("^" + java.util.regex.Pattern.quote(year) + "$", 1);
            filters.add(yFilter);
        }
        RowFilter<javax.swing.table.DefaultTableModel,Object> combined = filters.isEmpty() ? null : RowFilter.andFilter(filters);
        historySorter.setRowFilter(combined);
    }

    
}
