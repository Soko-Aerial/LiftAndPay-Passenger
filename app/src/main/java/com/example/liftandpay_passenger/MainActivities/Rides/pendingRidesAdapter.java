package com.example.liftandpay_passenger.MainActivities.Rides;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.liftandpay_passenger.R;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class pendingRidesAdapter extends RecyclerView.Adapter<pendingRidesAdapter.myViewHolder> {
    ArrayList<pendingRidesModel> dataholder;
    Context context;


    public pendingRidesAdapter(Context context, ArrayList<pendingRidesModel> dataholder) {
        this.dataholder = dataholder;
        this.context = context;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.model_pending_ride,parent,false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        holder.journey.setText(dataholder.get(holder.getAdapterPosition()).getJourney());
        holder.dateTime.setText(dataholder.get(holder.getAdapterPosition()).getDateTime());
        holder.distance.setText(dataholder.get(holder.getAdapterPosition()).getDistance());
        holder.price.setText(dataholder.get(holder.getAdapterPosition()).getPrice());
        holder.status.setText(dataholder.get(holder.getAdapterPosition()).getStatus());

        holder.theCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent = new Intent(context, PendingRideMapActivity.class);
                intent.putExtra("journey",dataholder.get(holder.getAdapterPosition()).getJourney());
                intent.putExtra("dateTime",dataholder.get(holder.getAdapterPosition()).getDateTime());
                intent.putExtra("distance",dataholder.get(holder.getAdapterPosition()).getDistance());
                intent.putExtra("stLat",dataholder.get(holder.getAdapterPosition()).getStartLat());
                intent.putExtra("stLon",dataholder.get(holder.getAdapterPosition()).getStartLon());
                intent.putExtra("endLat",dataholder.get(holder.getAdapterPosition()).getEndLat());
                intent.putExtra("endLon", dataholder.get(holder.getAdapterPosition()).getEndLon());
                intent.putExtra("rideId",dataholder.get(holder.getAdapterPosition()).getRideId());

                context.startActivity(intent);*/
            }
        });

        String status = holder.status.getText().toString();

        if (status.equals("Approved")){
            holder.infoLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.extraFadedSuccess));
            holder.journey.setBackgroundColor(ContextCompat.getColor(context, R.color.fadedSuccess));
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.success));
        }
        if (status.equals("Declined")){
            holder.infoLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.extraFadedSuccess));
            holder.journey.setBackgroundColor(ContextCompat.getColor(context, R.color.failure));
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.red));

        }
    }

    @Override
    public int getItemCount() {
        return dataholder.size();
    }

    static class myViewHolder extends RecyclerView.ViewHolder{
        TextView journey, dateTime,distance,price,status;
        MaterialCardView theCardView;
        LinearLayout infoLayout;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            journey = itemView.findViewById(R.id.t1);
            dateTime = itemView.findViewById(R.id.t2);
            distance=itemView.findViewById(R.id.distance);
            price = itemView.findViewById(R.id.price);
            status = itemView.findViewById(R.id.statusId);
            theCardView =  itemView.findViewById(R.id.pendingCardView);
            infoLayout = itemView.findViewById(R.id.infoLayout);
        }
    }
}
