package Service;

import Model.MedicineInventoryCRUD;
import Model.MedicineInventory;
import java.sql.SQLException;
import java.util.List;

public class MedicineInventoryService {
    private final MedicineInventoryCRUD dao;

    public MedicineInventoryService() {
        this.dao = new MedicineInventoryCRUD();
    }

    public ServiceResult<List<MedicineInventory>> listAll() {
        try {
            List<MedicineInventory> list = dao.readAll();
            return ServiceResult.ok(list);
        } catch (SQLException e) {
            return ServiceResult.fail(SqlErrorMapper.normalize(e));
        }
    }

    public ServiceResult<Void> create(MedicineInventory m) {
        try {
            dao.create(m);
            return ServiceResult.ok(null);
        } catch (SQLException e) {
            return ServiceResult.fail(SqlErrorMapper.normalize(e));
        }
    }

    public ServiceResult<Void> update(MedicineInventory m) {
        try {
            dao.update(m);
            return ServiceResult.ok(null);
        } catch (SQLException e) {
            return ServiceResult.fail(SqlErrorMapper.normalize(e));
        }
    }

    public ServiceResult<Void> delete(int id) {
        try {
            dao.delete(id);
            return ServiceResult.ok(null);
        } catch (SQLException e) {
            return ServiceResult.fail(SqlErrorMapper.normalize(e));
        }
    }

    public ServiceResult<Void> update(Model.MedicineInventory m) {
        try {
            dao.update(m);
            return ServiceResult.ok(null);
        } catch (SQLException e) {
            return ServiceResult.fail(SqlErrorMapper.normalize(e));
        }
    }

    public ServiceResult<Void> updateAllStatuses() {
        try {
            dao.updateAllStatuses();
            return ServiceResult.ok(null);
        } catch (SQLException e) {
            return ServiceResult.fail(SqlErrorMapper.normalize(e));
        }
    }
}