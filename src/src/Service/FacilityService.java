package Service;

import Model.FacilityCRUD;
import Model.Facility;
import Model.Status;
import Model.StatusDAO;
import Model.DBConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class FacilityService {
    private final FacilityCRUD dao;

    public FacilityService() {
        this.dao = new FacilityCRUD();
    }

    public ServiceResult<List<Facility>> listAll() {
        try {
            List<Facility> list = dao.readAll();
            return ServiceResult.ok(list);
        } catch (SQLException e) {
            return ServiceResult.fail(SqlErrorMapper.normalize(e));
        }
    }

    public ServiceResult<Void> createFacility(Facility f) {
        try {
            // Ensure facility status is resolved to a valid Status with ID
            if (f.getFacilityStatus() == null || f.getFacilityStatus().getStatusID() <= 0) {
                Status resolved = resolveStatusByName(f.getFacilityStatus() != null ? f.getFacilityStatus().getStatusName() : null);
                if (resolved != null) f.setFacilityStatus(resolved);
            }
            dao.create(f);
            return ServiceResult.ok(null);
        } catch (SQLException e) {
            return ServiceResult.fail(SqlErrorMapper.normalize(e));
        }
    }

    public ServiceResult<Void> updateFacility(Facility f) {
        try {
            if (f.getFacilityStatus() == null || f.getFacilityStatus().getStatusID() <= 0) {
                Status resolved = resolveStatusByName(f.getFacilityStatus() != null ? f.getFacilityStatus().getStatusName() : null);
                if (resolved != null) f.setFacilityStatus(resolved);
            }
            dao.update(f);
            return ServiceResult.ok(null);
        } catch (SQLException e) {
            return ServiceResult.fail(SqlErrorMapper.normalize(e));
        }
    }

    public ServiceResult<Void> softDelete(int id) {
        try {
            dao.softDelete(id);
            return ServiceResult.ok(null);
        } catch (SQLException e) {
            return ServiceResult.fail(SqlErrorMapper.normalize(e));
        }
    }

    public ServiceResult<Void> restore(int id) {
        try {
            dao.restore(id);
            return ServiceResult.ok(null);
        } catch (SQLException e) {
            return ServiceResult.fail(SqlErrorMapper.normalize(e));
        }
    }

    public ServiceResult<Facility> getById(int id) {
        try {
            Facility f = dao.getFacilityById(id);
            return ServiceResult.ok(f);
        } catch (SQLException e) {
            return ServiceResult.fail(SqlErrorMapper.normalize(e));
        }
    }

    private Status resolveStatusByName(String name) throws SQLException {
        if (name == null) return null;
        try (Connection conn = DBConnection.connectDB()) {
            List<Status> list = StatusDAO.getStatusByCategory(conn, "FacilityStatus");
            for (Status s : list) {
                if (s.getStatusName().equalsIgnoreCase(name)) return s;
            }
        }
        return null;
    }
}
