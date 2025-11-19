package Model;

import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class PrescriptionCRUD {

    private Connection conn;
    private static final int ARCHIVED_STATUS_ID = 8; // PrescriptionStatus 'Archived'

    public PrescriptionCRUD(Connection conn) {
        this.conn = conn;
    }

    // CREATE
    public boolean addPrescription(Prescription p) {
        String sql = "INSERT INTO prescription_receipt " +
                "(patientID, consultationID, medicineID, hWorkerID, distributionDate, qtyDistributed, isValidPrescription, inventoryUpdated, prescriptionStatusID) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, p.getPatientID());
            stmt.setInt(2, p.getConsultationID());
            stmt.setInt(3, p.getMedicineID());
            stmt.setInt(4, p.getHWorkerID());
            stmt.setDate(5, p.getDistributionDate());
            stmt.setInt(6, p.getQtyDistributed());
            stmt.setBoolean(7, p.isValidPrescription());
            stmt.setBoolean(8, p.isInventoryUpdated());
            stmt.setInt(9, p.getPrescriptionStatusID());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        p.setReceiptID(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // READ 
    public void loadPrescriptions(DefaultTableModel tableModel) {
        String sql = "SELECT * FROM prescription_receipt WHERE prescriptionStatusID != ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ARCHIVED_STATUS_ID);
            try (ResultSet rs = stmt.executeQuery()) {
                // Clear existing rows
                tableModel.setRowCount(0);

                while (rs.next()) {
                    Object[] row = {
                        rs.getInt("receiptID"),
                        rs.getInt("patientID"),
                        rs.getInt("consultationID"),
                        rs.getInt("medicineID"),
                        rs.getInt("hWorkerID"),
                        rs.getDate("distributionDate"),
                        rs.getInt("qtyDistributed"),
                        rs.getBoolean("isValidPrescription"),
                        rs.getBoolean("inventoryUpdated"),
                        rs.getInt("prescriptionStatusID")
                    };
                    tableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // UPDATE
    public boolean updatePrescription(Prescription p) {
        String sql = "UPDATE prescription_receipt SET patientID=?, consultationID=?, medicineID=?, hWorkerID=?, distributionDate=?, qtyDistributed=?, isValidPrescription=?, inventoryUpdated=?, prescriptionStatusID=? WHERE receiptID=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, p.getPatientID());
            stmt.setInt(2, p.getConsultationID());
            stmt.setInt(3, p.getMedicineID());
            stmt.setInt(4, p.getHWorkerID());
            stmt.setDate(5, p.getDistributionDate());
            stmt.setInt(6, p.getQtyDistributed());
            stmt.setBoolean(7, p.isValidPrescription());
            stmt.setBoolean(8, p.isInventoryUpdated());
            stmt.setInt(9, p.getPrescriptionStatusID());
            stmt.setInt(10, p.getReceiptID());

            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // SOFT DELETE (Archive)
    public boolean archivePrescription(int receiptID) {
        String sql = "UPDATE prescription_receipt SET prescriptionStatusID = ? WHERE receiptID=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, ARCHIVED_STATUS_ID);
            stmt.setInt(2, receiptID);

            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
