package Model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StatusDAO {
    private Connection conn;

    public StatusDAO(Connection conn) {
        this.conn = conn;
    }

    //get all statuses
    public List<Status> getStatusByCategory (String categoryName) throws SQLException {
        String sql = "SELECT s.statusID, s.statusCategoryID, s.statusName" +
                "FROM REF_Status s" +
                "JOIN REF_StatusCategory c ON s.statusCategoryID = c.statusCategoryID" +
                "WHERE c.categoryName = ?";

        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, categoryName);

        ResultSet rs = ps.executeQuery();

        List<Status> statusList = new ArrayList<>();
        while (rs.next()) {
            Status status = new Status(
                    rs.getInt("statusID"),
                    rs.getInt("statusCategoryID"),
                    rs.getString("statusName")
            );
            statusList.add(status);
        }
        return statusList;
    }

    //convert ID to status name
    public String getStatusName (int id) throws SQLException {
        String sql = "SELECT statusName FROM REF_Status WHERE statusID = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getString("statusName");
        }

        return null;
    }

    public Status getStatusByID(int statusID) throws SQLException {
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
