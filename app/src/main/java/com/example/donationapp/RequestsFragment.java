package com.example.donationapp;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donationapp.Adapter.RequestsAdapter;
import com.example.donationapp.Adapter.RequestsDonor;
import com.example.donationapp.Classes.Requestpost;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class RequestsFragment extends Fragment {

    public RequestsFragment() {
        // Required empty public constructor
    }

    private RecyclerView recyclerView;
    private RequestsDonor adapter;
    private DatabaseReference donationsRef;
    FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_requests, container, false);
        // Inflate the layout for this fragment
        recyclerView=view.findViewById(R.id.recycler);
        mAuth=FirebaseAuth.getInstance();
        RecyclerView.LayoutManager mLayoutManager = new MyDonationsFragment.LinearLayoutManagerWrapper(view.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(new MyDonationsFragment.LinearLayoutManagerWrapper(getContext()));
        // Initialize Firebase Database reference
        donationsRef = FirebaseDatabase.getInstance().getReference().child("requests");
        Query query = donationsRef.orderByChild("userId").equalTo(mAuth.getCurrentUser().getUid());
        // Configure FirebaseRecyclerOptions for the adapter
        FirebaseRecyclerOptions<Requestpost> options =
                new FirebaseRecyclerOptions.Builder<Requestpost>()
                        .setQuery(donationsRef, Requestpost.class)
                        .build();

        // Create and set the adapter
        adapter = new RequestsDonor(options);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        return view;
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