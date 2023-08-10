package com.example.donationapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.donationapp.Adapter.DonationsAdapter;
import com.example.donationapp.Adapter.DonationsRecyclerView;
import com.example.donationapp.Classes.Donation;
import com.example.donationapp.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MyDonationsFragment extends Fragment {

    public MyDonationsFragment() {
        // Required empty public constructor
    }

    private RecyclerView recyclerView;
    private DonationsAdapter adapter;
    private DatabaseReference donationsRef;
    FirebaseAuth mAuth;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ragment_my_donations, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewDonations);
        mAuth = FirebaseAuth.getInstance();
        Button postDonation = view.findViewById(R.id.postDonation);
        postDonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(view.getContext(), postActivity.class));
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManagerWrapper(view.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(new LinearLayoutManagerWrapper(getContext()));
        // Initialize Firebase Database reference
        donationsRef = FirebaseDatabase.getInstance().getReference().child("donations");
        Query query = donationsRef.orderByChild("uid").equalTo(mAuth.getCurrentUser().getUid());
        // Configure FirebaseRecyclerOptions for the adapter
        FirebaseRecyclerOptions<Donation> options =
                new FirebaseRecyclerOptions.Builder<Donation>()
                        .setQuery(query, Donation.class)
                        .build();

        // Create and set the adapter
        adapter = new DonationsAdapter(options);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        return view;
    }

    public static class LinearLayoutManagerWrapper extends LinearLayoutManager {

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

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}