package Service;

import Model.PatientCRUD;
import Model.Patient;
import java.sql.SQLException;
import java.util.List;

import ServiceResult;
import SqlErrorMapper;

public class PatientService {
    private final PatientCRUD dao;

    public PatientService() {
        this.dao = new PatientCRUD();
    }

    // read - return ServiceResult wrapping list or error
    public ServiceResult<List<Patient>> listAll() {
        try {
            List<Patient> list = dao.readAll();
            return ServiceResult.ok(list);
        } catch (SQLException e) {
            return ServiceResult.fail(SqlErrorMapper.normalize(e));
        }
    }

    // create - return ServiceResult<Void>
    public ServiceResult<Void> createPatient(Patient p) {
        try {
            dao.create(p);
            return ServiceResult.ok(null);
        } catch (SQLException e) {
            return ServiceResult.fail(SqlErrorMapper.normalize(e));
        }
    }

    // delete - return ServiceResult<Void>
    public ServiceResult<Void> deletePatient(int id) {
        try {
            dao.delete(id);
            return ServiceResult.ok(null);
        } catch (SQLException e) {
            return ServiceResult.fail(SqlErrorMapper.normalize(e));
        }
    }
}
