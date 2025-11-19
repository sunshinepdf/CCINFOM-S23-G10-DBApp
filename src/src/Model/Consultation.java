package Model;

import java.sql.Date;
import java.sql.Time;

public class Consultation {

    public enum Status {
        COMPLETED("Completed"), PENDING("Pending"), ARCHIVED("Archived");

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

    private int consultationID;
    private int patientID;
    private int hWorkerID;
    private int facilityID;
    private Date consultationDate;
    private Time consultationTime;
    private String symptoms;
    private String diagnosis;
    private String prescription;
    private Status consultationStatus;

    public Consultation(int consultationID, int patientID, int hWorkerID, int facilityID,
                        Date consultationDate, Time consultationTime,
                        String symptoms, String diagnosis, String prescription,
                        Status consultationStatus) {
        this.consultationID = consultationID;
        this.patientID = patientID;
        this.hWorkerID = hWorkerID;
        this.facilityID = facilityID;
        this.consultationDate = consultationDate;
        this.consultationTime = consultationTime;
        this.symptoms = symptoms;
        this.diagnosis = diagnosis;
        this.prescription = prescription;
        this.consultationStatus = consultationStatus;
    }

    // Getters and Setters
    public int getConsultationID() { return consultationID; }
    public void setConsultationID(int consultationID) { this.consultationID = consultationID; }

    public int getPatientID() { return patientID; }
    public void setPatientID(int patientID) { this.patientID = patientID; }

    public int getHWorkerID() { return hWorkerID; }
    public void setHWorkerID(int hWorkerID) { this.hWorkerID = hWorkerID; }

    public int getFacilityID() { return facilityID; }
    public void setFacilityID(int facilityID) { this.facilityID = facilityID; }

    public Date getConsultationDate() { return consultationDate; }
    public void setConsultationDate(Date consultationDate) { this.consultationDate = consultationDate; }

    public Time getConsultationTime() { return consultationTime; }
    public void setConsultationTime(Time consultationTime) { this.consultationTime = consultationTime; }

    public String getSymptoms() { return symptoms; }
    public void setSymptoms(String symptoms) { this.symptoms = symptoms; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public String getPrescription() { return prescription; }
    public void setPrescription(String prescription) { this.prescription = prescription; }

    public Status getConsultationStatus() { return consultationStatus; }
    public void setConsultationStatus(Status consultationStatus) { this.consultationStatus = consultationStatus; }
}
