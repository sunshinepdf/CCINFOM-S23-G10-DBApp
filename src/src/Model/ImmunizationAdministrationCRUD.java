package Model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ImmunizationAdministrationCRUD {

    // Use per-method connections (open/close within each method)

    //create
    public void create(ImmunizationAdministration ia) throws SQLException{
        String sql = "INSERT INTO immunization_administration (patientID, medicineID, " +
                "hWorkerID, administrationDate, vaccineType, dosageNumber, " +
                "nextVaccinationDate, immunizationStatus, sideEffects) VALUES " +
                "(?, ?, ?, ?, ?, ?, ?, ?, ?)";
         //[EDIT]: Refactored the connection portion to prevent long-live connection leaks
        try (Connection conn = DBConnection.connectDB();
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, ia.getPatientID());
            pstmt.setInt(2, ia.getMedicineID());
            pstmt.setInt(3, ia.gethWorkerID());
            pstmt.setDate(4, ia.getAdministrationDate());
            pstmt.setString(5, ia.getVaccineType());
            pstmt.setInt(6, ia.getDosageNumber());
            pstmt.setDate(7, ia.getNextVaccinationDate());
            pstmt.setInt(8, ia.getImmunizationStatus().getStatusID());
            pstmt.setString(9, ia.getSideEffects());

            pstmt.executeUpdate();
        }
    }

    //read all
    public List<ImmunizationAdministration> readAll() throws SQLException{
        List<ImmunizationAdministration> list = new ArrayList<>();
        String sql = "SELECT * FROM immunization_administration";

        //[EDIT]: Refactored the connection portion to prevent long-live connection leaks
        try (Connection conn = DBConnection.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()){

            while (rs.next()) {
                int statusID = rs.getInt("immunizationStatus");
                Status status = StatusDAO.getStatusByID(conn, statusID);

                ImmunizationAdministration ia = new ImmunizationAdministration(
                        rs.getInt("immunizationID"),
                        rs.getInt("patientID"),
                        rs.getInt("medicineID"),
                        rs.getInt("hWorkerID"),
                        rs.getDate("administrationDate"),
                        rs.getString("vaccineType"),
                        rs.getInt("dosageNumber"),
                        rs.getDate("nextVaccinationDate"),
                        status,
                        rs.getString("sideEffects")
                );
                list.add(ia);
            }
        }
        return list;
    }

    //update
    public void update(ImmunizationAdministration ia) throws SQLException {
        String sql = "UPDATE immunization_administration SET patientID=?, medicineID=?, " +
                "hWorkerID=?, administrationDate=?, vaccineType=?, dosageNumber=?, " +
                "nextVaccinationDate=?, immunizationStatus=?, sideEffects=? " +
                "WHERE immunizationID=?";
        //[EDIT]: Refactored the connection portion to prevent long-live connection leaks
        try (Connection conn = DBConnection.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ia.getPatientID());
            pstmt.setInt(2, ia.getMedicineID());
            pstmt.setInt(3, ia.gethWorkerID());
            pstmt.setDate(4, ia.getAdministrationDate());
            pstmt.setString(5, ia.getVaccineType());
            pstmt.setInt(6, ia.getDosageNumber());
            pstmt.setDate(7, ia.getNextVaccinationDate());
            pstmt.setInt(8, ia.getImmunizationStatus().getStatusID());
            pstmt.setString(9, ia.getSideEffects());
            pstmt.setInt(10, ia.getImmunizationID());

            pstmt.executeUpdate();
        }
    }

    //delete
    //TODO: Update Delete system to use update status instead of hard delete
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM immunization_administration WHERE immunizationID=?";
        //[EDIT]: Refactored the connection portion to prevent long-live connection leaks
        try (Connection conn = DBConnection.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            pstmt.executeUpdate();
        }
    }
}
