package Model;

public class StatusCategory {
    private int statusCategoryID;
    private String categoryName;

    public StatusCategory(int statusCategoryID) {}

    public StatusCategory(int statusCategoryID, String categoryName) {
        this.statusCategoryID = statusCategoryID;
        this.categoryName = categoryName;
    }

    public int getStatusCategoryID() {
        return statusCategoryID;
    }
    public void setStatusCategoryID(int statusCategoryID) {
        this.statusCategoryID = statusCategoryID;
    }

    public String getCategoryName() {
        return categoryName;
    }
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
