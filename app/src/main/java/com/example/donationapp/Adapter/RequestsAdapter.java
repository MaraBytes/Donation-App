package com.example.donationapp.Adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.donationapp.Classes.Donation;
import com.example.donationapp.Classes.Request;
import com.example.donationapp.Classes.Requestpost;
import com.example.donationapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RequestsAdapter extends FirebaseRecyclerAdapter<Requestpost, RequestsAdapter.requestViewholder> {
    public RequestsAdapter(@NonNull FirebaseRecyclerOptions<Requestpost> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull RequestsAdapter.requestViewholder holder, int position, @NonNull Requestpost model) {
        holder.request.setText(model.getRequest());
        String key = getRef(position).getKey();
        holder.carddel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                builder.setTitle("Confirm Delete")
                        .setMessage("Are you sure you want to delete this request?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // User clicked the Delete button, proceed with deletion
                                DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("requests").child(key);
                                databaseRef.removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // Item deleted successfully
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Failed to delete the item
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();

            }
        });
    }


    @NonNull
    @Override
    public RequestsAdapter.requestViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_requests, parent, false);
        return new RequestsAdapter.requestViewholder(view);
    }

    public class requestViewholder extends RecyclerView.ViewHolder {
        TextView request;
        CardView carddel;

        public requestViewholder(@NonNull View itemView) {
            super(itemView);
            request = itemView.findViewById(R.id.textRequest);
            carddel = itemView.findViewById(R.id.carddel);
        }
    }
}