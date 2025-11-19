package Model;

import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class RestockInvoiceCRUD {

    // create
    public void create(RestockInvoice invoice) throws SQLException {
        String sql = "INSERT INTO restock_invoice (SupplierID, PurchaseOrderID, DeliveryDate, ReceivedBy, TotalOrderCost, DeliveryStatus) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, invoice.getSupplierID());
            pstmt.setString(2, invoice.getPurchaseOrderID());
            pstmt.setDate(3, invoice.getDeliveryDate());
            pstmt.setInt(4, invoice.getReceivedBy());
            pstmt.setBigDecimal(5, invoice.getTotalOrderCost());
            pstmt.setInt(6, invoice.getDeliveryStatus().getStatusID());

            pstmt.executeUpdate();
        }
    }

    // read all
    public List<RestockInvoice> readAll() throws SQLException {
        List<RestockInvoice> invoices = new ArrayList<>();
        String sql = "SELECT ri.*, s.statusName, w.hWorkerFirstName, w.hWorkerLastName, " +
                    "sup.supplierName " +
                    "FROM restock_invoice ri " +
                    "JOIN REF_Status s ON ri.DeliveryStatus = s.statusID " +
                    "JOIN worker w ON ri.ReceivedBy = w.hWorkerID " +
                    "JOIN supplier sup ON ri.SupplierID = sup.supplierID " +
                    "WHERE s.statusCategoryID = 10";

        try (Connection conn = DBConnection.connectDB();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Status status = StatusDAO.getStatusByID(conn, rs.getInt("DeliveryStatus"));
                
                RestockInvoice invoice = new RestockInvoice(
                    rs.getInt("InvoiceID"),
                    rs.getInt("SupplierID"),
                    rs.getString("PurchaseOrderID"),
                    rs.getDate("DeliveryDate"),
                    rs.getInt("ReceivedBy"),
                    rs.getBigDecimal("TotalOrderCost"),
                    status
                );
                invoices.add(invoice);
            }
        }
        return invoices;
    }

    // update
    public void update(RestockInvoice invoice) throws SQLException {
        String sql = "UPDATE restock_invoice SET SupplierID=?, PurchaseOrderID=?, DeliveryDate=?, " +
                    "ReceivedBy=?, TotalOrderCost=?, DeliveryStatus=? WHERE InvoiceID=?";

        try (Connection conn = DBConnection.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, invoice.getSupplierID());
            pstmt.setString(2, invoice.getPurchaseOrderID());
            pstmt.setDate(3, invoice.getDeliveryDate());
            pstmt.setInt(4, invoice.getReceivedBy());
            pstmt.setBigDecimal(5, invoice.getTotalOrderCost());
            pstmt.setInt(6, invoice.getDeliveryStatus().getStatusID());
            pstmt.setInt(7, invoice.getInvoiceID());

            pstmt.executeUpdate();
        }
    }

    // get id
    public RestockInvoice getInvoiceById(int invoiceId) throws SQLException {
        String sql = "SELECT ri.*, s.statusName FROM restock_invoice ri " +
                    "JOIN REF_Status s ON ri.DeliveryStatus = s.statusID " +
                    "WHERE ri.InvoiceID = ? AND s.statusCategoryID = 10";

        try (Connection conn = DBConnection.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, invoiceId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Status status = StatusDAO.getStatusByID(conn, rs.getInt("DeliveryStatus"));
                    
                    return new RestockInvoice(
                        rs.getInt("InvoiceID"),
                        rs.getInt("SupplierID"),
                        rs.getString("PurchaseOrderID"),
                        rs.getDate("DeliveryDate"),
                        rs.getInt("ReceivedBy"),
                        rs.getBigDecimal("TotalOrderCost"),
                        status
                    );
                }
            }
        }
        return null;
    }

    // soft delete
    public void softDelete(int invoiceId) throws SQLException {
        String sql = "UPDATE restock_invoice SET DeliveryStatus = ? WHERE InvoiceID = ?";
        
        try (Connection conn = DBConnection.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, 29);
            pstmt.setInt(2, invoiceId);
            pstmt.executeUpdate();
        }
    }

    // Restore (Set status to Pending)
    public void restore(int invoiceId) throws SQLException {
        String sql = "UPDATE restock_invoice SET DeliveryStatus = ? WHERE InvoiceID = ?";
        
        try (Connection conn = DBConnection.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, 30);
            pstmt.setInt(2, invoiceId);
            pstmt.executeUpdate();
        }
    }

    public List<RestockInvoice> getInvoicesBySupplier(int supplierId) throws SQLException {
        List<RestockInvoice> invoices = new ArrayList<>();
        String sql = "SELECT ri.*, s.statusName FROM restock_invoice ri " +
                    "JOIN REF_Status s ON ri.DeliveryStatus = s.statusID " +
                    "WHERE ri.SupplierID = ? AND s.statusCategoryID = 10";

        try (Connection conn = DBConnection.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, supplierId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Status status = StatusDAO.getStatusByID(conn, rs.getInt("DeliveryStatus"));
                    
                    RestockInvoice invoice = new RestockInvoice(
                        rs.getInt("InvoiceID"),
                        rs.getInt("SupplierID"),
                        rs.getString("PurchaseOrderID"),
                        rs.getDate("DeliveryDate"),
                        rs.getInt("ReceivedBy"),
                        rs.getBigDecimal("TotalOrderCost"),
                        status
                    );
                    invoices.add(invoice);
                }
            }
        }
        return invoices;
    }

    public BigDecimal getTotalCostBySupplier(int supplierId) throws SQLException {
        String sql = "SELECT SUM(TotalOrderCost) as total FROM restock_invoice " +
                    "WHERE SupplierID = ? AND DeliveryStatus != 29";

        try (Connection conn = DBConnection.connectDB();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, supplierId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("total");
                }
            }
        }
        return BigDecimal.ZERO;
    }
}