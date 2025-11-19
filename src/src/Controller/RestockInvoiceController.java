package Controller;

import Service.RestockInvoiceService;
import Service.ServiceResult;
import View.RestockInvoicePanel;
import Model.RestockInvoice;
import java.util.function.Consumer;

public class RestockInvoiceController extends BaseController {
    private final RestockInvoicePanel view;
    private final RestockInvoiceService service;

    public RestockInvoiceController(RestockInvoicePanel view, RestockInvoiceService service) {
        this.view = view;
        this.service = service;
    }

    public void loadAll() {
        view.showLoading(true);
        executeInBackground(
                () -> service.listAll(),
                (ServiceResult<java.util.List<RestockInvoice>> res) -> {
                    view.showLoading(false);
                    if (!res.isSuccess()) { view.showError("Error loading invoices: " + res.getError()); return; }
                    view.showInvoices(res.getData());
                },
                thr -> { view.showLoading(false); view.showError("Unexpected error: " + thr.getMessage()); },
                null,
                null
        );
    }

    public void create(RestockInvoice invoice) {
        view.showLoading(true);
        executeInBackground(
                () -> service.create(invoice),
                (ServiceResult<Void> res) -> {
                    view.showLoading(false);
                    if (!res.isSuccess()) { view.showError("Error creating invoice: " + res.getError()); return; }
                    view.showInfo("Invoice created");
                    loadAll();
                },
                thr -> { view.showLoading(false); view.showError("Unexpected error: " + thr.getMessage()); },
                null,
                null
        );
    }

    public void update(RestockInvoice invoice) {
        view.showLoading(true);
        executeInBackground(
                () -> service.update(invoice),
                (ServiceResult<Void> res) -> {
                    view.showLoading(false);
                    if (!res.isSuccess()) { view.showError("Error updating invoice: " + res.getError()); return; }
                    view.showInfo("Invoice updated");
                    loadAll();
                },
                thr -> { view.showLoading(false); view.showError("Unexpected error: " + thr.getMessage()); },
                null,
                null
        );
    }

    public void softDelete(int id) {
        view.showLoading(true);
        executeInBackground(
                () -> service.softDelete(id),
                (ServiceResult<Void> res) -> {
                    view.showLoading(false);
                    if (!res.isSuccess()) { view.showError("Error deleting invoice: " + res.getError()); return; }
                    view.showInfo("Invoice deleted");
                    loadAll();
                },
                thr -> { view.showLoading(false); view.showError("Unexpected error: " + thr.getMessage()); },
                null,
                null
        );
    }

    public void fetchById(int id, Consumer<RestockInvoice> cb) {
        executeInBackground(
                () -> service.getById(id),
                (ServiceResult<RestockInvoice> res) -> {
                    if (res.isSuccess()) cb.accept(res.getData());
                    else view.showError(res.getError());
                },
                thr -> view.showError(thr.getMessage()),
                () -> view.showLoading(true),
                () -> view.showLoading(false)
        );
    }
}
