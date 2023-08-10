package com.example.donationapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.donationapp.Background.LocationUpdateService;
import com.example.donationapp.Classes.Donation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class postActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;
    private static final int PERMISSION_REQUEST_CODE = 3;
    private static final int MAX_IMAGE_WIDTH = 1024;
    private static final int MAX_IMAGE_HEIGHT = 1024;
    private static final int IMAGE_COMPRESSION_QUALITY = 80;
    private EditText editTextDescription, editTextLocation, editTextAge;
    private Button buttonUpload;
    CardView cardback;
    private EditText editTextPerishablePeriod;
    private int perishablePeriodInMinutes = 0;
    private ImageView imagePreview;
    ProgressBar progressBarupload;
    LinearLayout linear1;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private EditText editTextDate;
    private EditText editTextTime;
    private Calendar selectedDateTime = Calendar.getInstance();
    FirebaseAuth mAuth;
    private Uri imageUri;
    Spinner spinnerOptions;
    private String selectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        mAuth = FirebaseAuth.getInstance();
        spinnerOptions = findViewById(R.id.spinnerOptions);
        linear1 = findViewById(R.id.linear1);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.options_array, // Assuming you have an array defined in your strings.xml
                android.R.layout.simple_spinner_item
        );
        cardback = findViewById(R.id.cardback);
        cardback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(postActivity.this, LocationUpdateService.class);
                stopService(intent);
            }
        });
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        editTextDate = findViewById(R.id.editTextDate);
        editTextTime = findViewById(R.id.editTextTime);

        // Set click listeners for date and time EditText fields
        editTextDate.setOnClickListener(view -> showDatePickerDialog());
        editTextTime.setOnClickListener(view -> showTimePickerDialog());
        spinnerOptions.setAdapter(adapter);
        // Set the Spinner listener
        spinnerOptions.setOnItemSelectedListener(spinnerListener);
        // Initialize Firebase Realtime Database and Storage
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference();
        progressBarupload = findViewById(R.id.progressBarupload);
        // Initialize views
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextLocation = findViewById(R.id.editTextLocation);
        editTextAge = findViewById(R.id.editTextAge);
        buttonUpload = findViewById(R.id.buttonUpload);
        imagePreview = findViewById(R.id.imagePreview);
        startLOcation();
        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarupload.setVisibility(View.VISIBLE);
                uploadDonation();
            }
        });

        imagePreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    private void showDatePickerDialog() {
        int year = selectedDateTime.get(Calendar.YEAR);
        int month = selectedDateTime.get(Calendar.MONTH);
        int dayOfMonth = selectedDateTime.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                    // Set the selected date to the calendar instance
                    selectedDateTime.set(selectedYear, selectedMonth, selectedDayOfMonth);
                    // Display the selected date in the EditText
                    editTextDate.setText(formatDate(selectedDateTime.getTime()));
                }, year, month, dayOfMonth);
        datePickerDialog.show();
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(date);
    }


    private void showTimePickerDialog() {
        int hour = selectedDateTime.get(Calendar.HOUR_OF_DAY);
        int minute = selectedDateTime.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minuteOfDay) -> {
                    // Update the selected time in the calendar instance
                    selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    selectedDateTime.set(Calendar.MINUTE, minuteOfDay);
                    // Display the selected time in the EditText
                    editTextTime.setText(formatTime(selectedDateTime.getTime()));
                }, hour, minute, true);
        timePickerDialog.show();
    }

    private String formatTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(date);
    }


    private String formatPerishablePeriod(int minutes) {
        int hours = minutes / 60;
        int remainingMinutes = minutes % 60;

        if (hours > 0) {
            return String.format(Locale.getDefault(), "%d hours %d minutes", hours, remainingMinutes);
        } else {
            return String.format(Locale.getDefault(), "%d minutes", minutes);
        }
    }

    private void startLOcation() {
        Intent intent = new Intent(this, LocationUpdateService.class);
        startService(intent);
    }

    private void selectImage() {
        // Check if the required permissions are granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        } else {
            // Permission already granted, open image picker
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, open image picker
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
            } else {
                // Permission denied
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK && data != null) {
            // Image selected from gallery
            imageUri = data.getData();
            imagePreview.setImageURI(imageUri);
        }
    }

    private void uploadDonation() {
        String description = editTextDescription.getText().toString().trim();
        String location = editTextLocation.getText().toString().trim();
        String age = editTextAge.getText().toString().trim();
        String dateTime = editTextDate.getText().toString() + " " + editTextTime.getText().toString();

        // Perform input validation
        if (description.isEmpty() || location.isEmpty() || (!"Perishable".equals(selectedItem) && age.isEmpty())) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            progressBarupload.setVisibility(View.GONE);
            return;
        }

        // Check if an image is selected
        if (imageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            progressBarupload.setVisibility(View.GONE);
            return;
        }

        // Check if a spinner item is selected
        if (selectedItem == null) {
            Toast.makeText(this, "Please select an option from the spinner", Toast.LENGTH_SHORT).show();
            progressBarupload.setVisibility(View.GONE);
            return;
        }

        // Compress and resize the image before uploading
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            Bitmap resizedBitmap = resizeImage(bitmap, MAX_IMAGE_WIDTH, MAX_IMAGE_HEIGHT);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_COMPRESSION_QUALITY, baos);
            byte[] imageData = baos.toByteArray();

            // Upload the compressed image to Firebase Storage
            StorageReference imageRef = mStorage.child("donation_images").child(Objects.requireNonNull(imageUri.getLastPathSegment()));
            UploadTask uploadTask = imageRef.putBytes(imageData);
            uploadTask.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Image upload successful, get the download URL
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Upload the donation details to Firebase Realtime Database
                        Donation donation;
                        if ("Perishable".equals(selectedItem)) {
                            // Create a Perishable Donation object without age field
                            donation = new Donation(description, location, "", uri.toString(), mAuth.getCurrentUser().getUid(), "", selectedItem, dateTime);
                            //donation.setPerishablePeriod(formatPerishablePeriod(perishablePeriodInMinutes));
                        } else {
                            // Create a Non-Perishable Donation object with age field
                            donation = new Donation(description, location, age, uri.toString(), mAuth.getCurrentUser().getUid(), "", selectedItem, dateTime);
                        }

                        String donationId = mDatabase.child("donations").push().getKey();
                        if (donationId != null) {
                            mDatabase.child("donations").child(donationId).setValue(donation)
                                    .addOnCompleteListener(databaseTask -> {
                                        if (databaseTask.isSuccessful()) {
                                            // Show a success message
                                            Toast.makeText(postActivity.this, "Donation uploaded successfully", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(this, LocationUpdateService.class);
                                            stopService(intent);
                                            // Finish the activity
                                            finish();
                                        } else {
                                            // Donation upload to database failed
                                            Toast.makeText(postActivity.this, "Failed to upload donation details", Toast.LENGTH_SHORT).show();
                                        }
                                        progressBarupload.setVisibility(View.GONE);
                                    });
                        } else {
                            // Donation ID is null
                            Toast.makeText(postActivity.this, "Failed to generate donation ID", Toast.LENGTH_SHORT).show();
                            progressBarupload.setVisibility(View.GONE);
                        }
                    });
                } else {
                    // Image upload failed
                    Toast.makeText(postActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    progressBarupload.setVisibility(View.GONE);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(postActivity.this, "Failed to compress image", Toast.LENGTH_SHORT).show();
            progressBarupload.setVisibility(View.GONE);
        }
    }


    private Bitmap resizeImage(Bitmap bitmap, int maxImageWidth, int maxImageHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float ratioBitmap = (float) width / (float) height;
        float ratioMax = (float) maxImageWidth / (float) maxImageHeight;

        int finalWidth = maxImageWidth;
        int finalHeight = maxImageHeight;
        if (ratioMax > ratioBitmap) {
            finalWidth = (int) ((float) maxImageHeight * ratioBitmap);
        } else {
            finalHeight = (int) ((float) maxImageWidth / ratioBitmap);
        }

        return Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true);
    }

    // Define the Spinner item selection listener here
    private AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            selectedItem = parent.getItemAtPosition(position).toString();
            if ("Perishable".equals(selectedItem)) {
                editTextAge.setVisibility(View.GONE);
                linear1.setVisibility(View.VISIBLE);
            } else {
                editTextAge.setVisibility(View.VISIBLE);
                linear1.setVisibility(View.GONE);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // Do nothing here
        }
    };

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, LocationUpdateService.class);
        stopService(intent);
        super.onBackPressed();
    }
}
