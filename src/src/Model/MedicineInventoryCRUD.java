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
            pstmt.setInt(9, statusID);

            pstmt.executeUpdate();
        }
    }

    //read all
    public List<MedicineInventory> readAll() throws SQLException{
        List<MedicineInventory> medicines = new ArrayList<>();
        String sql = "SELECT * FROM medicine_inventory";

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
        String sql = "UPDATE medicines SET medicine_name=?, medicine_type=?, description=?, " +
                "quantity_in_stock=?, expiry_date=?, status=? " +
                "WHERE medicine_id=?";
        
        //[EDIT]: Refactored the connection portion to prevent long-live connection leaks
        try (Connection conn = DBConnection.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, m.getFacilityID());
            pstmt.setInt(2, m.getMedicineID());
            pstmt.setInt(3, m.getQuantityInStock());
            int statusID = convertStatusToID(m.getInventoryStatusID());
            pstmt.setInt(9, statusID);
            pstmt.setInt(7, m.getInventoryID());

            pstmt.executeUpdate();
        }
    }

    //delete
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM medicines WHERE medicine_id = ?";

         //[EDIT]: Refactored the connection portion to prevent long-live connection leaks
        try (Connection conn = DBConnection.connectDB();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
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