package Model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicineInventoryCRUD {

    public void updateAllStatuses() throws SQLException {
        String sql = "UPDATE medicine_inventory mi " +
                    "JOIN REF_Status s ON s.statusName = CASE " +
                    "  WHEN mi.quantityInStock = 0 THEN 'Out of Stock' " +
                    "  WHEN mi.quantityInStock <= 10 THEN 'Low Stock' " +
                    "  ELSE 'Available' " +
                    "END " +
                    "JOIN REF_StatusCategory c ON c.categoryName = 'MedicineInventoryStatus' " +
                    "AND s.statusCategoryID = c.statusCategoryID " +
                    "SET mi.inventoryStatusID = s.statusID " +
                    "WHERE mi.inventoryStatusID IS NULL OR mi.inventoryStatusID != s.statusID";
        
        try (Connection conn = DBConnection.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        }
    }

    public void create(MedicineInventory m) throws SQLException{
        String sql = "INSERT INTO medicine_inventory(facilityID, medicineID, quantityInStock, inventoryStatusID) " +
                "VALUES(?,?,?,?)";
        try (Connection conn = DBConnection.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, m.getFacilityID());
            pstmt.setInt(2, m.getMedicineID());
            pstmt.setInt(3, m.getQuantityInStock());
            int statusID = convertStatusToID(m.getStatus());
            pstmt.setInt(4, statusID); 

            pstmt.executeUpdate();
        }
    }

    public List<MedicineInventory> readAll() throws SQLException{
        List<MedicineInventory> medicines = new ArrayList<>();
        String sql = "SELECT mi.*, m.medicineName, m.medicineDesc, m.dosageForm, m.strength, m.batchNumber, " +
                    "s.statusName " +
                    "FROM medicine_inventory mi " +
                    "JOIN medicine m ON mi.medicineID = m.medicineID " +
                    "JOIN REF_Status s ON mi.inventoryStatusID = s.statusID";

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

    public void update(MedicineInventory m) throws SQLException {
        String sql = "UPDATE medicine_inventory SET facilityID=?, medicineID=?, quantityInStock=?, inventoryStatusID=? " +
                "WHERE inventoryID=?";
    
        try (Connection conn = DBConnection.connectDB();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, m.getFacilityID());
            pstmt.setInt(2, m.getMedicineID());
            pstmt.setInt(3, m.getQuantityInStock());
            int statusID = convertStatusToID(m.getStatus());
            pstmt.setInt(4, statusID); 
            pstmt.setInt(5, m.getInventoryID()); 

            pstmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM medicine_inventory WHERE inventoryID = ?";

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
        } else {
            return MedicineInventory.Status.OUT_OF_STOCK;
        }
    }
}