package com.example.liftandpay_passenger;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class completedRidesAdapter extends RecyclerView.Adapter<completedRidesAdapter.myviewholder> {
    ArrayList<completedRidesModel> dataholder;


    public completedRidesAdapter(ArrayList<completedRidesModel> dataholder) {
        this.dataholder = dataholder;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_design,parent,false);
        return  new myviewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
        holder.img.setImageResource(dataholder.get(position).getImage());
        holder.header.setText(dataholder.get(position).getHeader());
        holder.desc.setText(dataholder.get(position).getDesc());
        holder.distance.setText(dataholder.get(position).getDistance());
        holder.price.setText(dataholder.get(position).getPrice());
    }

    @Override
    public int getItemCount() {
        return dataholder.size();
    }

    class myviewholder extends RecyclerView.ViewHolder{
        ImageView img;
        TextView header,desc,distance,price;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img1);
            header = itemView.findViewById(R.id.t1);
            desc = itemView.findViewById(R.id.t2);
            distance=itemView.findViewById(R.id.distance);
            price = itemView.findViewById(R.id.price);

        }
    }
}