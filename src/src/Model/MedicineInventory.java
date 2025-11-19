package Model;

import java.sql.Date;

public class MedicineInventory {

    public enum MedicineType {
        MEDICINE("Medicine"), VACCINE("Vaccine");

        private final String label;

        MedicineType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public static MedicineType fromLabel(String value) {
            for (MedicineType type : MedicineType.values()) {
                if (type.getLabel().equalsIgnoreCase(value)) {
                    return type;
                }
            }
            return null;
        }
    }

    public enum Status {
        AVAILABLE("Available"), EXPIRED("Expired"), OUT_OF_STOCK("Out of Stock");

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

    private int medicineID;
    private String medicineName;
    private MedicineType medicineType;
    private String description;
    private int quantityInStock;
    private Date expiryDate;
    private Status status;

    public MedicineInventory(int medicineID, String medicineName, MedicineType medicineType, String description,
                   int quantityInStock, Date expiryDate, Status status) {
        this.medicineID = medicineID;
        this.medicineName = medicineName;
        this.medicineType = medicineType;
        this.description = description;
        this.quantityInStock = quantityInStock;
        this.expiryDate = expiryDate;
        this.status = status;
    }

    public MedicineInventory(int medicineID, String medicineName, String medicineType, String description,
                   int quantityInStock, Date expiryDate, String status) {
        this.medicineID = medicineID;
        this.medicineName = medicineName;
        this.medicineType = MedicineType.fromLabel(medicineType);
        this.description = description;
        this.quantityInStock = quantityInStock;
        this.expiryDate = expiryDate;
        this.status = Status.fromLabel(status);
    }

    public int getMedicineID() {
        return medicineID;
    }
    public void setMedicineID(int medicineID) {
        this.medicineID = medicineID;
    }

    public String getMedicineName() {
        return medicineName;
    }
    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public MedicineType getMedicineType() {
        return medicineType;
    }
    public void setMedicineType(MedicineType medicineType) {
        this.medicineType = medicineType;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }
    public void setQuantityInStock(int quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }
    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }
}