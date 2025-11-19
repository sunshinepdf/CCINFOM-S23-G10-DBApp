package Service;

import Model.HealthWorkerCRUD;
import Model.HealthWorker;
import Model.Status;
import Model.StatusDAO;
import Model.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class HealthWorkerService {
    private final HealthWorkerCRUD dao;

    public HealthWorkerService() {
        this.dao = new HealthWorkerCRUD();
    }

    public ServiceResult<List<HealthWorker>> listAll() {
        try {
            List<HealthWorker> list = dao.readAll();
            return ServiceResult.ok(list);
        } catch (SQLException e) {
            return ServiceResult.fail(SqlErrorMapper.normalize(e));
        }
    }

    public ServiceResult<Void> createWorker(HealthWorker w) {
        try {
            if (w.getWorkerStatus() == null || w.getWorkerStatus().getStatusID() <= 0) {
                Status resolved = resolveStatusByName(w.getWorkerStatus() != null ? w.getWorkerStatus().getStatusName() : null);
                if (resolved != null) w.setWorkerStatus(resolved);
            }
            dao.create(w);
            return ServiceResult.ok(null);
        } catch (SQLException e) {
            return ServiceResult.fail(SqlErrorMapper.normalize(e));
        }
    }

    public ServiceResult<Void> updateWorker(HealthWorker w) {
        try {
            if (w.getWorkerStatus() == null || w.getWorkerStatus().getStatusID() <= 0) {
                Status resolved = resolveStatusByName(w.getWorkerStatus() != null ? w.getWorkerStatus().getStatusName() : null);
                if (resolved != null) w.setWorkerStatus(resolved);
            }
            dao.update(w);
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

    public ServiceResult<HealthWorker> getById(int id) {
        try {
            HealthWorker hw = dao.getHealthWorkerById(id);
            return ServiceResult.ok(hw);
        } catch (SQLException e) {
            return ServiceResult.fail(SqlErrorMapper.normalize(e));
        }
    }

    private Status resolveStatusByName(String name) throws SQLException {
        if (name == null) return null;
        try (Connection conn = DBConnection.connectDB()) {
            List<Status> list = StatusDAO.getStatusByCategory(conn, "HealthWorkerStatus");
            for (Status s : list) {
                if (s.getStatusName().equalsIgnoreCase(name)) return s;
            }
        }
        return null;
    }
}
