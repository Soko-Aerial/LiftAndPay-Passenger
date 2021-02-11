package com.example.liftandpay_passenger;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class carBookAdapter extends RecyclerView.Adapter<carBookAdapter.myViewHolder> {
    ArrayList<carBookingModel> carHolder;

    public carBookAdapter(ArrayList<carBookingModel> carHolder){
        this.carHolder = carHolder;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_car_item_card,parent, false);

        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {


    }

    @Override
    public int getItemCount() {
        return carHolder.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.carID);
        }
    }
}
