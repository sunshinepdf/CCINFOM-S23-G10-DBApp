package Controller;

import Service.MedicineInventoryService;
import Model.MedicineInventory;
import View.MedicineInventoryPanel;

public class MedicineInventoryController extends BaseController {
    private final MedicineInventoryPanel view;
    private final MedicineInventoryService service;

    public MedicineInventoryController(MedicineInventoryPanel view, MedicineInventoryService service) {
        this.view = view;
        this.service = service;
    }

    public void loadMedicines() {
        view.showLoading(true);
        executeInBackground(
                () -> {
                    try (java.sql.Connection conn = Model.DBConnection.getConnection()) {
                        Model.ViewDAO vdao = new Model.ViewDAO(conn);
                        java.util.List<java.util.Map<String, Object>> rows = vdao.getMedicineInventoryStatus();
                        return Service.ServiceResult.ok(rows);
                    } catch (Exception e) {
                        return Service.ServiceResult.fail(e.getMessage());
                    }
                },
                res -> {
                    view.showLoading(false);
                    if (!res.isSuccess() || res.getData() == null || res.getData().isEmpty()) {
                        // fallback to service.listAll()
                        executeInBackground(
                                () -> service.listAll(),
                                res2 -> {
                                    view.showLoading(false);
                                    if (!res2.isSuccess()) {
                                        view.showError("Error loading medicines: " + res2.getError());
                                        return;
                                    }
                                    view.showMedicines(res2.getData());
                                },
                                thr2 -> { view.showLoading(false); view.showError("Unexpected error loading medicines: " + thr2.getMessage()); },
                                null,
                                null
                        );
                        return;
                    }
                    // success: show view rows
                    view.showMedicineInventoryView(res.getData());
                },
                thr -> {
                    view.showLoading(false);
                    // fallback to service.listAll()
                    executeInBackground(
                            () -> service.listAll(),
                            res2 -> {
                                view.showLoading(false);
                                if (!res2.isSuccess()) {
                                    view.showError("Error loading medicines: " + res2.getError());
                                    return;
                                }
                                view.showMedicines(res2.getData());
                            },
                            thr2 -> { view.showLoading(false); view.showError("Unexpected error loading medicines: " + thr2.getMessage()); },
                            null,
                            null
                    );
                },
                null,
                null
        );
    }

    public void addMedicine(MedicineInventory m) {
        view.showLoading(true);
        executeInBackground(
                () -> service.create(m),
                res -> {
                    view.showLoading(false);
                    if (!res.isSuccess()) {
                        view.showError("Error adding medicine: " + res.getError());
                        return;
                    }
                    view.showInfo("Medicine added successfully");
                    loadMedicines();
                },
                thr -> { view.showLoading(false); view.showError("Unexpected error adding medicine: " + thr.getMessage()); },
                null,
                null
        );
    }

    public void updateMedicine(MedicineInventory m) {
        view.showLoading(true);
        executeInBackground(
                () -> service.update(m),
                res -> {
                    view.showLoading(false);
                    if (!res.isSuccess()) {
                        view.showError("Error updating medicine: " + res.getError());
                        return;
                    }
                    view.showInfo("Medicine updated successfully");
                    loadMedicines();
                },
                thr -> { view.showLoading(false); view.showError("Unexpected error updating medicine: " + thr.getMessage()); },
                null,
                null
        );
    }

    public void deleteMedicine(int id) {
        view.showLoading(true);
        executeInBackground(
                () -> service.delete(id),
                res -> {
                    view.showLoading(false);
                    if (!res.isSuccess()) {
                        view.showError("Error deleting medicine: " + res.getError());
                        return;
                    }
                    view.showInfo("Medicine deleted successfully");
                    loadMedicines();
                },
                thr -> { view.showLoading(false); view.showError("Unexpected error deleting medicine: " + thr.getMessage()); },
                null,
                null
        );
    }

    public void updateAllStatuses() {
        executeInBackground(
                () -> service.updateAllStatuses(),
                res -> {
                    if (!res.isSuccess()) {
                        view.showError("Error updating statuses: " + res.getError());
                    }
                },
                thr -> view.showError("Unexpected error updating statuses: " + thr.getMessage()),
                null,
                null
        );
    }
}