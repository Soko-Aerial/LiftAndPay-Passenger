package com.example.liftandpay_passenger.MainActivities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.airbnb.lottie.LottieAnimationView;
import com.example.liftandpay_passenger.AVR_Activities.AvailableRides;
import com.example.liftandpay_passenger.MainActivities.Rides.PendingRideMapActivity;
import com.example.liftandpay_passenger.ProfileSetup.ProfileSettings;
import com.example.liftandpay_passenger.R;
import com.example.liftandpay_passenger.SearchedRide.SearchedRides;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liftandpay_passenger.fastClass.PassengersPreferedRideNotificationWorker;
import com.example.liftandpay_passenger.fastClass.StringFunction;
import com.example.liftandpay_passenger.ridePrefs.RidePreference;
import com.example.liftandpay_passenger.search.SearchActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.mapbox.mapboxsdk.Mapbox;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

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

    private ActivityResultLauncher activityResultLauncher;

    private TextView locationsDesc, dateAndTime, amount, originToDestination;
    private TextView driverName, carNumberPlate;

    private LinearLayout driverDetails;

    private LinearLayout historyBtn, profileBtn, paymentBtn;

    private TextView cancelBtn;

    String infoNameS, infoLonS, infoLatS;
    String infoNameD, infoLonD, infoLatD;

    private TextView statusActionBtn, rideViewText;
    private TextView statusId, rideStatus;

    private Vibrator vibrator;
    private TextToSpeech textToSpeech;
    private SharedPreferences lastRideSharedPrefs;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    private ShapeableImageView driverImage, myImage;
    private TextView profileSettings;
    private TextView setPref;

    private LottieAnimationView clockAnimate;



    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);
        Mapbox.getInstance(MainFragment.this, getString(R.string.mapbox_access_token));


        checkUsersDetails();

        dialogBuilder = new AlertDialog.Builder(MainFragment.this);
        lastRideSharedPrefs = getSharedPreferences("LAST_BOOKED_RIDE_FILE", MODE_PRIVATE);

        profileSettings = findViewById(R.id.profileSettings);

        myImage = findViewById(R.id.myImage);

        clockAnimate = findViewById(R.id.reverseClock);
        setPref = findViewById(R.id.setPref);
        driverImage = findViewById(R.id.driverImageId);
        myImage = findViewById(R.id.myImage);
        floatbtn = findViewById(R.id.floatBtn);
        rideSearchbtn = findViewById(R.id.ridSearchBtn);
        searchOrigin = findViewById(R.id.originSearchId);
        searchDestination = findViewById(R.id.destinationSearchId);
        connectorView = findViewById(R.id.connectorId);
        lowerView = findViewById(R.id.rideViewDetails);
        parentView = findViewById(R.id.parentView);

        driverDetails = findViewById(R.id.driverDetails);

        cancelBtn = findViewById(R.id.cancelBtnId);

        locationsDesc = findViewById(R.id.locationDesc);
        dateAndTime = findViewById(R.id.dateAndTime);
        originToDestination = findViewById(R.id.originToDestination);
        amount = findViewById(R.id.amount);

        driverName = findViewById(R.id.driverNameId);
        carNumberPlate = findViewById(R.id.carNumberPlate);
        statusActionBtn = findViewById(R.id.statusActionBtn);
        rideViewText = findViewById(R.id.rideViewText);
        statusId = findViewById(R.id.myStatusId);
        rideStatus = findViewById(R.id.driverStatusId);

        historyBtn = findViewById(R.id.rideHistoryBtn);
        paymentBtn = findViewById(R.id.paymentBtn);
        profileBtn = findViewById(R.id.profileBtn);




        ArrayAdapter<CharSequence> profileSeq = ArrayAdapter.createFromResource(this,R.array.profile_settings,R.layout.single_input_model);
        profileSeq.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        clockAnimate =findViewById(R.id.reverseClock);
        clockAnimate.animate().setDuration(4000).setStartDelay(4000);
