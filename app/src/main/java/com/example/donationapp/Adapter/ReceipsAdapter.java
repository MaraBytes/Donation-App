package com.example.donationapp.Adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.donationapp.Classes.Donation;
import com.example.donationapp.Classes.Request;
import com.example.donationapp.MapActivity;
import com.example.donationapp.R;
import com.example.donationapp.detailsActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class ReceipsAdapter extends FirebaseRecyclerAdapter<Request, ReceipsAdapter.receipsViewholder> {
    public ReceipsAdapter(@NonNull FirebaseRecyclerOptions<Request> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull receipsViewholder holder, int position, @NonNull Request model) {
        String key = getRef(position).getKey();
        holder.approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(holder.itemView.getContext());
                alertDialogBuilder.setTitle("Confirm");
                alertDialogBuilder.setMessage("You have contacted recipient and wish to delete item?");
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Perform the delete operation here
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("donations").child(model.getKey());
                        databaseReference.removeValue();
                    }
                });
                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked the "Cancel" button, do nothing or dismiss the dialog
                        dialog.dismiss();
                    }
                });

                // Create and show the dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
        holder.contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference requestedItemsRef = FirebaseDatabase.getInstance().getReference().child("Requested").child(key);
                ValueEventListener valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String emaime = snapshot.child("email").getValue(String.class);
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto", emaime, null));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                        emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                        holder.itemView.getContext().startActivity(Intent.createChooser(emailIntent, "Send email..."));

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle the onCancelled event, if necessary
                    }
                };

                requestedItemsRef.addValueEventListener(valueEventListener);

            }
        });
        holder.locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double latitude = model.getLatitude(); // Replace with your actual latitude value
                double longitude = model.getLongitude(); // Replace with your actual longitude value
                Intent mapIntent = new Intent(v.getContext(), MapActivity.class);
                mapIntent.putExtra("latitude", latitude);
                mapIntent.putExtra("longitude", longitude);
                v.getContext().startActivity(mapIntent);
            }
        });
    }

    @NonNull
    @Override
    public receipsViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.receips, parent, false);
        return new ReceipsAdapter.receipsViewholder(view);
    }

    public class receipsViewholder extends RecyclerView.ViewHolder {
        TextView descriptionTextView;
        TextView locationTextView;
        Button contact, locate, approve;

        public receipsViewholder(@NonNull View itemView) {
            super(itemView);
            contact = itemView.findViewById(R.id.contact);
            locate = itemView.findViewById(R.id.locate);
            approve = itemView.findViewById(R.id.approve);
        }
    }
}