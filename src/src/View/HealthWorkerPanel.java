package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.List;
import Controller.HealthWorkerController;
import Service.HealthWorkerService;
import Model.HealthWorker;
import Model.Status;

public class HealthWorkerPanel extends JPanel {
    private JTable workerTable;
    private DefaultTableModel tableModel;
    private HealthWorkerController controller;
    private HealthWorkerService hwService;
    private JTextField searchField;

    public HealthWorkerPanel() {
        try {
            hwService = new HealthWorkerService();
            initializePanel();
            controller = new HealthWorkerController(this, hwService);
            controller.loadWorkers();
        } catch (Exception e) {
            System.out.println("Database not available - using demo mode");
            initializePanel();
            addSampleData();
        }
    }

    private void addSampleData() {
        // sample data
        tableModel.addRow(new Object[]{1, 101, "Dela Cruz", "Juan", "Barangay Health Worker", "09171234567", "Active"});
        tableModel.addRow(new Object[]{2, 101, "Reyes", "Maria", "Nurse", "09179876543", "Active"});
        tableModel.addRow(new Object[]{3, 102, "Santos", "Pedro", "Doctor", "09171122334", "Inactive"});
        tableModel.addRow(new Object[]{4, 101, "Gonzales", "Ana", "Midwife", "09173334455", "Active"});
        tableModel.addRow(new Object[]{5, 103, "Lim", "Michael", "Health Inspector", "09175556677", "Active"});
    }

    private void initializePanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Barangay Health Worker Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add Worker");
        JButton editButton = new JButton("Edit Worker");
        JButton deleteButton = new JButton("Delete Worker");
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
        String[] columns = {"Worker ID", "Facility ID", "Last Name", "First Name", "Position", "Contact Information", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        workerTable = new JTable(tableModel);

        setColumnWidths();
        Font largerFont = new Font("Arial", Font.PLAIN, 14);
        workerTable.setRowHeight(25);
        workerTable.setFont(largerFont);
        workerTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        workerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(workerTable);
        scrollPane.setPreferredSize(new Dimension(1200, 400));

        addButton.addActionListener(e -> showAddWorkerDialog());
        refreshButton.addActionListener(e -> loadWorkerData());
        deleteButton.addActionListener(e -> deleteSelectedWorker());
        editButton.addActionListener(e -> editSelectedWorker());
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
        workerTable.getTableHeader().setReorderingAllowed(false);

        int[] widths = {80, 80, 100, 100, 150, 150, 80};
        for (int i = 0; i < widths.length; i++) {
            column = workerTable.getColumnModel().getColumn(i);
            column.setPreferredWidth(widths[i]);
            column.setResizable(true);
        }
    }

    private void loadWorkerData() {
        controller.loadWorkers();
    }

