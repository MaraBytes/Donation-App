package com.example.donationapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.donationapp.Classes.UserHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class registerActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword, editTextContact;
    private Spinner spinnerUserType;
    private Button buttonRegister;
    private TextView textViewLogin, textViewForgotPassword;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Authentication and Realtime Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize views
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        spinnerUserType = findViewById(R.id.spinnerUserType);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewLogin = findViewById(R.id.textViewLogin);
        editTextContact = findViewById(R.id.editTextContact);
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);
        progressBar = findViewById(R.id.progressBar);

        // Configure spinner with user types
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.user_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUserType.setAdapter(adapter);

        // Set click listeners
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open login activity
                // Add your code here to open the login activity
            }
        });

        textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open forgot password activity
                // Add your code here to open the forgot password activity
            }
        });
    }

    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String userType = spinnerUserType.getSelectedItem().toString();
        String cont = editTextContact.getText().toString().trim();

        // Perform input validation
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        } else if (cont.length() < 13) {
            Toast.makeText(this, "Contact too short", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Register user with Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // Hide progress bar
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            // User registration successful
                            FirebaseUser currentUser = mAuth.getCurrentUser();

                            // Store user type in Firebase Realtime Database
                            UserHelper userHelper = new UserHelper(currentUser.getUid(), cont, userType);
                            mDatabase.child("users").child(currentUser.getUid()).setValue(userHelper);

                            // Redirect users based on user type
                            if (userType.equals("Donor")) {
                                // User is a donor, redirect to DonorActivity
                                UserHelper userHelperD = new UserHelper(currentUser.getUid(), cont, userType);
                                mDatabase.child("users").child(currentUser.getUid()).setValue(userHelperD);
                                Intent intent = new Intent(registerActivity.this, DonorActivity.class);
                                startActivity(intent);
                                finish(); // Optional: finish the current activity to prevent going back
                            } else if (userType.equals("Recipient")) {
                                // User is a recipient, redirect to RecipientActivity
                                UserHelper userHelperR = new UserHelper(currentUser.getUid(), cont, userType);
                                mDatabase.child("users").child(currentUser.getUid()).setValue(userHelperR);
                                Intent intent = new Intent(registerActivity.this, RecipientActivity.class);
                                startActivity(intent);
                                finish(); // Optional: finish the current activity to prevent going back
                            }
                        } else {
                            // User registration failed
                            Toast.makeText(registerActivity.this, "Registration failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}