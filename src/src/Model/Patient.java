package Model;

import java.sql.Date;

public class Patient {

    public enum Gender {
        MALE("Male"), FEMALE("Female");

        private final String label;

        Gender(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public static Gender fromGender (String value) {
            for (Gender g : Gender.values()) {
                if (g.getLabel().equalsIgnoreCase(value)) {
                    return g;
                }
            }
            return null;
        }
    }

    public enum BloodType {
        O_POS("O+"), O_NEG("O-"),
        A_POS("A+"), A_NEG("A-"),
        B_POS("B+"), B_NEG("B-"),
        AB_POS("AB+"), AB_NEG("AB-");

        private final String label;

        BloodType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public static BloodType fromBloodType(String value) {
            for (BloodType b : BloodType.values()) {
                if (b.getLabel().equalsIgnoreCase(value)) {
                    return b;
                }
            }
            return null;
        }
    }

    public enum Status {
        ACTIVE("Active"), INACTIVE("Inactive");

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

    private int patientID;
    private String lastName;
    private String firstName;
    private Date birthDate;
    private Gender gender;
    private BloodType bloodType;
    private String address;
    private int primaryPhone;
    private String emergencyContact;
    private Status patientStatus;

    public Patient(int patientID, String lastName, String firstName, Date birthDate, Gender gender,
                   BloodType bloodType, String address, int primaryPhone, String emergencyContact,
                   Status patientStatus) {
        this.patientID = patientID;
        this.lastName = lastName;
        this.firstName = firstName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.bloodType = bloodType;
        this.address = address;
        this.primaryPhone = primaryPhone;
        this.emergencyContact = emergencyContact;
        this.patientStatus = patientStatus;
    }

    public int getPatientID() {
        return patientID;
    }
    public void setPatientID(int patientID) {
        this.patientID = patientID;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public Date getBirthDate() {
        return birthDate;
    }
    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Gender getGender() {
        return gender;
    }
    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public BloodType getBloodType() {
        return bloodType;
    }
    public void setBloodType(BloodType bloodType) {
        this.bloodType = bloodType;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public int getPrimaryPhone() {
        return primaryPhone;
    }
    public void setPrimaryPhone(int primaryPhone) {
        this.primaryPhone = primaryPhone;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }
    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public Status getPatientStatus() {
        return patientStatus;
    }
    public void setPatientStatus(Status patientStatus) {
        this.patientStatus = patientStatus;
    }
}
