package Service;

import Model.Consultation;
import Model.ConsultationCRUD;
import java.sql.SQLException;
import java.util.List;

public class ConsultationService {
    private final ConsultationCRUD dao = new ConsultationCRUD();

    public ServiceResult<List<Consultation>> listAll() {
        try {
            List<Consultation> list = dao.readAll();
            return ServiceResult.ok(list);
        } catch (SQLException e) {
            return ServiceResult.fail(SqlErrorMapper.normalize(e));
        }
    }

    public ServiceResult<Void> create(Consultation c) {
        try {
            dao.create(c);
            return ServiceResult.ok(null);
        } catch (SQLException e) {
            return ServiceResult.fail(SqlErrorMapper.normalize(e));
        }
    }

    public ServiceResult<Void> update(Consultation c) {
        try {
            dao.update(c);
            return ServiceResult.ok(null);
        } catch (SQLException e) {
            return ServiceResult.fail(SqlErrorMapper.normalize(e));
        }
    }

    public ServiceResult<Void> archive(int id) {
        try {
            dao.archive(id);
            return ServiceResult.ok(null);
        } catch (SQLException e) {
            return ServiceResult.fail(SqlErrorMapper.normalize(e));
        }
    }
}
