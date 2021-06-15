package com.example.liftandpay_passenger.AVR_Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.liftandpay_passenger.AVR_Activities.Chats.ChatActivity_avr;
import com.example.liftandpay_passenger.CheckMapActivity;
import com.example.liftandpay_passenger.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.plugins.places.picker.PlacePicker;

import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class AvailableRideDialog extends BottomSheetDialogFragment {

    TextView carDetails;
    TextView checkMap;
    TextView bookRide;
    TextView chatDriver;
    private String mainDriverId;
    private double startLat;
    private double startLon;
    private double endLat;
    private double endLon;
    private String rideDriverId;


    private static final int PLACE_SELECTION_REQUEST_CODE = 56789;

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String thePassengerId = mAuth.getUid();


    private Map<String, Object> ride;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.available_ride_dialog, container, false);

        carDetails = v.findViewById(R.id.carDetails_avrId);
        checkMap = v.findViewById(R.id.checkMap_avrId);
        bookRide = v.findViewById(R.id.bookRide_avrId);
        chatDriver = v.findViewById(R.id.chatDriver_avrId);


        carDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getContext(), CarDetailsActivity_avr.class);
                startActivity(intent);
            }
        });

        checkMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getContext(), CheckMapActivity.class);
               startActivity(intent);
            }
        });

        chatDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ChatActivity_avr.class);
                startActivity(intent);
            }
        });

        bookRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PickUpLocationActivity.class);
                intent.putExtra("endLat",endLat);
                intent.putExtra("endLon", endLon);
                intent.putExtra("startLat",startLat);
                intent.putExtra("startLon", startLon);
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
}
