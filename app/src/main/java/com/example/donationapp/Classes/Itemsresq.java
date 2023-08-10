package com.example.donationapp.Classes;

public class Itemsresq {
    String uid, email, key;

    public Itemsresq(String uid, String email, String key) {
        this.uid = uid;
        this.email = email;
        this.key = key;
    }

    public Itemsresq() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
