package co.ehealth.e_health;

public class Places {

    public Places(String address, String location, String phone) {
        this.address = address;
        this.location = location;
        this.phone = phone;
    }

    private String address;
    private String location;
    private String phone;


    public Places() {


    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


}
