package View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import Controller.PatientController;
import Service.PatientService;

import java.awt.*;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import javax.swing.SwingUtilities;

import Model.DBConnection;
import Model.ViewDAO;
    
import Model.Patient; 

public class PatientPanel extends JPanel {
    private JTable patientTable;
    private DefaultTableModel tableModel;
    private PatientService patientService;
    private PatientController controller;
    private JTextField searchField;

    // not from sql, just for running
    public PatientPanel() {
        try {
            patientService = new PatientService();
            initializePanel();
            controller = new PatientController(this, patientService);
            controller.loadPatients();
        } catch (Exception e) {
            System.out.println("Database not available - using demo mode");
            initializePanel();
            addSampleData();
        }
    }

    private void addSampleData() {
        // sample data
        tableModel.addRow(new Object[]{1, "Doe", "John", "1990-05-15", "Male", "O+", "123 Main Street", "1234567890", "Jane Doe - 0987654321"});
        tableModel.addRow(new Object[]{2, "Smith", "Jane", "1985-08-22", "Female", "A+", "456 Oak Avenue", "0987654321", "John Smith - 1234567890"});
        tableModel.addRow(new Object[]{3, "Johnson", "Bob", "1978-12-10", "Male", "B-", "789 Pine Road", "5551234567", "Alice Johnson - 555987654"});
    }

    private void initializePanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Patient Records Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add Patient");
        JButton editButton = new JButton("Edit Patient");
        JButton viewButton = new JButton("View");
        JButton deleteButton = new JButton("Delete Patient");
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
        String[] columns = {"ID", "Last Name", "First Name", "Birth Date", "Gender", "Blood Type", "Address", "Primary Phone", "Emergency Contact"};
        tableModel = new DefaultTableModel(columns, 0);
        patientTable = new JTable(tableModel);

        setColumnWidths();
        Font largerFont = new Font("Arial", Font.PLAIN, 14); 
        patientTable.setRowHeight(25);
        patientTable.setFont(largerFont);

        JScrollPane scrollPane = new JScrollPane(patientTable);

