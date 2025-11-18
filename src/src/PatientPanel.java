import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import Model.Patient;
import Model.PatientCRUD;

public class PatientPanel extends JPanel {
    private JTable patientTable;
    private DefaultTableModel tableModel;
    private Model.PatientCRUD patientCRUD;

    public PatientPanel() {
        patientCRUD = new Model.PatientCRUD();
        initializePanel();
        loadPatientData();
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
        JButton deleteButton = new JButton("Delete Patient");
        JButton refreshButton = new JButton("Refresh");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        // table
        String[] columns = {"ID", "Last Name", "First Name", "Birth Date", "Gender", "Blood Type", "Phone", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        patientTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(patientTable);

        addButton.addActionListener(e -> showAddPatientDialog());
        refreshButton.addActionListener(e -> loadPatientData());
        deleteButton.addActionListener(e -> deleteSelectedPatient());
        editButton.addActionListener(e -> editSelectedPatient());

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadPatientData() {
        try {
            List<Model.Patient> patients = patientCRUD.getAllPatients();
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
                    patient.getPrimaryPhone(),
                    patient.getPatientStatus().getLabel()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading patients: " + e.getMessage(), 
                "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddPatientDialog() {
        JTextField lastNameField = new JTextField();
        JTextField firstNameField = new JTextField();
        JTextField phoneField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Last Name:"));
        panel.add(lastNameField);
        panel.add(new JLabel("First Name:"));
        panel.add(firstNameField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Patient",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                int phoneNumber = Integer.parseInt(phoneField.getText().trim());
                Model.Patient patient = new Model.Patient(
                    0, // id 
                    lastNameField.getText(),
                    firstNameField.getText(),
                    new java.sql.Date(System.currentTimeMillis()), // current time
                    Model.Patient.Gender.MALE, // gender
                    Model.Patient.BloodType.O_POS, // blood type
                    "", // address
                    phoneNumber,
                    "", // contact
                    Model.Patient.PatientStatus.ALIVE
                );
                patientCRUD.createPatient(patient);
                loadPatientData(); // refresh
                JOptionPane.showMessageDialog(this, "Patient added successfully!");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error adding patient: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid phone number (numbers only)",
            "Invalid Phone", JOptionPane.ERROR_MESSAGE);
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
            try {
                patientCRUD.deletePatient(patientId);
                loadPatientData();
                JOptionPane.showMessageDialog(this, "Patient deleted successfully");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting patient: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            }
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
}