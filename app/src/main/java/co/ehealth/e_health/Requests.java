package co.ehealth.e_health;

public class Requests {

    public Requests(String age, String firstname, String lastname, String gender, String image, String joined, String location, String phone) {
        Age = age;
        Firstname = firstname;
        Lastname = lastname;
        Gender = gender;
        Image = image;
        Joined = joined;
        Location = location;
        Phone = phone;
    }

    private String Age;
    private String Firstname;
    private String Lastname;
    private String Gender;
    private String Image;
    private String Joined;
    private String Location;
    private String Phone;

    public Requests() {

    }


    public String getAge() {
        return Age;
    }

    public void setAge(String age) {
        Age = age;
    }

    public String getFirstname() {
        return Firstname;
    }

    public void setFirstname(String firstname) {
        Firstname = firstname;
    }

    public String getLastname() {
        return Lastname;
    }

    public void setLastname(String lastname) {
        Lastname = lastname;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getJoined() {
        return Joined;
    }

    public void setJoined(String joined) {
        Joined = joined;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

}
