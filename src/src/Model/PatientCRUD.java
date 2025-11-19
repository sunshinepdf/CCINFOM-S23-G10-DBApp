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
                "gender, bloodType, address, primaryPhone, emergencyContact, statusID) " +
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
            int statusID = convertStatusToID(p.getPatientStatus());
            pstmt.setInt(9, statusID);

            pstmt.executeUpdate();
        }
    }

    //read all
    public List<Patient> readAll() throws SQLException{
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT p.*, s.statusName FROM patient p " +
                    "JOIN REF_Status s ON p.statusID = s.statusID" +
                    "WHERE s.statusCategoryID = 3";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Patient p = new Patient(
                    rs.getInt("patientID"),
                    rs.getString("lastName"),
                    rs.getString("firstName"),
                    rs.getDate("birthDate"),
                    Patient.Gender.fromGender(rs.getString("gender")),
                    Patient.BloodType.fromBloodType(rs.getString("bloodType")),
                    rs.getString("address"),
                    rs.getInt("primaryPhone"),
                    rs.getString("emergencyContact"),
                    convertStatusNameToEnum(rs.getString("statusName"))
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
                "WHERE patientID=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, p.getLastName());
            pstmt.setString(2, p.getFirstName());
            pstmt.setDate(3, p.getBirthDate());
            pstmt.setString(4, p.getGender().getLabel());
            pstmt.setString(5, p.getBloodType().getLabel());
            pstmt.setString(6, p.getAddress());
            pstmt.setInt(7, p.getPrimaryPhone());
            pstmt.setString(8, p.getEmergencyContact());
            int statusID = convertStatusToID(p.getPatientStatus());
            pstmt.setInt(9, statusID);
            pstmt.setInt(10, p.getPatientID());

            pstmt.executeUpdate();
        }
    }

    //delete
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM patient WHERE patientID = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private int convertStatusToID(Patient.Status status) {
        switch (status) {
            case ACTIVE: return 6;
            case INACTIVE: return 7; 
            default: return 6; 
        }
    }

    private Patient.Status convertStatusNameToEnum(String statusName) {
        if ("Active".equalsIgnoreCase(statusName)) {
            return Patient.Status.ACTIVE;
        } else if ("Inactive".equalsIgnoreCase(statusName)) {
            return Patient.Status.INACTIVE;
        } else {
            return Patient.Status.ACTIVE;
        }
    }

    public Patient getPatientById(int patientId) throws SQLException {
        String sql = "SELECT p.*, s.statusName FROM patient p " +
                    "JOIN REF_Status s ON p.statusID = s.statusID " +
                    "WHERE p.patientID = ? AND s.statusCategoryID = 3";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, patientId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Patient(
                        rs.getInt("patientID"),
                        rs.getString("lastName"),
                        rs.getString("firstName"),
                        rs.getDate("birthDate"),
                        Patient.Gender.fromGender(rs.getString("gender")),
                        Patient.BloodType.fromBloodType(rs.getString("bloodType")),
                        rs.getString("address"),
                        rs.getInt("primaryPhone"),
                        rs.getString("emergencyContact"),
                        convertStatusNameToEnum(rs.getString("statusName"))
                    );
                }
            }
        }
        return null;
    }
}
