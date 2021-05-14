package com.example.liftandpay_passenger;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

import static java.security.AccessController.getContext;

public class SearchedRides extends AppCompatActivity {

    //Variables and Widgets for RecyclerView Adapter
    private RecyclerView recyclerView;
    private SearchView searchView;
    ArrayList<carBookingModel> carholder;


    //Firebase components declarations
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searched_rides);


        recyclerView = findViewById(R.id.searchedRidesRecycler);
        carholder = new ArrayList<>();


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

                               String endLoc =  Objects.requireNonNull(snapshots.getData().getOrDefault("endLocation","null")).toString();
                               String stLoc = Objects.requireNonNull(snapshots.getData().getOrDefault("startLocation","null")).toString();
                               String rideCost = Objects.requireNonNull(snapshots.getData().getOrDefault("Ride Cost","null")).toString();
                               String rideDate = Objects.requireNonNull(snapshots.getData().getOrDefault("Ride Date","null")).toString();
                               String rideTime = Objects.requireNonNull(snapshots.getData().getOrDefault("Ride Time","null")).toString();


                                carBookingModel carBookingModel =
                                        new carBookingModel(1,stLoc,endLoc, rideCost,rideDate,rideTime, snapshots.getId().toString());
                                carholder.add(carBookingModel);

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