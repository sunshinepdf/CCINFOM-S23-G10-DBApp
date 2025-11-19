package View;

import Controller.ImmunizationAdministrationController;
import Service.ImmunizationAdministrationService;
import Model.ImmunizationAdministration;
import Model.Status;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.sql.Date;

public class ImmunizationAdministrationPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private ImmunizationAdministrationController controller;

    public ImmunizationAdministrationPanel() {
        try {
            ImmunizationAdministrationService service = new ImmunizationAdministrationService();
            initializePanel();
            controller = new ImmunizationAdministrationController(this, service);
            controller.loadAll();
        } catch (Exception e) {
            System.out.println("Database not available - using demo mode");
            initializePanel();
            tableModel.setRowCount(0);
        }
    }

    private void initializePanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JPanel header = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Immunization Administration", SwingConstants.LEFT);
        title.setFont(new Font("Arial", Font.BOLD, 22));

        JPanel buttons = new JPanel(new FlowLayout());
        JButton addBtn = new JButton("Add Record");
        JButton editBtn = new JButton("Edit Record");
        JButton delBtn = new JButton("Delete Record");
        JButton refreshBtn = new JButton("Refresh");
        buttons.add(addBtn); buttons.add(editBtn); buttons.add(delBtn); buttons.add(refreshBtn);

        header.add(title, BorderLayout.WEST); header.add(buttons, BorderLayout.EAST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        JButton searchBtn = new JButton("Search");
        JButton clearBtn = new JButton("Clear");
        searchPanel.add(new JLabel("Search by Patient ID:"));
        searchPanel.add(searchField); searchPanel.add(searchBtn); searchPanel.add(clearBtn);

        String[] cols = {"ID","PatientID","MedicineID","HealthWorkerID","Admin Date","Vaccine","Dose #","Next Date","Status","Side Effects"};
        tableModel = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r,int c){return false;} };
        table = new JTable(tableModel);
        setColumnWidths();

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(header, BorderLayout.NORTH);
        northPanel.add(searchPanel, BorderLayout.SOUTH);
        add(northPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        refreshBtn.addActionListener(e -> loadData());
        addBtn.addActionListener(e -> showAddDialog());
        editBtn.addActionListener(e -> editSelected());
        delBtn.addActionListener(e -> deleteSelected());
        searchBtn.addActionListener(e -> performSearch());
        clearBtn.addActionListener(e -> clearSearch());
    }

    private void setColumnWidths() {
        TableColumn c; int[] widths = {80,80,80,100,120,140,80,120,120,200};
        for (int i=0;i<widths.length;i++) { c = table.getColumnModel().getColumn(i); c.setPreferredWidth(widths[i]); }
    }

    public void loadData() {
        try {
            if (controller != null) {
                controller.loadAll();
                return;
            }
            tableModel.setRowCount(0);
        } catch (Exception e) {
            ErrorDialog.showError("Error loading records: " + e.getMessage());
        }
    }

    private void showAddDialog() {
        JTextField patientField = new JTextField();
        JTextField medicineField = new JTextField();
        JTextField hwField = new JTextField();
        JTextField dateField = new JTextField(java.time.LocalDate.now().toString());
        JTextField vaccineField = new JTextField();
        JTextField doseField = new JTextField("1");
        JTextField nextDateField = new JTextField();
        JTextField sideEffectsField = new JTextField();
        JTextField statusField = new JTextField("1");

        JPanel p = new JPanel(new GridLayout(0,2,5,5));
        p.add(new JLabel("Patient ID:")); p.add(patientField);
        p.add(new JLabel("Medicine ID:")); p.add(medicineField);
        p.add(new JLabel("HealthWorker ID:")); p.add(hwField);
        p.add(new JLabel("Administration Date (YYYY-MM-DD):")); p.add(dateField);
        p.add(new JLabel("Vaccine Type:")); p.add(vaccineField);
        p.add(new JLabel("Dosage Number:")); p.add(doseField);
        p.add(new JLabel("Next Vaccination Date (YYYY-MM-DD):")); p.add(nextDateField);
        p.add(new JLabel("Side Effects:")); p.add(sideEffectsField);
        p.add(new JLabel("Status ID:")); p.add(statusField);

        int res = JOptionPane.showConfirmDialog(this, p, "Add Immunization", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                int patientID = Integer.parseInt(patientField.getText().trim());
                int medID = Integer.parseInt(medicineField.getText().trim());
                int hw = Integer.parseInt(hwField.getText().trim());
                Date adminDate = Date.valueOf(dateField.getText().trim());
                String vaccine = vaccineField.getText().trim();
                int dose = Integer.parseInt(doseField.getText().trim());
                Date nextDate = nextDateField.getText().trim().isEmpty() ? null : Date.valueOf(nextDateField.getText().trim());
                String side = sideEffectsField.getText().trim();
                int statusID = Integer.parseInt(statusField.getText().trim());

                ImmunizationAdministration ia = new ImmunizationAdministration(0, patientID, medID, hw, adminDate, vaccine, dose, nextDate, new Status(statusID,0,"") , side);
                if (controller != null) controller.create(ia);
            } catch (Exception e) { ErrorDialog.showError("Error adding record: " + e.getMessage()); }
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow(); if (row == -1) { JOptionPane.showMessageDialog(this, "Select a record to edit"); return; }
        int id = (int) tableModel.getValueAt(row,0);
        try {
            // load values from table
            int patient = (int) tableModel.getValueAt(row,1);
            int med = (int) tableModel.getValueAt(row,2);
            int hw = (int) tableModel.getValueAt(row,3);
            Date adminDate = (Date) tableModel.getValueAt(row,4);
            String vaccine = (String) tableModel.getValueAt(row,5);
            int dose = (int) tableModel.getValueAt(row,6);
            Date next = (Date) tableModel.getValueAt(row,7);
            String side = (String) tableModel.getValueAt(row,9);
            String statusLabel = (String) tableModel.getValueAt(row,8);

            JTextField patientField = new JTextField(String.valueOf(patient));
            JTextField medicineField = new JTextField(String.valueOf(med));
            JTextField hwField = new JTextField(String.valueOf(hw));
            JTextField dateField = new JTextField(adminDate.toString());
            JTextField vaccineField = new JTextField(vaccine);
            JTextField doseField = new JTextField(String.valueOf(dose));
            JTextField nextField = new JTextField(next != null ? next.toString() : "");
            JTextField sideField = new JTextField(side);
            JTextField statusField = new JTextField(statusLabel);

            JPanel p = new JPanel(new GridLayout(0,2,5,5));
            p.add(new JLabel("Patient ID:")); p.add(patientField);
            p.add(new JLabel("Medicine ID:")); p.add(medicineField);
            p.add(new JLabel("HealthWorker ID:")); p.add(hwField);
            p.add(new JLabel("Administration Date (YYYY-MM-DD):")); p.add(dateField);
            p.add(new JLabel("Vaccine Type:")); p.add(vaccineField);
            p.add(new JLabel("Dosage Number:")); p.add(doseField);
            p.add(new JLabel("Next Vaccination Date (YYYY-MM-DD):")); p.add(nextField);
            p.add(new JLabel("Side Effects:")); p.add(sideField);
            p.add(new JLabel("Status Label or ID:")); p.add(statusField);

            int res = JOptionPane.showConfirmDialog(this, p, "Edit Record", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                try {
                    ImmunizationAdministration ia = new ImmunizationAdministration(id, Integer.parseInt(patientField.getText().trim()), Integer.parseInt(medicineField.getText().trim()), Integer.parseInt(hwField.getText().trim()), Date.valueOf(dateField.getText().trim()), vaccineField.getText().trim(), Integer.parseInt(doseField.getText().trim()), nextField.getText().trim().isEmpty() ? null : Date.valueOf(nextField.getText().trim()), new Status(0,0,statusField.getText().trim()), sideField.getText().trim());
                    if (controller != null) controller.update(ia);
                } catch (Exception ex) { ErrorDialog.showError("Error updating record: " + ex.getMessage()); }
            }

        } catch (Exception e) { ErrorDialog.showError("Error preparing edit: " + e.getMessage()); }
    }

    private void deleteSelected() {
        int row = table.getSelectedRow(); if (row == -1) { JOptionPane.showMessageDialog(this, "Select a record to delete"); return; }
        int id = (int) tableModel.getValueAt(row,0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete record " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (controller != null) controller.delete(id);
        }
    }

    private void performSearch() {
        String s = searchField.getText().trim(); if (s.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter patient ID to search"); return; }
        try {
            int pid = Integer.parseInt(s);
            for (int i=0;i<tableModel.getRowCount();i++) {
                if (((Integer)tableModel.getValueAt(i,1)) == pid) { table.setRowSelectionInterval(i,i); table.scrollRectToVisible(table.getCellRect(i,0,true)); return; }
            }
            JOptionPane.showMessageDialog(this, "No matching records");
        } catch (NumberFormatException e) { JOptionPane.showMessageDialog(this, "Enter a numeric patient ID"); }
    }

    private void clearSearch() { searchField.setText(""); table.clearSelection(); loadData(); }

    // View helper methods for controllers
    public void showLoading(boolean loading) { setCursor(loading ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor()); }
    public void showError(String msg) { ErrorDialog.showError(msg); }
    public void showInfo(String msg) { ErrorDialog.showInfo(msg); }

    public void showImmunizations(java.util.List<Model.ImmunizationAdministration> list) {
        tableModel.setRowCount(0);
        if (list == null || list.isEmpty()) { ErrorDialog.showInfo("No immunization records returned"); return; }
        for (Model.ImmunizationAdministration ia : list) {
            tableModel.addRow(new Object[]{ ia.getImmunizationID(), ia.getPatientID(), ia.getMedicineID(), ia.gethWorkerID(), ia.getAdministrationDate(), ia.getVaccineType(), ia.getDosageNumber(), ia.getNextVaccinationDate(), ia.getImmunizationStatus() != null ? ia.getImmunizationStatus().getLabel() : "", ia.getSideEffects() });
        }
    }
}
