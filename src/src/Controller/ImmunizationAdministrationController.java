package Controller;

import Service.ImmunizationAdministrationService;
import Service.ServiceResult;
import View.ImmunizationAdministrationPanel;
import Model.ImmunizationAdministration;

public class ImmunizationAdministrationController extends BaseController {
    private final ImmunizationAdministrationPanel view;
    private final ImmunizationAdministrationService service;

    public ImmunizationAdministrationController(ImmunizationAdministrationPanel view, ImmunizationAdministrationService service) {
        this.view = view;
        this.service = service;
    }

    public void loadAll() {
        view.showLoading(true);
        executeInBackground(
                () -> service.listAll(),
                (ServiceResult<java.util.List<ImmunizationAdministration>> res) -> {
                    view.showLoading(false);
                    if (!res.isSuccess()) { view.showError("Error loading records: " + res.getError()); return; }
                    view.showImmunizations(res.getData());
                },
                thr -> { view.showLoading(false); view.showError("Unexpected error: " + thr.getMessage()); },
                null,
                null
        );
    }

    public void create(ImmunizationAdministration item) {
        view.showLoading(true);
        executeInBackground(
                () -> service.create(item),
                (ServiceResult<Void> res) -> {
                    view.showLoading(false);
                    if (!res.isSuccess()) { view.showError("Error adding: " + res.getError()); return; }
                    view.showInfo("Added");
                    loadAll();
                },
                thr -> { view.showLoading(false); view.showError("Unexpected error: " + thr.getMessage()); },
                null,
                null
        );
    }

    public void update(ImmunizationAdministration item) {
        view.showLoading(true);
        executeInBackground(
                () -> service.update(item),
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

    public void delete(int id) {
        view.showLoading(true);
        executeInBackground(
                () -> service.delete(id),
                (ServiceResult<Void> res) -> {
                    view.showLoading(false);
                    if (!res.isSuccess()) { view.showError("Error deleting: " + res.getError()); return; }
                    view.showInfo("Deleted");
                    loadAll();
                },
                thr -> { view.showLoading(false); view.showError("Unexpected error: " + thr.getMessage()); },
                null,
                null
        );
    }
}
