package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import Model.Supplier;
import Model.Status;
import Controller.SupplierController;

public class SupplierPanel extends JPanel {
    private JTable supplierTable;
    private DefaultTableModel tableModel;
    private SupplierController controller;
    private JTextField searchField;

    public SupplierPanel() {
        initializePanel();
        this.controller = new SupplierController();
        loadSupplierData();
    }


    private void addSampleData() {
        // sample data
        tableModel.addRow(new Object[]{1, "MedExpress Pharma", "123 Business Ave, Manila", "09171234567", "Pharmaceutical", 3, "Regular supplier of antibiotics", "Operational"});
        tableModel.addRow(new Object[]{2, "Global Medical Supplies", "456 Commerce St, Quezon City", "09179876543", "Medical Equipment", 5, "Bulk orders available", "Operational"});
        tableModel.addRow(new Object[]{3, "HealthFirst Distributors", "789 Trade Road, Makati", "09171122334", "General Medical", 2, "Fast delivery guaranteed", "Closed"});
    }

    private void initializePanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Medicine Supplier Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add Supplier");
        JButton editButton = new JButton("Edit Supplier");
        JButton viewButton = new JButton("View");
        JButton deleteButton = new JButton("Delete Supplier");
        JButton refreshButton = new JButton("Refresh");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        // search
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchLabel = new JLabel("Search by Name:");
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        JButton clearButton = new JButton("Clear");

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(clearButton);

