package com.example.liftandpay_passenger.SearchedRide;

import static com.example.liftandpay_passenger.fastClass.DistanceCalc.distanceBtnCoordinates;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.liftandpay_passenger.R;
import com.example.liftandpay_passenger.fastClass.StringFunction;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.google.type.LatLng;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class SearchedRides extends AppCompatActivity {

    //Variables and Widgets for RecyclerView Adapter
    private RecyclerView recyclerView;
    private SearchView searchView;
    ArrayList<carBookingModel> carholder;
    ArrayList<carBookingModel> filteredTrip = new ArrayList<>();

    private ImageButton timeFlt, dateFlt, seatFlt , fltBtn;

    private DatePicker datePicker;
    private TimePicker timePicker;
    private EditText seatPicker;

    private int filterValue;

    private TextView backBtn, availableRidesText;

    LatLng endCord;
    LatLng stCord;


    //Firebase components declarations
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searched_rides);


        dateFlt = findViewById(R.id.dateFilterId);
        timeFlt = findViewById(R.id.timeFilterId);
        seatFlt = findViewById(R.id.seatFilterId);
        fltBtn = findViewById(R.id.filterBtnId);

        datePicker = findViewById(R.id.datePickerId);
        timePicker = findViewById(R.id.timePickerId);
        seatPicker = findViewById(R.id.seatPickerId);

        recyclerView = findViewById(R.id.searchedRidesRecycler);
        carholder = new ArrayList<>();
        Bundle bundle = getIntent().getExtras();
        String stLocs = bundle.getString("stLoc");
        String endLocs = bundle.getString("endLoc");

        Log.e("Locs001", stLocs);
        Log.e("Locs002", endLocs);


        availableRidesText = findViewById(R.id.availableRides);
        backBtn = findViewById(R.id.availableBckBtn);

        backBtn.setOnClickListener(view -> {
            finish();
        });


        db.collection("Rides")
                .whereNotEqualTo("driversStatus", "Cancelled")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        carholder.clear();

                        Log.i("Search", "Completed");

                        if (task.isSuccessful()) {
                            Log.i("Search", "Successful");
                            Snackbar.make(recyclerView, "Successful", Snackbar.LENGTH_SHORT).show();

                            for (QueryDocumentSnapshot snapshots : task.getResult()) {

                                Snackbar.make(recyclerView, "Fetching ...", 5000).show();

                                String name = Objects.requireNonNull(snapshots.getData().getOrDefault("driverName", "NO NAME")).toString();
                                String endLoc = Objects.requireNonNull(snapshots.getData().getOrDefault("endLocation", "null")).toString();
                                String stLoc = Objects.requireNonNull(snapshots.getData().getOrDefault("startLocation", "null")).toString();
                                String rideCost = Objects.requireNonNull(snapshots.getData().getOrDefault("rideCost", "null")).toString();
                                String rideDate = Objects.requireNonNull(snapshots.getData().getOrDefault("rideDate", "null")).toString();
                                String rideTime = Objects.requireNonNull(snapshots.getData().getOrDefault("rideTime", "null")).toString();
                                double startLat = (double) snapshots.getData().getOrDefault("startLat", null);
                                double startLon = (double) snapshots.getData().getOrDefault("startLon", null);
                                double endLon = (double) snapshots.getData().getOrDefault("endLon", null);
                                double endLat = (double) snapshots.getData().getOrDefault("endLat", null);
                                int numberOfSeats = Integer.parseInt(snapshots.getData().get("Number Of Occupants").toString());


                                Log.e("Name", stLoc + " ->" + endLoc + "");

                                double sLat = Double.parseDouble(new StringFunction(stLocs).splitStringWithAndGet(",", 0));
                                Log.e("sLat", sLat + "");

                                double sLon = Double.parseDouble(new StringFunction(stLocs).splitStringWithAndGet(",", 1));
                                Log.e("sLon", sLon + "");

                                double eLat = Double.parseDouble(new StringFunction(endLocs).splitStringWithAndGet(",", 0));
                                Log.e("eLon", eLat + "");

                                double eLon = Double.parseDouble(new StringFunction(endLocs).splitStringWithAndGet(",", 1));
                                Log.e("eLat", eLon + "");

                                double startingDistance = distanceBtnCoordinates(startLat, startLon, sLat, sLon);
                                Log.e("startingDistance", startingDistance + "");

                                double endingDistance = distanceBtnCoordinates(endLat, endLon, eLat, eLon);
                                Log.e("endingDistance", endingDistance + "");


                                if (startingDistance <= 4 && endingDistance <= 4) {
                                    carBookingModel carBookingModel =
                                            new carBookingModel(
                                                    name,
                                                    numberOfSeats,
                                                    stLoc,
                                                    endLoc,
                                                    rideCost,
                                                    rideDate,
                                                    rideTime,
                                                    snapshots.getId(),
                                                    startLat, startLon,
                                                    endLat, endLon
                                            );

                                    carholder.add(carBookingModel);
                                }


                            }
                            recyclerView.setLayoutManager(new LinearLayoutManager(SearchedRides.this, LinearLayoutManager.VERTICAL, false));
                            recyclerView.setAdapter(new carBookAdapter(carholder, SearchedRides.this));

                            Snackbar.make(recyclerView, "Completed", Snackbar.LENGTH_SHORT).show();

                        } else {
                            Log.i("Search", "Failed");
                            Snackbar.make(recyclerView, "Not Successful", Snackbar.LENGTH_SHORT).show();
                        }

                        task.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SearchedRides.this, "Failing to connect to internet connection"
                                        , Toast.LENGTH_LONG).show();

                                Log.e("Searched Rides", e.getMessage());
                            }
                        });


                        /*//Activate Filter Actions*/
                        // TimeFilter
                        timeFlt.setOnClickListener(View -> {
                            switchFilterAction(toTIMEFILTER);
                        });

                        //DateFilter
                        dateFlt.setOnClickListener(View -> {
                            switchFilterAction(toDATEFILTER);
                        });

                        //SeatFilter
                        seatFlt.setOnClickListener(View -> {
                            switchFilterAction(toSEATFILTER);
                        });

                    }
                });



        //When the filter button is clicked
        fltBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //check fo the filterValue to perform action

                //filterValue 100 means date, 200 means time, 300 means seats
                if(filterValue == 100){

                }
                if (filterValue == 200){

                }
                if(filterValue == 300){
                    filteredTrip.clear();

                    for (carBookingModel newTrip : carholder) {

                        if (Integer.valueOf(newTrip.getNumberOfSeats()).equals(Integer.valueOf(seatPicker.getText().toString()))) {
                            filteredTrip.add(newTrip);
                        }

                    }
                    recyclerView.setLayoutManager(new LinearLayoutManager(SearchedRides.this, LinearLayoutManager.VERTICAL, false));
                    recyclerView.setAdapter(new carBookAdapter(filteredTrip, SearchedRides.this));

                }
            }
        });






    }


    String toTIMEFILTER = "TIME";
    String toDATEFILTER = "DATE";
    String toSEATFILTER = "SEATS";




    /**
     * Switches among the filter buttons
     * <p>
     * toTIMEFILTER, toDATEFILTER, toSEATFILTER
     *
     * @param btnAction One of {@link #toTIMEFILTER}, {@link #toDATEFILTER} or {@link #toSEATFILTER}.
     * @attr ref android.R.styleable#View_visibility
     */
    void switchFilterAction(String btnAction) {

        switch (btnAction) {

            case "DATE":
                Log.i("Filter Action", "Date Filter");
                //unselect all filters and select date filter;
                datePicker.setVisibility(View.VISIBLE);
                timePicker.setVisibility(View.GONE);
                seatPicker.setVisibility(View.GONE);
                filterValue = 100;
                break;


            case "TIME":
                Log.i("Filter Action", "Time Filter");
                //unselect all filters and select time filter;
                datePicker.setVisibility(View.GONE);
                timePicker.setVisibility(View.VISIBLE);
                seatPicker.setVisibility(View.GONE);
                filterValue = 200;
                break;

            case "SEATS":
                Log.i("Filter Action", "Seats Filter");
                //unselect all filters and select seat filter;
                datePicker.setVisibility(View.GONE);
                timePicker.setVisibility(View.GONE);
                seatPicker.setVisibility(View.VISIBLE);
                filterValue = 300;

                break;

            default:
                Log.i("Filter Action", "No Filter");

                datePicker.setVisibility(View.GONE);
                timePicker.setVisibility(View.GONE);
                seatPicker.setVisibility(View.GONE);
                filterValue = 000;

        }
    }


}

