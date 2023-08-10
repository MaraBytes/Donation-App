package com.example.donationapp.Background;

public class UserCoordinates {
    private double latitude;
    private double longitude;

    public UserCoordinates() {
        // Empty constructor required for Firebase
    }

    public UserCoordinates(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getter methods for latitude and longitude
    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
