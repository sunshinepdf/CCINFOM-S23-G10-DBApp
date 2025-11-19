import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import Model.MedicineInventoryCRUD;
import Model.MedicineInventory;

public class MedicineInventoryPanel extends JPanel {
    private JTable medicineTable;
    private DefaultTableModel tableModel;
    private MedicineInventoryCRUD medicineCRUD;
    private JTextField searchField;

    public MedicineInventoryPanel() {
        try {
            medicineCRUD = new MedicineInventoryCRUD();
            medicineCRUD.updateAllStatuses();
            initializePanel();
            loadMedicineData();
        } catch (Exception e) {
            System.out.println("Database not available - using demo mode");
            initializePanel();
            addSampleData();
        }
    }

    private void addSampleData() {
        // sample data
        tableModel.addRow(new Object[]{1001, "Paracetamol 500mg", "Tablet", "Pain and fever relief", 150, "2025-12-31", "Available"});
        tableModel.addRow(new Object[]{1002, "Amoxicillin 250mg", "Capsule", "Antibiotic for bacterial infections", 25, "2024-08-15", "Expired"});
        tableModel.addRow(new Object[]{1003, "Vitamin C 1000mg", "Tablet", "Immune system support", 0, "2024-10-20", "Out of Stock"});
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
        JLabel searchLabel = new JLabel("Search:");
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        JButton clearButton = new JButton("Clear");

        searchField.addActionListener(e -> performSearch());
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(clearButton);

        // table
        String[] columns = {"Medicine ID", "Medicine Name", "Medicine Type", "Description", "Quantity in Stock", "Expiry Date", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        medicineTable = new JTable(tableModel);

        setColumnWidths();
        Font largerFont = new Font("Arial", Font.PLAIN, 14); 
        medicineTable.setRowHeight(25);
        medicineTable.setFont(largerFont);

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

        // medicine ID
        column = medicineTable.getColumnModel().getColumn(0);
        column.setPreferredWidth(80);
        column.setResizable(false);
        
        // medicine name
        column = medicineTable.getColumnModel().getColumn(1);
        column.setPreferredWidth(150);
        column.setResizable(false);
        
        // medicine type
        column = medicineTable.getColumnModel().getColumn(2);
        column.setPreferredWidth(100);
        column.setResizable(false);
        
        // description
        column = medicineTable.getColumnModel().getColumn(3);
        column.setPreferredWidth(200);
        column.setResizable(false);
        
        // quantity in stock
        column = medicineTable.getColumnModel().getColumn(4);
        column.setPreferredWidth(100);
        column.setResizable(false);

        // expiry date
        column = medicineTable.getColumnModel().getColumn(5);
        column.setPreferredWidth(100);
        column.setResizable(false);
        
        // status
        column = medicineTable.getColumnModel().getColumn(6);
        column.setPreferredWidth(100);
        column.setResizable(false);
    }

    private void loadMedicineData() {
        try {
            List<MedicineInventory> medicines = medicineCRUD.readAll();
            tableModel.setRowCount(0); // clear data

            if (medicines == null) {
                JOptionPane.showMessageDialog(this, "No medicine data returned from database. The table might not exist yet.",
                    "Database Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            for (MedicineInventory medicine : medicines) {
                tableModel.addRow(new Object[]{
                    medicine.getMedicineID(),
                    medicine.getMedicineName(),
                    medicine.getMedicineType().getLabel(),
                    medicine.getDescription(),
                    medicine.getQuantityInStock(),
                    medicine.getExpiryDate(),
                    medicine.getStatus().getLabel()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading medicines: " + e.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddMedicineDialog() {
        JTextField nameField = new JTextField();
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Medicine", "Vaccine"});
        JTextArea descriptionArea = new JTextArea(3, 20);
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        JTextField quantityField = new JTextField();
        JTextField expiryField = new JTextField("2024-12-31");

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Medicine Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Medicine Type:"));
        panel.add(typeCombo);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionScroll);
        panel.add(new JLabel("Quantity in Stock:"));
        panel.add(quantityField);
        panel.add(new JLabel("Expiry Date (YYYY-MM-DD):"));
        panel.add(expiryField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Medicine",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                int quantity = Integer.parseInt(quantityField.getText().trim());
                java.sql.Date expiryDate = java.sql.Date.valueOf(expiryField.getText());
                
                MedicineInventory medicine = new MedicineInventory(
                    -1,
                    nameField.getText(),
                    (String) typeCombo.getSelectedItem(), 
                    descriptionArea.getText(),
                    quantity,
                    expiryDate,
                    "Available"
                );
                
                medicineCRUD.create(medicine);
                loadMedicineData();
                JOptionPane.showMessageDialog(this, "Medicine added successfully!");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error adding medicine: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid quantity (numbers only)",
                    "Invalid Quantity", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD",
                    "Invalid Date", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedMedicine() {
        int selectedRow = medicineTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a medicine to delete");
            return;
        }
        
        int medicineId = (int) tableModel.getValueAt(selectedRow, 0);
        String medicineName = (String) tableModel.getValueAt(selectedRow, 1);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete medicine: " + medicineName + " (ID: " + medicineId + ")?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                medicineCRUD.delete(medicineId);
                loadMedicineData();
                JOptionPane.showMessageDialog(this, "Medicine deleted successfully");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting medicine: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            }
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
            String medicineName = ((String) tableModel.getValueAt(i, 1)).toLowerCase();
            String medicineType = ((String) tableModel.getValueAt(i, 2)).toLowerCase();
            String description = ((String) tableModel.getValueAt(i, 3)).toLowerCase();
            
            if (medicineName.contains(searchText) || medicineType.contains(searchText) || description.contains(searchText)) {
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