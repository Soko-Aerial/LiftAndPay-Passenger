package com.example.liftandpay_passenger.SearchedRide;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.liftandpay_passenger.AVR_Activities.AvailableRides;
import com.example.liftandpay_passenger.R;
import com.example.liftandpay_passenger.fastClass.StringFunction;
import com.mapbox.mapboxsdk.geometry.LatLng;


import java.util.ArrayList;

public class carBookAdapter extends RecyclerView.Adapter<carBookAdapter.myViewHolder> {
    private ArrayList<carBookingModel> carHolder;
    Context carBookContext;
    public LatLng startLatLng;
    public LatLng endLatLng;

    public carBookAdapter(ArrayList<carBookingModel> carHolder, Context carBookContext){
        this.carHolder = carHolder;
        this.carBookContext = carBookContext;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_car_item_card,parent, false);

        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {

        holder.name.setText(carHolder.get(position).getName());
        holder.amount.setText(carHolder.get(position).getCostPerKilometer().toString());
        holder.startLocation.setText(carHolder.get(position).getStartLocation());
        holder.endLocation.setText(carHolder.get(position).getEndLocation());
        holder.date.setText(carHolder.get(position).getDateId());
        holder.time.setText(carHolder.get(position).getTimeId());
        holder.rideDriverId = carHolder.get(position).getDriverId();
        holder.numberOfSeats.setText("0/"+carHolder.get(position).getNumberOfSeats()+"seats");
        holder.stLat = carHolder.get(position).getStLat();
        holder.stLon = carHolder.get(position).getStLon();
        holder.endLat = carHolder.get(position).getEndLat();
        holder.endLon = carHolder.get(position).getEndLon();
        holder.driverName = carHolder.get(position).getName();
        holder.driverId = new StringFunction(holder.rideDriverId).removeLastNumberOfCharacter(2);



        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedPreferences = carBookContext.getSharedPreferences("AVRDialogFile",Context.MODE_PRIVATE);
              
                sharedPreferences.edit().putString("TheMainDriverId",holder.driverId).apply();
                sharedPreferences.edit().putString("TheRideDriverId",holder.rideDriverId).apply();

                Intent intent = new Intent(carBookContext,AvailableRides.class);
                intent.putExtra("theDriverName", holder.driverName);
                intent.putExtra("theDriverId",holder.driverId);
                intent.putExtra("theRideDriverId",holder.rideDriverId);
                intent.putExtra("endLat",holder.endLat);
                intent.putExtra("endLon",holder.endLon);
                intent.putExtra("startLat",holder.stLat);
                intent.putExtra("startLon",holder.stLon);
                intent.putExtra("theTime", carHolder.get(position).getTimeId());
                intent.putExtra("theDistance",carHolder.get(position).getCostPerKilometer());
                intent.putExtra("theJourney",carHolder.get(position).getStartLocation()+" - "+carHolder.get(position).getEndLocation());
                carBookContext.startActivity(intent);

            }
        });



    }

    @Override
    public int getItemCount() {
        return carHolder.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        LinearLayout itemLayout;
        TextView name;
        TextView amount;
        TextView startLocation;
        TextView endLocation;
        TextView date;
        TextView time;
        TextView numberOfSeats;
        String driverId;
        String rideDriverId;
        String driverName;
        double stLat,stLon;
        double endLat, endLon;



        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            numberOfSeats = itemView.findViewById(R.id.numberOfSeatsId);
            amount = itemView.findViewById(R.id.amountPerKilometer);
            startLocation = itemView.findViewById(R.id.startLocationId);
            endLocation = itemView.findViewById(R.id.endLocationId);
            date = itemView.findViewById(R.id.dateModelId);
            time = itemView.findViewById(R.id.timeModelId);
            name = itemView.findViewById(R.id.driverNameId);

            itemLayout = itemView.findViewById(R.id.rideItemId);

        }
    }
}
