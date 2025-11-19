package Model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MedicineCRUD {

    //create
    public void create(Medicine m) throws SQLException {
        String sql = "INSERT INTO medicine(medicineID, medicineName, medicineDesc, dosageForm, " +
                "strength, batchNumber, medicineStatusID) " +
                "VALUES (?,?,?,?,?,?,?)";

        try(Connection conn = DBConnection.connectDB();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, m.getMedicineName());
            pstmt.setString(2, m.getMedicineDesc());
            pstmt.setString(3, m.getDosageForm());
            pstmt.setString(4, m.getStrength());
            pstmt.setString(5, m.getBatchNumber());
            int statusID = convertStatusToID(m.getMedicineStatus());
            pstmt.setInt(6, statusID);

            pstmt.executeUpdate();
        }
    }

    //read
    public List<Medicine> readAll() throws SQLException {
        List<Medicine> medicine = new ArrayList<>();
        String sql = "SELECT m.*, s.statusName FROM medicine m " +
                "JOIN REF_Status s ON m.medicineStatusID = s.statusID" +
                "WHERE s.statusCategoryID = 4";

        try(Connection conn = DBConnection.connectDB();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery()) {

            while(rs.next()) {
                Medicine m = new Medicine (
                        rs.getInt("medicineID"),
                        rs.getString("medicineName"),
                        rs.getString("medicineDesc"),
                        rs.getString("dosageForm"),
                        rs.getString("strength"),
                        rs.getString("batchNumber"),
                        convertStatusNameToEnum(rs.getString("statusName"))
                );
                medicine.add(m);
            }
        }
        return medicine;
    }

    //getByID
    public Medicine getPatientById(int medicineID) throws SQLException {
        String sql = "SELECT m.*, s.statusName FROM medicine m " +
                "JOIN REF_Status s ON m.medicineStatusID = s.statusID" +
                "WHERE s.statusCategoryID = 4";

        try(Connection conn = DBConnection.connectDB();
        PreparedStatement pstmt = conn.prepareStatement(sql);) {
            pstmt.setInt(1, medicineID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if(rs.next()) {
                    return new Medicine (
                            rs.getInt("medicineID"),
                            rs.getString("medicineName"),
                            rs.getString("medicineDesc"),
                            rs.getString("dosageForm"),
                            rs.getString("strength"),
                            rs.getString("batchNumber"),
                            convertStatusNameToEnum(rs.getString("statusName"))
                    );
                }
            }
        }
        return null;
    }

    //update
    public void update (Medicine m) throws SQLException {

        String sql = "UPDATE medicine SET medicineName=?, medicineDesc=?, dosageForm=?, " +
                "strength=?, batchNumber=?, medicineStatusID=?" +
                "WHERE medicineID=?";

        try(Connection conn = DBConnection.connectDB();
        PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, m.getMedicineName());
            pstmt.setString(2, m.getMedicineDesc());
            pstmt.setString(3, m.getDosageForm());
            pstmt.setString(4, m.getStrength());
            pstmt.setString(5, m.getBatchNumber());
            int statusID = convertStatusToID(m.getMedicineStatus());
            pstmt.setInt(6, statusID);
            pstmt.setInt(7, m.getMedicineID());

            pstmt.executeUpdate();
        }
    }

    //delete
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM medicine WHERE medicineID = ?";

        try (Connection conn = DBConnection.connectDB();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    private int convertStatusToID(Medicine.Status status) {
        switch (status) {
            case AVAILABLE: return 8;
            case DISCONTINUED: return 9;
            case RECALLED: return 10;
            default: return 8;
        }
    }

    private Medicine.Status convertStatusNameToEnum(String statusName) {
        if ("available".equalsIgnoreCase(statusName)) {
            return Medicine.Status.AVAILABLE;
        } else if ("discontinued".equalsIgnoreCase(statusName)) {
            return Medicine.Status.DISCONTINUED;
        } else if ("batch recalled".equalsIgnoreCase(statusName)) {
            return Medicine.Status.RECALLED;
        } else {
            return Medicine.Status.AVAILABLE;
        }
    }
}
