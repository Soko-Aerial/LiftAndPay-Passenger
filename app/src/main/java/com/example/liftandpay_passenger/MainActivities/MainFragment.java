package com.example.liftandpay_passenger.MainActivities;

import static android.content.Context.MODE_PRIVATE;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.fragment.app.Fragment;

import com.example.liftandpay_passenger.AVR_Activities.AvailableRides;
import com.example.liftandpay_passenger.MainActivities.Rides.PendingRideMapActivity;
import com.example.liftandpay_passenger.R;
import com.example.liftandpay_passenger.SearchedRide.SearchedRides;
import com.example.liftandpay_passenger.SettingUp.SignUp003;
import com.example.liftandpay_passenger.fastClass.LongiLati;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liftandpay_passenger.fastClass.StringFunction;
import com.example.liftandpay_passenger.search.SearchActivity;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mapbox.mapboxsdk.Mapbox;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import nl.nos.imagin.Imagin;
import nl.nos.imagin.SingleTapHandler;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

public class MainFragment extends AppCompatActivity {


    TextView searchOrigin;
    TextView searchDestination;

    private TextView rideSearchbtn;
    private FloatingActionButton floatbtn;
    View connectorView;
    LinearLayout lowerView;
    LinearLayout parentView;

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String json = null;

    private   ActivityResultLauncher activityResultLauncher;

    private TextView locationsDesc, dateAndTime, amount, originToDestination;
    private TextView driverName, carNumberPlate;

    private LinearLayout driverDetails;

    private LinearLayout historyBtn, profileBtn, paymentBtn;

    String infoNameS, infoLonS, infoLatS;
    String infoNameD, infoLonD, infoLatD;

    private TextView statusActionBtn,rideViewText;

