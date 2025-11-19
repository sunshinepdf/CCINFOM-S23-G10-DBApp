package Controller;

import MedicineInventoryService;
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
                () -> service.listAll(),
                res -> {
                    view.showLoading(false);
                    if (!res.isSuccess()) {
                        view.showError("Error loading medicines: " + res.getError());
                        return;
                    }
                    view.showMedicines(res.getData());
                },
                thr -> { view.showLoading(false); view.showError("Unexpected error loading medicines: " + thr.getMessage()); },
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
