package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import Controller.MedicineInventoryController;
import Service.MedicineInventoryService;

import java.awt.*;
import java.util.List;
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
        // sample data
        tableModel.addRow(new Object[]{1001, 1, 1, 150, "Available"});
        tableModel.addRow(new Object[]{1002, 2, 2, 25, "Low Stock"});
        tableModel.addRow(new Object[]{1003, 3, 3, 0, "Out of Stock"});
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
        JButton deleteButton = new JButton("Delete Medicine");
        JButton refreshButton = new JButton("Refresh");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        // search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchLabel = new JLabel("Search by Medicine ID:");
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        JButton clearButton = new JButton("Clear");

        searchField.addActionListener(e -> performSearch());
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(clearButton);

        String[] columns = {"Inventory ID", "Facility ID", "Medicine ID", "Quantity in Stock", "Status"};
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

        // inventory ID
        column = medicineTable.getColumnModel().getColumn(0);
        column.setPreferredWidth(80);
        column.setResizable(false);
        
        // facility ID
        column = medicineTable.getColumnModel().getColumn(1);
        column.setPreferredWidth(80);
        column.setResizable(false);
        
        // medicine ID
        column = medicineTable.getColumnModel().getColumn(2);
        column.setPreferredWidth(80);
        column.setResizable(false);
        
        // quantity in stock
        column = medicineTable.getColumnModel().getColumn(3);
        column.setPreferredWidth(100);
        column.setResizable(false);
        
        // status
        column = medicineTable.getColumnModel().getColumn(4);
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

        for (MedicineInventory medicine : medicines) {
            tableModel.addRow(new Object[]{
                medicine.getInventoryID(),
                medicine.getFacilityID(),
                medicine.getMedicineID(),
                medicine.getQuantityInStock(),
                medicine.getStatus() != null ? medicine.getStatus().getLabel() : ""
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
        JTextField facilityIdField = new JTextField();
        JTextField medicineIdField = new JTextField();
        JTextField quantityField = new JTextField();
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Available", "Low Stock", "Out of Stock"});

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Facility ID:"));
        panel.add(facilityIdField);
        panel.add(new JLabel("Medicine ID:"));
        panel.add(medicineIdField);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);
        panel.add(new JLabel("Status:"));
        panel.add(statusCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Medicine Inventory",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                int facilityID = Integer.parseInt(facilityIdField.getText().trim());
                int medicineID = Integer.parseInt(medicineIdField.getText().trim());
                int quantity = Integer.parseInt(quantityField.getText().trim());
                
                MedicineInventory medicine = new MedicineInventory(
                    -1,
                    facilityID,
                    medicineID,
                    quantity,
                    MedicineInventory.Status.valueOf(((String)statusCombo.getSelectedItem()).toUpperCase().replace(" ", "_"))
                );
                
                controller.addMedicine(medicine);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for IDs and quantity",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, "Invalid status selection",
                    "Invalid Status", JOptionPane.ERROR_MESSAGE);
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
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete inventory record: " + inventoryId + "?", 
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
        
        int inventoryId = (int) tableModel.getValueAt(selectedRow, 0);
        int facilityId = (int) tableModel.getValueAt(selectedRow, 1);
        int medicineId = (int) tableModel.getValueAt(selectedRow, 2);
        int quantity = (int) tableModel.getValueAt(selectedRow, 3);
        String status = (String) tableModel.getValueAt(selectedRow, 4);

        JTextField facilityIdField = new JTextField(String.valueOf(facilityId));
        JTextField medicineIdField = new JTextField(String.valueOf(medicineId));
        JTextField quantityField = new JTextField(String.valueOf(quantity));
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Available", "Low Stock", "Out of Stock"});
        statusCombo.setSelectedItem(status);

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Facility ID:"));
        panel.add(facilityIdField);
        panel.add(new JLabel("Medicine ID:"));
        panel.add(medicineIdField);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);
        panel.add(new JLabel("Status:"));
        panel.add(statusCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Medicine Inventory - ID: " + inventoryId,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                int newFacilityID = Integer.parseInt(facilityIdField.getText().trim());
                int newMedicineID = Integer.parseInt(medicineIdField.getText().trim());
                int newQuantity = Integer.parseInt(quantityField.getText().trim());
                
                MedicineInventory medicine = new MedicineInventory(
                    inventoryId,
                    newFacilityID,
                    newMedicineID,
                    newQuantity,
                    MedicineInventory.Status.valueOf(((String)statusCombo.getSelectedItem()).toUpperCase().replace(" ", "_"))
                );
                
                controller.updateMedicine(medicine);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for IDs and quantity",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, "Invalid status selection",
                    "Invalid Status", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void performSearch() {
        String searchText = searchField.getText().toLowerCase().trim();
        if (searchText.isEmpty()) {
            return;
        }
        
        try {
            int searchId = Integer.parseInt(searchText);
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                int medicineId = (int) tableModel.getValueAt(i, 2);
                int inventoryId = (int) tableModel.getValueAt(i, 0);
                
                if (medicineId == searchId || inventoryId == searchId) {
                    medicineTable.setRowSelectionInterval(i, i);
                    medicineTable.scrollRectToVisible(medicineTable.getCellRect(i, 0, true));
                    return;
                }
            }
            
            JOptionPane.showMessageDialog(this, "No medicines found matching ID: " + searchText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a numeric Medicine ID or Inventory ID");
        }
    }

    private void clearSearch() {
        searchField.setText("");
        medicineTable.clearSelection();
    }
}