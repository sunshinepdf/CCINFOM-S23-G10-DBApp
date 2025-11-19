package Model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConsultationCRUD {

    private final FacilityCRUD facilityCRUD = new FacilityCRUD();

    // CREATE the consultation
    public void create(Consultation consultation) throws SQLException {
        // Validate whether related IDs exist
        if (!recordExists("Patient", "patientID", consultation.getPatientID())) {
            throw new SQLException("Patient ID does not exist!");
        }
        if (!recordExists("HealthWorker", "hWorkerID", consultation.getHWorkerID())) {
            throw new SQLException("Health Worker ID does not exist!");
        }
        if (!recordExists("Facility", "facilityID", consultation.getFacilityID())) {
            throw new SQLException("Facility ID does not exist.!");
        }

        // CHECK whether consultation time is within facility operating hours
        if (!isWithinFacilityHours(consultation.getFacilityID(), consultation.getConsultationTime())) {
            throw new SQLException("Consultation time is outside facility operating hours!");
        }

        // INSERT consultation
        String sql = "INSERT INTO medical_consultation(patientID, hWorkerID, facilityID, consultationDate, " +
                     "consultationTime, symptoms, diagnosis, prescription, statusID) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, consultation.getPatientID());
            pstmt.setInt(2, consultation.getHWorkerID());
            pstmt.setInt(3, consultation.getFacilityID());
            pstmt.setDate(4, consultation.getConsultationDate());
            pstmt.setTime(5, consultation.getConsultationTime());
            pstmt.setString(6, consultation.getSymptoms());
            pstmt.setString(7, consultation.getDiagnosis());
            pstmt.setString(8, consultation.getPrescription());
            pstmt.setInt(9, convertStatusToID(consultation.getConsultationStatus()));
            pstmt.executeUpdate();
        }
    }

    // READ all the consultations
    public List<Consultation> readAll() throws SQLException {
        List<Consultation> consultations = new ArrayList<>();
        String sql = "SELECT c.*, s.statusName FROM medical_consultation c " +
                     "JOIN REF_Status s ON c.consultationStatusID = s.statusID " +
                     "WHERE s.statusCategoryID = 7";

        try (Connection conn = DBConnection.connectDB();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                consultations.add(new Consultation(
                        rs.getInt("consultationID"),
                        rs.getInt("patientID"),
                        rs.getInt("hWorkerID"),
                        rs.getInt("facilityID"),
                        rs.getDate("consultationDate"),
                        rs.getTime("consultationTime"),
                        rs.getString("symptoms"),
                        rs.getString("diagnosis"),
                        rs.getString("prescription"),
                        convertStatusNameToEnum(rs.getString("statusName"))
                ));
            }
        }
        return consultations;
    }

    // READ by ID
    public Consultation getById(int consultationID) throws SQLException {
        String sql = "SELECT c.*, s.statusName FROM medical_consultation c " +
                     "JOIN REF_Status s ON c.consultationStatusID = s.statusID " +
                     "WHERE c.consultationID = ? AND s.statusCategoryID = 7";
        try (Connection conn = DBConnection.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, consultationID);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Consultation(
                            rs.getInt("consultationID"),
                            rs.getInt("patientID"),
                            rs.getInt("hWorkerID"),
                            rs.getInt("facilityID"),
                            rs.getDate("consultationDate"),
                            rs.getTime("consultationTime"),
                            rs.getString("symptoms"),
                            rs.getString("diagnosis"),
                            rs.getString("prescription"),
                            convertStatusNameToEnum(rs.getString("statusName"))
                    );
                }
            }
        }
        return null;
    }
    // UPDATE the consultation
    public void update(Consultation consultation) throws SQLException {
        if (!isWithinFacilityHours(consultation.getFacilityID(), consultation.getConsultationTime())) {
            throw new SQLException("Consultation time is outside facility operating hours.");
        }

        String sql = "UPDATE medical_consultation SET patientID=?, hWorkerID=?, facilityID=?, " +
                     "consultationDate=?, consultationTime=?, symptoms=?, diagnosis=?, " +
                     "prescription=?, consultationStatusID=? WHERE consultationID=?";
        try (Connection conn = DBConnection.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, consultation.getPatientID());
            pstmt.setInt(2, consultation.getHWorkerID());
            pstmt.setInt(3, consultation.getFacilityID());
            pstmt.setDate(4, consultation.getConsultationDate());
            pstmt.setTime(5, consultation.getConsultationTime());
            pstmt.setString(6, consultation.getSymptoms());
            pstmt.setString(7, consultation.getDiagnosis());
            pstmt.setString(8, consultation.getPrescription());
            pstmt.setInt(9, convertStatusToID(consultation.getConsultationStatus()));
            pstmt.setInt(10, consultation.getConsultationID());
            pstmt.executeUpdate();
        }
    }

    // SOFT DELETE (archive)
    public void archive(int consultationID) throws SQLException {
        String sql = "UPDATE medical_consultation SET consultationStatusID = ? WHERE consultationID=?";
        try (Connection conn = DBConnection.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, 3); // Archived
            pstmt.setInt(2, consultationID);
            pstmt.executeUpdate();
        }
    }

    // RESTORE
    public void restore(int consultationID) throws SQLException {
        String sql = "UPDATE medical_consultation SET consultationStatusID = ? WHERE consultationID=?";
        try (Connection conn = DBConnection.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, 2); // Pending
            pstmt.setInt(2, consultationID);
            pstmt.executeUpdate();
        }
    }

    // A helper to check if a record exists
    private boolean recordExists(String table, String column, int id) throws SQLException {
        String sql = "SELECT 1 FROM " + table + " WHERE " + column + " = ?";
        try (Connection conn = DBConnection.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    // A helper to whether check consultation time within facility hours
    private boolean isWithinFacilityHours(int facilityID, Time consultationTime) throws SQLException {
        Facility facility = facilityCRUD.getFacilityById(facilityID);
        if (facility == null) return false;
        return !consultationTime.before(facility.getShiftStart()) &&
               !consultationTime.after(facility.getShiftEnd());
    }

    // Convert enum to statusID
    private int convertStatusToID(Consultation.Status status) {
        if (status == null) return 2;
        switch (status) {
            case PENDING: return 2;
            case COMPLETED: return 1;
            case ARCHIVED: return 3;
            default: return 2;
        }
    }

    // Convert DB statusName to enum
    private Consultation.Status convertStatusNameToEnum(String statusName) {
        if ("Completed".equalsIgnoreCase(statusName)) return Consultation.Status.COMPLETED;
        if ("Pending".equalsIgnoreCase(statusName)) return Consultation.Status.PENDING;
        if ("Archived".equalsIgnoreCase(statusName)) return Consultation.Status.ARCHIVED;
        return Consultation.Status.PENDING;
    }
}