//        clockAnimate.reverseAnimationSpeed();

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        textToSpeech = new TextToSpeech(MainFragment.this,
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        textToSpeech.setLanguage(Locale.UK);
                    }
                });

        profileSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainFragment.this, ProfileSettings.class);
                startActivity(i);
               // mAuth.signOut();
            }
        });

        myImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainFragment.this, ProfileSettings.class);
                startActivity(i);
            }
        });

        setPref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainFragment.this, RidePreference.class);

//                Intent i = new Intent(MainFragment.this, MenuListActivity.class);
                startActivity(i);
//                mAuth.signOut();

                /*Uri uri = Uri.parse("https://liftnpay.gameitupgh.com/public/getform");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);*/

            }
        });

        historyBtn.setOnClickListener(view -> {
            Intent i = new Intent(MainFragment.this, RidesFragment.class);
            startActivity(i);
        });

        paymentBtn.setOnClickListener(view -> {
        });

        profileBtn.setOnClickListener(view -> {
            Intent i = new Intent(MainFragment.this, ProfileSettings.class);
            startActivity(i);
        });

        lowerView.setVisibility(View.GONE);
        statusId.setVisibility(View.INVISIBLE);
        rideStatus.setVisibility(View.INVISIBLE);
        rideViewText.setVisibility(View.INVISIBLE);

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
                    Intent intent = new Intent(MainFragment.this, SearchedRides.class);
                    intent.putExtra("stLoc", infoLatS + "," + infoLonS);
                    intent.putExtra("endLoc", infoLatD + "," + infoLonD);
                    startActivity(intent);
                }
            }
        });


        floatbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    AuthUI.getInstance()
                            .signOut(MainFragment.this)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                public void onComplete(@NonNull Task<Void> task) {
                                    // user is now signed out
                                    startActivity(new Intent(MainFragment.this, MainActivity.class));
                                    finish();
                                }
                            });

