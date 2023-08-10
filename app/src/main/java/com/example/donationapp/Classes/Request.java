package com.example.donationapp.Classes;

public class Request {
    private String request;
    private String userId;
    private double latitude;
    private double longitude;
    private String email;
    private String key;
    public Request() {
        // Default constructor required for Firebase
    }

    public Request(String request, String userId, double latitude, double longitude, String email, String key) {
        this.request = request;
        this.userId = userId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.email = email;
        this.key = key;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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