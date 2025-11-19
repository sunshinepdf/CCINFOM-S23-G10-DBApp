package Model;

import java.sql.Time;

public class Facility {

    public enum Status {
        OPERATIONAL("Operational"), CLOSED("Closed"), UNDER_MAINTENANCE("Under Maintenance");

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

    private int facilityID;
    private String facilityName;
    private String address;
    private String contactNumber;
    private Time shiftStart;
    private Time shiftEnd;
    private Status facilityStatus;

    public Facility(int facilityID, String facilityName, String address, String contactNumber,
                   Time shiftStart, Time shiftEnd, Status facilityStatus) {
        this.facilityID = facilityID;
        this.facilityName = facilityName;
        this.address = address;
        this.contactNumber = contactNumber;
        this.shiftStart = shiftStart;
        this.shiftEnd = shiftEnd;
        this.facilityStatus = facilityStatus;
    }

    // Getters and Setters
    public int getFacilityID() { 
        return facilityID; 
    }
    public void setFacilityID(int facilityID) { 
        this.facilityID = facilityID; 
    }

    public String getFacilityName() { 
        return facilityName; 
    }
    public void setFacilityName(String facilityName) { 
        this.facilityName = facilityName; 
    }

    public String getAddress() { 
        return address; 
    }
    public void setAddress(String address) { 
        this.address = address; 
    }

    public String getContactNumber() { 
        return contactNumber; 
    }
    public void setContactNumber(String contactNumber) { 
        this.contactNumber = contactNumber; 
    }

    public Time getShiftStart() { 
        return shiftStart; 
    }
    public void setShiftStart(Time shiftStart) { 
        this.shiftStart = shiftStart; 
    }

    public Time getShiftEnd() { 
        return shiftEnd; 
    }
    public void setShiftEnd(Time shiftEnd) { 
        this.shiftEnd = shiftEnd; 
    }

    public Status getFacilityStatus() { 
        return facilityStatus; 
    }
    public void setFacilityStatus(Status facilityStatus) { 
        this.facilityStatus = facilityStatus; 
    }
}