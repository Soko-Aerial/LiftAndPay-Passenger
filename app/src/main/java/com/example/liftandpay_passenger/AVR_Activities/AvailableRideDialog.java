package com.example.liftandpay_passenger.AVR_Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.liftandpay_passenger.AVR_Activities.Chats.ChatActivity_avr;
import com.example.liftandpay_passenger.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class AvailableRideDialog extends AppCompatActivity {


    TextView bookRide;
    ImageView chatDriver;
    private String mainDriverId;
    private double startLat;
    private double startLon;
    private double endLat;
    private double endLon;
    private String rideDriverId;
    private String startTime;
    private String distance;
    private String journey;





    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.available_ride_dialog, container, false);

        bookRide = v.findViewById(R.id.driverNxtBtnId);
        chatDriver = v.findViewById(R.id.messageBtn);

        chatDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AvailableRideDialog.this, ChatActivity_avr.class);
                startActivity(intent);
            }
        });

        bookRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AvailableRideDialog.this, PickUpLocationActivity.class);
                intent.putExtra("endLat",endLat);
                intent.putExtra("endLon", endLon);
                intent.putExtra("startLat",startLat);
                intent.putExtra("startLon", startLon);
                intent.putExtra("distance", distance);
                intent.putExtra("journey", journey);
                intent.putExtra("startTime", startTime);
                startActivity(intent);
            }
        });
        return v;
    }

    public void setMainDriverId(String mainDriverId) {
        this.mainDriverId = mainDriverId;
        /*the mainDriverId is the id on the firestore database. It has the Driver's id without an index attached to it. Can be accessed from the Drivers collection*/
    }

    public void setRideDriverId(String rideDriverId) {
        this.rideDriverId = rideDriverId;
        /*the rideDriverId is the id on the firestore database. It has the Driver's id with an index attached to it. Can be accessed from the Rides collection*/
    }

    public void setStartLat(double startLat) {
        this.startLat = startLat;
    }

    public void setStartLon(double startLon) {
        this.startLon = startLon;
    }

    public void setEndLat(double endLat) {
        this.endLat = endLat;
    }

    public void setEndLon(double endLon) {
        this.endLon = endLon;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setJourney(String journey) {
        this.journey = journey;
    }
}
