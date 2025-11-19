package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import Controller.MedicineInventoryController;
import Service.MedicineInventoryService;

import java.awt.*;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import javax.swing.SwingUtilities;

import Model.DBConnection;
import Model.ViewDAO;
import Model.MedicineInventory;

public class MedicineInventoryPanel extends JPanel {
    private JTable medicineTable;
    private DefaultTableModel tableModel;
    private MedicineInventoryController controller;
    private JTextField searchField;

    public MedicineInventoryPanel() {
        initializePanel();
        MedicineInventoryService service = new MedicineInventoryService();
        controller = new MedicineInventoryController(this, service);
        controller.updateAllStatuses();
        controller.loadMedicines();
    }

    public void addSampleData() {
        // sample data (inventory view format)
        tableModel.addRow(new Object[]{1001, "Barangay Health Center Malolos", "Paracetamol", 150, "Available", "OK"});
        tableModel.addRow(new Object[]{1002, "Barangay Health Center Bicutan", "Amoxicillin", 25, "Low Stock", "Low"});
        tableModel.addRow(new Object[]{1003, "Barangay Health Center Taguig", "Vitamin C", 0, "Out of Stock", "Low"});
    }

    private void initializePanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Medicine Inventory Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add Medicine");
        JButton editButton = new JButton("Edit Medicine");
        JButton viewButton = new JButton("View");
        JButton deleteButton = new JButton("Delete Medicine");
        JButton refreshButton = new JButton("Refresh");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        // search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchLabel = new JLabel("Search by Name:");
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        JButton clearButton = new JButton("Clear");

        searchField.addActionListener(e -> performSearch());
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(clearButton);

        // table
        String[] columns = {"Inventory ID", "Facility", "Medicine", "Quantity in Stock", "Inventory Status", "Stock Level"};
        tableModel = new DefaultTableModel(columns, 0);
        medicineTable = new JTable(tableModel);

        setColumnWidths();
        Font largerFont = new Font("Arial", Font.PLAIN, 14); 
        medicineTable.setRowHeight(25);
        medicineTable.setFont(largerFont);
        medicineTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        medicineTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        medicineTable.setEnabled(false);

        JScrollPane scrollPane = new JScrollPane(medicineTable);

        addButton.addActionListener(e -> showAddMedicineDialog());
        viewButton.addActionListener(e -> {
            showLoading(true);
            new Thread(() -> {
                try (Connection conn = DBConnection.getConnection()) {
                    ViewDAO vdao = new ViewDAO(conn);
                    List<Map<String, Object>> rows = vdao.getMedicineInventoryStatus();
                    SwingUtilities.invokeLater(() -> {
                        View.ViewDialog.showView(this, "Medicine Inventory Status View", rows);
                        showLoading(false);
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        showLoading(false);
                        showError("Error loading view: " + ex.getMessage());
                    });
                }
            }).start();
        });
        refreshButton.addActionListener(e -> loadMedicineData());
        deleteButton.addActionListener(e -> deleteSelectedMedicine());
        editButton.addActionListener(e -> editSelectedMedicine());
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
        medicineTable.getTableHeader().setReorderingAllowed(false);
        // Inventory ID
        column = medicineTable.getColumnModel().getColumn(0);
        column.setPreferredWidth(80);
        column.setResizable(false);

        // Facility (name)
        column = medicineTable.getColumnModel().getColumn(1);
        column.setPreferredWidth(200);
        column.setResizable(false);

        // Medicine (name)
        column = medicineTable.getColumnModel().getColumn(2);
        column.setPreferredWidth(200);
        column.setResizable(false);

        // Quantity in stock
        column = medicineTable.getColumnModel().getColumn(3);
        column.setPreferredWidth(120);
        column.setResizable(false);

        // inventory status
        column = medicineTable.getColumnModel().getColumn(4);
        column.setPreferredWidth(140);
        column.setResizable(false);

