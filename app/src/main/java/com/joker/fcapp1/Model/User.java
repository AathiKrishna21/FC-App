package com.joker.fcapp1.Model;

import com.joker.fcapp1.ui.profile.ProfileFragment;

public class User {
    private String Name;
    private String Phonenumber,Email,ProfileUrl;

    public User(String name, String phonenumber, String email, String profileurl) {
        Name = name;
        Phonenumber = phonenumber;
        Email = email;
        ProfileUrl = profileurl;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setProfileurl(String profileurl) {
        ProfileUrl = profileurl;
    }

    public String getEmail() {
        return Email;
    }

    public String getProfileurl() {
        return ProfileUrl;
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