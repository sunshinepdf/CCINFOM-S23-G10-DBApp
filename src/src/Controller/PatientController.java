package Controller;

import Service.PatientService;
import View.PatientPanel;

public class PatientController extends BaseController {
    private final PatientPanel view;
    private final PatientService service;

    public PatientController(PatientPanel view, PatientService service) {
        this.view = view;
        this.service = service;
    }

    public void loadPatients() {
        view.showLoading(true);
        executeInBackground(
                () -> service.listAll(),
                res -> {
                    view.showLoading(false);
                    if (!res.isSuccess()) {
                        view.showError("Error loading patients: " + res.getError());
                        return;
                    }
                    view.showPatients(res.getData());
                },
                thr -> { view.showLoading(false); view.showError("Unexpected error loading patients: " + thr.getMessage()); },
                null,
                null
        );
    }

    public void addPatient(Model.Patient p) {
        view.showLoading(true);
        executeInBackground(
                () -> service.createPatient(p),
                res -> {
                    view.showLoading(false);
                    if (!res.isSuccess()) {
                        view.showError("Error adding patient: " + res.getError());
                        return;
                    }
                    view.showInfo("Patient added successfully!");
                    loadPatients();
                },
                thr -> { view.showLoading(false); view.showError("Unexpected error adding patient: " + thr.getMessage()); },
                null,
                null
        );
    }

    public void deletePatient(int id) {
        view.showLoading(true);
        executeInBackground(
                () -> service.deletePatient(id),
                res -> {
                    view.showLoading(false);
                    if (!res.isSuccess()) {
                        view.showError("Error deleting patient: " + res.getError());
                        return;
                    }
                    view.showInfo("Patient deleted successfully");
                    loadPatients();
                },
                thr -> { view.showLoading(false); view.showError("Unexpected error deleting patient: " + thr.getMessage()); },
                null,
                null
        );
    }
}
