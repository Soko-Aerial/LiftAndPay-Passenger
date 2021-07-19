package com.example.liftandpay_passenger.SearchedRide;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liftandpay_passenger.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.type.LatLng;

import java.util.ArrayList;
import java.util.Objects;

public class SearchedRides extends AppCompatActivity {

    //Variables and Widgets for RecyclerView Adapter
    private RecyclerView recyclerView;
    private SearchView searchView;
    ArrayList<carBookingModel> carholder;

    private TextView backBtn;

    LatLng endCord;
    LatLng stCord;


    //Firebase components declarations
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searched_rides);


        recyclerView = findViewById(R.id.searchedRidesRecycler);
        carholder = new ArrayList<>();
        Bundle bundle = getIntent().getExtras();
        String stLocs = bundle.getString("stLoc");
        String endLocs = bundle.getString("endLoc");

        backBtn =  findViewById(R.id.availableBckBtn);

        backBtn.setOnClickListener( view->{
            finish();
        });


        db.collection("Rides")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Snackbar.make(recyclerView, "Completed", Snackbar.LENGTH_SHORT).show();

                        carholder.clear();

                        if(task.isSuccessful())
                        {

                            for(QueryDocumentSnapshot snapshots : task.getResult()){
                              String name =  Objects.requireNonNull(snapshots.getData().getOrDefault("driverName","null")).toString();
                              String endLoc =  Objects.requireNonNull(snapshots.getData().getOrDefault("endLocation","null")).toString();
                              String stLoc = Objects.requireNonNull(snapshots.getData().getOrDefault("startLocation","null")).toString();
                              String rideCost = Objects.requireNonNull(snapshots.getData().getOrDefault("Ride Cost","null")).toString();
                              String rideDate = Objects.requireNonNull(snapshots.getData().getOrDefault("Ride Date","null")).toString();
                              String rideTime = Objects.requireNonNull(snapshots.getData().getOrDefault("Ride Time","null")).toString();
                              double startLat = (double) snapshots.getData().getOrDefault("startLat",null);
                              double startLon = (double) snapshots.getData().getOrDefault("startLon",null);
                              double endLon = (double) snapshots.getData().getOrDefault("endLon",null);
                              double endLat = (double) snapshots.getData().getOrDefault("endLat",null);
                              int numberOfSeats = Integer.parseInt(snapshots.getData().get("Number Of Occupants").toString());



                               if (endLoc.toUpperCase().trim().equals(endLocs.toUpperCase().trim()) && stLoc.toUpperCase().trim().equals(stLocs.toUpperCase().trim())){
                                   carBookingModel carBookingModel =
                                           new carBookingModel(
                                                   name,
                                                   numberOfSeats,
                                                   stLoc,
                                                   endLoc,
                                                   rideCost,
                                                   rideDate,
                                                   rideTime,
                                                   snapshots.getId().toString(),
                                                   startLat,startLon,
                                                   endLat,endLon);
                                   carholder.add(carBookingModel);
                               }


                            }
                            recyclerView.setLayoutManager(new LinearLayoutManager(SearchedRides.this,LinearLayoutManager.VERTICAL,false));
                            recyclerView.setAdapter(new carBookAdapter(carholder, SearchedRides.this));


                        }
                       task.addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception e) {
                               Toast.makeText(SearchedRides.this, e.getMessage()
                                       , Toast.LENGTH_LONG).show();
                           }
                       });


                    }
                });

    }


}

