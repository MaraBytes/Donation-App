package com.example.donationapp.Adapter;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.donationapp.Classes.Requestpost;
import com.example.donationapp.MapActivity;
import com.example.donationapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;


public class RequestsDonor extends FirebaseRecyclerAdapter<Requestpost, RequestsDonor.requestViewholder> {
    public RequestsDonor(@NonNull FirebaseRecyclerOptions<Requestpost> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull RequestsDonor.requestViewholder holder, int position, @NonNull Requestpost model) {
        holder.request.setText(model.getRequest());
        holder.email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", model.getEmail(), null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                holder.itemView.getContext().startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });
        holder.contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double latitude = model.getLatitude(); // Replace with your actual latitude value
                double longitude = model.getLongitude(); // Replace with your actual longitude value
                Intent mapIntent = new Intent(v.getContext(), MapActivity.class);
                mapIntent.putExtra("latitude", latitude);
                mapIntent.putExtra("longitude", longitude);
                v.getContext().startActivity(mapIntent);
                /*Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", model.getEmail(), null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                holder.itemView.getContext().startActivity(Intent.createChooser(emailIntent, "Send email..."));*/
            }
        });
    }


    @NonNull
    @Override
    public RequestsDonor.requestViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_requestsdonor, parent, false);
        return new RequestsDonor.requestViewholder(view);
    }

    public class requestViewholder extends RecyclerView.ViewHolder {
        TextView request;
        Button contact, email;

        public requestViewholder(@NonNull View itemView) {
            super(itemView);
            request = itemView.findViewById(R.id.textCli);
            email = itemView.findViewById(R.id.email);
            contact = itemView.findViewById(R.id.contactli);
        }
    }
}