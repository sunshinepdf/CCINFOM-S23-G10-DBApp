package View;

import Controller.ConsultationController;
import Service.ConsultationService;
import Model.Consultation;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

public class ConsultationPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private ConsultationController controller;

    public ConsultationPanel() {
        try {
            ConsultationService service = new ConsultationService();
            initializePanel();
            controller = new ConsultationController(this, service);
            controller.loadAll();
        } catch (Exception e) {
            // fallback to demo mode
            System.out.println("Database not available - using demo mode");
            initializePanel();
            tableModel.setRowCount(0);
        }
    }

    private void initializePanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JPanel header = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Consultations", SwingConstants.LEFT);
        title.setFont(new Font("Arial", Font.BOLD, 22));

        JPanel buttons = new JPanel(new FlowLayout());
        JButton addBtn = new JButton("Add Consultation");
        JButton editBtn = new JButton("Edit Consultation");
        JButton delBtn = new JButton("Archive Consultation");
        JButton refreshBtn = new JButton("Refresh");
        buttons.add(addBtn); buttons.add(editBtn); buttons.add(delBtn); buttons.add(refreshBtn);

        header.add(title, BorderLayout.WEST); header.add(buttons, BorderLayout.EAST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        JButton searchBtn = new JButton("Search");
        JButton clearBtn = new JButton("Clear");
        searchPanel.add(new JLabel("Search by Patient ID:"));
        searchPanel.add(searchField); searchPanel.add(searchBtn); searchPanel.add(clearBtn);

        String[] cols = {"ConsultationID","PatientID","HealthWorkerID","FacilityID","ConsultationDate","ConsultationTime","Symptoms","Diagnosis","Prescription","Status"};
        tableModel = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r,int c){return false;} };
        table = new JTable(tableModel);
        setColumnWidths();

        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(header, BorderLayout.NORTH);
        northPanel.add(searchPanel, BorderLayout.SOUTH);
        add(northPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        refreshBtn.addActionListener(e -> loadData());
        addBtn.addActionListener(e -> showAddDialog());
        editBtn.addActionListener(e -> editSelected());
        delBtn.addActionListener(e -> archiveSelected());
        searchBtn.addActionListener(e -> performSearch());
        clearBtn.addActionListener(e -> clearSearch());
    }

    private void setColumnWidths() {
        TableColumn c; int[] widths = {80,80,100,100,120,120,200,200,200,120};
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
            ErrorDialog.showError("Error loading consultations: " + e.getMessage());
        }
    }

    private void showAddDialog() {
        JTextField patientField = new JTextField();
        JTextField hwField = new JTextField();
        JTextField facilityField = new JTextField();
        JTextField dateField = new JTextField(java.time.LocalDate.now().toString());
        JTextField timeField = new JTextField("09:00:00");
        JTextField symptomsField = new JTextField();
        JTextField diagnosisField = new JTextField();
        JTextField prescriptionField = new JTextField();
        JTextField statusField = new JTextField("Pending");

        JPanel p = new JPanel(new GridLayout(0,2,5,5));
        p.add(new JLabel("Patient ID:")); p.add(patientField);
        p.add(new JLabel("HealthWorker ID:")); p.add(hwField);
        p.add(new JLabel("Facility ID:")); p.add(facilityField);
        p.add(new JLabel("Consultation Date (YYYY-MM-DD):")); p.add(dateField);
        p.add(new JLabel("Consultation Time (HH:MM:SS):")); p.add(timeField);
        p.add(new JLabel("Symptoms:")); p.add(symptomsField);
        p.add(new JLabel("Diagnosis:")); p.add(diagnosisField);
        p.add(new JLabel("Prescription Notes:")); p.add(prescriptionField);
        p.add(new JLabel("Status (Pending/Completed):")); p.add(statusField);

        int res = JOptionPane.showConfirmDialog(this, p, "Add Consultation", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                int pid = Integer.parseInt(patientField.getText().trim());
                int hw = Integer.parseInt(hwField.getText().trim());
                int fid = Integer.parseInt(facilityField.getText().trim());
                Date d = Date.valueOf(dateField.getText().trim());
                Time t = Time.valueOf(timeField.getText().trim());
                String symptoms = symptomsField.getText().trim();
                String diag = diagnosisField.getText().trim();
                String pres = prescriptionField.getText().trim();

                Consultation c = new Consultation(0, pid, hw, fid, d, t, symptoms, diag, pres, Consultation.Status.PENDING);
                if (controller != null) controller.create(c);
                else tableModel.addRow(new Object[]{0, pid, hw, fid, d, t, symptoms, diag, pres, Consultation.Status.PENDING});
            } catch (Exception e) { ErrorDialog.showError("Error adding consultation: " + e.getMessage()); }
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow(); if (row == -1) { JOptionPane.showMessageDialog(this, "Select a consultation to edit"); return; }
        int id = (int) tableModel.getValueAt(row,0);
        try {
            int pid = (int) tableModel.getValueAt(row,1);
            int hw = (int) tableModel.getValueAt(row,2);
            int fid = (int) tableModel.getValueAt(row,3);
            Date d = (Date) tableModel.getValueAt(row,4);
            Time t = (Time) tableModel.getValueAt(row,5);
            String symptoms = (String) tableModel.getValueAt(row,6);
            String diag = (String) tableModel.getValueAt(row,7);
            String pres = (String) tableModel.getValueAt(row,8);
            String statusLabel = (String) tableModel.getValueAt(row,9);

            JTextField patientField = new JTextField(String.valueOf(pid));
            JTextField hwField = new JTextField(String.valueOf(hw));
            JTextField facilityField = new JTextField(String.valueOf(fid));
            JTextField dateField = new JTextField(d.toString());
            JTextField timeField = new JTextField(t.toString());
            JTextField symptomsField = new JTextField(symptoms);
            JTextField diagnosisField = new JTextField(diag);
            JTextField prescriptionField = new JTextField(pres);
            JTextField statusField = new JTextField(statusLabel);

            JPanel p = new JPanel(new GridLayout(0,2,5,5));
            p.add(new JLabel("Patient ID:")); p.add(patientField);
            p.add(new JLabel("HealthWorker ID:")); p.add(hwField);
            p.add(new JLabel("Facility ID:")); p.add(facilityField);
            p.add(new JLabel("Consultation Date (YYYY-MM-DD):")); p.add(dateField);
            p.add(new JLabel("Consultation Time (HH:MM:SS):")); p.add(timeField);
            p.add(new JLabel("Symptoms:")); p.add(symptomsField);
            p.add(new JLabel("Diagnosis:")); p.add(diagnosisField);
            p.add(new JLabel("Prescription Notes:")); p.add(prescriptionField);
            p.add(new JLabel("Status Label or ID:")); p.add(statusField);

            int res = JOptionPane.showConfirmDialog(this, p, "Edit Consultation", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                try {
                    Consultation c = new Consultation(id, Integer.parseInt(patientField.getText().trim()), Integer.parseInt(hwField.getText().trim()), Integer.parseInt(facilityField.getText().trim()), Date.valueOf(dateField.getText().trim()), Time.valueOf(timeField.getText().trim()), symptomsField.getText().trim(), diagnosisField.getText().trim(), prescriptionField.getText().trim(), Consultation.Status.PENDING);
                    if (controller != null) controller.update(c);
                } catch (Exception ex) { ErrorDialog.showError("Error updating consultation: " + ex.getMessage()); }
            }
        } catch (Exception e) { ErrorDialog.showError("Error preparing edit: " + e.getMessage()); }
    }

    private void archiveSelected() {
        int row = table.getSelectedRow(); if (row == -1) { JOptionPane.showMessageDialog(this, "Select a consultation to archive"); return; }
        int id = (int) tableModel.getValueAt(row,0);
        int confirm = JOptionPane.showConfirmDialog(this, "Archive consultation " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (controller != null) controller.archive(id);
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

    // View helper methods for controller compatibility
    public void showLoading(boolean loading) { setCursor(loading ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor()); }
    public void showError(String msg) { ErrorDialog.showError(msg); }
    public void showInfo(String msg) { ErrorDialog.showInfo(msg); }

    public void showConsultations(java.util.List<Model.Consultation> list) {
        tableModel.setRowCount(0);
        if (list == null || list.isEmpty()) { ErrorDialog.showInfo("No consultation records returned"); return; }
        for (Model.Consultation c : list) {
            tableModel.addRow(new Object[]{ c.getConsultationID(), c.getPatientID(), c.getHWorkerID(), c.getFacilityID(), c.getConsultationDate(), c.getConsultationTime(), c.getSymptoms(), c.getDiagnosis(), c.getPrescription(), c.getConsultationStatus() != null ? c.getConsultationStatus().name() : "" });
        }
    }
}
