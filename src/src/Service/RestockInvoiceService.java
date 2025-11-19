package Service;

import Model.RestockInvoice;
import Model.RestockInvoiceCRUD;
import java.sql.SQLException;
import java.util.List;

public class RestockInvoiceService {
    private final RestockInvoiceCRUD dao = new RestockInvoiceCRUD();

    public ServiceResult<List<RestockInvoice>> listAll() {
        try {
            List<RestockInvoice> list = dao.readAll();
            return ServiceResult.ok(list);
        } catch (SQLException e) {
            return ServiceResult.fail(SqlErrorMapper.normalize(e));
        }
    }

    public ServiceResult<Void> create(RestockInvoice item) {
        try {
            dao.create(item);
            return ServiceResult.ok(null);
        } catch (SQLException e) {
            return ServiceResult.fail(SqlErrorMapper.normalize(e));
        }
    }

    public ServiceResult<Void> update(RestockInvoice item) {
        try {
            dao.update(item);
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

    public ServiceResult<RestockInvoice> getById(int id) {
        try {
            RestockInvoice r = dao.getInvoiceById(id);
            return ServiceResult.ok(r);
        } catch (SQLException e) {
            return ServiceResult.fail(SqlErrorMapper.normalize(e));
        }
    }
}
