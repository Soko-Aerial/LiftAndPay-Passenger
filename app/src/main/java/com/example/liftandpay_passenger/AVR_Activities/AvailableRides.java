package com.example.liftandpay_passenger.AVR_Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.liftandpay_passenger.AVR_Activities.Chats.ChatActivity_avr;
import com.example.liftandpay_passenger.R;

public class AvailableRides extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_rides);

        bookRide = findViewById(R.id.driverNxtBtnId);
        chatDriver = findViewById(R.id.messageBtn);

        mainDriverId = getIntent().getStringExtra("theDriverId");
        startLat = getIntent().getDoubleExtra("startLat",0.0);
        startLon =getIntent().getDoubleExtra("startLon",0.0);
        endLat =getIntent().getDoubleExtra("endLat",0.0);
        endLon =getIntent().getDoubleExtra("endLon",0.0);
        rideDriverId =getIntent().getStringExtra("theRideDriverId");
        startTime =getIntent().getStringExtra("theTime");
        journey = getIntent().getStringExtra("theJourney");
        distance =getIntent().getStringExtra("theDistance");


        chatDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AvailableRides.this, ChatActivity_avr.class);
                startActivity(intent);
            }
        });

        bookRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AvailableRides.this, PickUpLocationActivity.class);
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
    }


}