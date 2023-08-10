package com.example.donationapp.Classes;

public class UserHelper {
    String uid, contact, userType;

    public UserHelper(String uid, String contact, String userType) {
        this.uid = uid;
        this.contact = contact;
        this.userType = userType;
    }

    public UserHelper() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
