package Controller;

import Service.HealthWorkerService;
import View.HealthWorkerPanel;
import Model.HealthWorker;

import java.util.function.Consumer;

public class HealthWorkerController extends BaseController {
    private final HealthWorkerPanel view;
    private final HealthWorkerService service;

    public HealthWorkerController(HealthWorkerPanel view, HealthWorkerService service) {
        this.view = view;
        this.service = service;
    }

    public void loadWorkers() {
        executeInBackground(
                () -> service.listAll(),
                res -> {
                    if (res.isSuccess()) view.showWorkers(res.getData());
                    else view.showError(res.getError());
                },
                t -> view.showError(t.getMessage()),
                () -> view.showLoading(true),
                () -> view.showLoading(false)
        );
    }

    public void addWorker(HealthWorker w) {
        executeInBackground(
                () -> service.createWorker(w),
                res -> {
                    if (res.isSuccess()) {
                        view.showInfo("Health worker added");
                        loadWorkers();
                    } else view.showError(res.getError());
                },
                t -> view.showError(t.getMessage()),
                () -> view.showLoading(true),
                () -> view.showLoading(false)
        );
    }

    public void updateWorker(HealthWorker w) {
        executeInBackground(
                () -> service.updateWorker(w),
                res -> {
                    if (res.isSuccess()) {
                        view.showInfo("Health worker updated");
                        loadWorkers();
                    } else view.showError(res.getError());
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
                        view.showInfo("Health worker deactivated");
                        loadWorkers();
                    } else view.showError(res.getError());
                },
                t -> view.showError(t.getMessage()),
                () -> view.showLoading(true),
                () -> view.showLoading(false)
        );
    }

    public void fetchById(int id, Consumer<HealthWorker> cb) {
        executeInBackground(
                () -> service.getById(id),
                res -> {
                    if (res.isSuccess()) cb.accept(res.getData());
                    else view.showError(res.getError());
                },
                t -> view.showError(t.getMessage()),
                () -> view.showLoading(true),
                () -> view.showLoading(false)
        );
    }
}
