package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;
import Model.FacilityCRUD;
import Model.Facility;

public class FacilityPanel extends JPanel {
    private JTable facilityTable;
    private DefaultTableModel tableModel;
    private FacilityCRUD facilityCRUD;
    private JTextField searchField;

    public FacilityPanel() {
        try {
            facilityCRUD = new FacilityCRUD();
            initializePanel();
            loadFacilityData();
        } catch (Exception e) {
            System.out.println("Database not available - using demo mode");
            initializePanel();
            addSampleData();
        }
    }

    private void addSampleData() {
        // sample data
        tableModel.addRow(new Object[]{1, "Barangay Health Center - Main", "123 Main Street, Barangay 1", "09171234567", "08:00:00", "17:00:00", "Operational"});
        tableModel.addRow(new Object[]{2, "Barangay Health Center - Annex", "456 Oak Avenue, Barangay 2", "09179876543", "07:00:00", "16:00:00", "Operational"});
        tableModel.addRow(new Object[]{3, "Barangay Clinic - Satellite", "789 Pine Road, Barangay 3", "09171122334", "09:00:00", "18:00:00", "Under Maintenance"});
    }

    private void initializePanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Barangay Facility Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add Facility");
        JButton editButton = new JButton("Edit Facility");
        JButton deleteButton = new JButton("Delete Facility");
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

        // Table
        String[] columns = {"Facility ID", "Facility Name", "Address", "Contact Number", "Shift Start", "Shift End", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        facilityTable = new JTable(tableModel);

        setColumnWidths();
        Font largerFont = new Font("Arial", Font.PLAIN, 14);
        facilityTable.setRowHeight(25);
        facilityTable.setFont(largerFont);
        facilityTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        facilityTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        facilityTable.setEnabled(false);

        JScrollPane scrollPane = new JScrollPane(facilityTable);
        scrollPane.setPreferredSize(new Dimension(1200, 400));

        addButton.addActionListener(e -> showAddFacilityDialog());
        refreshButton.addActionListener(e -> loadFacilityData());
        deleteButton.addActionListener(e -> deleteSelectedFacility());
        editButton.addActionListener(e -> editSelectedFacility());
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
        facilityTable.getTableHeader().setReorderingAllowed(false);

        int[] widths = {80, 150, 200, 120, 100, 100, 120};
        for (int i = 0; i < widths.length; i++) {
            column = facilityTable.getColumnModel().getColumn(i);
            column.setPreferredWidth(widths[i]);
            column.setResizable(true);
        }
    }

