package Model;

import java.sql.*;
import java.util.List;

public class PatientCRUD {

    private Connection conn = DBConnection.getConnection();

    //CREATE
    public void create(Patient p) throws SQLException{
        String sql = "INSERT INTO patient(lastName, firstName, birthDate, " +
                "gender, bloodType, address, primaryPhone, emergencyContact, patientStatus) " +
                "VALUES(?,?,?,?,?,?,?,?,?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, p.getLastName());
            pstmt.setString(2, p.getFirstName());
            pstmt.setDate(3, p.getBirthDate());
            pstmt.setString(4, p.getGender().getLabel());
            pstmt.setString(5, p.getBloodType().getLabel());
            pstmt.setString(6, p.getAddress());
            pstmt.setInt(7, p.getPrimaryPhone());
            pstmt.setString(8, p.getEmergencyContact());
            pstmt.setInt(9, p.getPatientStatus());

            pstmt.executeUpdate();
        }
    }

    
    public List<Patient> getAll() throws SQLException{
        /*List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patient";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Patient p = new Patient(
                        rs.getInt("patientID"),
                        rs.getString("lastName"),
                        rs.getString("firstName"),
                        rs.getDate("birthDate"),
                        Patient.Gender.fromGender(rs.getString("gender")),  // if using enum with display value
                        Patient.BloodType.fromBloodType("bloodType"),
                        rs.getString("address"),
                        rs.getInt("primaryPhone"),
                        rs.getString("emergencyContact"),
                        Patient.PatientStatus.fromPatientStatus("patientStatus")
                );
                patients.add(p);
            }
        }
        return patients;*/
        return null; // just testing gui
    }

    //UPDATE
    public void update(Patient p) throws SQLException {
        String sql = "UPDATE patient SET lastName=?, firstName=?, birthDate=?, " +
                "bloodType=?, address=?, primaryPhone=?, emergencyContact=?, patientStatus=?" +
                " WHERE patientID=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, p.getLastName());
            pstmt.setString(2, p.getFirstName());
            pstmt.setDate(3, p.getBirthDate());
            pstmt.setString(4, p.getBloodType().getLabel());
            pstmt.setString(5, p.getAddress());
            pstmt.setInt(6, p.getPrimaryPhone());
            pstmt.setString(7, p.getEmergencyContact());
            pstmt.setInt(8, p.getPatientStatus());
            pstmt.setInt(9, p.getPatientID());

            pstmt.executeUpdate();
        }
    }

    // DELETE
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM patient WHERE patientID = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
