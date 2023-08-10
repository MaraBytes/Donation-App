package com.example.donationapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donationapp.Adapter.DonationsAdapter;
import com.example.donationapp.Adapter.DonationsReceipient;
import com.example.donationapp.Classes.Donation;
import com.example.donationapp.Classes.Request;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ReceipientDonations extends Fragment {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private EditText editRequest;
    private Button addRequestButton;
    private RecyclerView recyclerView;
    private DonationsReceipient adapter;
    private DatabaseReference requestsRef, requestsRef1;
    private View emptyStateView;

    public ReceipientDonations() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_receipientrequests, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewRequests);
        editRequest = view.findViewById(R.id.editRequest);
        addRequestButton = view.findViewById(R.id.addRequest);
        emptyStateView = view.findViewById(R.id.emptyStateView); // Find the empty state view by its ID

        // Initialize Firebase Database reference
        requestsRef = FirebaseDatabase.getInstance().getReference().child("donations");
        requestsRef1 = FirebaseDatabase.getInstance().getReference().child("requests");

        // Configure FirebaseRecyclerOptions for the adapter
        Query query = requestsRef.orderByChild("taken").equalTo("");
        FirebaseRecyclerOptions<Donation> options = new FirebaseRecyclerOptions.Builder<Donation>().setQuery(query, Donation.class).build();

        // Create and set the adapter
        adapter = new DonationsReceipient(options) {
            @Override
            public void onDataChanged() {
                super.onDataChanged();

                // Check if the RecyclerView is empty and show/hide the empty state view accordingly
                if (isRecyclerViewEmpty()) {
                    emptyStateView.setVisibility(View.VISIBLE);
                } else {
                    emptyStateView.setVisibility(View.GONE);
                }
            }
        };
        recyclerView.setAdapter(adapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManagerWrapper(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        addRequestButton.setOnClickListener(v -> {
            String request = editRequest.getText().toString().trim();
            if (!request.isEmpty()) {
                checkLocationPermission(request);
            } else {
                Toast.makeText(getContext(), "Please enter a request", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private boolean isRecyclerViewEmpty() {
        return adapter.getItemCount() == 0;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
        checkLocationPermission("");
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void checkLocationPermission(String request) {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // Permission is already granted, get the location
            getLocation(request);
        }
    }

    private void getLocation(String request) {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted, retrieve the last known location
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), location -> {
                        if (location != null) {
                            // Use the location
                            //String request1 = editRequest.getText().toString().trim();
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            if (!request.isEmpty()) {
                                uploadRequest(request, latitude, longitude);
                            }

                        } else {
                            Toast.makeText(getContext(), "Your GPS might be disabled", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void uploadRequest(String request, double latitude, double longitude) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid();
        Request newRequest = new Request(request, uid, latitude, longitude, mAuth.getCurrentUser().getEmail(), "key");
        requestsRef1.push().setValue(newRequest)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Request added successfully", Toast.LENGTH_SHORT).show();
                        editRequest.setText("");
                    } else {
                        Toast.makeText(getContext(), "Failed to add request", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission granted, get the location
                getLocation("");
            } else {
                // Location permission denied
                Toast.makeText(getContext(), "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class LinearLayoutManagerWrapper extends LinearLayoutManager {

        public LinearLayoutManagerWrapper(Context context) {
            super(context);
        }

        public LinearLayoutManagerWrapper(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public LinearLayoutManagerWrapper(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        public boolean supportsPredictiveItemAnimations() {
            return false;
        }
    }
}