        // stock level
        column = medicineTable.getColumnModel().getColumn(5);
        column.setPreferredWidth(100);
        column.setResizable(false);
    }

    private void loadMedicineData() {
        controller.loadMedicines();
    }

    public void showMedicines(List<MedicineInventory> medicines) {
        tableModel.setRowCount(0); // clear data

        if (medicines == null) {
            JOptionPane.showMessageDialog(this, "No medicine data returned from database. The table might not exist yet.",
                    "Database Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (MedicineInventory mi : medicines) {
            tableModel.addRow(new Object[]{
                    mi.getInventoryID(),
                    String.valueOf(mi.getFacilityID()),
                    String.valueOf(mi.getMedicineID()),
                    mi.getQuantityInStock(),
                    mi.getInventoryStatusID() != null ? mi.getInventoryStatusID().getLabel() : "",
                    ""
            });
        }
    }

    // show rows returned from the SQL view (with names)
    public void showMedicineInventoryView(List<Map<String, Object>> rows) {
        tableModel.setRowCount(0);
        if (rows == null || rows.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No inventory data returned from database.", "Database Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (Map<String, Object> row : rows) {
            Object inventoryID = row.get("inventoryID");
            Object facilityName = row.get("facilityName");
            Object medicineName = row.get("medicineName");
            Object qty = row.get("quantityInStock");
            Object invStatus = row.get("inventoryStatus");
            Object stockLevel = row.get("stockLevel");

            tableModel.addRow(new Object[]{
                    inventoryID,
                    facilityName,
                    medicineName,
                    qty,
                    invStatus,
                    stockLevel
            });
        }
    }

    public void showLoading(boolean loading) {
        setCursor(loading ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Database Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    private void showAddMedicineDialog() {
        JTextField facilityField = new JTextField();
        JTextField medicineIdField = new JTextField();
        JTextField quantityField = new JTextField();
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Available", "Expired", "Low Stock", "Out of Stock"});

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Facility ID:"));
        panel.add(facilityField);
        panel.add(new JLabel("Medicine ID:"));
        panel.add(medicineIdField);
        panel.add(new JLabel("Quantity in Stock:"));
        panel.add(quantityField);
        panel.add(new JLabel("Status:"));
        panel.add(statusCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Medicine Inventory",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int facilityID = Integer.parseInt(facilityField.getText().trim());
                int medicineID = Integer.parseInt(medicineIdField.getText().trim());
                int quantity = Integer.parseInt(quantityField.getText().trim());

                Model.MedicineInventory.Status status = Model.MedicineInventory.Status.fromLabel((String) statusCombo.getSelectedItem());

                MedicineInventory mi = new MedicineInventory(
                        -1,
                        facilityID,
                        medicineID,
                        quantity,
                        status
                );

                controller.addMedicine(mi);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for Facility ID, Medicine ID and Quantity",
                        "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedMedicine() {
        int selectedRow = medicineTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a medicine to delete");
            return;
        }
        
        int inventoryId = (int) tableModel.getValueAt(selectedRow, 0);
        String medicineName = String.valueOf(tableModel.getValueAt(selectedRow, 2));
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete inventory item: " + medicineName + " (Inventory ID: " + inventoryId + ")?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            controller.deleteMedicine(inventoryId);
        }
    }

    private void editSelectedMedicine() {
        int selectedRow = medicineTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a medicine to edit");
            return;
        }
        JOptionPane.showMessageDialog(this, "Edit feature coming soon!");
    }

    private void performSearch() {
        String searchText = searchField.getText().toLowerCase().trim();
        if (searchText.isEmpty()) {
            return;
        }
        
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String facility = String.valueOf(tableModel.getValueAt(i, 1)).toLowerCase();
            String medicineName = String.valueOf(tableModel.getValueAt(i, 2)).toLowerCase();
            String status = String.valueOf(tableModel.getValueAt(i, 4)).toLowerCase();

            if (facility.contains(searchText) || medicineName.contains(searchText) || status.contains(searchText)) {
                medicineTable.setRowSelectionInterval(i, i);
                medicineTable.scrollRectToVisible(medicineTable.getCellRect(i, 0, true));
                return;
            }
        }
        
        JOptionPane.showMessageDialog(this, "No medicines found matching: " + searchText);
    }

    private void clearSearch() {
        searchField.setText("");
        medicineTable.clearSelection();
    }
}