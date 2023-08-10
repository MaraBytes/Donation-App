package com.example.donationapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.donationapp.Adapter.DonationsReceipient;
import com.example.donationapp.Adapter.ReceipsAdapter;
import com.example.donationapp.Classes.Donation;
import com.example.donationapp.Classes.Request;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class detailsActivity extends AppCompatActivity {
    ReceipsAdapter adapter;
    DatabaseReference donationsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Intent intent = getIntent();
        String key = intent.getStringExtra("key");
        String desc = intent.getStringExtra("desc");
        String age = intent.getStringExtra("age");
        String loc = intent.getStringExtra("loc");
        String imgurl = intent.getStringExtra("url");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        RecyclerView recyclerView = findViewById(R.id.receips);
        // Initialize Firebase Database reference
        donationsRef = FirebaseDatabase.getInstance().getReference().child("Requested");
        Query query = donationsRef.orderByChild("key").equalTo(key);
        RecyclerView.LayoutManager mLayoutManager = new MyDonationsFragment.LinearLayoutManagerWrapper(detailsActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(new MyDonationsFragment.LinearLayoutManagerWrapper(detailsActivity.this));
        FirebaseRecyclerOptions<Request> options = new FirebaseRecyclerOptions.Builder<Request>().setQuery(query, Request.class).build();
        // Create and set the adapter
        adapter = new ReceipsAdapter(options);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        LinearLayout linearLayout = findViewById(R.id.linear);
        ImageView imageViewDonation = findViewById(R.id.imageViewDonation);
        TextView descriptionTextView = findViewById(R.id.description);
        TextView locationTextView = findViewById(R.id.location);
        TextView ageTextView = findViewById(R.id.age);

// Set data in the views
        descriptionTextView.setText(desc);
        locationTextView.setText(loc);
        ageTextView.setText(age);

// Use an image loading library to set the image from the donation object into the ImageView
// For example, using Glide:
        Glide.with(this).load(imgurl).into(imageViewDonation);
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
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}