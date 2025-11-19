package Model;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportDAO {
    private Connection conn;

    public ReportDAO(Connection conn) {
        this.conn = conn;
    }

    private List<Map<String, Object>> getReportFromView(String viewName) throws SQLException {
        String sql = "SELECT * FROM" + viewName;
        try(PreparedStatement ps = conn.prepareStatement(sql);
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

    //consultation summary reports
    public List<Map<String, Object>> getConsultationSummaryWeek() throws SQLException {
        return getReportFromView("ConsultationSummary_Week");
    }

    public List<Map<String, Object>> getConsultationSummaryMonth() throws SQLException {
        return getReportFromView("ConsultationSummary_Month");
    }

    public List<Map<String, Object>> getConsultationSummaryYear() throws SQLException {
        return getReportFromView("ConsultationSummary_Year");
    }

    //immunization impact reports
    public List<Map<String, Object>> getImmunizationImpactWeek() throws SQLException {
        return getReportFromView("ImmunizationImpact_Week");
    }

    public List<Map<String, Object>> getImmunizationImpactMonth() throws SQLException {
        return getReportFromView("ImmunizationImpact_Month");
    }

    public List<Map<String, Object>> getImmunizationImpactYear() throws SQLException {
        return getReportFromView("ImmunizationImpact_Year");
    }

    //medicine inventory and utilization report
    public List<Map<String, Object>> getMedicineInventoryUtilization() throws SQLException {
        return getReportFromView("MedicineInventoryUtilization");
    }

    //disease monitoring reports
    public List<Map<String, Object>> getDiseaseCaseMonitoringWeek() throws SQLException {
        return getReportFromView("DiseaseCaseMonitoring_Week");
    }

    public List<Map<String, Object>> getDiseaseCaseMonitoringMonth() throws SQLException {
        return getReportFromView("DiseaseCaseMonitoring_Month");
    }

    public List<Map<String, Object>> getDiseaseCaseMonitoringYear() throws SQLException {
        return getReportFromView("DiseaseCaseMonitoring_Year");
    }
}
