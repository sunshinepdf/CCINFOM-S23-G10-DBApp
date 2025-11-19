package Controller;

import Service.SupplierService;
import Service.ServiceResult;
import Model.Supplier;
import java.util.List;
import java.util.function.Consumer;

public class SupplierController extends BaseController {
    private final SupplierService service;

    public SupplierController() {
        this.service = new SupplierService();
    }

    public void listSuppliers(Consumer<ServiceResult<List<Supplier>>> onDone, Consumer<Throwable> onError) {
        executeInBackground(() -> service.listAll(), onDone, onError, null, null);
    }

    public void createSupplier(Supplier s, Runnable onSuccess, Consumer<Throwable> onError) {
        executeInBackground(() -> service.createSupplier(s), res -> {
            if (res.isSuccess()) {
                if (onSuccess != null) onSuccess.run();
            }
        }, onError, null, null);
    }

    public void updateSupplier(Supplier s, Runnable onSuccess, Consumer<Throwable> onError) {
        executeInBackground(() -> service.updateSupplier(s), res -> {
            if (res.isSuccess()) {
                if (onSuccess != null) onSuccess.run();
            }
        }, onError, null, null);
    }

    public void deleteSupplier(int id, Runnable onSuccess, Consumer<Throwable> onError) {
        executeInBackground(() -> service.softDelete(id), res -> {
            if (res.isSuccess()) {
                if (onSuccess != null) onSuccess.run();
            }
        }, onError, null, null);
    }

    public void getSupplierById(int id, Consumer<ServiceResult<Supplier>> onDone, Consumer<Throwable> onError) {
        executeInBackground(() -> service.getById(id), onDone, onError, null, null);
    }
}
