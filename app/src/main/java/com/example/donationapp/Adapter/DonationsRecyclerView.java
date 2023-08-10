package com.example.donationapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.donationapp.Classes.Donation;
import com.example.donationapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;

public class DonationsRecyclerView extends FirebaseRecyclerAdapter<Donation, DonationsRecyclerView.DonationViewHolder> {

    public DonationsRecyclerView(@NonNull FirebaseRecyclerOptions<Donation> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull DonationViewHolder holder, int position, @NonNull Donation model) {
        holder.bindData(model);
    }

    @NonNull
    @Override
    public DonationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donation, parent, false);
        return new DonationViewHolder(view);
    }

    static class DonationViewHolder extends RecyclerView.ViewHolder {
        private TextView descriptionTextView;
        private TextView locationTextView;
        private TextView ageTextView;
        private ImageView imageView;

        DonationViewHolder(@NonNull View itemView) {
            super(itemView);
            descriptionTextView = itemView.findViewById(R.id.textViewDescription);
            locationTextView = itemView.findViewById(R.id.textViewLocation);
            ageTextView = itemView.findViewById(R.id.textViewAge);
            imageView = itemView.findViewById(R.id.imageViewDonation);
        }

        void bindData(Donation donation) {
            descriptionTextView.setText(donation.getDescription());
            locationTextView.setText(donation.getLocation());
            ageTextView.setText(donation.getAge());
            Glide.with(itemView.getContext()).load(donation.getImageUrl()).into(imageView);
        }
    }
}