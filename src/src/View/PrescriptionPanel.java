package View;

import Controller.PrescriptionController;
import Service.PrescriptionService;
import Model.Prescription;

import Model.DBConnection;
import Model.PrescriptionCRUD;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Objects;

public class PrescriptionPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private PrescriptionController controller;

    public PrescriptionPanel() {
        try {
            PrescriptionService service = new PrescriptionService();
            initializePanel();
            controller = new PrescriptionController(this, service);
            controller.loadPrescriptions();
        } catch (Exception e) {
            initializePanel();
            loadPrescriptionData();
        }
    }

    private void initializePanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JPanel header = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Prescription Transactions", SwingConstants.LEFT);
        title.setFont(new Font("Arial", Font.BOLD, 22));

        JPanel buttons = new JPanel(new FlowLayout());
        JButton addBtn = new JButton("Add Prescription");
        JButton editBtn = new JButton("Edit Prescription");
        JButton delBtn = new JButton("Archive Prescription");
        JButton refreshBtn = new JButton("Refresh");

        buttons.add(addBtn);
        buttons.add(editBtn);
        buttons.add(delBtn);
        buttons.add(refreshBtn);

        header.add(title, BorderLayout.WEST);
        header.add(buttons, BorderLayout.EAST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        JButton searchBtn = new JButton("Search");
        JButton clearBtn = new JButton("Clear");
        searchPanel.add(new JLabel("Search by Patient ID:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(clearBtn);

        String[] cols = {"Receipt ID","Patient ID","Consultation ID","Medicine ID","HealthWorker ID","Distribution Date","Qty","Valid","Inventory Updated","StatusID"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        setColumnWidths();

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(header, BorderLayout.NORTH);
        northPanel.add(searchPanel, BorderLayout.SOUTH);
        add(northPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        refreshBtn.addActionListener(e -> loadPrescriptionData());
        addBtn.addActionListener(e -> showAddDialog());
        editBtn.addActionListener(e -> editSelected());
        delBtn.addActionListener(e -> archiveSelected());
        searchBtn.addActionListener(e -> performSearch());
        clearBtn.addActionListener(e -> clearSearch());
    }

    private void setColumnWidths() {
        TableColumn c;
        int[] widths = {80,80,100,100,100,120,60,80,100,80};
        for (int i = 0; i < widths.length; i++) {
            c = table.getColumnModel().getColumn(i);
            c.setPreferredWidth(widths[i]);
        }
    }

    public void loadPrescriptionData() {
        try {
            if (controller != null) {
                controller.loadPrescriptions();
                return;
            }
            try (Connection conn = DBConnection.connectDB()) {
                PrescriptionCRUD crud = new PrescriptionCRUD(conn);
                crud.loadPrescriptions(tableModel);
            }
        } catch (SQLException e) {
            ErrorDialog.showError("Error loading prescriptions: " + e.getMessage());
        }
    }

    private void showAddDialog() {
        JTextField patientField = new JTextField();
        JTextField consultField = new JTextField();
        JTextField medicineField = new JTextField();
        JTextField hwField = new JTextField();
        JTextField dateField = new JTextField(java.time.LocalDate.now().toString());
        JTextField qtyField = new JTextField("1");
        JCheckBox validBox = new JCheckBox("Valid Prescription", true);
        JCheckBox invBox = new JCheckBox("Inventory Updated", false);
        JTextField statusField = new JTextField("1");

        JPanel p = new JPanel(new GridLayout(0,2,5,5));
        p.add(new JLabel("Patient ID:")); p.add(patientField);
        p.add(new JLabel("Consultation ID:")); p.add(consultField);
        p.add(new JLabel("Medicine ID:")); p.add(medicineField);
        p.add(new JLabel("HealthWorker ID:")); p.add(hwField);
        p.add(new JLabel("Distribution Date (YYYY-MM-DD):")); p.add(dateField);
        p.add(new JLabel("Quantity:")); p.add(qtyField);
        p.add(validBox); p.add(invBox);
        p.add(new JLabel("Status ID:")); p.add(statusField);

        int res = JOptionPane.showConfirmDialog(this, p, "Add Prescription", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                int patientID = Integer.parseInt(patientField.getText().trim());
                int consultID = Integer.parseInt(consultField.getText().trim());
                int medID = Integer.parseInt(medicineField.getText().trim());
                int hwID = Integer.parseInt(hwField.getText().trim());
                Date distDate = Date.valueOf(Objects.requireNonNull(dateField.getText()).trim());
                int qty = Integer.parseInt(qtyField.getText().trim());
                boolean valid = validBox.isSelected();
                boolean inv = invBox.isSelected();
                int status = Integer.parseInt(statusField.getText().trim());

                Prescription pres = new Prescription(patientID, consultID, medID, hwID, distDate, qty, valid, inv, status);
                if (controller != null) {
                    controller.addPrescription(pres);
                } else {
                    String sql = "INSERT INTO prescription_receipt (patientID, consultationID, medicineID, hWorkerID, distributionDate, qtyDistributed, isValidPrescription, inventoryUpdated, prescriptionStatusID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    try (Connection conn = DBConnection.connectDB();
                         java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setInt(1, patientID);
                        stmt.setInt(2, consultID);
                        stmt.setInt(3, medID);
                        stmt.setInt(4, hwID);
                        stmt.setDate(5, distDate);
                        stmt.setInt(6, qty);
                        stmt.setBoolean(7, valid);
                        stmt.setBoolean(8, inv);
                        stmt.setInt(9, status);
                        int rows = stmt.executeUpdate();
                        if (rows > 0) { loadPrescriptionData(); ErrorDialog.showInfo("Prescription added"); }
                        else ErrorDialog.showError("Failed to add prescription");
                    }
                }
            } catch (NumberFormatException nfe) {
                ErrorDialog.showError("Please enter valid numeric values for IDs and quantity.");
            } catch (SQLException | IllegalArgumentException ex) {
                ErrorDialog.showError("Error adding prescription: " + ex.getMessage());
            }
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a prescription to edit"); return; }
        int receiptID = (int) tableModel.getValueAt(row, 0);
        int patientID = (int) tableModel.getValueAt(row,1);
        int consultID = (int) tableModel.getValueAt(row,2);
        int medID = (int) tableModel.getValueAt(row,3);
        int hwID = (int) tableModel.getValueAt(row,4);
        Date distDate = (Date) tableModel.getValueAt(row,5);
        int qty = (int) tableModel.getValueAt(row,6);
        boolean valid = (boolean) tableModel.getValueAt(row,7);
        boolean inv = (boolean) tableModel.getValueAt(row,8);
        int status = (int) tableModel.getValueAt(row,9);

        JTextField patientField = new JTextField(String.valueOf(patientID));
        JTextField consultField = new JTextField(String.valueOf(consultID));
        JTextField medicineField = new JTextField(String.valueOf(medID));
        JTextField hwField = new JTextField(String.valueOf(hwID));
        JTextField dateField = new JTextField(distDate.toString());
        JTextField qtyField = new JTextField(String.valueOf(qty));
        JCheckBox validBox = new JCheckBox("Valid Prescription", valid);
        JCheckBox invBox = new JCheckBox("Inventory Updated", inv);
        JTextField statusField = new JTextField(String.valueOf(status));

        JPanel p = new JPanel(new GridLayout(0,2,5,5));
        p.add(new JLabel("Patient ID:")); p.add(patientField);
        p.add(new JLabel("Consultation ID:")); p.add(consultField);
        p.add(new JLabel("Medicine ID:")); p.add(medicineField);
        p.add(new JLabel("HealthWorker ID:")); p.add(hwField);
        p.add(new JLabel("Distribution Date (YYYY-MM-DD):")); p.add(dateField);
        p.add(new JLabel("Quantity:")); p.add(qtyField);
        p.add(validBox); p.add(invBox);
        p.add(new JLabel("Status ID:")); p.add(statusField);

        int res = JOptionPane.showConfirmDialog(this, p, "Edit Prescription", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                int npatient = Integer.parseInt(patientField.getText().trim());
                int nconsult = Integer.parseInt(consultField.getText().trim());
                int nmed = Integer.parseInt(medicineField.getText().trim());
                int nhw = Integer.parseInt(hwField.getText().trim());
                Date ndate = Date.valueOf(dateField.getText().trim());
                int nqty = Integer.parseInt(qtyField.getText().trim());
                boolean nvalid = validBox.isSelected();
                boolean ninv = invBox.isSelected();
                int nstatus = Integer.parseInt(statusField.getText().trim());

                Prescription pres = new Prescription(npatient, nconsult, nmed, nhw, ndate, nqty, nvalid, ninv, nstatus);
                pres.setReceiptID(receiptID);
                if (controller != null) {
                    controller.updatePrescription(pres);
                } else {
                    String sql = "UPDATE prescription_receipt SET patientID=?, consultationID=?, medicineID=?, hWorkerID=?, distributionDate=?, qtyDistributed=?, isValidPrescription=?, inventoryUpdated=?, prescriptionStatusID=? WHERE receiptID=?";
                    try (Connection conn = DBConnection.connectDB();
                         java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setInt(1, npatient);
                        stmt.setInt(2, nconsult);
                        stmt.setInt(3, nmed);
                        stmt.setInt(4, nhw);
                        stmt.setDate(5, ndate);
                        stmt.setInt(6, nqty);
                        stmt.setBoolean(7, nvalid);
                        stmt.setBoolean(8, ninv);
                        stmt.setInt(9, nstatus);
                        stmt.setInt(10, receiptID);
                        int rows = stmt.executeUpdate();
                        if (rows > 0) { loadPrescriptionData(); ErrorDialog.showInfo("Prescription updated"); }
                        else ErrorDialog.showError("Failed to update prescription");
                    }
                }
            } catch (NumberFormatException nfe) {
                ErrorDialog.showError("Please enter valid numeric values for IDs and quantity.");
            } catch (SQLException | IllegalArgumentException ex) {
                ErrorDialog.showError("Error updating prescription: " + ex.getMessage());
            }
        }
    }

    private void archiveSelected() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select a prescription to archive"); return; }
        int receiptID = (int) tableModel.getValueAt(row,0);
        int confirm = JOptionPane.showConfirmDialog(this, "Archive prescription ID " + receiptID + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (controller != null) controller.archivePrescription(receiptID);
            else {
                try (Connection conn = DBConnection.connectDB();
                     java.sql.PreparedStatement stmt = conn.prepareStatement("UPDATE prescription_receipt SET prescriptionStatusID = ? WHERE receiptID = ?")) {
                    stmt.setInt(1, 8); // archived status id per CRUD
                    stmt.setInt(2, receiptID);
                    int rows = stmt.executeUpdate();
                    if (rows > 0) { loadPrescriptionData(); ErrorDialog.showInfo("Archived"); }
                    else ErrorDialog.showError("Failed to archive");
                } catch (SQLException e) { ErrorDialog.showError(e.getMessage()); }
            }
        }
    }

    private void performSearch() {
        String s = searchField.getText().trim();
        if (s.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter patient ID to search"); return; }
        try {
            int pid = Integer.parseInt(s);
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (((Integer)tableModel.getValueAt(i,1)) == pid) {
                    table.setRowSelectionInterval(i,i);
                    table.scrollRectToVisible(table.getCellRect(i,0,true));
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "No matching prescriptions");
        } catch (NumberFormatException e) { JOptionPane.showMessageDialog(this, "Enter a numeric patient ID"); }
    }

    private void clearSearch() { searchField.setText(""); table.clearSelection(); loadPrescriptionData(); }

    // View helper methods used by controllers
    public void showLoading(boolean loading) {
        setCursor(loading ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
    }

    public void showError(String msg) { ErrorDialog.showError(msg); }
    public void showInfo(String msg) { ErrorDialog.showInfo(msg); }

    public void showPrescriptions(java.util.List<Model.Prescription> list) {
        tableModel.setRowCount(0);
        if (list == null || list.isEmpty()) { ErrorDialog.showInfo("No prescriptions returned"); return; }
        for (Model.Prescription p : list) {
            tableModel.addRow(new Object[]{ p.getReceiptID(), p.getPatientID(), p.getConsultationID(), p.getMedicineID(), p.getHWorkerID(), p.getDistributionDate(), p.getQtyDistributed(), p.isValidPrescription(), p.isInventoryUpdated(), p.getPrescriptionStatusID() });
        }
    }
}

