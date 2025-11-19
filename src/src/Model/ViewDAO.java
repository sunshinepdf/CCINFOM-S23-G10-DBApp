package Model;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewDAO {
    private Connection conn;

    public ViewDAO(Connection conn) {
        this.conn = conn;
    }

    // Generic method to get a report from any view
    private List<Map<String, Object>> getReportFromView(String viewName) throws SQLException {
        String sql = "SELECT * FROM " + viewName;  // Added space after FROM
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            List<Map<String, Object>> rows = new ArrayList<>();
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(meta.getColumnLabel(i), rs.getObject(i));
                }
                rows.add(row);
            }
            return rows;
        }
    }

    // Medicine reports
    public List<Map<String, Object>> getMedicineDistributionHistory() throws SQLException {
        return getReportFromView("medicine_distribution_history");
    }

    public List<Map<String, Object>> getMedicineInventoryStatus() throws SQLException {
        return getReportFromView("medicine_inventory_status");
    }

    // Health worker reports
    public List<Map<String, Object>> getHealthWorkerAssignedPatients() throws SQLException {
        return getReportFromView("healthworker_assigned_patients_view");
    }

    // Facility reports
    public List<Map<String, Object>> getFacilityDetails() throws SQLException {
        return getReportFromView("facilityDetails_view");
    }

    // Patient reports
    public List<Map<String, Object>> getPatientConsultations() throws SQLException {
        return getReportFromView("patientConsultations_view");
    }

    // Immunization reports
    public List<Map<String, Object>> getImmunizationSummary() throws SQLException {
        return getReportFromView("immunization_summary");
    }
}
