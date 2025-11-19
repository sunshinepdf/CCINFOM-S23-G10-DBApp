package Model;

public class Supplier {

    private int supplierID;
    private String supplierName;
    private String address;
    private String contactDetails;
    private String supplierType;
    private int deliveryLeadTime;
    private String transactionDetails;
    private Status supplierStatus;

    public Supplier(int supplierID, String supplierName, String supplierAddress, String supplierContactNum,
                   String supplierType, int deliveryLeadTime, String transactionDetails, Status supplierStatus) {
        this.supplierID = supplierID;
        this.supplierName = supplierName;
        this.address = supplierAddress;
        this.contactDetails = supplierContactNum;
        this.supplierType = supplierType;
        this.deliveryLeadTime = deliveryLeadTime;
        this.transactionDetails = transactionDetails;
        this.supplierStatus = supplierStatus;
    }

    // Getters and Setters
    public int getSupplierID() { 
        return supplierID; 
    }
    public void setSupplierID(int supplierID) { 
        this.supplierID = supplierID; 
    }

    public String getSupplierName() { 
        return supplierName; 
    }
    public void setSupplierName(String supplierName) { 
        this.supplierName = supplierName; 
    }

    public String getAddress() { 
        return address; 
    }
    public void setAddress(String address) { 
        this.address = address; 
    }

    public String getContactDetails() { 
        return contactDetails; 
    }
    public void setContactDetails(String contactDetails) { 
        this.contactDetails = contactDetails; 
    }

    public String getSupplierType() { 
        return supplierType; 
    }
    public void setSupplierType(String supplierType) { 
        this.supplierType = supplierType; 
    }

    public int getDeliveryLeadTime() { 
        return deliveryLeadTime; 
    }
    public void setDeliveryLeadTime(int deliveryLeadTime) { 
        this.deliveryLeadTime = deliveryLeadTime; 
    }

    public String getTransactionDetails() { 
        return transactionDetails; 
    }
    public void setTransactionDetails(String transactionDetails) { 
        this.transactionDetails = transactionDetails; 
    }

    public Status getSupplierStatus() { 
        return supplierStatus; 
    }
    public void setSupplierStatus(Status supplierStatus) { 
        this.supplierStatus = supplierStatus; 
    }
}