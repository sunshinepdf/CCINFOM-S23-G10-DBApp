import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import Model.SupplierCRUD;
import Model.Supplier;

public class SupplierPanel extends JPanel {
    private JTable supplierTable;
    private DefaultTableModel tableModel;
    private SupplierCRUD supplierCRUD;
    private JTextField searchField;

    public SupplierPanel() {
        try {
            supplierCRUD = new SupplierCRUD();
            initializePanel();
            loadSupplierData();
        } catch (Exception e) {
            System.out.println("Database not available - using demo mode");
            initializePanel();
            addSampleData();
        }
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
        JButton deleteButton = new JButton("Delete Supplier");
        JButton refreshButton = new JButton("Refresh");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
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
        try {
            List<Supplier> suppliers = supplierCRUD.readAll();
            tableModel.setRowCount(0); // Clear data

            if (suppliers == null || suppliers.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No supplier data returned from database.",
                    "Database Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            for (Supplier supplier : suppliers) {
                tableModel.addRow(new Object[]{
                    supplier.getSupplierID(),
                    supplier.getSupplierName(),
                    supplier.getAddress(),
                    supplier.getContactDetails(),
                    supplier.getSupplierType(),
                    supplier.getDeliveryLeadTime(),
                    supplier.getTransactionDetails(),
                    supplier.getSupplierStatus().getLabel()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading suppliers: " + e.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
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
            try {
                // Validate inputs
                if (nameField.getText().trim().isEmpty() ||
                    addressField.getText().trim().isEmpty() ||
                    contactField.getText().trim().isEmpty() ||
                    typeField.getText().trim().isEmpty()) {
                    
                    JOptionPane.showMessageDialog(this, "Please fill in all required fields",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int leadTime = Integer.parseInt(leadTimeField.getText().trim());
                
                Supplier supplier = new Supplier(
                    -1,
                    nameField.getText().trim(),
                    addressField.getText().trim(),
                    contactField.getText().trim(),
                    typeField.getText().trim(),
                    leadTime,
                    transactionField.getText().trim(),
                    Supplier.Status.fromLabel((String)statusCombo.getSelectedItem())
                );
                
                supplierCRUD.create(supplier);
                loadSupplierData();
                JOptionPane.showMessageDialog(this, "Supplier added successfully!");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error adding supplier: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number for delivery lead time",
                    "Invalid Lead Time", JOptionPane.ERROR_MESSAGE);
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
            try {
                supplierCRUD.softDelete(supplierId);
                loadSupplierData();
                JOptionPane.showMessageDialog(this, "Supplier closed successfully");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error closing supplier: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editSelectedSupplier() {
        int selectedRow = supplierTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a supplier to edit");
            return;
        }
        
        try {
            int supplierId = (int) tableModel.getValueAt(selectedRow, 0);
            Supplier supplier = supplierCRUD.getSupplierById(supplierId);
            
            if (supplier == null) {
                JOptionPane.showMessageDialog(this, "Supplier not found in database");
                return;
            }
            
            showEditSupplierDialog(supplier);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading supplier data: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
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
            try {
                // Validate inputs
                if (nameField.getText().trim().isEmpty() ||
                    addressField.getText().trim().isEmpty() ||
                    contactField.getText().trim().isEmpty() ||
                    typeField.getText().trim().isEmpty()) {
                    
                    JOptionPane.showMessageDialog(this, "Please fill in all required fields",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int leadTime = Integer.parseInt(leadTimeField.getText().trim());
                
                Supplier updatedSupplier = new Supplier(
                    supplier.getSupplierID(),
                    nameField.getText().trim(),
                    addressField.getText().trim(),
                    contactField.getText().trim(),
                    typeField.getText().trim(),
                    leadTime,
                    transactionField.getText().trim(),
                    Supplier.Status.fromLabel((String)statusCombo.getSelectedItem())
                );
                
                supplierCRUD.update(updatedSupplier);
                loadSupplierData();
                JOptionPane.showMessageDialog(this, "Supplier updated successfully!");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error updating supplier: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number for delivery lead time",
                    "Invalid Lead Time", JOptionPane.ERROR_MESSAGE);
            }
        }
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