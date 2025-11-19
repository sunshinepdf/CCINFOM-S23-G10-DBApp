package Controller;

import Service.PrescriptionService;
import Service.ServiceResult;
import View.PrescriptionPanel;
import Model.Prescription;

public class PrescriptionController extends BaseController {
    private final PrescriptionPanel view;
    private final PrescriptionService service;

    public PrescriptionController(PrescriptionPanel view, PrescriptionService service) {
        this.view = view;
        this.service = service;
    }

    public void loadPrescriptions() {
        view.showLoading(true);
        executeInBackground(
                () -> service.listAll(),
                (ServiceResult<java.util.List<Prescription>> res) -> {
                    view.showLoading(false);
                    if (!res.isSuccess()) { view.showError("Error loading prescriptions: " + res.getError()); return; }
                    view.showPrescriptions(res.getData());
                },
                thr -> { view.showLoading(false); view.showError("Unexpected error loading prescriptions: " + thr.getMessage()); },
                null,
                null
        );
    }

    public void addPrescription(Prescription p) {
        view.showLoading(true);
        executeInBackground(
                () -> service.addPrescription(p),
                (ServiceResult<Void> res) -> {
                    view.showLoading(false);
                    if (!res.isSuccess()) { view.showError("Error adding prescription: " + res.getError()); return; }
                    view.showInfo("Prescription added");
                    loadPrescriptions();
                },
                thr -> { view.showLoading(false); view.showError("Unexpected error adding prescription: " + thr.getMessage()); },
                null,
                null
        );
    }

    public void updatePrescription(Prescription p) {
        view.showLoading(true);
        executeInBackground(
                () -> service.updatePrescription(p),
                (ServiceResult<Void> res) -> {
                    view.showLoading(false);
                    if (!res.isSuccess()) { view.showError("Error updating prescription: " + res.getError()); return; }
                    view.showInfo("Prescription updated");
                    loadPrescriptions();
                },
                thr -> { view.showLoading(false); view.showError("Unexpected error updating prescription: " + thr.getMessage()); },
                null,
                null
        );
    }

    public void archivePrescription(int id) {
        view.showLoading(true);
        executeInBackground(
                () -> service.archivePrescription(id),
                (ServiceResult<Void> res) -> {
                    view.showLoading(false);
                    if (!res.isSuccess()) { view.showError("Error archiving prescription: " + res.getError()); return; }
                    view.showInfo("Prescription archived");
                    loadPrescriptions();
                },
                thr -> { view.showLoading(false); view.showError("Unexpected error archiving prescription: " + thr.getMessage()); },
                null,
                null
        );
    }
}
