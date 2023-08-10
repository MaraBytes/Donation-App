package com.example.donationapp.Classes;

public class Donation {
    private String description;
    private String location;
    private String age;
    private String imageUrl;
    private String uid;
    private String taken;
    private String selectedItem;
    private String dateTime;

    public Donation() {
        // Default constructor required for Firebase Realtime Database
    }

    public Donation(String description, String location, String age, String imageUrl, String uid, String taken, String selectedItem, String dateTime) {
        this.description = description;
        this.location = location;
        this.age = age;
        this.imageUrl = imageUrl;
        this.uid = uid;
        this.taken = taken;
        this.selectedItem = selectedItem;
        this.dateTime = dateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTaken() {
        return taken;
    }

    public void setTaken(String taken) {
        this.taken = taken;
    }

    public String getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(String selectedItem) {
        this.selectedItem = selectedItem;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
