package Model;


public class HealthWorker {

    private int workerID;
    private int facilityID;
    private String lastName;
    private String firstName;
    private String position;  
    private String contactInformation;
    private Status workerStatus;

    public HealthWorker(int workerID, int facilityID, String lastName, String firstName, 
                       String position, String contactInformation, Status workerStatus) {
        this.workerID = workerID;
        this.facilityID = facilityID;
        this.lastName = lastName;
        this.firstName = firstName;
        this.position = position;
        this.contactInformation = contactInformation;
        this.workerStatus = workerStatus;
    }

    public int getWorkerID() { 
        return workerID; 
    }
    public void setWorkerID(int workerID) { 
        this.workerID = workerID; 
    }

    public int getFacilityID() { 
        return facilityID; 
    }
    public void setFacilityID(int facilityID) { 
        this.facilityID = facilityID; 
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

    public String getPosition() { 
        return position; 
    }
    public void setPosition(String position) { 
        this.position = position; 
    }

    public String getContactInformation() { 
        return contactInformation; 
    }
    public void setContactInformation(String contactInformation) { 
        this.contactInformation = contactInformation; 
    }

    public Status getWorkerStatus() { 
        return workerStatus; 
    }
    public void setWorkerStatus(Status workerStatus) { 
        this.workerStatus = workerStatus; 
    }

    public String getWorkerStatusLabel() {
        return workerStatus != null ? workerStatus.getStatusName() : "Unknown";
    }
    
}