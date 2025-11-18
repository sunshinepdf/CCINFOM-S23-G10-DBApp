package Model;

import java.sql.Date;

public class ImmunizationAdministration {
    private int immunizationID;
    private int patientID;
    private int medicineID;
    private int hWorkerID;
    private Date administrationDate;
    private String vaccineType;
    private int dosageNumber;
    private Date nextVaccinationDate;
    private String sideEffects;
    private Status immunizationStatus;

    public ImmunizationAdministration(int immunizationID, int patientID, int medicineID,
                                      int hWorkerID, Date administrationDate, String vaccineType,
                                      int dosageNumber, Date nextVaccinationDate,
                                      Status immunizationStatus, String sideEffects) {
        this.immunizationID = immunizationID;
        this.patientID = patientID;
        this.medicineID = medicineID;
        this.hWorkerID = hWorkerID;
        this.administrationDate = administrationDate;
        this.vaccineType = vaccineType;
        this.dosageNumber = dosageNumber;
        this.nextVaccinationDate = nextVaccinationDate;
        this.immunizationStatus = immunizationStatus;
        this.sideEffects = sideEffects;

    }

    public int getImmunizationID() {
        return immunizationID;
    }

    public void setImmunizationID(int immunizationID) {
        this.immunizationID = immunizationID;
    }

    public int getPatientID() {
        return patientID;
    }

    public void setPatientID(int patientID) {
        this.patientID = patientID;
    }

    public int getMedicineID() {
        return medicineID;
    }

    public void setMedicineID(int medicineID) {
        this.medicineID = medicineID;
    }

    public int gethWorkerID() {
        return hWorkerID;
    }

    public void sethWorkerID(int hWorkerID) {
        this.hWorkerID = hWorkerID;
    }

    public Date getAdministrationDate() {
        return administrationDate;
    }

    public void setAdministrationDate(Date administrationDate) {
        this.administrationDate = administrationDate;
    }

    public String getVaccineType() {
        return vaccineType;
    }

    public void setVaccineType(String vaccineType) {
        this.vaccineType = vaccineType;
    }

    public int getDosageNumber() {
        return dosageNumber;
    }

    public void setDosageNumber(int dosageNumber) {
        this.dosageNumber = dosageNumber;
    }

    public Date getNextVaccinationDate() {
        return nextVaccinationDate;
    }

    public void setNextVaccinationDate(Date nextVaccinationDate) {
        this.nextVaccinationDate = nextVaccinationDate;
    }

    public String getSideEffects() {
        return sideEffects;
    }

    public void setSideEffects(String sideEffects) {
        this.sideEffects = sideEffects;
    }

    public Status getImmunizationStatus() {
        return immunizationStatus;
    }

    public void setImmunizationStatus(Status immunizationStatus) {
        this.immunizationStatus = immunizationStatus;
    }
}