        // table
        String[] columns = {"Supplier ID", "Supplier Name", "Address", "Contact Details", "Supplier Type", "Delivery Lead Time (days)", "Transaction Details", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        supplierTable = new JTable(tableModel);

        setColumnWidths();
        Font largerFont = new Font("Arial", Font.PLAIN, 14);
        supplierTable.setRowHeight(25);
        supplierTable.setFont(largerFont);
        supplierTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        supplierTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        supplierTable.setEnabled(false);

        JScrollPane scrollPane = new JScrollPane(supplierTable);
        scrollPane.setPreferredSize(new Dimension(1300, 400));

        addButton.addActionListener(e -> showAddSupplierDialog());
        viewButton.addActionListener(e -> {
            showLoading(true);
            controller.listSuppliers(res -> {
                showLoading(false);
                if (res.isSuccess()) {
                    List<Model.Supplier> suppliers = res.getData();
                    if (suppliers == null || suppliers.isEmpty()) {
                        showInfo("No supplier data returned from database.");
                        return;
                    }
                    List<Map<String, Object>> rows = new ArrayList<>();
                    for (Model.Supplier s : suppliers) {
                        Map<String, Object> m = new HashMap<>();
                        m.put("Supplier ID", s.getSupplierID());
                        m.put("Supplier Name", s.getSupplierName());
                        m.put("Address", s.getAddress());
                        m.put("Contact Details", s.getContactDetails());
                        m.put("Supplier Type", s.getSupplierType());
                        m.put("Delivery Lead Time (days)", s.getDeliveryLeadTime());
                        m.put("Transaction Details", s.getTransactionDetails());
                        m.put("Status", s.getSupplierStatus() != null ? s.getSupplierStatus().getLabel() : "");
                        rows.add(m);
                    }
                    View.ViewDialog.showView(this, "Suppliers", rows);
                } else {
                    showError(res.getError());
                }
            }, ex -> {
                showLoading(false);
                showError(ex.getMessage());
            });
        });
        refreshButton.addActionListener(e -> loadSupplierData());
        deleteButton.addActionListener(e -> deleteSelectedSupplier());
        editButton.addActionListener(e -> editSelectedSupplier());
        searchButton.addActionListener(e -> performSearch());
        clearButton.addActionListener(e -> clearSearch());

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(headerPanel, BorderLayout.NORTH);
        northPanel.add(searchPanel, BorderLayout.SOUTH);

        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void setColumnWidths() {
        TableColumn column;
        supplierTable.getTableHeader().setReorderingAllowed(false);

        // supplier id
        column = supplierTable.getColumnModel().getColumn(0);
        column.setPreferredWidth(80);
        column.setResizable(false);

        // name
        column = supplierTable.getColumnModel().getColumn(1);
        column.setPreferredWidth(150);
        column.setResizable(false);

        // address
        column = supplierTable.getColumnModel().getColumn(2);
        column.setPreferredWidth(200);
        column.setResizable(false);

        // contact
        column = supplierTable.getColumnModel().getColumn(3);
        column.setPreferredWidth(120);
        column.setResizable(false);

        // supplier type
        column = supplierTable.getColumnModel().getColumn(4);
        column.setPreferredWidth(120);
        column.setResizable(false);

        // delivery time
        column = supplierTable.getColumnModel().getColumn(5);
        column.setPreferredWidth(120);
        column.setResizable(false);

        // transaction
        column = supplierTable.getColumnModel().getColumn(6);
        column.setPreferredWidth(200);
        column.setResizable(false);

        // status
        column = supplierTable.getColumnModel().getColumn(7);
        column.setPreferredWidth(100);
        column.setResizable(false);
    }

    private void loadSupplierData() {
        showLoading(true);
        controller.listSuppliers(res -> {
            showLoading(false);
            if (res.isSuccess()) {
                List<Supplier> suppliers = res.getData();
                if (suppliers == null || suppliers.isEmpty()) {
                    showInfo("No supplier data returned from database.");
                    tableModel.setRowCount(0);
                    return;
                }
                showSuppliers(suppliers);
            } else {
                showError(res.getError());
            }
        }, ex -> {
            showLoading(false);
            showError(ex.getMessage());
        });
    }

    private void showAddSupplierDialog() {
        JTextField nameField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField contactField = new JTextField();
        JTextField typeField = new JTextField();
        JTextField leadTimeField = new JTextField("0");
        JTextField transactionField = new JTextField();
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Operational", "Closed"});

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.setPreferredSize(new Dimension(500, 300));
        
        panel.add(new JLabel("Supplier Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(new JLabel("Contact Details:"));
        panel.add(contactField);
        panel.add(new JLabel("Supplier Type:"));
        panel.add(typeField);
        panel.add(new JLabel("Delivery Lead Time (days):"));
        panel.add(leadTimeField);
        panel.add(new JLabel("Transaction Details:"));
        panel.add(transactionField);
        panel.add(new JLabel("Status:"));
        panel.add(statusCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Supplier",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            // Validate inputs
            if (nameField.getText().trim().isEmpty() ||
                addressField.getText().trim().isEmpty() ||
                contactField.getText().trim().isEmpty() ||
                typeField.getText().trim().isEmpty()) {

                showError("Please fill in all required fields");
                return;
            }

            try {
                int leadTime = Integer.parseInt(leadTimeField.getText().trim());

                Supplier supplier = new Supplier(
                    -1,
                    nameField.getText().trim(),
                    addressField.getText().trim(),
                    contactField.getText().trim(),
                    typeField.getText().trim(),
                    leadTime,
                    transactionField.getText().trim(),
                    new Status(-1, 0, (String) statusCombo.getSelectedItem())
                );

                controller.createSupplier(supplier, () -> {
                    loadSupplierData();
                    showInfo("Supplier added successfully!");
                }, ex -> showError(ex.getMessage()));
            } catch (NumberFormatException e) {
                showError("Please enter a valid number for delivery lead time");
            }
        }
    }

    private void deleteSelectedSupplier() {
        int selectedRow = supplierTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a supplier to delete");
            return;
        }
        
        int supplierId = (int) tableModel.getValueAt(selectedRow, 0);
        String supplierName = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to close supplier:\n" +
            "ID: " + supplierId + " - " + supplierName + "?\n\n" +
            "Note: This will set the supplier status to 'Closed' but preserve all data.",
            "Confirm Closure", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            controller.deleteSupplier(supplierId, () -> {
                loadSupplierData();
                showInfo("Supplier closed successfully");
            }, ex -> showError(ex.getMessage()));
        }
    }

    private void editSelectedSupplier() {
        int selectedRow = supplierTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a supplier to edit");
            return;
        }
        
        int supplierId = (int) tableModel.getValueAt(selectedRow, 0);
        showLoading(true);
        controller.getSupplierById(supplierId, res -> {
            showLoading(false);
            if (res.isSuccess()) {
                Supplier supplier = res.getData();
                if (supplier == null) {
                    showError("Supplier not found in database");
                    return;
                }
                showEditSupplierDialog(supplier);
            } else {
                showError(res.getError());
            }
        }, ex -> {
            showLoading(false);
            showError(ex.getMessage());
        });
    }

