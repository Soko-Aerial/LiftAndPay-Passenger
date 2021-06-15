package com.example.liftandpay_passenger.MainActivities.Rides;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
        holder.img.setBackgroundResource(dataholder.get(position).getImage());
        holder.header.setText(dataholder.get(position).getHeader());
        holder.desc.setText(dataholder.get(position).getDesc());
        holder.distance.setText(dataholder.get(position).getDistance());
        holder.price.setText(dataholder.get(position).getPrice());
        holder.theCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PendingRideMapActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataholder.size();
    }

    static class myViewHolder extends RecyclerView.ViewHolder{
        ImageView img;
        TextView header,desc,distance,price;
        MaterialCardView theCardView;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img1);
            header = itemView.findViewById(R.id.t1);
            desc = itemView.findViewById(R.id.t2);
            distance=itemView.findViewById(R.id.distance);
            price = itemView.findViewById(R.id.price);
            theCardView =  itemView.findViewById(R.id.pendingCardView);

        }
    }
}
