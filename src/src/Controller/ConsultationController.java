package Controller;

import Service.ConsultationService;
import Service.ServiceResult;
import View.ConsultationPanel;
import Model.Consultation;

public class ConsultationController extends BaseController {
    private final ConsultationService service;
    private final ConsultationPanel view;

    public ConsultationController(ConsultationPanel view, ConsultationService service) {
        this.view = view;
        this.service = service;
    }

    public void loadAll() {
        view.showLoading(true);
        executeInBackground(
                () -> service.listAll(),
                (ServiceResult<java.util.List<Consultation>> res) -> {
                    view.showLoading(false);
                    if (!res.isSuccess()) { view.showError("Error loading consultations: " + res.getError()); return; }
                    view.showConsultations(res.getData());
                },
                thr -> { view.showLoading(false); view.showError("Unexpected error: " + thr.getMessage()); },
                null,
                null
        );
    }

    public void create(Consultation c) {
        view.showLoading(true);
        executeInBackground(
                () -> service.create(c),
                (ServiceResult<Void> res) -> {
                    view.showLoading(false);
                    if (!res.isSuccess()) { view.showError("Error creating: " + res.getError()); return; }
                    view.showInfo("Created");
                    loadAll();
                },
                thr -> { view.showLoading(false); view.showError("Unexpected error: " + thr.getMessage()); },
                null,
                null
        );
    }

    public void update(Consultation c) {
        view.showLoading(true);
        executeInBackground(
                () -> service.update(c),
                (ServiceResult<Void> res) -> {
                    view.showLoading(false);
                    if (!res.isSuccess()) { view.showError("Error updating: " + res.getError()); return; }
                    view.showInfo("Updated");
                    loadAll();
                },
                thr -> { view.showLoading(false); view.showError("Unexpected error: " + thr.getMessage()); },
                null,
                null
        );
    }

    public void archive(int id) {
        view.showLoading(true);
        executeInBackground(
                () -> service.archive(id),
                (ServiceResult<Void> res) -> {
                    view.showLoading(false);
                    if (!res.isSuccess()) { view.showError("Error archiving: " + res.getError()); return; }
                    view.showInfo("Archived");
                    loadAll();
                },
                thr -> { view.showLoading(false); view.showError("Unexpected error: " + thr.getMessage()); },
                null,
                null
        );
    }
}
