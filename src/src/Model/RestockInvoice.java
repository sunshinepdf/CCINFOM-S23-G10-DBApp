package Model;

import java.math.BigDecimal;
import java.sql.Date;

public class RestockInvoice {
    private int invoiceID;
    private int supplierID;
    private String purchaseOrderID;
    private Date deliveryDate;
    private int receivedBy;
    private BigDecimal totalOrderCost;
    private Status deliveryStatus;

    public RestockInvoice(int invoiceID, int supplierID, String purchaseOrderID, 
                         Date deliveryDate, int receivedBy, BigDecimal totalOrderCost, 
                         Status deliveryStatus) {
        this.invoiceID = invoiceID;
        this.supplierID = supplierID;
        this.purchaseOrderID = purchaseOrderID;
        this.deliveryDate = deliveryDate;
        this.receivedBy = receivedBy;
        this.totalOrderCost = totalOrderCost;
        this.deliveryStatus = deliveryStatus;
    }

    public int getInvoiceID() { 
        return invoiceID; 
    }
    public void setInvoiceID(int invoiceID) { 
        this.invoiceID = invoiceID; 
    }

    public int getSupplierID() { 
        return supplierID; 
    }
    public void setSupplierID(int supplierID) { 
        this.supplierID = supplierID; 
    }

    public String getPurchaseOrderID() { 
        return purchaseOrderID; 
    }
    public void setPurchaseOrderID(String purchaseOrderID) { 
        this.purchaseOrderID = purchaseOrderID; 
    }

    public Date getDeliveryDate() { 
        return deliveryDate; 
    }
    public void setDeliveryDate(Date deliveryDate) { 
        this.deliveryDate = deliveryDate; 
    }

    public int getReceivedBy() { 
        return receivedBy; 
    }
    public void setReceivedBy(int receivedBy) { 
        this.receivedBy = receivedBy; 
    }

    public BigDecimal getTotalOrderCost() { 
        return totalOrderCost; 
    }
    public void setTotalOrderCost(BigDecimal totalOrderCost) { 
        this.totalOrderCost = totalOrderCost; 
    }

    public Status getDeliveryStatus() { 
        return deliveryStatus; 
    }
    public void setDeliveryStatus(Status deliveryStatus) { 
        this.deliveryStatus = deliveryStatus;
    }
}