package com.example.donationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class checkUser extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_user);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Retrieve the current user's ID
        String userId = mAuth.getCurrentUser().getUid();

        // Retrieve user type from Firebase Realtime Database
        DatabaseReference userRef = mDatabase.child("users").child(userId).child("userType");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String userType = dataSnapshot.getValue(String.class);
                // Here, you can access the user type and perform actions accordingly
                if (userType != null) {
                    if (userType.equals("Donor")) {
                        // User is a donor
                        Toast.makeText(checkUser.this, "Welcome Donor!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(checkUser.this, DonorActivity.class));
                    } else if (userType.equals("Recipient")) {
                        // User is a recipient
                        startActivity(new Intent(checkUser.this, RecipientActivity.class));
                        Toast.makeText(checkUser.this, "Welcome Recipient!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                Toast.makeText(checkUser.this, "Failed to retrieve user type", Toast.LENGTH_SHORT).show();
            }
        });
    }
}