        addButton.addActionListener(e -> showAddPatientDialog());
        viewButton.addActionListener(e -> {
            showLoading(true);
            new Thread(() -> {
                try (Connection conn = DBConnection.getConnection()) {
                    ViewDAO vdao = new ViewDAO(conn);
                    List<Map<String, Object>> rows = vdao.getPatientConsultations();
                    SwingUtilities.invokeLater(() -> {
                        View.ViewDialog.showView(this, "Patient Consultations View", rows);
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
        refreshButton.addActionListener(e -> loadPatientData());
        deleteButton.addActionListener(e -> deleteSelectedPatient());
        editButton.addActionListener(e -> editSelectedPatient());
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
        patientTable.getTableHeader().setReorderingAllowed(false);

        // ID
        column = patientTable.getColumnModel().getColumn(0);
        column.setPreferredWidth(40);
        column.setResizable(false);
        
        // last name
        column = patientTable.getColumnModel().getColumn(1);
        column.setPreferredWidth(80);
        column.setResizable(false);
        
        // first name
        column = patientTable.getColumnModel().getColumn(2);
        column.setPreferredWidth(80);
        column.setResizable(false);
        
        // birth date 
        column = patientTable.getColumnModel().getColumn(3);
        column.setPreferredWidth(90);
        column.setResizable(false);
        
        // gender
        column = patientTable.getColumnModel().getColumn(4);
        column.setPreferredWidth(50);
        column.setResizable(false);

        // blood type
        column = patientTable.getColumnModel().getColumn(5);
        column.setPreferredWidth(60);
        column.setResizable(false);
        
        // address
        column = patientTable.getColumnModel().getColumn(6);
        column.setPreferredWidth(150);
        column.setResizable(false);
        
        // primary phone
        column = patientTable.getColumnModel().getColumn(7);
        column.setPreferredWidth(100);
        column.setResizable(false);
        
        // emergency contact 
        column = patientTable.getColumnModel().getColumn(8);
        column.setPreferredWidth(150);
        column.setResizable(false);
    }

    private void loadPatientData() {
        controller.loadPatients();
    }

    // view helpers used by PatientController
    public void showPatients(java.util.List<Model.Patient> patients) {
        tableModel.setRowCount(0); // clear data

        if (patients == null) {
            JOptionPane.showMessageDialog(this, "No patient data returned from database. The table might not exist yet.",
                    "Database Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        for (Model.Patient patient : patients) {
            tableModel.addRow(new Object[]{
                    patient.getPatientID(),
                    patient.getLastName(),
                    patient.getFirstName(),
                    patient.getBirthDate(),
                    patient.getGender().getLabel(),
                    patient.getBloodType().getLabel(),
                    patient.getAddress(),
                    patient.getPrimaryPhone(),
                    patient.getEmergencyContact()
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

    private void showAddPatientDialog() {
        JTextField lastNameField = new JTextField();
        JTextField firstNameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField emergencyContactField = new JTextField();
        JTextField birthDateField = new JTextField("2023-01-01");
        JComboBox<String> genderCombo = new JComboBox<>(new String[]{"Male", "Female"});
        JComboBox<String> bloodTypeCombo = new JComboBox<>(new String[]{"O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-"});

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Last Name:"));
        panel.add(lastNameField);
        panel.add(new JLabel("First Name:"));
        panel.add(firstNameField);
        panel.add(new JLabel("Birth Date (YYYY-MM-DD):"));
        panel.add(birthDateField);
        panel.add(new JLabel("Gender:"));
        panel.add(genderCombo);
        panel.add(new JLabel("Blood Type:"));
        panel.add(bloodTypeCombo);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(new JLabel("Primary Phone:"));
        panel.add(phoneField);
        panel.add(new JLabel("Emergency Contact:"));
        panel.add(emergencyContactField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Patient",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                String phoneNumber = phoneField.getText().trim();
                java.sql.Date birthDate = java.sql.Date.valueOf(birthDateField.getText());
                
                Patient patient = new Patient(
                    -1,
                    lastNameField.getText(),
                    firstNameField.getText(),
                    birthDate,
                    Patient.Gender.valueOf(((String)genderCombo.getSelectedItem()).toUpperCase()),
                    Patient.BloodType.valueOf(((String)bloodTypeCombo.getSelectedItem()).replace("+", "_POS").replace("-", "_NEG")),
                    addressField.getText(),
                    phoneNumber,
                    emergencyContactField.getText(),
                    null // patient status??? dead or alive?
                );
                
                // delegate to controller (controller runs background task)
                controller.addPatient(patient);
            } catch (NumberFormatException e) {
                ErrorDialog.showError("Please enter a valid phone number (numbers only)");
            } catch (IllegalArgumentException e) {
                ErrorDialog.showError("Invalid date format. Use YYYY-MM-DD");
            }
        }
    }

    private void deleteSelectedPatient() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a patient to delete");
            return;
        }
        
        int patientId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete patient ID: " + patientId + "?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // delegate to controller
            controller.deletePatient(patientId);
        }
    }

    private void editSelectedPatient() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a patient to edit");
            return;
        }
        JOptionPane.showMessageDialog(this, "Edit feature coming soon!");
    }

    private void performSearch() {
        String searchText = searchField.getText().toLowerCase().trim();
        if (searchText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a name to search");
            return;
        }
        
        boolean found = false;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String lastName = ((String) tableModel.getValueAt(i, 1)).toLowerCase();
            String firstName = ((String) tableModel.getValueAt(i, 2)).toLowerCase();
            
            if (lastName.contains(searchText) || firstName.contains(searchText)) {
                patientTable.setRowSelectionInterval(i, i);
                patientTable.scrollRectToVisible(patientTable.getCellRect(i, 0, true));
                found = true;
                break;
            }
        }
        
        if (!found) {
            JOptionPane.showMessageDialog(this, "No patients found matching: " + searchText);
        }
    }

    private void clearSearch() {
        searchField.setText("");
        patientTable.clearSelection();
        loadPatientData();
    }
}