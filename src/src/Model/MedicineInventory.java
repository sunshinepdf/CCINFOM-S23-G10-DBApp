package Model;

import java.sql.Date;

public class MedicineInventory {

    public enum Status {
        AVAILABLE("Available"), EXPIRED("Expired"), LOW_STOCK("Low Stock"), OUT_OF_STOCK("Out of Stock");

        private final String label;

        Status(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public static Status fromLabel(String value) {
            for (Status status : Status.values()) {
                if (status.getLabel().equalsIgnoreCase(value)) {
                    return status;
                }
            }
            return null;
        }
    }

    private int inventoryID;
    private int facilityID;
    private int medicineID;
    private int quantityInStock;
    private Status inventoryStatusID;

    public MedicineInventory(int inventoryID, int facilityID, int medicineID, int quantityInStock,
                             Status inventoryStatusID) {
        this.inventoryID = inventoryID;
        this.facilityID = facilityID;
        this.medicineID = medicineID;
        this.quantityInStock = quantityInStock;
        this.inventoryStatusID = inventoryStatusID;
    }

    public int getInventoryID() {
        return inventoryID;
    }
    public void setInventoryID(int inventoryID) {
        this.inventoryID = inventoryID;
    }

    public int getFacilityID() {
        return facilityID;
    }
    public void setFacilityID(int facilityID) {
        this.facilityID = facilityID;
    }

    public int getMedicineID() {
        return medicineID;
    }
    public void setMedicineID(int medicineID) {
        this.medicineID = medicineID;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }
    public void setQuantityInStock(int quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    public Status getInventoryStatusID() {
        return inventoryStatusID;
    }
    public void setInventoryStatusID(Status inventoryStatusID) {
        this.inventoryStatusID = inventoryStatusID;
    }
}