    private void showAddWorkerDialog() {
        JTextField facilityIdField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField firstNameField = new JTextField();
        JTextField positionField = new JTextField();
        JTextField contactField = new JTextField();
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Active", "Inactive"});

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.setPreferredSize(new Dimension(400, 200));
        
        panel.add(new JLabel("Facility ID:"));
        panel.add(facilityIdField);
        panel.add(new JLabel("Last Name:"));
        panel.add(lastNameField);
        panel.add(new JLabel("First Name:"));
        panel.add(firstNameField);
        panel.add(new JLabel("Position:"));
        panel.add(positionField);
        panel.add(new JLabel("Contact Information:"));
        panel.add(contactField);
        panel.add(new JLabel("Status:"));
        panel.add(statusCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Health Worker",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
            // Validate inputs
                if (facilityIdField.getText().trim().isEmpty() ||
                    lastNameField.getText().trim().isEmpty() ||
                    firstNameField.getText().trim().isEmpty() ||
                    positionField.getText().trim().isEmpty() ||
                    contactField.getText().trim().isEmpty()) {
                    
                    JOptionPane.showMessageDialog(this, "Please fill in all fields",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int facilityID = Integer.parseInt(facilityIdField.getText().trim());

                HealthWorker worker = new HealthWorker(
                    -1,
                    facilityID,
                    lastNameField.getText().trim(),
                    firstNameField.getText().trim(),
                    positionField.getText().trim(),
                    contactField.getText().trim(),
                    new Status(-1, 0, (String) statusCombo.getSelectedItem())
                );
                controller.addWorker(worker);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid Facility ID (numbers only)",
                    "Invalid Facility ID", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedWorker() {
    int selectedRow = workerTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a health worker to delete");
        return;
    }
    
    int workerId = (int) tableModel.getValueAt(selectedRow, 0);
    String workerName = (String) tableModel.getValueAt(selectedRow, 2) + " " + 
                       (String) tableModel.getValueAt(selectedRow, 3);
    
    int confirm = JOptionPane.showConfirmDialog(this, 
        "Are you sure you want to deactivate health worker:\n" + 
        "ID: " + workerId + " - " + workerName + "?\n\n" +
        "Note: This will set the worker status to 'Inactive' but preserve all data.", 
        "Confirm Deactivation", JOptionPane.YES_NO_OPTION);
    
    if (confirm == JOptionPane.YES_OPTION) {
        controller.softDelete(workerId);
    }
}

    private void editSelectedWorker() {
        int selectedRow = workerTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a health worker to edit");
            return;
        }
        
        int workerId = (int) tableModel.getValueAt(selectedRow, 0);
        controller.fetchById(workerId, hw -> {
            if (hw == null) {
                JOptionPane.showMessageDialog(this, "Health worker not found in database");
                return;
            }
            showEditWorkerDialog(hw);
        });
    }

    private void showEditWorkerDialog(HealthWorker worker) {
        JTextField facilityIdField = new JTextField(String.valueOf(worker.getFacilityID()));
        JTextField lastNameField = new JTextField(worker.getLastName());
        JTextField firstNameField = new JTextField(worker.getFirstName());
        JTextField positionField = new JTextField(worker.getPosition());
        JTextField contactField = new JTextField(worker.getContactInformation());
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Active", "Inactive"});
        statusCombo.setSelectedItem(worker.getWorkerStatus().getLabel());

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.setPreferredSize(new Dimension(400, 200));
        
        panel.add(new JLabel("Facility ID:"));
        panel.add(facilityIdField);
        panel.add(new JLabel("Last Name:"));
        panel.add(lastNameField);
        panel.add(new JLabel("First Name:"));
        panel.add(firstNameField);
        panel.add(new JLabel("Position:"));
        panel.add(positionField);
        panel.add(new JLabel("Contact Information:"));
        panel.add(contactField);
        panel.add(new JLabel("Status:"));
        panel.add(statusCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Edit Health Worker - ID: " + worker.getWorkerID(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                if (facilityIdField.getText().trim().isEmpty() ||
                    lastNameField.getText().trim().isEmpty() ||
                    firstNameField.getText().trim().isEmpty() ||
                    positionField.getText().trim().isEmpty() ||
                    contactField.getText().trim().isEmpty()) {
                    
                    JOptionPane.showMessageDialog(this, "Please fill in all fields",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int facilityID = Integer.parseInt(facilityIdField.getText().trim());
                
                HealthWorker updatedWorker = new HealthWorker(
                    worker.getWorkerID(),
                    facilityID,
                    lastNameField.getText().trim(),
                    firstNameField.getText().trim(),
                    positionField.getText().trim(),
                    contactField.getText().trim(),
                    new Status(-1, 0, (String)statusCombo.getSelectedItem())
                );

                controller.updateWorker(updatedWorker);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid Facility ID (numbers only)",
                    "Invalid Facility ID", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // View helpers used by HealthWorkerController
    public void showWorkers(java.util.List<HealthWorker> workers) {
        tableModel.setRowCount(0);
        if (workers == null) {
            JOptionPane.showMessageDialog(this, "No health worker data returned from database.", "Database Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        for (HealthWorker worker : workers) {
            tableModel.addRow(new Object[]{
                    worker.getWorkerID(),
                    worker.getFacilityID(),
                    worker.getLastName(),
                    worker.getFirstName(),
                    worker.getPosition(),
                    worker.getContactInformation(),
                    worker.getWorkerStatus() != null ? worker.getWorkerStatus().getLabel() : ""
            });
        }
    }

    public void showLoading(boolean loading) {
        setCursor(loading ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
    }

    public void showError(String message) {
        ErrorDialog.showError(message);
    }

    public void showInfo(String message) {
        ErrorDialog.showInfo(message);
    }

    private void performSearch() {
        String searchText = searchField.getText().toLowerCase().trim();
        if (searchText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a name to search");
            return;
        }
        
        boolean found = false;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String lastName = tableModel.getValueAt(i, 2).toString().toLowerCase();
            String firstName = tableModel.getValueAt(i, 3).toString().toLowerCase();
            
            if (lastName.contains(searchText) || firstName.contains(searchText)) {
                workerTable.setRowSelectionInterval(i, i);
                workerTable.scrollRectToVisible(workerTable.getCellRect(i, 0, true));
                found = true;
                break;
            }
        }
        
        if (!found) {
            JOptionPane.showMessageDialog(this, "No health workers found matching: " + searchText);
        }
    }

    private void clearSearch() {
        searchField.setText("");
        workerTable.clearSelection();
        loadWorkerData();
    }
}