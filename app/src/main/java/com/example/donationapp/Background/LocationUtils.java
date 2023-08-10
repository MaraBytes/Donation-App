package com.example.donationapp.Background;

import com.google.android.gms.location.LocationRequest;

public class LocationUtils {
    private static final long INTERVAL = 5000; // Update location every 5 seconds
    private static final long FASTEST_INTERVAL = 3000; // Fastest update interval is 3 seconds

    public static LocationRequest getLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }
}
