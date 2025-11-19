package Service;

import Model.DBConnection;
import Model.Prescription;
import Model.PrescriptionCRUD;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionService {
    public ServiceResult<List<Prescription>> listAll() {
        List<Prescription> out = new ArrayList<>();
        try (Connection conn = DBConnection.connectDB()) {
            // load via direct query since CRUD exposes table-based helper
            java.sql.PreparedStatement stmt = conn.prepareStatement("SELECT * FROM prescription_receipt WHERE prescriptionStatusID != ?");
            stmt.setInt(1, 8);
            try (java.sql.ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Prescription p = new Prescription(rs.getInt("patientID"), rs.getInt("consultationID"), rs.getInt("medicineID"), rs.getInt("hWorkerID"), rs.getDate("distributionDate"), rs.getInt("qtyDistributed"), rs.getBoolean("isValidPrescription"), rs.getBoolean("inventoryUpdated"), rs.getInt("prescriptionStatusID"));
                    p.setReceiptID(rs.getInt("receiptID"));
                    out.add(p);
                }
            }
            return ServiceResult.ok(out);
        } catch (SQLException e) {
            return ServiceResult.fail(SqlErrorMapper.normalize(e));
        }
    }

    public ServiceResult<Void> addPrescription(Prescription p) {
        try (Connection conn = DBConnection.connectDB()) {
            PrescriptionCRUD crud = new PrescriptionCRUD(conn);
            boolean ok = crud.addPrescription(p);
            if (ok) return ServiceResult.ok(null);
            return ServiceResult.fail("Failed to insert prescription");
        } catch (SQLException e) {
            return ServiceResult.fail(SqlErrorMapper.normalize(e));
        }
    }

    public ServiceResult<Void> updatePrescription(Prescription p) {
        try (Connection conn = DBConnection.connectDB()) {
            PrescriptionCRUD crud = new PrescriptionCRUD(conn);
            boolean ok = crud.updatePrescription(p);
            if (ok) return ServiceResult.ok(null);
            return ServiceResult.fail("No rows updated");
        } catch (SQLException e) {
            return ServiceResult.fail(SqlErrorMapper.normalize(e));
        }
    }

    public ServiceResult<Void> archivePrescription(int id) {
        try (Connection conn = DBConnection.connectDB()) {
            PrescriptionCRUD crud = new PrescriptionCRUD(conn);
            boolean ok = crud.archivePrescription(id);
            if (ok) return ServiceResult.ok(null);
            return ServiceResult.fail("Failed to archive");
        } catch (SQLException e) {
            return ServiceResult.fail(SqlErrorMapper.normalize(e));
        }
    }
}
