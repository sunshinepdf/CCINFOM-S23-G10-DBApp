package Model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientCRUD {

    private Connection conn = DBConnection.getConnection();
    StatusDAO statusDAO = new StatusDAO(conn);

    //create
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
            pstmt.setInt(9, p.getPatientStatus().getStatusID());

            pstmt.executeUpdate();
        }
    }

    //read all
    public List<Patient> readAll() throws SQLException{
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patient";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int statusID = rs.getInt("statusID");
                Status status = statusDAO.getStatusByID(statusID);

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
                        status
                );
                patients.add(p);
            }
        }
        return patients;
    }

    //update
    public void update(Patient p) throws SQLException {
        String sql = "UPDATE patient SET lastName=?, firstName=?, birthDate=?, gender=?, " +
                "bloodType=?, address=?, primaryPhone=?, emergencyContact=?, patientStatus=?" +
                " WHERE patientID=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, p.getLastName());
            pstmt.setString(2, p.getFirstName());
            pstmt.setDate(3, p.getBirthDate());
            pstmt.setString(4, p.getGender().getLabel());
            pstmt.setString(5, p.getBloodType().getLabel());
            pstmt.setString(6, p.getAddress());
            pstmt.setInt(7, p.getPrimaryPhone());
            pstmt.setString(8, p.getEmergencyContact());
            pstmt.setInt(9, p.getPatientStatus().getStatusID());
            pstmt.setInt(10, p.getPatientID());

            pstmt.executeUpdate();
        }
    }

    //delete
    public void delete(int id) throws SQLException {
        String sql = "{CALL sp_delete_patient(?)}";

        try (CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
