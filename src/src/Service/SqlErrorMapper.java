package Service;

import java.sql.SQLException;

public final class SqlErrorMapper {
    private SqlErrorMapper() {}

    // Simple normalizer: can be extended to map SQLState or vendor codes to friendlier messages
    public static String normalize(SQLException e) {
        if (e == null) return "Unknown database error";
        String state = e.getSQLState();
        if (state != null && state.startsWith("45")) {
            // Example: SQLSTATE '45000' used by SIGNAL in triggers
            return e.getMessage();
        }
        // Fallback to message
        return e.getMessage();
    }
}