    private SharedPreferences lastRideSharedPrefs;


    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);
        Mapbox.getInstance( MainFragment.this, getString(R.string.mapbox_access_token));


        lastRideSharedPrefs = getSharedPreferences("LAST_BOOKED_RIDE_FILE", MODE_PRIVATE);

        floatbtn = findViewById(R.id.floatBtn);
        rideSearchbtn = findViewById(R.id.ridSearchBtn);
        searchOrigin = findViewById(R.id.originSearchId);
        searchDestination = findViewById(R.id.destinationSearchId);
        connectorView = findViewById(R.id.connectorId);
        lowerView = findViewById(R.id.rideViewDetails);
        parentView = findViewById(R.id.parentView);

        driverDetails = findViewById(R.id.driverDetails);

        locationsDesc = findViewById(R.id.locationDesc);
        dateAndTime = findViewById(R.id.dateAndTime);
        originToDestination = findViewById(R.id.originToDestination);
        amount = findViewById(R.id.amount);

        driverName = findViewById(R.id.driverNameId);
        statusActionBtn = findViewById(R.id.statusActionBtn);
        rideViewText = findViewById(R.id.rideViewText);

        historyBtn = findViewById(R.id.rideHistoryBtn);
        paymentBtn = findViewById(R.id.paymentBtn);
        profileBtn = findViewById(R.id.profileBtn);


        historyBtn.setOnClickListener(view ->{
            Intent i = new Intent(MainFragment.this, RidesFragment.class);
            startActivity(i);
        });

        paymentBtn.setOnClickListener(view ->{
            Intent i = new Intent(MainFragment.this, PayFragment.class);
            startActivity(i);
        });

        profileBtn.setOnClickListener(view ->{
            Intent i = new Intent(MainFragment.this, ProfileFragment.class);
            startActivity(i);
        });




        lowerView.setVisibility(View.GONE);

        searchOrigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainFragment.this, SearchActivity.class);
                startActivityIfNeeded(i, 110);
            }
        });

        searchDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainFragment.this, SearchActivity.class);
                startActivityIfNeeded(i, 100);
            }
        });

        rideSearchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchOrigin.getText().toString().toUpperCase().trim().equals("") ||
                        searchDestination.getText().toString().toUpperCase().trim().equals("")) {
                    Toast.makeText(MainFragment.this, "Can't Search", Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(MainFragment.this   , SearchedRides.class);
                    intent.putExtra("stLoc", infoLatS + ","+ infoLonS);
                    intent.putExtra("endLoc", infoLatD + ","+ infoLonD);
                    startActivity(intent);
                }
            }
        });


        floatbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                mAuth.signOut();
                Intent intent = new Intent(MainFragment.this, SignUp003.class);
                startActivity(intent);
            }
        });




        db.collection("Passenger").document(mAuth.getUid()).collection("Rides").document("Pending Rides").get(Source.SERVER)
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        Log.e("BookedRides", "Completed");

                        if (task.isSuccessful()) {

                            Log.e("BookedRides", "Successful");

                            if (task.getResult().contains("BookedRides")) {

                                Log.e("BookedRides", "Contains Some Rides");

                                ArrayList<String> bookedRides = (ArrayList<String>) task.getResult().get("BookedRides");

                                if (!bookedRides.isEmpty()) {

                                    Log.e("BookedRides", "Is not empty");

                                    String lastBookedRide = bookedRides.get(bookedRides.size() - 1);

                                    db.collection("Rides").document(lastBookedRide).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            Log.e("BookedRides001", "Completed");

                                            if (task.getResult().exists()) {
                                                Log.e("BookedRides001", "Exists");


                                                task.getResult().getReference().collection("Booked By").document(mAuth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                                                        Log.e("BookedRides001", "Is Listening for data");

                                                        lowerView.setVisibility(View.VISIBLE);

                                                        lastRideSharedPrefs.edit().putString("TheLocationDesc", value.getString("Location Desc")).apply();
                                                        lastRideSharedPrefs.edit().putString("Status", value.getString("Status")).apply();
                                                        lastRideSharedPrefs.edit().putFloat("ThePickupLon", Float.parseFloat(String.valueOf(value.getDouble("Long")))).apply();
                                                        lastRideSharedPrefs.edit().putFloat("ThePickupLat", Float.parseFloat(String.valueOf(value.getDouble("Lat")))).apply();


                                                        String status = value.getString("Status");

                                                        Log.e("Status",status);

                                                        //Check if Status on database is empty. If it is empty, Print Current Ride.

                                                        assert status != null;
                                                        if (!status.trim().equals(""))
                                                        rideViewText.setText(status);
                                                        else
                                                        rideViewText.setText("Current Ride");


                                                        locationsDesc.setText(value.getString("Location Desc"));


                                                        task.getResult().getReference().addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                                                                double stLat =value.getDouble("startLat");
                                                                double stLon =value.getDouble("startLon");
                                                                double eLat = value.getDouble("endLat");
                                                                double eLon = value.getDouble("endLon");

                                                                lastRideSharedPrefs.edit().putString("theDriverId", value.getString("myId")).apply();
                                                                lastRideSharedPrefs.edit().putString("theDriverPhone", value.getString("phone number")).apply();
                                                                lastRideSharedPrefs.edit().putFloat("theStartLon", Float.parseFloat(String.valueOf(stLon))).apply();
                                                                lastRideSharedPrefs.edit().putFloat("theStartLat", Float.parseFloat(String.valueOf(stLat))).apply();
                                                                lastRideSharedPrefs.edit().putFloat("theEndLon", Float.parseFloat(String.valueOf(eLon))).apply();
                                                                lastRideSharedPrefs.edit().putFloat("theEndLat", Float.parseFloat(String.valueOf(eLat))).apply();
                                                                lastRideSharedPrefs.edit().putString("theRideCost", value.getString("Ride Cost")).apply();
                                                                lastRideSharedPrefs.edit().putString("theRideTime", value.getString("Ride Time")).apply();
                                                                lastRideSharedPrefs.edit().putString("theRideDate", value.getString("Ride Date")).apply();
                                                                lastRideSharedPrefs.edit().putString("theDriverName", value.getString("driverName")).apply();
                                                                lastRideSharedPrefs.edit().putString("theStartLocation", value.getString("startLocation")).apply();
                                                                lastRideSharedPrefs.edit().putString("theEndLocation", value.getString("endLocation")).apply();


                                                                String originDestination = value.getString("startLocation") + "-" + value.getString("endLocation");
                                                                String dateTime = value.getString("Ride Date") + " " + value.getString("Ride Time");

                                                                dateAndTime.setText(dateTime);
                                                                originToDestination.setText(originDestination);

                                                                amount.setText(new StringFunction(value.getString("Ride Cost")).splitStringWithAndGet("/",0));
                                                                driverName.setText(value.getString("driverName"));

                                                                statusActionBtn.setOnClickListener(Viewv ->{
                                                                    Intent intent = new Intent(MainFragment.this, PendingRideMapActivity.class);
                                                                    intent.putExtra("journey",originDestination);
                                                                    intent.putExtra("dateTime",dateTime);
                                                                    intent.putExtra("distance",amount.getText().toString());
                                                                    intent.putExtra("stLat",stLat);
                                                                    intent.putExtra("stLon",stLon);
                                                                    intent.putExtra("endLat",eLat);
                                                                    intent.putExtra("endLon",eLon);
                                                                    intent.putExtra("rideId",lastBookedRide);

                                                                    startActivity(intent);
                                                                });

                                                                driverDetails.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        Intent i = new Intent(MainFragment.this, AvailableRides.class);
                                                                        i.putExtra("Purpose","ForView");
                                                                        startActivity(i);
                                                                    }
                                                                });
                                                            }
                                                        });

                                                    }
                                                });
                                            }
                                        }
                                    });
                                } else {
                                    Log.e("BookedRides", "Is empty");
                                }
                            } else {
                                Log.e("BookedRides", "Does not contain bookedrides");

                            }
                        } else {
                            Log.e("BookedRides", "Is not successful");

                        }
                    }
                });


    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lastRideSharedPrefs.edit().clear().apply();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("onActivity001", "started");
        if (resultCode == Activity.RESULT_OK && requestCode == 100 ) {

            Log.e("onActivity001", "Result is ok");

            if (data != null) {
                Log.e("onActivity001", "Data not null");

                if (data.hasExtra(new SearchActivity().theNameID))
                    Log.e("onActivity001", "has Name");
                if (data.hasExtra(new SearchActivity().theLatID))
                    Log.e("onActivity001", "has Latitude");
                if (data.hasExtra(new SearchActivity().theLonID))
                    Log.e("onActivity001", "has Longitude");
                String datas = data.getExtras().getString("theLocationName") + " " + data.getExtras().getDouble("theLat", 0.0) + " " + data.getExtras().getDouble("theLon", 0.0);
                Log.e("onActivity001-Data", datas);

                searchDestination.setText(data.getExtras().getString("theLocationName"));

                Log.e("Result", data.getExtras().getString(new SearchActivity().theNameID));
                infoNameD = data.getExtras().getString("theLocationName");
                infoLonD = String.valueOf(data.getExtras().getDouble("theLon", 0.0));
                infoLatD  = String.valueOf(data.getExtras().getDouble("theLat", 0.0));

                Log.i("onActivity001", "Lon :" + infoLonD);
                Log.i("onActivity001", "Lat :" + infoLatD);
            }


        }


        if (resultCode == Activity.RESULT_OK && requestCode == 110 ) {

            Log.e("onActivity001", "Result is ok");

            if (data != null) {
                Log.e("onActivity001", "Data not null");

                if (data.hasExtra(new SearchActivity().theNameID))
                    Log.e("onActivity001", "has Name");
                if (data.hasExtra(new SearchActivity().theLatID))
                    Log.e("onActivity001", "has Latitude");
                if (data.hasExtra(new SearchActivity().theLonID))
                    Log.e("onActivity001", "has Longitude");
                String datas = data.getExtras().getString("theLocationName") + " " + data.getExtras().getDouble("theLat", 0.0) + " " + data.getExtras().getDouble("theLon", 0.0);
                Log.e("onActivity001-Data", datas);

                searchOrigin.setText(data.getExtras().getString("theLocationName"));

                Log.e("Result", data.getExtras().getString(new SearchActivity().theNameID));
                 infoNameS = data.getExtras().getString("theLocationName");
                 infoLonS = String.valueOf(data.getExtras().getDouble("theLon", 0.0));
                 infoLatS = String.valueOf(data.getExtras().getDouble("theLat", 0.0));

                Log.i("onActivity001", "Lon :" + infoLonS);
                Log.i("onActivity001", "Lat :" + infoLatS);
            }


        }
    }
}