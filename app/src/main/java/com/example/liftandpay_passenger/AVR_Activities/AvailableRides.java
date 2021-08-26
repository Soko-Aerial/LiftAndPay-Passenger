package com.example.liftandpay_passenger.AVR_Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.liftandpay_passenger.AVR_Activities.Chats.ChatActivity_avr;
import com.example.liftandpay_passenger.ImageViewActivity;
import com.example.liftandpay_passenger.R;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import nl.nos.imagin.Imagin;
import nl.nos.imagin.SingleTapHandler;

public class AvailableRides extends AppCompatActivity {

    TextView bookRide;
    ImageView chatDriver;
    private String mainDriverId, name, purpose;
    private double startLat;
    private double startLon;
    private double endLat;
    private double endLon;
    private String rideDriverId;
    private String startTime;
    private String distance;
    private String journey;

    private ImageView image001,image002, image003, image004;

    private TextView journeyText, nameText;

    private LinearLayout driverDetailsFooter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_rides);

        bookRide = findViewById(R.id.driverNxtBtnId);
        chatDriver = findViewById(R.id.messageBtn);
        journeyText =  findViewById(R.id.journeyId);
        driverDetailsFooter = findViewById(R.id.driverProfileFooter);

        nameText = findViewById(R.id.nameId);
        image001 = findViewById(R.id.image001);
        image002 = findViewById(R.id.image002);
        image003 = findViewById(R.id.image003);
        image004 = findViewById(R.id.image004);

        //from carBookAdapter.java
        purpose = getIntent().getStringExtra("Purpose");
        mainDriverId = getIntent().getStringExtra("theDriverId");
        startLat = getIntent().getDoubleExtra("startLat",0.0);
        startLon =getIntent().getDoubleExtra("startLon",0.0);
        endLat =getIntent().getDoubleExtra("endLat",0.0);
        endLon =getIntent().getDoubleExtra("endLon",0.0);
        rideDriverId =getIntent().getStringExtra("theRideDriverId");
        startTime =getIntent().getStringExtra("theTime");
        journey = getIntent().getStringExtra("theJourney");
        distance =getIntent().getStringExtra("theDistance");
        name = getIntent().getStringExtra("theName" );

        journeyText.setText(journey);
        nameText.setText(name);

        if (purpose.equals("ForView")){
            driverDetailsFooter.setVisibility(View.GONE);
        }

        chatDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AvailableRides.this, ChatActivity_avr.class);
                startActivity(intent);
            }
        });


        image001.setOnClickListener(view->{
            Intent intent = new Intent(AvailableRides.this, ImageViewActivity.class);
            startActivity(intent);
        });
        image002.setOnClickListener(view->{
            Intent intent = new Intent(AvailableRides.this, ImageViewActivity.class);
            startActivity(intent);
        });
        image003.setOnClickListener(view->{
            Intent intent = new Intent(AvailableRides.this, ImageViewActivity.class);
            startActivity(intent);
        });
        image004.setOnClickListener(view->{
            Intent intent = new Intent(AvailableRides.this, ImageViewActivity.class);
            startActivity(intent);
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