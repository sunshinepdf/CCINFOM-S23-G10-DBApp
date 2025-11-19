package Controller;

import Service.FacilityService;
import View.FacilityPanel;
import Model.Facility;

import java.util.function.Consumer;

public class FacilityController extends BaseController {
    private final FacilityPanel view;
    private final FacilityService service;

    public FacilityController(FacilityPanel view, FacilityService service) {
        this.view = view;
        this.service = service;
    }

    public void loadFacilities() {
        executeInBackground(
                () -> service.listAll(),
                res -> {
                    if (res.isSuccess()) {
                        view.showFacilities(res.getData());
                    } else {
                        view.showError(res.getError());
                    }
                },
                t -> view.showError(t.getMessage()),
                () -> view.showLoading(true),
                () -> view.showLoading(false)
        );
    }

    public void addFacility(Facility f) {
        executeInBackground(
                () -> service.createFacility(f),
                res -> {
                    if (res.isSuccess()) {
                        view.showInfo("Facility added successfully");
                        loadFacilities();
                    } else {
                        view.showError(res.getError());
                    }
                },
                t -> view.showError(t.getMessage()),
                () -> view.showLoading(true),
                () -> view.showLoading(false)
        );
    }

    public void updateFacility(Facility f) {
        executeInBackground(
                () -> service.updateFacility(f),
                res -> {
                    if (res.isSuccess()) {
                        view.showInfo("Facility updated successfully");
                        loadFacilities();
                    } else {
                        view.showError(res.getError());
                    }
                },
                t -> view.showError(t.getMessage()),
                () -> view.showLoading(true),
                () -> view.showLoading(false)
        );
    }

    public void softDelete(int id) {
        executeInBackground(
                () -> service.softDelete(id),
                res -> {
                    if (res.isSuccess()) {
                        view.showInfo("Facility closed successfully");
                        loadFacilities();
                    } else {
                        view.showError(res.getError());
                    }
                },
                t -> view.showError(t.getMessage()),
                () -> view.showLoading(true),
                () -> view.showLoading(false)
        );
    }

    public void fetchFacilityById(int id, Consumer<Facility> callback) {
        executeInBackground(
                () -> service.getById(id),
                res -> {
                    if (res.isSuccess()) {
                        callback.accept(res.getData());
                    } else {
                        view.showError(res.getError());
                    }
                },
                t -> view.showError(t.getMessage()),
                () -> view.showLoading(true),
                () -> view.showLoading(false)
        );
    }
}
