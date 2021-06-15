package com.example.liftandpay_passenger.SearchedRide;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.liftandpay_passenger.AVR_Activities.AvailableRideDialog;
import com.example.liftandpay_passenger.R;
import com.example.liftandpay_passenger.fastClass.StringFunction;
import com.mapbox.mapboxsdk.geometry.LatLng;


import java.util.ArrayList;

public class carBookAdapter extends RecyclerView.Adapter<carBookAdapter.myViewHolder> {
    ArrayList<carBookingModel> carHolder;
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

        holder.amount.setText(carHolder.get(position).getCostPerKilometer().toString());
        holder.startLocation.setText(carHolder.get(position).getStartLocation());
        holder.endLocation.setText(carHolder.get(position).getEndLocation());
        holder.date.setText(carHolder.get(position).getDateId());
        holder.time.setText(carHolder.get(position).getTimeId());
        holder.rideDriverId = carHolder.get(position).getDriverId();

        holder.stLat = carHolder.get(position).getStLat();
        holder.stLon = carHolder.get(position).getStLon();
        holder.endLat = carHolder.get(position).getEndLat();
        holder.endLon = carHolder.get(position).getEndLon();
        holder.driverId = new StringFunction(holder.rideDriverId).removeLastNumberOfCharacter(2);



        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AvailableRideDialog availableRideDialog = new AvailableRideDialog();

              SharedPreferences sharedPreferences = carBookContext.getSharedPreferences("AVRDialogFile",Context.MODE_PRIVATE);
              
                sharedPreferences.edit().putString("TheMainDriverId",holder.driverId).apply();
                sharedPreferences.edit().putString("TheRideDriverId",holder.rideDriverId).apply();

                availableRideDialog.setMainDriverId(holder.driverId);
                availableRideDialog.setRideDriverId(holder.rideDriverId);
                availableRideDialog.setEndLat(holder.endLat);
                availableRideDialog.setEndLon(holder.endLon);
                availableRideDialog.setStartLat(holder.stLat);
                availableRideDialog.setStartLon(holder.stLon);
                FragmentManager manager = ((AppCompatActivity) carBookContext).getSupportFragmentManager();
                availableRideDialog.show(manager, null);

            }
        });



    }

    @Override
    public int getItemCount() {
        return carHolder.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        LinearLayout itemLayout;
        ImageView image;
        TextView amount;
        TextView startLocation;
        TextView endLocation;
        TextView date;
        TextView time;
        String driverId;
        String rideDriverId;
        double stLat,stLon;
        double endLat, endLon;



        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.carID);
            amount = itemView.findViewById(R.id.amountPerKilometer);
            startLocation = itemView.findViewById(R.id.startLocationId);
            endLocation = itemView.findViewById(R.id.endLocationId);
            date = itemView.findViewById(R.id.dateModelId);
            time = itemView.findViewById(R.id.timeModelId);

            itemLayout = itemView.findViewById(R.id.rideItemId);

        }
    }
}