package com.example.donationapp.Adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.donationapp.Classes.Donation;
import com.example.donationapp.Classes.Itemsresq;
import com.example.donationapp.Classes.Request;
import com.example.donationapp.MapActivity;
import com.example.donationapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DonationsReceipient extends FirebaseRecyclerAdapter<Donation, DonationsReceipient.donationsViewholder> {
    public DonationsReceipient(@NonNull FirebaseRecyclerOptions<Donation> options) {
        super(options);
    }

    FirebaseAuth mAuth;

    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onBindViewHolder(@NonNull donationsViewholder holder, @SuppressLint("RecyclerView") int position, @NonNull Donation model) {
        holder.descriptionTextView.setText(model.getDescription());
        String key = getRef(position).getKey();
        holder.locationTextView.setText(model.getLocation());
        holder.ageTextView.setText(model.getAge());
        DatabaseReference status = FirebaseDatabase.getInstance().getReference("donations").child(key);
        status.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String status = snapshot.child("selectedItem").getValue(String.class);
                    String expiry = snapshot.child("dateTime").getValue(String.class);

                    if (status.equals("Perishable")) {
                        holder.layme.setVisibility(View.VISIBLE);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        try {
                            Date targetDate = sdf.parse(expiry);
                            Date currentDate = new Date(); // Current date and time

                            long timeDifferenceMillis = targetDate.getTime() - currentDate.getTime();
                            if (timeDifferenceMillis > 0) {
                                // Create a new CountDownTimer with the remaining time difference
                                new CountDownTimer(timeDifferenceMillis, 1000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                        long seconds = millisUntilFinished / 1000;
                                        long minutes = seconds / 60;
                                        long hours = minutes / 60;
                                        long days = hours / 24;

                                        if (days > 0) {
                                            holder.period.setText(days + " day");
                                        } else if (hours > 0) {
                                            holder.period.setText(hours + " h");
                                        } else if (minutes > 0) {
                                            holder.period.setText(minutes + " min");
                                        } else {
                                            holder.period.setText(seconds + " sec");
                                        }
                                    }

                                    @Override
                                    public void onFinish() {
                                        // Timer finished, remove the item or update as needed
                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("donations").child(key);
                                        databaseReference.removeValue();
                                        notifyDataSetChanged();
                                    }
                                }.start();
                            } else {
                                // Time has already passed, remove the item or update as needed
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("donations").child(key);
                                databaseReference.removeValue();
                                notifyDataSetChanged();
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                            Toast.makeText(holder.itemView.getContext(), "Invalid", Toast.LENGTH_SHORT).show();
                        }
                    } else if (status.equals("Non-Perishable")) {
                        holder.layme.setVisibility(View.GONE);
                        // Handle Non-Perishable items as needed
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.locatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(model.getUid());
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Double longitude = snapshot.child("longitude").getValue(Double.class);
                        Double latitude = snapshot.child("latitude").getValue(Double.class);
                        Intent mapIntent = new Intent(v.getContext(), MapActivity.class);
                        mapIntent.putExtra("latitude", latitude);
                        mapIntent.putExtra("longitude", longitude);
                        v.getContext().startActivity(mapIntent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        mAuth = FirebaseAuth.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(holder.itemView.getContext());
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Requested_items").child(key);
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Requested");
        DatabaseReference donations = FirebaseDatabase.getInstance().getReference("donations").child(key);
        Query query = databaseReference.orderByChild("uid").equalTo(mAuth.getCurrentUser().getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    holder.requestbtn.setVisibility(View.GONE);
                } else if (!snapshot.exists()) {
                    holder.requestbtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Glide.with(holder.itemView.getContext()).load(model.getImageUrl()).into(holder.imageView);
        holder.requestbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = getRef(position).getKey();
                Itemsresq itemsresq = new Itemsresq(mAuth.getCurrentUser().getUid(), mAuth.getCurrentUser().getEmail(), key);
                databaseReference.push().setValue(itemsresq);
                if (ActivityCompat.checkSelfPermission(holder.itemView.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener((Activity) holder.itemView.getContext(), new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        double latitude = location.getLatitude();
                                        double longitude = location.getLongitude();
                                        Request request = new Request("", mAuth.getUid(), latitude, longitude, mAuth.getCurrentUser().getEmail(), key);
                                        databaseReference1.push().setValue(request);
                                        Toast.makeText(holder.itemView.getContext(), "Request submitted. Donor will contact you", Toast.LENGTH_LONG).show();
                                        donations.child("taken").setValue("taken");

                                    } else {
                                        Toast.makeText(holder.itemView.getContext(), "Unable to retrieve location", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    // Location permission is not granted
                    Toast.makeText(holder.itemView.getContext(), "Location permission not granted", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @NonNull
    @Override
    public donationsViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donation_receipients, parent, false);
        return new DonationsReceipient.donationsViewholder(view);
    }

    public class donationsViewholder extends RecyclerView.ViewHolder {
        TextView descriptionTextView;
        TextView locationTextView;
        TextView ageTextView, period;
        LinearLayout layme;
        ImageView imageView;
        public Button requestbtn, locatebtn;

        public donationsViewholder(@NonNull View itemView) {
            super(itemView);
            descriptionTextView = itemView.findViewById(R.id.textViewDescription);
            locationTextView = itemView.findViewById(R.id.textViewLocation);
            ageTextView = itemView.findViewById(R.id.textViewAge);
            requestbtn = itemView.findViewById(R.id.requestbtn);
            period = itemView.findViewById(R.id.period);
            layme = itemView.findViewById(R.id.layme);
            locatebtn = itemView.findViewById(R.id.locatebtn);
            imageView = itemView.findViewById(R.id.imageViewDonation);
        }
    }
}