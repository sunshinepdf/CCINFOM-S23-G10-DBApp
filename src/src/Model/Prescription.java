package Model;

import java.sql.Date;

public class Prescription {

    private int receiptID;
    private int patientID;
    private int consultationID;
    private int medicineID;
    private int hWorkerID;
    private Date distributionDate;
    private int qtyDistributed;
    private boolean isValidPrescription;
    private boolean inventoryUpdated;
    private int prescriptionStatusID;

    public Prescription(int patientID, int consultationID, int medicineID, int hWorkerID,
                        Date distributionDate, int qtyDistributed, boolean isValidPrescription,
                        boolean inventoryUpdated, int prescriptionStatusID) {
        this.patientID = patientID;
        this.consultationID = consultationID;
        this.medicineID = medicineID;
        this.hWorkerID = hWorkerID;
        this.distributionDate = distributionDate;
        this.qtyDistributed = qtyDistributed;
        this.isValidPrescription = isValidPrescription;
        this.inventoryUpdated = inventoryUpdated;
        this.prescriptionStatusID = prescriptionStatusID;
    }

    // Getters and setters
    public int getReceiptID() { 
        return receiptID;
    }
    public void setReceiptID(int receiptID) { 
        this.receiptID = receiptID; 
    }

    public int getPatientID() { 
        return patientID; 
    }
    public void setPatientID(int patientID) { 
        this.patientID = patientID; 
    }

    public int getConsultationID() { 
        return consultationID; 
    }
    public void setConsultationID(int consultationID) { 
        this.consultationID = consultationID; 
    }

    public int getMedicineID() { 
        return medicineID; 
    }
    public void setMedicineID(int medicineID) { 
        this.medicineID = medicineID; 
    }

    public int getHWorkerID() { 
        return hWorkerID; 
    }
    public void setHWorkerID(int hWorkerID) { 
        this.hWorkerID = hWorkerID; 
    }

    public Date getDistributionDate() { 
        return distributionDate; 
    }
    public void setDistributionDate(Date distributionDate) { 
        this.distributionDate = distributionDate; 
    }

    public int getQtyDistributed() { 
        return qtyDistributed; 
    }
    public void setQtyDistributed(int qtyDistributed) { 
        this.qtyDistributed = qtyDistributed; 
    }

    public boolean isValidPrescription() { 
        return isValidPrescription; 
    }
    public void setValidPrescription(boolean validPrescription) { 
        isValidPrescription = validPrescription; 
    }

    public boolean isInventoryUpdated() { 
        return inventoryUpdated; 
    }
    public void setInventoryUpdated(boolean inventoryUpdated) { 
        this.inventoryUpdated = inventoryUpdated; 
    }

    public int getPrescriptionStatusID() { 
        return prescriptionStatusID; 
    }
    public void setPrescriptionStatusID(int prescriptionStatusID) { 
        this.prescriptionStatusID = prescriptionStatusID; 
    }
}
