package Model;

import java.sql.Date;

public class MedicineInventory {
    private int inventoryID;
    private int facilityID;
    private int medicineID;
    private int quantityInStock;
    private Status status;

    public MedicineInventory(int inventoryID, int facilityID, int medicineID, int quantityInStock, Status status) {
        this.inventoryID = inventoryID;
        this.facilityID = facilityID;
        this.medicineID = medicineID;
        this.quantityInStock = quantityInStock;
        this.status = status;
    }

    // Add missing getters and setters
    public int getInventoryID() { 
        return inventoryID; 
    }
    
    public int getFacilityID() { 
        return facilityID; 
    }
    
    public int getMedicineID() { 
        return medicineID; 
    }
    
    public int getQuantityInStock() { 
        return quantityInStock; 
    }
    
    public Status getStatus() { 
        return status; 
    }
    
    public void setInventoryID(int inventoryID) {
        this.inventoryID = inventoryID;
    }
    
    public void setFacilityID(int facilityID) {
        this.facilityID = facilityID;
    }
    
    public void setMedicineID(int medicineID) {
        this.medicineID = medicineID;
    }
    
    public void setQuantityInStock(int quantityInStock) {
        this.quantityInStock = quantityInStock;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status {
        AVAILABLE("Available"),
        EXPIRED("Expired"),
        LOW_STOCK("Low Stock"),
        OUT_OF_STOCK("Out of Stock");

        private final String label;

        Status(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public static Status fromLabel(String label) {
            for (Status status : values()) {
                if (status.label.equalsIgnoreCase(label)) {
                    return status;
                }
            }
            return OUT_OF_STOCK;
        }
    }
}