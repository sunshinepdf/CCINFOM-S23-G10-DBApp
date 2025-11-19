package Model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicineInventoryCRUD {

    // [EDITS] Edited to use per-method connections (open/close within each method)

    private String calculateStatus(int quantity, java.sql.Date expiryDate) {
        java.util.Date currentUtilDate = new java.util.Date();
        java.sql.Date currentDate = new java.sql.Date(currentUtilDate.getTime());

        if (expiryDate.before(currentDate)) {
            return "Expired";
        }
        
        if (quantity == 0) {
            return "Out of Stock";
        } else if (quantity <= 10) {
            return "Low Stock";
        } else {
            return "Available";
        }
    }

    //create
    public void create(MedicineInventory m) throws SQLException{
        String sql = "INSERT INTO medicine_inventory(facilityID, medicineID, quantityInStock, " +
                "inventoryStatusID) " +
                "VALUES(?,?,?,?)";
        //[EDIT]: Refactored the connection portion to prevent long-live connection leaks
        try (Connection conn = DBConnection.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, m.getFacilityID());
            pstmt.setInt(2, m.getMedicineID());
            pstmt.setInt(3, m.getQuantityInStock());
            int statusID = convertStatusToID(m.getInventoryStatusID());
            pstmt.setInt(4, statusID);

            pstmt.executeUpdate();
        }
    }

    //read all
    public List<MedicineInventory> readAll() throws SQLException{
        List<MedicineInventory> medicines = new ArrayList<>();
        String sql = "SELECT mi.*, rs.statusName FROM medicine_inventory mi LEFT JOIN REF_Status rs ON mi.inventoryStatusID = rs.statusID";

         //[EDIT]: Refactored the connection portion to prevent long-live connection leaks
        try (Connection conn = DBConnection.connectDB();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                MedicineInventory m = new MedicineInventory(
                    rs.getInt("inventoryID"),
                    rs.getInt("facilityID"),
                    rs.getInt("medicineID"),
                    rs.getInt("quantityInStock"),
                    convertStatusNameToEnum(rs.getString("statusName"))
                );
                medicines.add(m);
            }
        }
        return medicines;
    }

    //update
    public void update(MedicineInventory m) throws SQLException {
        String sql = "UPDATE medicine_inventory SET facilityID = ?, medicineID = ?, quantityInStock = ?, inventoryStatusID = ? WHERE inventoryID = ?";

        try (Connection conn = DBConnection.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, m.getFacilityID());
            pstmt.setInt(2, m.getMedicineID());
            pstmt.setInt(3, m.getQuantityInStock());
            int statusID = convertStatusToID(m.getInventoryStatusID());
            pstmt.setInt(4, statusID);
            pstmt.setInt(5, m.getInventoryID());

            pstmt.executeUpdate();
        }
    }

    //delete
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM medicine_inventory WHERE inventoryID = ?";

        try (Connection conn = DBConnection.connectDB();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    // Update inventoryStatusID for all inventory rows based on quantity thresholds
    public void updateAllStatuses() throws SQLException {
        String sql = "UPDATE medicine_inventory SET inventoryStatusID = CASE WHEN quantityInStock = 0 THEN 16 WHEN quantityInStock <= 10 THEN 15 ELSE 13 END";
        try (Connection conn = DBConnection.connectDB();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        }
    }

    private int convertStatusToID(MedicineInventory.Status status) {
        switch (status) {
            case AVAILABLE: return 13;
            case EXPIRED: return 14;
            case LOW_STOCK: return 15;
            case OUT_OF_STOCK: return 16;
            default: return 16;
        }
    }

    private MedicineInventory.Status convertStatusNameToEnum(String statusName) {
        if ("Available".equalsIgnoreCase(statusName)) {
            return MedicineInventory.Status.AVAILABLE;
        } else if ("Expired".equalsIgnoreCase(statusName)) {
            return MedicineInventory.Status.EXPIRED;
        } else if ("Low Stock".equalsIgnoreCase(statusName)) {
            return MedicineInventory.Status.LOW_STOCK;
        } else if ("Out of Stock".equalsIgnoreCase(statusName)) {
            return MedicineInventory.Status.OUT_OF_STOCK;
        }else {
            return MedicineInventory.Status.OUT_OF_STOCK;
        }
    }
}