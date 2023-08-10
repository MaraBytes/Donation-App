package com.example.donationapp.Adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.donationapp.Classes.Donation;
import com.example.donationapp.R;
import com.example.donationapp.detailsActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DonationsAdapter extends FirebaseRecyclerAdapter<Donation, DonationsAdapter.donationsViewholder> {
    public DonationsAdapter(@NonNull FirebaseRecyclerOptions<Donation> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull donationsViewholder holder, int position, @NonNull Donation model) {
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
        DatabaseReference databaseReferenceme = FirebaseDatabase.getInstance().getReference("donations").child(key);
        databaseReferenceme.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String takencon = snapshot.child("taken").getValue(String.class);
                    if (takencon.equals("taken")) {
                        holder.mainlay.setBackgroundResource(R.drawable.round_item_taken);
                    } else if (takencon.equals("")) {
                        holder.mainlay.setBackgroundResource(R.drawable.round_item);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.carddelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                builder.setTitle("Confirm Delete")
                        .setMessage("Are you sure you want to delete this item?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // User clicked the Delete button, proceed with deletion
                                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("donations").child(key);
                                databaseRef.removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(holder.itemView.getContext(), "Item deleted", Toast.LENGTH_SHORT).show();
                                                // Item deleted successfully
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(holder.itemView.getContext(), "Unable to delete item", Toast.LENGTH_SHORT).show();
                                                // Failed to delete the item
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        Glide.with(holder.itemView.getContext()).load(model.getImageUrl()).into(holder.imageView);

        holder.receipientsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "Hello, ReceiverActivity!";
                Intent intent = new Intent(holder.itemView.getContext(), detailsActivity.class);
                intent.putExtra("key", key);
                intent.putExtra("desc", model.getDescription());
                intent.putExtra("age", model.getAge());
                intent.putExtra("loc", model.getLocation());
                intent.putExtra("url", model.getImageUrl());
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }


    @NonNull
    @Override
    public donationsViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donation, parent, false);
        return new DonationsAdapter.donationsViewholder(view);
    }


    public class donationsViewholder extends RecyclerView.ViewHolder {
        TextView descriptionTextView;
        TextView locationTextView;
        TextView ageTextView, period;
        ImageView imageView;
        RelativeLayout mainlay;
        Button receipientsBtn;
        LinearLayout layme;
        CardView carddelete;
        CountDownTimer countDownTimer; // CountDownTimer to store the timer for perishable items

        public donationsViewholder(@NonNull View itemView) {
            super(itemView);
            descriptionTextView = itemView.findViewById(R.id.textViewDescription);
            locationTextView = itemView.findViewById(R.id.textViewLocation);
            ageTextView = itemView.findViewById(R.id.textViewAge);
            mainlay = itemView.findViewById(R.id.mainlay);
            layme = itemView.findViewById(R.id.layme);
            period = itemView.findViewById(R.id.period);
            receipientsBtn = itemView.findViewById(R.id.receipientsBtn);
            carddelete = itemView.findViewById(R.id.carddelete);
            imageView = itemView.findViewById(R.id.imageViewDonation);
        }
    }
}
