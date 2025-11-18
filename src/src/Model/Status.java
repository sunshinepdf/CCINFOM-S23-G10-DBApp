package Model;

public class Status {
    private int statusID;
    private int statusCategoryID;
    private String statusName;

    public Status(int statusID, int statusCategoryID, String statusName) {
        this.statusID = statusID;
        this.statusCategoryID = statusCategoryID;
        this.statusName = statusName;
    }

    public int getStatusID() {
        return statusID;
    }

    public void setStatusID(int statusID) {
        this.statusID = statusID;
    }

    public int getStatusCategoryID() {
        return statusCategoryID;
    }

    public void setStatusCategoryID(int statusCategoryID) {
        this.statusCategoryID = statusCategoryID;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
}