    private void showEditSupplierDialog(Supplier supplier) {
        JTextField nameField = new JTextField(supplier.getSupplierName());
        JTextField addressField = new JTextField(supplier.getAddress());
        JTextField contactField = new JTextField(supplier.getContactDetails());
        JTextField typeField = new JTextField(supplier.getSupplierType());
        JTextField leadTimeField = new JTextField(String.valueOf(supplier.getDeliveryLeadTime()));
        JTextField transactionField = new JTextField(supplier.getTransactionDetails());
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Operational", "Closed"});
        statusCombo.setSelectedItem(supplier.getSupplierStatus().getLabel());

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.setPreferredSize(new Dimension(500, 300));
        
        panel.add(new JLabel("Supplier Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(new JLabel("Contact Details:"));
        panel.add(contactField);
        panel.add(new JLabel("Supplier Type:"));
        panel.add(typeField);
        panel.add(new JLabel("Delivery Lead Time (days):"));
        panel.add(leadTimeField);
        panel.add(new JLabel("Transaction Details:"));
        panel.add(transactionField);
        panel.add(new JLabel("Status:"));
        panel.add(statusCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Supplier - ID: " + supplier.getSupplierID(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            // Validate inputs
            if (nameField.getText().trim().isEmpty() ||
                addressField.getText().trim().isEmpty() ||
                contactField.getText().trim().isEmpty() ||
                typeField.getText().trim().isEmpty()) {

                showError("Please fill in all required fields");
                return;
            }

            try {
                int leadTime = Integer.parseInt(leadTimeField.getText().trim());

                Supplier updatedSupplier = new Supplier(
                    supplier.getSupplierID(),
                    nameField.getText().trim(),
                    addressField.getText().trim(),
                    contactField.getText().trim(),
                    typeField.getText().trim(),
                    leadTime,
                    transactionField.getText().trim(),
                    new Status(-1, 0, (String) statusCombo.getSelectedItem())
                );

                controller.updateSupplier(updatedSupplier, () -> {
                    loadSupplierData();
                    showInfo("Supplier updated successfully!");
                }, ex -> showError(ex.getMessage()));
            } catch (NumberFormatException e) {
                showError("Please enter a valid number for delivery lead time");
            }
        }
    }

    // View helper methods used by controller callbacks
    private void showSuppliers(List<Supplier> suppliers) {
        tableModel.setRowCount(0);
        for (Supplier supplier : suppliers) {
            tableModel.addRow(new Object[]{
                supplier.getSupplierID(),
                supplier.getSupplierName(),
                supplier.getAddress(),
                supplier.getContactDetails(),
                supplier.getSupplierType(),
                supplier.getDeliveryLeadTime(),
                supplier.getTransactionDetails(),
                supplier.getSupplierStatus() != null ? supplier.getSupplierStatus().getLabel() : ""
            });
        }
    }

    private void showLoading(boolean loading) {
        // simple UI feedback: enable/disable table and buttons
        supplierTable.setEnabled(!loading);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void performSearch() {
        String searchText = searchField.getText().toLowerCase().trim();
        if (searchText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a name to search");
            return;
        }
        
        boolean found = false;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String supplierName = tableModel.getValueAt(i, 1).toString().toLowerCase();
            
            if (supplierName.contains(searchText)) {
                supplierTable.setRowSelectionInterval(i, i);
                supplierTable.scrollRectToVisible(supplierTable.getCellRect(i, 0, true));
                found = true;
                break;
            }
        }
        
        if (!found) {
            JOptionPane.showMessageDialog(this, "No suppliers found matching: " + searchText);
        }
    }

    private void clearSearch() {
        searchField.setText("");
        supplierTable.clearSelection();
        loadSupplierData();
    }
}