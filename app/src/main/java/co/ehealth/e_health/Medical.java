package co.ehealth.e_health;

public class Medical {


    public Medical(String image, String added, String icon, String status) {
        this.image = image;
        this.added = added;
        this.icon = icon;
        this.status = status;
    }

    private String image;
    private String added;
    private String icon;
    private String status;


    public Medical() {

    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAdded() {
        return added;
    }

    public void setAdded(String added) {
        this.added = added;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
