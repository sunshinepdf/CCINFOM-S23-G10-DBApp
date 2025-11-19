package View;

import Model.RestockInvoice;
import Model.RestockInvoiceCRUD;
import Model.Status;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Date;
import java.util.List;

public class RestockInvoicePanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    public RestockInvoicePanel() {
        initializePanel();
        loadInvoices();
    }

    private void initializePanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JPanel header = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Restock Invoices", SwingConstants.LEFT);
        title.setFont(new Font("Arial", Font.BOLD, 22));

        JPanel buttons = new JPanel(new FlowLayout());
        JButton addBtn = new JButton("Add Invoice");
        JButton editBtn = new JButton("Edit Invoice");
        JButton delBtn = new JButton("Close Invoice");
        JButton refreshBtn = new JButton("Refresh");

        buttons.add(addBtn); buttons.add(editBtn); buttons.add(delBtn); buttons.add(refreshBtn);
        header.add(title, BorderLayout.WEST); header.add(buttons, BorderLayout.EAST);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(20);
        JButton searchBtn = new JButton("Search");
        JButton clearBtn = new JButton("Clear");
        searchPanel.add(new JLabel("Search by PO ID:"));
        searchPanel.add(searchField); searchPanel.add(searchBtn); searchPanel.add(clearBtn);

        String[] cols = {"InvoiceID","SupplierID","PO ID","Delivery Date","Received By","Total Cost","Delivery Status"};
        tableModel = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r,int c){return false;} };
        table = new JTable(tableModel);
        setColumnWidths();

        add(header, BorderLayout.NORTH);
        add(searchPanel, BorderLayout.BEFORE_FIRST_LINE);
        add(new JScrollPane(table), BorderLayout.CENTER);

        refreshBtn.addActionListener(e -> loadInvoices());
        addBtn.addActionListener(e -> showAddDialog());
        editBtn.addActionListener(e -> editSelected());
        delBtn.addActionListener(e -> closeSelected());
        searchBtn.addActionListener(e -> performSearch());
        clearBtn.addActionListener(e -> clearSearch());
    }

    private void setColumnWidths() {
        TableColumn c;
        int[] widths = {80, 100, 200, 120, 100, 120, 140};
        for (int i = 0; i < widths.length; i++) { c = table.getColumnModel().getColumn(i); c.setPreferredWidth(widths[i]); }
    }

    public void loadInvoices() {
        try {
            RestockInvoiceCRUD crud = new RestockInvoiceCRUD();
            List<RestockInvoice> list = crud.readAll();
            tableModel.setRowCount(0);
            if (list == null || list.isEmpty()) {
                ErrorDialog.showInfo("No invoices returned");
                return;
            }
            for (RestockInvoice inv : list) {
                tableModel.addRow(new Object[]{
                        inv.getInvoiceID(),
                        inv.getSupplierID(),
                        inv.getPurchaseOrderID(),
                        inv.getDeliveryDate(),
                        inv.getReceivedBy(),
                        inv.getTotalOrderCost(),
                        inv.getDeliveryStatus() != null ? inv.getDeliveryStatus().getLabel() : ""
                });
            }
        } catch (SQLException e) {
            ErrorDialog.showError("Error loading invoices: " + e.getMessage());
        }
    }

    private void showAddDialog() {
        JTextField supplierField = new JTextField();
        JTextField poField = new JTextField();
        JTextField dateField = new JTextField(java.time.LocalDate.now().toString());
        JTextField receivedField = new JTextField();
        JTextField totalField = new JTextField("0.00");
        JTextField statusField = new JTextField("1");

        JPanel p = new JPanel(new GridLayout(0,2,5,5));
        p.add(new JLabel("Supplier ID:")); p.add(supplierField);
        p.add(new JLabel("Purchase Order ID:")); p.add(poField);
        p.add(new JLabel("Delivery Date (YYYY-MM-DD):")); p.add(dateField);
        p.add(new JLabel("Received By (Worker ID):")); p.add(receivedField);
        p.add(new JLabel("Total Order Cost:")); p.add(totalField);
        p.add(new JLabel("Delivery Status ID:")); p.add(statusField);

        int res = JOptionPane.showConfirmDialog(this, p, "Add Invoice", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                int supplierID = Integer.parseInt(supplierField.getText().trim());
                String po = poField.getText().trim();
                Date delivery = Date.valueOf(dateField.getText().trim());
                int receivedBy = Integer.parseInt(receivedField.getText().trim());
                BigDecimal total = new BigDecimal(totalField.getText().trim());
                int statusID = Integer.parseInt(statusField.getText().trim());

                RestockInvoice inv = new RestockInvoice(0, supplierID, po, delivery, receivedBy, total, new Status(statusID,0,"") );
                RestockInvoiceCRUD crud = new RestockInvoiceCRUD();
                crud.create(inv);
                loadInvoices();
                ErrorDialog.showInfo("Invoice created");
            } catch (Exception e) { ErrorDialog.showError("Error creating invoice: " + e.getMessage()); }
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select an invoice to edit"); return; }
        int invoiceID = (int) tableModel.getValueAt(row,0);
        try {
            RestockInvoiceCRUD crud = new RestockInvoiceCRUD();
            RestockInvoice inv = crud.getInvoiceById(invoiceID);
            if (inv == null) { ErrorDialog.showError("Invoice not found"); return; }

            JTextField supplierField = new JTextField(String.valueOf(inv.getSupplierID()));
            JTextField poField = new JTextField(inv.getPurchaseOrderID());
            JTextField dateField = new JTextField(inv.getDeliveryDate().toString());
            JTextField receivedField = new JTextField(String.valueOf(inv.getReceivedBy()));
            JTextField totalField = new JTextField(inv.getTotalOrderCost().toString());
            JTextField statusField = new JTextField(String.valueOf(inv.getDeliveryStatus() != null ? inv.getDeliveryStatus().getStatusID() : 1));

            JPanel p = new JPanel(new GridLayout(0,2,5,5));
            p.add(new JLabel("Supplier ID:")); p.add(supplierField);
            p.add(new JLabel("Purchase Order ID:")); p.add(poField);
            p.add(new JLabel("Delivery Date (YYYY-MM-DD):")); p.add(dateField);
            p.add(new JLabel("Received By (Worker ID):")); p.add(receivedField);
            p.add(new JLabel("Total Order Cost:")); p.add(totalField);
            p.add(new JLabel("Delivery Status ID:")); p.add(statusField);

            int res = JOptionPane.showConfirmDialog(this, p, "Edit Invoice", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                try {
                    inv.setSupplierID(Integer.parseInt(supplierField.getText().trim()));
                    inv.setPurchaseOrderID(poField.getText().trim());
                    inv.setDeliveryDate(Date.valueOf(dateField.getText().trim()));
                    inv.setReceivedBy(Integer.parseInt(receivedField.getText().trim()));
                    inv.setTotalOrderCost(new BigDecimal(totalField.getText().trim()));
                    inv.setDeliveryStatus(new Status(Integer.parseInt(statusField.getText().trim()),0,"") );
                    crud.update(inv);
                    loadInvoices();
                    ErrorDialog.showInfo("Invoice updated");
                } catch (Exception ex) { ErrorDialog.showError("Error updating invoice: " + ex.getMessage()); }
            }

        } catch (SQLException e) { ErrorDialog.showError("Error loading invoice: " + e.getMessage()); }
    }

    private void closeSelected() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this, "Select an invoice to close"); return; }
        int invoiceID = (int) tableModel.getValueAt(row,0);
        int confirm = JOptionPane.showConfirmDialog(this, "Close invoice " + invoiceID + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                RestockInvoiceCRUD crud = new RestockInvoiceCRUD();
                crud.softDelete(invoiceID);
                loadInvoices();
                ErrorDialog.showInfo("Invoice closed");
            } catch (SQLException e) { ErrorDialog.showError("Error closing invoice: " + e.getMessage()); }
        }
    }

    private void performSearch() {
        String q = searchField.getText().trim();
        if (q.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter purchase order ID to search"); return; }
        for (int i=0;i<tableModel.getRowCount();i++) {
            if (tableModel.getValueAt(i,2).toString().equalsIgnoreCase(q)) {
                table.setRowSelectionInterval(i,i);
                table.scrollRectToVisible(table.getCellRect(i,0,true));
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "No matching invoice");
    }

    private void clearSearch() { searchField.setText(""); table.clearSelection(); loadInvoices(); }

    // View helper methods used by controllers
    public void showLoading(boolean loading) { setCursor(loading ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor()); }
    public void showError(String msg) { ErrorDialog.showError(msg); }
    public void showInfo(String msg) { ErrorDialog.showInfo(msg); }

    public void showInvoices(java.util.List<Model.RestockInvoice> list) {
        tableModel.setRowCount(0);
        if (list == null || list.isEmpty()) { ErrorDialog.showInfo("No invoices returned"); return; }
        for (Model.RestockInvoice inv : list) {
            tableModel.addRow(new Object[]{ inv.getInvoiceID(), inv.getSupplierID(), inv.getPurchaseOrderID(), inv.getDeliveryDate(), inv.getReceivedBy(), inv.getTotalOrderCost(), inv.getDeliveryStatus() != null ? inv.getDeliveryStatus().getLabel() : "" });
        }
    }
}