    private void loadFacilityData() {
        try {
            List<Facility> facilities = facilityCRUD.readAll();
            tableModel.setRowCount(0); // Clear data

            if (facilities == null || facilities.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No facility data returned from database.",
                    "Database Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            for (Facility facility : facilities) {
                tableModel.addRow(new Object[]{
                    facility.getFacilityID(),
                    facility.getFacilityName(),
                    facility.getAddress(),
                    facility.getContactNumber(),
                    facility.getShiftStart(),
                    facility.getShiftEnd(),
                    facility.getFacilityStatus().getLabel()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading facilities: " + e.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddFacilityDialog() {
        JTextField nameField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField contactField = new JTextField();
        JTextField shiftStartField = new JTextField("08:00:00");
        JTextField shiftEndField = new JTextField("17:00:00");
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Operational", "Closed", "Under Maintenance"});

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.setPreferredSize(new Dimension(500, 250));
        
        panel.add(new JLabel("Facility Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(new JLabel("Contact Number:"));
        panel.add(contactField);
        panel.add(new JLabel("Shift Start (HH:MM:SS):"));
        panel.add(shiftStartField);
        panel.add(new JLabel("Shift End (HH:MM:SS):"));
        panel.add(shiftEndField);
        panel.add(new JLabel("Status:"));
        panel.add(statusCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Facility",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                // Validate inputs
                if (nameField.getText().trim().isEmpty() ||
                    addressField.getText().trim().isEmpty() ||
                    shiftStartField.getText().trim().isEmpty() ||
                    shiftEndField.getText().trim().isEmpty()) {
                    
                    JOptionPane.showMessageDialog(this, "Please fill in all required fields",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Time shiftStart = Time.valueOf(shiftStartField.getText().trim());
                Time shiftEnd = Time.valueOf(shiftEndField.getText().trim());
                
                Facility facility = new Facility(
                    -1,
                    nameField.getText().trim(),
                    addressField.getText().trim(),
                    contactField.getText().trim(),
                    shiftStart,
                    shiftEnd,
                    Facility.Status.fromLabel((String)statusCombo.getSelectedItem())
                );
                
                facilityCRUD.create(facility);
                loadFacilityData();
                JOptionPane.showMessageDialog(this, "Facility added successfully!");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error adding facility: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, "Invalid time format. Please use HH:MM:SS format (e.g., 08:00:00)",
                    "Invalid Time Format", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedFacility() {
        int selectedRow = facilityTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a facility to delete");
            return;
        }
        
        int facilityId = (int) tableModel.getValueAt(selectedRow, 0);
        String facilityName = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to close facility:\n" + 
            "ID: " + facilityId + " - " + facilityName + "?\n\n" +
            "Note: This will set the facility status to 'Closed' but preserve all data.", 
            "Confirm Closure", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                facilityCRUD.softDelete(facilityId); // Use soft delete instead of delete
                loadFacilityData();
                JOptionPane.showMessageDialog(this, "Facility closed successfully");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error closing facility: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editSelectedFacility() {
        int selectedRow = facilityTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a facility to edit");
            return;
        }
        
        try {
            int facilityId = (int) tableModel.getValueAt(selectedRow, 0);
            Facility facility = facilityCRUD.getFacilityById(facilityId);
            
            if (facility == null) {
                JOptionPane.showMessageDialog(this, "Facility not found in database");
                return;
            }
            
            showEditFacilityDialog(facility);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading facility data: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showEditFacilityDialog(Facility facility) {
        JTextField nameField = new JTextField(facility.getFacilityName());
        JTextField addressField = new JTextField(facility.getAddress());
        JTextField contactField = new JTextField(facility.getContactNumber());
        JTextField shiftStartField = new JTextField(facility.getShiftStart().toString());
        JTextField shiftEndField = new JTextField(facility.getShiftEnd().toString());
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Operational", "Closed", "Under Maintenance"});
        statusCombo.setSelectedItem(facility.getFacilityStatus().getLabel());

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.setPreferredSize(new Dimension(500, 250));
        
        panel.add(new JLabel("Facility Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(new JLabel("Contact Number:"));
        panel.add(contactField);
        panel.add(new JLabel("Shift Start (HH:MM:SS):"));
        panel.add(shiftStartField);
        panel.add(new JLabel("Shift End (HH:MM:SS):"));
        panel.add(shiftEndField);
        panel.add(new JLabel("Status:"));
        panel.add(statusCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Facility - ID: " + facility.getFacilityID(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                // Validate inputs
                if (nameField.getText().trim().isEmpty() ||
                    addressField.getText().trim().isEmpty() ||
                    shiftStartField.getText().trim().isEmpty() ||
                    shiftEndField.getText().trim().isEmpty()) {
                    
                    JOptionPane.showMessageDialog(this, "Please fill in all required fields",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Time shiftStart = Time.valueOf(shiftStartField.getText().trim());
                Time shiftEnd = Time.valueOf(shiftEndField.getText().trim());
                
                Facility updatedFacility = new Facility(
                    facility.getFacilityID(),
                    nameField.getText().trim(),
                    addressField.getText().trim(),
                    contactField.getText().trim(),
                    shiftStart,
                    shiftEnd,
                    Facility.Status.fromLabel((String)statusCombo.getSelectedItem())
                );
                
                facilityCRUD.update(updatedFacility);
                loadFacilityData();
                JOptionPane.showMessageDialog(this, "Facility updated successfully!");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error updating facility: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, "Invalid time format. Please use HH:MM:SS format (e.g., 08:00:00)",
                    "Invalid Time Format", JOptionPane.ERROR_MESSAGE);
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
            String facilityName = tableModel.getValueAt(i, 1).toString().toLowerCase();
            
            if (facilityName.contains(searchText)) {
                facilityTable.setRowSelectionInterval(i, i);
                facilityTable.scrollRectToVisible(facilityTable.getCellRect(i, 0, true));
                found = true;
                break;
            }
        }
        
        if (!found) {
            JOptionPane.showMessageDialog(this, "No facilities found matching: " + searchText);
        }
    }

    private void clearSearch() {
        searchField.setText("");
        facilityTable.clearSelection();
        loadFacilityData();
    }
}