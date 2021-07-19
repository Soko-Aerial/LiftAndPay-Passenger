package com.example.liftandpay_passenger.MainActivities.Rides;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_design,parent,false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        holder.journey.setText(dataholder.get(position).getJourney());
        holder.dateTime.setText(dataholder.get(position).getDateTime());
        holder.distance.setText(dataholder.get(position).getDistance());
        holder.price.setText(dataholder.get(position).getPrice());
        holder.status.setText(dataholder.get(position).getStatus());

        holder.theCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PendingRideMapActivity.class);
                intent.putExtra("journey",dataholder.get(position).getJourney());
                intent.putExtra("dateTime",dataholder.get(position).getDateTime());
                intent.putExtra("distance",dataholder.get(position).getDistance());
                intent.putExtra("stLat",dataholder.get(position).getStartLat());
                intent.putExtra("stLon",dataholder.get(position).getStartLon());
                intent.putExtra("endLat",dataholder.get(position).getEndLat());
                intent.putExtra("endLon", dataholder.get(position).getEndLon());
                intent.putExtra("rideId",dataholder.get(position).getRideId());

                context.startActivity(intent);
            }
        });

        String status = holder.status.getText().toString();

        if (status.equals("Approved")){
            holder.journey.setBackgroundColor(ContextCompat.getColor(context, R.color.success));
            holder.price.setTextColor(ContextCompat.getColor(context, R.color.success));
        }
        if (status.equals("Declined")){
            holder.journey.setBackgroundColor(ContextCompat.getColor(context, R.color.failure));
            holder.price.setTextColor(ContextCompat.getColor(context, R.color.failure));

        }
    }

    @Override
    public int getItemCount() {
        return dataholder.size();
    }

    static class myViewHolder extends RecyclerView.ViewHolder{
        TextView journey, dateTime,distance,price,status;
        MaterialCardView theCardView;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            journey = itemView.findViewById(R.id.t1);
            dateTime = itemView.findViewById(R.id.t2);
            distance=itemView.findViewById(R.id.distance);
            price = itemView.findViewById(R.id.price);
            status = itemView.findViewById(R.id.statusId);
            theCardView =  itemView.findViewById(R.id.pendingCardView);
        }
    }
}
