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
        String sql = "INSERT INTO medicines(medicine_name, medicine_type, description, " +
                "quantity_in_stock, expiry_date, status) " +
                "VALUES(?,?,?,?,?,?)";
        //[EDIT]: Refactored the connection portion to prevent long-live connection leaks
        try (Connection conn = DBConnection.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, m.getMedicineName());
            pstmt.setString(2, m.getMedicineType().getLabel());
            pstmt.setString(3, m.getDescription());
            pstmt.setInt(4, m.getQuantityInStock());
            pstmt.setDate(5, m.getExpiryDate());
            String calculatedStatus = calculateStatus(m.getQuantityInStock(), m.getExpiryDate());
            pstmt.setString(6, calculatedStatus);

            pstmt.executeUpdate();
        }
    }

    //read all
    public List<MedicineInventory> readAll() throws SQLException{
        List<MedicineInventory> medicines = new ArrayList<>();
        String sql = "SELECT * FROM medicines";

         //[EDIT]: Refactored the connection portion to prevent long-live connection leaks
        try (Connection conn = DBConnection.connectDB();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                MedicineInventory m = new MedicineInventory(
                    rs.getInt("medicine_id"),
                    rs.getString("medicine_name"),
                    MedicineInventory.MedicineType.fromLabel(rs.getString("medicine_type")),
                    rs.getString("description"),
                    rs.getInt("quantity_in_stock"),
                    rs.getDate("expiry_date"),
                    MedicineInventory.Status.fromLabel(rs.getString("status"))
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
            pstmt.setString(1, m.getMedicineName());
            pstmt.setString(2, m.getMedicineType().getLabel());
            pstmt.setString(3, m.getDescription());
            pstmt.setInt(4, m.getQuantityInStock());
            pstmt.setDate(5, m.getExpiryDate());
            String calculatedStatus = calculateStatus(m.getQuantityInStock(), m.getExpiryDate());
            pstmt.setString(6, calculatedStatus);
            pstmt.setInt(7, m.getMedicineID());

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

    public void updateAllStatuses() throws SQLException {
        String sql = "UPDATE medicines SET status = CASE " +
                    "WHEN expiry_date < CURDATE() THEN 'Expired' " +
                    "WHEN quantity_in_stock = 0 THEN 'Out of Stock' " +
                    "WHEN quantity_in_stock <= 10 THEN 'Low Stock' " +
                    "ELSE 'Available' END";
                    
         //[EDIT]: Refactored the connection portion to prevent long-live connection leaks
        try (Connection conn = DBConnection.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.executeUpdate();
        }
    }
}