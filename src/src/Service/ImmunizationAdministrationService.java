package Service;

import Model.ImmunizationAdministration;
import Model.ImmunizationAdministrationCRUD;
import java.sql.SQLException;
import java.util.List;

public class ImmunizationAdministrationService {
    private final ImmunizationAdministrationCRUD dao = new ImmunizationAdministrationCRUD();

    public ServiceResult<List<ImmunizationAdministration>> listAll() {
        try {
            List<ImmunizationAdministration> list = dao.readAll();
            return ServiceResult.ok(list);
        } catch (SQLException e) {
            return ServiceResult.fail(SqlErrorMapper.normalize(e));
        }
    }

    public ServiceResult<Void> create(ImmunizationAdministration item) {
        try {
            dao.create(item);
            return ServiceResult.ok(null);
        } catch (SQLException e) {
            return ServiceResult.fail(SqlErrorMapper.normalize(e));
        }
    }

    public ServiceResult<Void> update(ImmunizationAdministration item) {
        try {
            dao.update(item);
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
}
