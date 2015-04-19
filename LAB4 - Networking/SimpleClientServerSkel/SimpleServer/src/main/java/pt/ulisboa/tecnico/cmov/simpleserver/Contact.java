package pt.ulisboa.tecnico.cmov.simpleserver;

/**
 * Created by Diogo on 18-Apr-15.
 */
public class Contact {
    String name;
    String address;
    Integer phone_number;
    String email;

    public Contact(String name, String address, Integer phone_number, String email) {
        this.name = name;
        this.address = address;
        this.phone_number = phone_number;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(Integer phone_number) {
        this.phone_number = phone_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        String contact = "---\nCreated contact\n";
        contact += "Name : " + getName() + "\n";
        contact += "Address : " + getAddress() + "\n";
        contact += "Phone Number : " + getPhone_number() + "\n";
        contact += "Email : " + getAddress() + "\n";
        return contact;
    }
}
