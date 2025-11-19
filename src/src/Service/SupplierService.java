package Service;

import Model.SupplierCRUD;
import Model.Supplier;
import Model.Status;
import Model.StatusDAO;
import Model.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class SupplierService {
    private final SupplierCRUD dao;

    public SupplierService() {
        this.dao = new SupplierCRUD();
    }

    public ServiceResult<List<Supplier>> listAll() {
        try {
            List<Supplier> list = dao.readAll();
            return ServiceResult.ok(list);
        } catch (SQLException e) {
            return ServiceResult.fail(SqlErrorMapper.normalize(e));
        }
    }

    public ServiceResult<Void> createSupplier(Supplier s) {
        try {
            if (s.getSupplierStatus() == null || s.getSupplierStatus().getStatusID() <= 0) {
                Status resolved = resolveStatusByName(s.getSupplierStatus() != null ? s.getSupplierStatus().getStatusName() : null);
                if (resolved != null) s.setSupplierStatus(resolved);
            }
            dao.create(s);
            return ServiceResult.ok(null);
        } catch (SQLException e) {
            return ServiceResult.fail(SqlErrorMapper.normalize(e));
        }
    }

    public ServiceResult<Void> updateSupplier(Supplier s) {
        try {
            if (s.getSupplierStatus() == null || s.getSupplierStatus().getStatusID() <= 0) {
                Status resolved = resolveStatusByName(s.getSupplierStatus() != null ? s.getSupplierStatus().getStatusName() : null);
                if (resolved != null) s.setSupplierStatus(resolved);
            }
            dao.update(s);
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

    public ServiceResult<Supplier> getById(int id) {
        try {
            Supplier s = dao.getSupplierById(id);
            return ServiceResult.ok(s);
        } catch (SQLException e) {
            return ServiceResult.fail(SqlErrorMapper.normalize(e));
        }
    }

    private Status resolveStatusByName(String name) throws SQLException {
        if (name == null) return null;
        try (Connection conn = DBConnection.connectDB()) {
            List<Status> list = StatusDAO.getStatusByCategory(conn, "SupplierStatus");
            for (Status st : list) {
                if (st.getStatusName().equalsIgnoreCase(name)) return st;
            }
        }
        return null;
    }
}
