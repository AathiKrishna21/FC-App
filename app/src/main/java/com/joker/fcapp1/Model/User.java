package com.joker.fcapp1.Model;

public class User {
    private String Name;
    private String Phonenumber;

    public User(String name,String phonenumber) {
        Name = name;
        Phonenumber = phonenumber;
    }

    public User() {
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhonenumber() {
        return Phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        Phonenumber = phonenumber;
    }
}
