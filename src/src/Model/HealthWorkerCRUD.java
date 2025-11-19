package Model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HealthWorkerCRUD {

    // create
    public void create(HealthWorker hw) throws SQLException {
        String sql = "INSERT INTO worker(facilityID, lastName, firstName, " +
                "position, contactInformation, statusID) " +
                "VALUES(?,?,?,?,?,?)";

        try (Connection conn = DBConnection.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, hw.getFacilityID());
            pstmt.setString(2, hw.getLastName());
            pstmt.setString(3, hw.getFirstName());
            pstmt.setString(4, hw.getPosition());
            pstmt.setString(5, hw.getContactInformation());
            pstmt.setInt(6, hw.getWorkerStatus().getStatusID());

            pstmt.executeUpdate();
        }
    }

    // read all
    public List<HealthWorker> readAll() throws SQLException {
        List<HealthWorker> workers = new ArrayList<>();
        String sql = "SELECT hw.*, s.statusName FROM worker hw " +
                    "JOIN REF_Status s ON hw.statusID = s.statusID " +
                    "WHERE s.statusCategoryID = 2";

        try (Connection conn = DBConnection.connectDB();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int statusID = rs.getInt("workerStatus");
                Status status = StatusDAO.getStatusByID(conn, statusID);

                HealthWorker hw = new HealthWorker(
                    rs.getInt("workerID"),
                    rs.getInt("facilityID"),
                    rs.getString("lastName"),
                    rs.getString("firstName"),
                    rs.getString("position"),
                    rs.getString("contactInformation"),
                    status
                );
                workers.add(hw);
            }
        }
        return workers;
    }

    // update
    public void update(HealthWorker hw) throws SQLException {
        String sql = "UPDATE worker SET facilityID=?, lastName=?, firstName=?, " +
                "position=?, contactInformation=?, statusID=? " +
                "WHERE workerID=?";
        
        try (Connection conn = DBConnection.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, hw.getFacilityID());
            pstmt.setString(2, hw.getLastName());
            pstmt.setString(3, hw.getFirstName());
            pstmt.setString(4, hw.getPosition()); 
            pstmt.setString(5, hw.getContactInformation());
            pstmt.setInt(6, hw.getWorkerStatus().getStatusID());
            pstmt.setInt(7, hw.getWorkerID());

            pstmt.executeUpdate();
        }
    }

    // soft delete
    public void softDelete(int workerId) throws SQLException {
        String sql = "UPDATE worker SET statusID = ? WHERE workerID = ?";
        
        try (Connection conn = DBConnection.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, 5); 
            pstmt.setInt(2, workerId);
            pstmt.executeUpdate();
        }
    }

    public void restore(int workerId) throws SQLException {
        String sql = "UPDATE worker SET statusID = ? WHERE workerID = ?";
        
        try (Connection conn = DBConnection.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, 4);
            pstmt.setInt(2, workerId);
            pstmt.executeUpdate();
        }
    }

    // get id
    public HealthWorker getHealthWorkerById(int workerId) throws SQLException {
        String sql = "SELECT hw.*, s.statusName FROM worker hw " +
                    "JOIN REF_Status s ON hw.statusID = s.statusID " +
                    "WHERE hw.workerID = ? AND s.statusCategoryID = 2";
        
        try (Connection conn = DBConnection.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, workerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int statusID = rs.getInt("workerStatus");
                    Status status = StatusDAO.getStatusByID(conn, statusID);

                    return new HealthWorker(
                        rs.getInt("workerID"),
                        rs.getInt("facilityID"),
                        rs.getString("lastName"),
                        rs.getString("firstName"),
                        rs.getString("position"), 
                        rs.getString("contactInformation"),
                        status
                    );
                }
            }
        }
        return null;
    }

    public List<HealthWorker> getHealthWorkersByFacility(int facilityId) throws SQLException {
        List<HealthWorker> workers = new ArrayList<>();
        String sql = "SELECT hw.*, s.statusName FROM worker hw " +
                    "JOIN REF_Status s ON hw.statusID = s.statusID " +
                    "WHERE hw.facilityID = ? AND s.statusCategoryID = 2";
        
        try (Connection conn = DBConnection.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, facilityId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int statusID = rs.getInt("workerStatus");
                    Status status = StatusDAO.getStatusByID(conn, statusID);

                    HealthWorker hw = new HealthWorker(
                        rs.getInt("workerID"),
                        rs.getInt("facilityID"),
                        rs.getString("lastName"),
                        rs.getString("firstName"),
                        rs.getString("position"),
                        rs.getString("contactInformation"),
                        status
                    );
                    workers.add(hw);
                }
            }
        }
        return workers;
    }
}