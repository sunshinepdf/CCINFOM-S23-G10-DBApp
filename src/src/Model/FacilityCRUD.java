package Model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FacilityCRUD {
    private Connection conn = DBConnection.getConnection();
    private StatusDAO statusDAO = new StatusDAO(conn);

    // create
    public void create(Facility facility) throws SQLException {
        String sql = "INSERT INTO facility(facilityName, address, contactNumber, shiftStart, shiftEnd, statusID) " +
                "VALUES(?,?,?,?,?,?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, facility.getFacilityName());
            pstmt.setString(2, facility.getAddress());
            pstmt.setString(3, facility.getContactNumber());
            pstmt.setTime(4, facility.getShiftStart());
            pstmt.setTime(5, facility.getShiftEnd());
            
            int statusID = convertStatusToID(facility.getFacilityStatus());
            pstmt.setInt(6, statusID);

            pstmt.executeUpdate();
        }
    }

    // read all
    public List<Facility> readAll() throws SQLException {
        List<Facility> facilities = new ArrayList<>();
        String sql = "SELECT f.*, s.statusName FROM facility f " +
                    "JOIN REF_Status s ON f.statusID = s.statusID " +
                    "WHERE s.statusCategoryID = 1";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Facility facility = new Facility(
                    rs.getInt("facilityID"),
                    rs.getString("facilityName"),
                    rs.getString("address"),
                    rs.getString("contactNumber"),
                    rs.getTime("shiftStart"),
                    rs.getTime("shiftEnd"),
                    convertStatusNameToEnum(rs.getString("statusName"))
                );
                facilities.add(facility);
            }
        }
        return facilities;
    }

    // update
    public void update(Facility facility) throws SQLException {
        String sql = "UPDATE facility SET facilityName=?, address=?, contactNumber=?, " +
                "shiftStart=?, shiftEnd=?, statusID=? " +
                "WHERE facilityID=?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, facility.getFacilityName());
            pstmt.setString(2, facility.getAddress());
            pstmt.setString(3, facility.getContactNumber());
            pstmt.setTime(4, facility.getShiftStart());
            pstmt.setTime(5, facility.getShiftEnd());
            
            int statusID = convertStatusToID(facility.getFacilityStatus());
            pstmt.setInt(6, statusID);
            pstmt.setInt(7, facility.getFacilityID());

            pstmt.executeUpdate();
        }
    }

    public void softDelete(int facilityId) throws SQLException {
        String sql = "UPDATE facility SET statusID = ? WHERE facilityID = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, 2); 
            pstmt.setInt(2, facilityId);
            pstmt.executeUpdate();
        }
    }
   
    public void restore(int facilityId) throws SQLException {
        String sql = "UPDATE facility SET statusID = ? WHERE facilityID = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, 1); 
            pstmt.setInt(2, facilityId);
            pstmt.executeUpdate();
        }
    }

    public Facility getFacilityById(int facilityId) throws SQLException {
        String sql = "SELECT f.*, s.statusName FROM facility f " +
                    "JOIN REF_Status s ON f.statusID = s.statusID " +
                    "WHERE f.facilityID = ? AND s.statusCategoryID = 1";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, facilityId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Facility(
                        rs.getInt("facilityID"),
                        rs.getString("facilityName"),
                        rs.getString("address"),
                        rs.getString("contactNumber"),
                        rs.getTime("shiftStart"),
                        rs.getTime("shiftEnd"),
                        convertStatusNameToEnum(rs.getString("statusName"))
                    );
                }
            }
        }
        return null;
    }

    private int convertStatusToID(Facility.Status status) {
        switch (status) {
            case OPERATIONAL: return 1;       
            case CLOSED: return 2;           
            case UNDER_MAINTENANCE: return 3; 
            default: return 1; 
        }
    }

    private Facility.Status convertStatusNameToEnum(String statusName) {
        if ("Operational".equalsIgnoreCase(statusName)) {
            return Facility.Status.OPERATIONAL;
        } else if ("Closed".equalsIgnoreCase(statusName)) {
            return Facility.Status.CLOSED;
        } else if ("Under Maintenance".equalsIgnoreCase(statusName)) {
            return Facility.Status.UNDER_MAINTENANCE;
        } else {
            return Facility.Status.OPERATIONAL;
        }
    }
}