package com.example.liftandpay_passenger.MainActivities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.liftandpay_passenger.R;
import com.example.liftandpay_passenger.MainActivities.Rides.pendingRidesAdapter;
import com.example.liftandpay_passenger.MainActivities.Rides.pendingRidesModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class RidesFragment extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<pendingRidesModel> dataholder;

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String mUId = mAuth.getUid();
    private pendingRidesModel ob1;

    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pending_rides);
        // Inflate the layout for this fragment

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(RidesFragment.this ,LinearLayoutManager.VERTICAL,false));
        dataholder = new ArrayList<>();


        db.collection("Rides").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()){
                    for (DocumentSnapshot documentSnapshot :  task.getResult())
                    {
                       documentSnapshot.getReference().collection("Booked By").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                           @Override
                           public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task)
                           {
                               if (task.isSuccessful())
                               {
                                   for(DocumentSnapshot documentSnapshot1: task.getResult())
                                   {
                                       if (documentSnapshot1.getId().equals(mUId))
                                       {
                                           String rideId = documentSnapshot.getId();
                                           String rideDistance = documentSnapshot.getString("Ride Distance");
                                           String rideCost = documentSnapshot.getString("Ride Cost");
                                           String status = documentSnapshot1.getString("Status");
                                           String dateTime = documentSnapshot.getString("Ride Date") + " " + documentSnapshot.getString("Ride Time");
                                           String journey = documentSnapshot.getString("startLocation") + " - " + documentSnapshot.getString("endLocation");
                                           double startLat = documentSnapshot.getDouble("startLat");
                                           double endtLat = documentSnapshot.getDouble("endLat");
                                           double startLon = documentSnapshot.getDouble("startLon");
                                           double endLon = documentSnapshot.getDouble("endLon");

                                           ob1 = new pendingRidesModel(journey,dateTime,rideDistance,rideCost, startLat,endtLat,startLon,endLon,status,rideId);
                                           dataholder.add(ob1);
                                       }
                                   }
                                   recyclerView.setAdapter(new pendingRidesAdapter(RidesFragment.this,dataholder));
                               }
                               else
                               {
                                   Toast.makeText(RidesFragment.this,"taskNotSuccessful002",Toast.LENGTH_SHORT).show();
                               }
                           }
                       });
                    }

                }
                else
                {
                    Toast.makeText(RidesFragment.this,"taskNotSuccessful001",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}