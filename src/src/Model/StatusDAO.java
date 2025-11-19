package Model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class StatusDAO {

    private StatusDAO() { /* utility class */ }

    public static List<Status> getStatusByCategory(Connection conn, String categoryName) throws SQLException {
        String sql = "SELECT s.statusID, s.statusCategoryID, s.statusName " +
                     "FROM REF_Status s " +
                     "JOIN REF_StatusCategory c ON s.statusCategoryID = c.statusCategoryID " +
                     "WHERE c.categoryName = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, categoryName);
            try (ResultSet rs = ps.executeQuery()) {
                List<Status> statusList = new ArrayList<>();
                while (rs.next()) {
                    statusList.add(new Status(
                        rs.getInt("statusID"),
                        rs.getInt("statusCategoryID"),
                        rs.getString("statusName")
                    ));
                }
                return statusList;
            }
        }
    }

    public static String getStatusName(Connection conn, int id) throws SQLException {
        String sql = "SELECT statusName FROM REF_Status WHERE statusID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("statusName") : null;
            }
        }
    }

    public static Status getStatusByID(Connection conn, int statusID) throws SQLException {
        String sql = "SELECT statusID, statusCategoryID, statusName FROM REF_Status WHERE statusID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, statusID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Status(
                        rs.getInt("statusID"),
                        rs.getInt("statusCategoryID"),
                        rs.getString("statusName")
                    );
                }
            }
        }
        return null;
    }
}