//                mAuth.signOut();
                /*Intent intent = new Intent(MainFragment.this, SignUp003.class);
                startActivity(intent);*/
            }
        });


        storage.getReference().child("Passenger").child(Objects.requireNonNull(mAuth.getUid())).child("profile.png").getDownloadUrl().addOnCompleteListener(
                new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Uri> task) {

                        if (task.isSuccessful()) {
                            Picasso.get().load(task.getResult().toString()).into(myImage);
                        }

                    }
                }
        );


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


                                    db.collection("Rides").document(lastBookedRide)
                                            .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                @Override
                                                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                                                    Log.e("BookedRides001", "Completed");

                                                    assert value != null;
                                                    if (value.exists()) {
                                                        Log.e("BookedRides001", "Exists");

                                                        String driverStatus =": "+ value.getString("driversStatus");

                                                        rideStatus.setText(driverStatus);

                                                        value.getReference().collection("Booked By").document(mAuth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onEvent(@Nullable DocumentSnapshot value1, @Nullable FirebaseFirestoreException error) {

                                                                Log.e("BookedRides001", "Is Listening for data");

                                                                lowerView.setVisibility(View.VISIBLE);
                                                                statusId.setVisibility(View.VISIBLE);
                                                                rideStatus.setVisibility(View.VISIBLE);
                                                                rideViewText.setVisibility(View.VISIBLE);

                                                                lastRideSharedPrefs.edit().putString("TheLocationDesc", value1.getString("Location Desc")).apply();
                                                                lastRideSharedPrefs.edit().putString("Status", value1.getString("Status")).apply();
                                                                lastRideSharedPrefs.edit().putFloat("ThePickupLon", Float.parseFloat(String.valueOf(value1.getDouble("Long")))).apply();
                                                                lastRideSharedPrefs.edit().putFloat("ThePickupLat", Float.parseFloat(String.valueOf(value1.getDouble("Lat")))).apply();

                                                                String status = value1.getString("Status");
                                                                statusId.setText(status);

                                                                Log.e("Status", status);

                                                                if (status.equals("Driver almost there"))
                                                                {
                                                                  vibrator.vibrate(3000);
                                                                  textToSpeech.speak(status, TextToSpeech.QUEUE_FLUSH, null,status);
                                                                  dialogBuilder.setView(LayoutInflater.from(MainFragment.this).inflate(R.layout.dialog_status_dialog, null));
                                                                  dialogBuilder.setCancelable(false);
                                                                  dialogBuilder.setPositiveButton("Alright", new DialogInterface.OnClickListener() {
                                                                      @Override
                                                                      public void onClick(DialogInterface dialog, int which) {
                                                                          dialog.dismiss();
                                                                      }
                                                                  });
                                                                  dialog = dialogBuilder.show();


                                                                  new Handler().postDelayed(new Runnable() {
                                                                      @Override
                                                                      public void run() {


                                                                      }
                                                                  },10000);
                                                                }

                                                                //Check if Status on database is empty. If it is empty, Print Current Ride.
                                                                assert status != null;
                                                                if (!status.trim().equals(""))
                                                                    statusId.setText(status);
                                                                else
                                                                    statusId.setText("Current Ride");


                                                                locationsDesc.setText(value1.getString("Location Desc"));


                                                                value.getReference().addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {


                                                                        assert value != null;
                                                                        if(value.exists()){

                                                                            double stLat = value.getDouble("startLat");
                                                                            double stLon = value.getDouble("startLon");
                                                                            double eLat = value.getDouble("endLat");
                                                                            double eLon = value.getDouble("endLon");

                                                                            lastRideSharedPrefs.edit().putString("TheMainDriverId", value.getString("myId"));
                                                                            lastRideSharedPrefs.edit().putString("theDriverId", new StringFunction(lastBookedRide).splitStringWithAndGet(" ", 0)).apply();
                                                                            lastRideSharedPrefs.edit().putString("theDriverPhone", value.getString("phone number")).apply();
                                                                            lastRideSharedPrefs.edit().putFloat("theStartLon", Float.parseFloat(String.valueOf(stLon))).apply();
                                                                            lastRideSharedPrefs.edit().putFloat("theStartLat", Float.parseFloat(String.valueOf(stLat))).apply();
                                                                            lastRideSharedPrefs.edit().putFloat("theEndLon", Float.parseFloat(String.valueOf(eLon))).apply();
                                                                            lastRideSharedPrefs.edit().putFloat("theEndLat", Float.parseFloat(String.valueOf(eLat))).apply();
                                                                            lastRideSharedPrefs.edit().putString("theRideCost", value.getString("rideCost")).apply();
                                                                            lastRideSharedPrefs.edit().putString("theRideTime", value.getString("rideTime")).apply();
                                                                            lastRideSharedPrefs.edit().putString("theRideDate", value.getString("rideDate")).apply();
                                                                            lastRideSharedPrefs.edit().putString("theDriverName", value.getString("driverName")).apply();
                                                                            lastRideSharedPrefs.edit().putString("theStartLocation", value.getString("startLocation")).apply();
                                                                            lastRideSharedPrefs.edit().putString("theEndLocation", value.getString("endLocation")).apply();


                                                                            String originDestination = value.getString("startLocation") + "-" + value.getString("endLocation");
                                                                            String dateTime = value.getString("rideDate") + " " + value.getString("rideTime");

                                                                            dateAndTime.setText(dateTime);
                                                                            originToDestination.setText(originDestination);

                                                                            amount.setText(new StringFunction(value.getString("rideCost")).splitStringWithAndGet("/", 0));
                                                                            driverName.setText(value.getString("driverName"));
                                                                            carNumberPlate.setText(value.getString("plate"));

                                                                            statusActionBtn.setOnClickListener(Viewv -> {

                                                                                Intent intent = new Intent(MainFragment.this, PendingRideMapActivity.class);
                                                                                intent.putExtra("theDriverId",value.getString("myId") );
                                                                                intent.putExtra("journey", originDestination);
                                                                                intent.putExtra("dateTime", dateTime);
                                                                                intent.putExtra("distance", amount.getText().toString());
                                                                                intent.putExtra("stLat", stLat);
                                                                                intent.putExtra("stLon", stLon);
                                                                                intent.putExtra("endLat", eLat);
                                                                                intent.putExtra("endLon", eLon);
                                                                                intent.putExtra("rideId", lastBookedRide);
                                                                                intent.putExtra("driverName", driverName.getText().toString());
                                                                                intent.putExtra("plate", carNumberPlate.getText().toString());

                                                                                Log.i("TheDriverID",intent.getStringExtra("theDriverId"));

                                                                                startActivity(intent);
                                                                            });


                                                                            cancelBtn.setOnClickListener(new View.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(View view) {

                                                                                    AlertDialog.Builder builder
                                                                                            = new AlertDialog
                                                                                            .Builder(MainFragment.this);

                                                                                    // Set the message show for the Alert time
                                                                                    builder.setMessage("Do you want to cancel this ride?");
                                                                                    builder.setTitle("Cancel");
                                                                                    builder.setCancelable(true);
                                                                                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                                            dialog.dismiss();
                                                                                        }
                                                                                    }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(DialogInterface dialog, int which) {
//                                                                                            deleteTheRide(lastAvailableRideId);
                                                                                            dialog.dismiss();
                                                                                        }
                                                                                    });
                                                                                    builder.create().show();

                                                                                }
                                                                            });

                                                                            driverDetails.setOnClickListener(new View.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(View v) {
                                                                                    Intent i = new Intent(MainFragment.this, AvailableRides.class);
                                                                                    i.putExtra("Purpose", "ForView");
                                                                                    i.putExtra("theDriverId",value.getString("myId") );
                                                                                    i.putExtra("theDriverId",value.getString("myId") );
                                                                                    i.putExtra("theJourney", value.getString("startLocation") + "-" + value.getString("endLocation"));
                                                                                    i.putExtra("theTime", dateTime);
                                                                                    i.putExtra("theDistance", amount.getText().toString());
                                                                                    i.putExtra("startLat", stLat);
                                                                                    i.putExtra("startLon", stLon);
                                                                                    i.putExtra("endLat", eLat);
                                                                                    i.putExtra("endLon", eLon);
                                                                                    i.putExtra("theRideDriverId", lastBookedRide);
                                                                                    i.putExtra("theDriverName", driverName.getText().toString());
                                                                                    i.putExtra("plate", carNumberPlate.getText().toString());

                                                                                    startActivity(i);
                                                                                }





                                                                            });

                                                                            storage.getReference().child("Driver").child(value.getString("myId")).child("profile.png").getDownloadUrl().addOnCompleteListener(
                                                                                    new OnCompleteListener<Uri>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull @NotNull Task<Uri> task) {

                                                                                            if (task.isSuccessful()) {
                                                                                                Picasso.get().load(task.getResult().toString()).into(driverImage);
                                                                                            }

                                                                                        }
                                                                                    }
                                                                            );

                                                                        }
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


        startNotificationWorker(MainFragment.this);


    }


    @Override
    public void onResume() {
        super.onResume();
        checkUsersDetails();

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
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {

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
                infoLatD = String.valueOf(data.getExtras().getDouble("theLat", 0.0));

                Log.i("onActivity001", "Lon :" + infoLonD);
                Log.i("onActivity001", "Lat :" + infoLatD);
            }


        }


        if (resultCode == Activity.RESULT_OK && requestCode == 110) {

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

    private void checkUsersDetails(){
        db.collection("Passenger").document(mAuth.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if (!value.contains("Name") || !value.contains("Email")){
                    Intent i = new Intent(MainFragment.this, ProfileSettings.class );
                    startActivity(i);
                }

            }
        });
    }

    static void startNotificationWorker(Context context) {
        OneTimeWorkRequest passengerRideNotificationWorker = new OneTimeWorkRequest.Builder(PassengersPreferedRideNotificationWorker.class).build();
        WorkManager.getInstance(context).enqueue(passengerRideNotificationWorker);
    }


}
