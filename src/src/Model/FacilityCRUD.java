package Model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FacilityCRUD {

    // create
    public void create(Facility facility) throws SQLException {
        String sql = "INSERT INTO facility(facilityName, address, contactNumber, shiftStart, shiftEnd, facilityStatusID) " +
                "VALUES(?,?,?,?,?,?)";

        try (Connection conn = DBConnection.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, facility.getFacilityName());
            pstmt.setString(2, facility.getAddress());
            pstmt.setString(3, facility.getContactNumber());
            pstmt.setTime(4, facility.getShiftStart());
            pstmt.setTime(5, facility.getShiftEnd());
            pstmt.setInt(6, facility.getFacilityStatus().getStatusID());

            pstmt.executeUpdate();
        }
    }

    // read all
    public List<Facility> readAll() throws SQLException {
        List<Facility> facilities = new ArrayList<>();
        String sql = "SELECT f.*, s.statusName FROM facility f " +
                    "JOIN REF_Status s ON f.facilityStatusID = s.statusID " +
                    "WHERE s.statusCategoryID = 1";

        try (Connection conn = DBConnection.connectDB();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int statusID =  rs.getInt("facilityStatusID");
                Status status = StatusDAO.getStatusByID(conn,statusID);

                Facility facility = new Facility(
                    rs.getInt("facilityID"),
                    rs.getString("facilityName"),
                    rs.getString("facilityAddress"),
                    rs.getString("facilityContactNum"),
                    rs.getTime("shiftStart"),
                    rs.getTime("shiftEnd"),
                    status
                );
                facilities.add(facility);
            }
        }
        return facilities;
    }

    // update
    public void update(Facility facility) throws SQLException {
        String sql = "UPDATE facility SET facilityName=?, address=?, contactNumber=?, " +
                "shiftStart=?, shiftEnd=?, facilityStatusID=? " +
                "WHERE facilityID=?";
        
        try (Connection conn = DBConnection.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, facility.getFacilityName());
            pstmt.setString(2, facility.getAddress());
            pstmt.setString(3, facility.getContactNumber());
            pstmt.setTime(4, facility.getShiftStart());
            pstmt.setTime(5, facility.getShiftEnd());
            pstmt.setInt(6, facility.getFacilityStatus().getStatusID());
            pstmt.setInt(7, facility.getFacilityID());

            pstmt.executeUpdate();
        }
    }

    public void softDelete(int facilityId) throws SQLException {
        String sql = "UPDATE facility SET facilityStatusID = ? WHERE facilityID = ?";
        
        try (Connection conn = DBConnection.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, 2); 
            pstmt.setInt(2, facilityId);
            pstmt.executeUpdate();
        }
    }
   
    public void restore(int facilityId) throws SQLException {
        String sql = "UPDATE facility SET facilityStatusID = ? WHERE facilityID = ?";
        
        try (Connection conn = DBConnection.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, 1); 
            pstmt.setInt(2, facilityId);
            pstmt.executeUpdate();
        }
    }

    public Facility getFacilityById(int facilityId) throws SQLException {
        String sql = "SELECT f.*, s.statusName FROM facility f " +
                    "JOIN REF_Status s ON f.facilityStatusID = s.statusID " +
                    "WHERE f.facilityID = ? AND s.statusCategoryID = 1";
        
        try (Connection conn = DBConnection.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, facilityId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int statusID =  rs.getInt("facilityStatusID");
                    Status status = StatusDAO.getStatusByID(conn, statusID);

                    return new Facility(
                        rs.getInt("facilityID"),
                        rs.getString("facilityName"),
                        rs.getString("facilityAddress"),
                        rs.getString("facilityContactNum"),
                        rs.getTime("shiftStart"),
                        rs.getTime("shiftEnd"),
                        status
                    );
                }
            }
        }
        return null;
    }
}