package Model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/BHMS_DB?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "1n1t14lP@55!";

    // checked method for callers that will manage connections per-operation
    public static Connection connectDB() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    // optional backward-compatible wrapper
    public static Connection getConnection() {
        try {
            return connectDB();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
