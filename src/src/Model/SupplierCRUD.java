package Model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierCRUD {

    // create
    public void create(Supplier supplier) throws SQLException {
        String sql = "INSERT INTO supplier(supplierName, address, contactDetails, supplierType, deliveryLeadTime, transactionDetails, statusID) " +
                "VALUES(?,?,?,?,?,?,?)";

        try (Connection conn = DBConnection.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, supplier.getSupplierName());
            pstmt.setString(2, supplier.getAddress());
            pstmt.setString(3, supplier.getContactDetails());
            pstmt.setString(4, supplier.getSupplierType());
            pstmt.setInt(5, supplier.getDeliveryLeadTime());
            pstmt.setString(6, supplier.getTransactionDetails());
            
            int statusID = convertStatusToID(supplier.getSupplierStatus());
            pstmt.setInt(7, statusID);

            pstmt.executeUpdate();
        }
    }

    // read all
    public List<Supplier> readAll() throws SQLException {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT s.*, st.statusName FROM supplier s " +
                    "JOIN REF_Status st ON s.statusID = st.statusID " +
                    "WHERE st.statusCategoryID = 5"; 

        try (Connection conn = DBConnection.connectDB();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Supplier supplier = new Supplier(
                    rs.getInt("supplierID"),
                    rs.getString("supplierName"),
                    rs.getString("address"),
                    rs.getString("contactDetails"),
                    rs.getString("supplierType"),
                    rs.getInt("deliveryLeadTime"),
                    rs.getString("transactionDetails"),
                    convertStatusNameToEnum(rs.getString("statusName"))
                );
                suppliers.add(supplier);
            }
        }
        return suppliers;
    }

    // update
    public void update(Supplier supplier) throws SQLException {
        String sql = "UPDATE supplier SET supplierName=?, address=?, contactDetails=?, " +
                "supplierType=?, deliveryLeadTime=?, transactionDetails=?, statusID=? " +
                "WHERE supplierID=?";
        
        try (Connection conn = DBConnection.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, supplier.getSupplierName());
            pstmt.setString(2, supplier.getAddress());
            pstmt.setString(3, supplier.getContactDetails());
            pstmt.setString(4, supplier.getSupplierType());
            pstmt.setInt(5, supplier.getDeliveryLeadTime());
            pstmt.setString(6, supplier.getTransactionDetails());
            
            int statusID = convertStatusToID(supplier.getSupplierStatus());
            pstmt.setInt(7, statusID);
            pstmt.setInt(8, supplier.getSupplierID());

            pstmt.executeUpdate();
        }
    }

    // soft delete
    public void softDelete(int supplierId) throws SQLException {
        String sql = "UPDATE supplier SET statusID = ? WHERE supplierID = ?";
        
        try (Connection conn = DBConnection.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, 12);
            pstmt.setInt(2, supplierId);
            pstmt.executeUpdate();
        }
    }
   
    public void restore(int supplierId) throws SQLException {
        String sql = "UPDATE supplier SET statusID = ? WHERE supplierID = ?";
        
        try (Connection conn = DBConnection.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, 11);
            pstmt.setInt(2, supplierId);
            pstmt.executeUpdate();
        }
    }

    public Supplier getSupplierById(int supplierId) throws SQLException {
        String sql = "SELECT s.*, st.statusName FROM supplier s " +
                    "JOIN REF_Status st ON s.statusID = st.statusID " +
                    "WHERE s.supplierID = ? AND st.statusCategoryID = 5";
        
        try (Connection conn = DBConnection.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, supplierId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Supplier(
                        rs.getInt("supplierID"),
                        rs.getString("supplierName"),
                        rs.getString("address"),
                        rs.getString("contactDetails"),
                        rs.getString("supplierType"),
                        rs.getInt("deliveryLeadTime"),
                        rs.getString("transactionDetails"),
                        convertStatusNameToEnum(rs.getString("statusName"))
                    );
                }
            }
        }
        return null;
    }

    private int convertStatusToID(Supplier.Status status) {
        switch (status) {
            case OPERATIONAL: return 11;
            case CLOSED: return 12;     
            default: return 11; 
        }
    }

    private Supplier.Status convertStatusNameToEnum(String statusName) {
        if ("Operational".equalsIgnoreCase(statusName)) {
            return Supplier.Status.OPERATIONAL;
        } else if ("Closed".equalsIgnoreCase(statusName)) {
            return Supplier.Status.CLOSED;
        } else {
            return Supplier.Status.OPERATIONAL;
        }
    }
}