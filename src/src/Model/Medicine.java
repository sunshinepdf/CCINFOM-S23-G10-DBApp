package Model;

public class Medicine {

    public enum Status {
        AVAILABLE("available"), DISCONTINUED("discontinued"), RECALLED("batch recalled");

        private final String label;

        Status(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public static Medicine.Status fromLabel(String value) {
            for (Medicine.Status status : Medicine.Status.values()) {
                if (status.getLabel().equalsIgnoreCase(value)) {
                    return status;
                }
            }
            return null;
        }
    }

    private int medicineID;
    private String medicineName;
    private String medicineDesc;
    private String dosageForm;
    private String strength;
    private String batchNumber;
    private Status medicineStatus;

    public Medicine (int medicineID, String medicineName, String medicineDesc,
                     String dosageForm, String strength, String batchNumber, Status medicineStatusID) {
        this.medicineID = medicineID;
        this.medicineName = medicineName;
        this.medicineDesc = medicineDesc;
        this.dosageForm = dosageForm;
        this.strength = strength;
        this.batchNumber = batchNumber;
        this.medicineStatus = medicineStatusID;
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

    public String getMedicineDesc() {
        return medicineDesc;
    }

    public void setMedicineDesc(String medicineDesc) {
        this.medicineDesc = medicineDesc;
    }

    public String getDosageForm() {
        return dosageForm;
    }

    public void setDosageForm(String dosageForm) {
        this.dosageForm = dosageForm;
    }

    public String getStrength() {
        return strength;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public Status getMedicineStatus() {
        return medicineStatus;

    }

    public void setMedicineStatus(Status medicineStatus) {
        this.medicineStatus = medicineStatus;
    }
}
