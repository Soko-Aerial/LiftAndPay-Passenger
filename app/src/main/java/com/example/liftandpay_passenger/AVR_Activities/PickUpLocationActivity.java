package com.example.liftandpay_passenger.AVR_Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liftandpay_passenger.MainActivities.MainActivity;
import com.example.liftandpay_passenger.MainActivities.RidesFragment;
import com.example.liftandpay_passenger.R;
import com.example.liftandpay_passenger.overpass.model;
import com.example.liftandpay_passenger.overpass.modelface;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

import static com.example.liftandpay_passenger.fastClass.DistanceCalc.distanceBtnCoordinates;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
import static com.mapbox.mapboxsdk.style.layers.Property.VISIBLE;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.visibility;

public class PickUpLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    //Shared prefferences
    private SharedPreferences sharedPreferences;
    private SharedPreferences sharedPreferencesAVR;

    //mapbox variables
    private MapView mapView;
    private MapboxMap mapboxMap;
    Button selectLocationButton;

    //layers
    private final String geojsonSourceLayerId = "geojsonSourceLayerId";
    private final String symbolIconId = "symbolIconId";
    private final String mapBoxStyleUrl = "mapbox://styles/hubert-brako/cknk4g1t6031l17to153efhbs";

    //markers
    private ImageView hoveringMarker;
    private Layer droppedMarkerLayer;
    private Style myStyle;


    //location variables
    private LatLng myLoc;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationComponent locationComponent;
    private PermissionsManager permissionsManager;

    //routing variables
    private DirectionsRoute currentRoute;
    private static final String TAG = "DirectionsActivity";
    private NavigationMapRoute navigationMapRoute;

    //Firebase variables
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String thePassengersId = FirebaseAuth.getInstance().getUid();

    //HashMap variables
    private Map<String, Object> passengerBookingInfo;

    //Text view declaration
    private TextView startTimeText, distanceText, journeyText;

    private TextView mapTitleID;

    private TextInputEditText inputEditText;

    private SymbolManager symbolManager;

    private List<Feature> symbolLayerIconFeatureList = new ArrayList<>();
    private LatLng points, pointd;

    private Point originPoint, destinationPoint;
    //Overpass Retrofit Variable declaration
    private Retrofit retrofit;
    private modelface modelface;
    private Call<model> call;
    int i, z;

    private String myName;
    private ArrayList<String> bookedRides = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(PickUpLocationActivity.this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_check_map);

        selectLocationButton = findViewById(R.id.mapButtonId);
        selectLocationButton.setText("Pickup From This Location");

        startTimeText = findViewById(R.id.startTimeId);
        distanceText = findViewById(R.id.distanceId);
        journeyText = findViewById(R.id.journeyId);

        startTimeText.setText(getIntent().getStringExtra("startTime"));
        distanceText.setText(getIntent().getStringExtra("distance"));
        journeyText.setText(getIntent().getStringExtra("journey"));


        retrofit = new Retrofit.Builder()
                .baseUrl("https://overpass-api.de/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        modelface = retrofit.create(modelface.class);

        call = modelface.getData();

        mapTitleID = findViewById(R.id.mapTitleId);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(PickUpLocationActivity.this);
    }


    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {

        sharedPreferences = getApplicationContext().getSharedPreferences("RIDEFILE", MODE_PRIVATE);
        sharedPreferencesAVR = getApplicationContext().getSharedPreferences("AVRDialogFile", MODE_PRIVATE);
        sharedPreferences.edit().putString("TheOrderId", "hELLO").apply();

        this.mapboxMap = mapboxMap;
        locationComponent = mapboxMap.getLocationComponent();


        //fetch starting and ending points from the previous activity
        double stLat = getIntent().getDoubleExtra("startLat", 0.0);
        double stLon = getIntent().getDoubleExtra("startLon", 0.0);
        double endLat = getIntent().getDoubleExtra("endLat", 0.0);
        double endLon = getIntent().getDoubleExtra("endLon", 0.0);

        //distance between origin and destination. Note that this is not the route distance
        double distanceBtnOriginAndDestination = distanceBtnCoordinates(stLat, stLon, endLat, endLon) / 1.5;

        //check whether the points were well fetched.
        if (stLat != 0.0 || stLon != 0.0 || endLat != 0.0 || endLon != 0.0) {
            points = new LatLng(stLat, stLon);
            pointd = new LatLng(endLat, endLon);

            //limit the camera to the starting and ending points
            LatLngBounds latLngBounds = new LatLngBounds.Builder()
                    .include(points)
                    .include(pointd)
                    .build();

            mapboxMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 150));

            originPoint = Point.fromLngLat(stLon, stLat);
            destinationPoint = Point.fromLngLat(endLon, endLat);


        }
        //Do this if fetching points fail
        else {
            AlertDialog.Builder builder
                    = new AlertDialog
                    .Builder(PickUpLocationActivity.this);

            // Set the message show for the Alert
            builder.setMessage("Unable to fetch cordinates");
            builder.setTitle("Routing Error!");
            builder.setCancelable(true);
            builder.create().show();

        }


        //if the fetched cordinates is within the waypoints


        mapboxMap.setStyle(new Style.Builder().fromUri(mapBoxStyleUrl).withImage("ICON_ID", BitmapFactory.decodeResource(
                PickUpLocationActivity.this.getResources(), R.drawable.bus_stop)).withSource((new GeoJsonSource("SOURCE_ID",
                FeatureCollection.fromFeatures(symbolLayerIconFeatureList)))).withLayer(new SymbolLayer("LAYER_ID", "SOURCE_ID")
                .withProperties(
                        iconImage("ICON_ID"),
                        iconAllowOverlap(true),
                        iconIgnorePlacement(true))), new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {

                myStyle = style;
                addMarker(myStyle, points, "origin", R.drawable.markerselected, "Start");
                addMarker(myStyle, pointd, "destination", R.drawable.markerselected, "Destination");
                // Add the symbol layer icon to map for future use
                style.addImage(symbolIconId, BitmapFactory.decodeResource(
                        PickUpLocationActivity.this.getResources(), R.drawable.mapbox_logo_icon));

                setUpSource(style);
                setupLayer(style);
                addDestinationIconSymbolLayer(style);

                //Route between the two points
                getRoute(originPoint, destinationPoint);


                hoveringMarker = new ImageView(PickUpLocationActivity.this);
                hoveringMarker.setImageResource(R.drawable.markerselecting);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);


                hoveringMarker.setLayoutParams(params);
                mapView.addView(hoveringMarker);

                selectLocationButton.setOnClickListener(view -> {
                    if (hoveringMarker.getVisibility() == View.VISIBLE) {
                        final LatLng mapTargetLatLng = mapboxMap.getCameraPosition().target;
                        hoveringMarker.setImageResource(R.drawable.markerselected);
                        selectLocationButton.setBackgroundColor(
                                ContextCompat.getColor(PickUpLocationActivity.this, R.color.black));
                        selectLocationButton.setText("Generating location ...");

                        // Show the SymbolLayer icon to represent the selected map location
                        if (style.getLayer("DROPPED_MARKER_LAYER_ID") != null) {
                            GeoJsonSource source = style.getSourceAs("dropped-marker-source-id");
                            if (source != null) {
                                source.setGeoJson(Point.fromLngLat(mapTargetLatLng.getLongitude(), mapTargetLatLng.getLatitude()));
                            }
                            droppedMarkerLayer = style.getLayer("DROPPED_MARKER_LAYER_ID");
                            if (droppedMarkerLayer != null) {
                                droppedMarkerLayer.setProperties(visibility(VISIBLE));
                            }
                        }

                        passengerPickUpLocMarker(Point.fromLngLat(mapTargetLatLng.getLongitude(), mapTargetLatLng.getLatitude()));

                    } else {

                        // Switch the button appearance back to select a location.
                        selectLocationButton.setBackgroundColor(
                                ContextCompat.getColor(PickUpLocationActivity.this, R.color.primaryColors));
                        selectLocationButton.setText("Pickup from this location");

                        hoveringMarker.setImageResource(R.drawable.markerselecting);

                        droppedMarkerLayer = style.getLayer("DROPPED_MARKER_LAYER_ID");
                        if (droppedMarkerLayer != null) {
                            droppedMarkerLayer.setProperties(visibility(Property.NONE));
                        }
                    }

                    // Open a dialog box to accept pickup location description
                    AlertDialog dlg = new AlertDialog.Builder(PickUpLocationActivity.this)
                            .setView(R.layout.single_input_model)
                            .setTitle("Describe your pickup location")
                            .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    // Switch the button appearance back to select a location.
                                    selectLocationButton.setBackgroundColor(
                                            ContextCompat.getColor(PickUpLocationActivity.this, R.color.primaryColors));
                                    selectLocationButton.setText("Pickup from this location");

                                    hoveringMarker.setImageResource(R.drawable.markerselecting);

                                    droppedMarkerLayer = style.getLayer("DROPPED_MARKER_LAYER_ID");
                                    if (droppedMarkerLayer != null) {
                                        droppedMarkerLayer.setProperties(visibility(Property.NONE));
                                    }
                                }
                            })
                            .create();


                    dlg.setButton(DialogInterface.BUTTON_POSITIVE, "Book", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            inputEditText = (TextInputEditText) dlg.findViewById(R.id.inputViewId);

                            Toast.makeText(PickUpLocationActivity.this, inputEditText.getText().toString(), Toast.LENGTH_LONG).show();

                            //When Booking is completed
                            String theRideDriverId = sharedPreferencesAVR.getString("TheRideDriverId", "Null");
                            String theMainDriverId = sharedPreferencesAVR.getString("TheMainDriverId", "Null");
                            passengerBookingInfo = new HashMap<>();
                            final LatLng mapTargetLatLng = mapboxMap.getCameraPosition().target;

                            db.collection("Passenger").document(thePassengersId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    //Fetch the name of the passenger and update the database. This will be retrieved by the driver at the requested passenger.
                                    myName = documentSnapshot.getString("Name");

                                    Toast.makeText(PickUpLocationActivity.this, myName, Toast.LENGTH_LONG).show();
                                    passengerBookingInfo.put("Lat", mapTargetLatLng.getLatitude());
                                    passengerBookingInfo.put("Long", mapTargetLatLng.getLongitude());
                                    passengerBookingInfo.put("Name", myName);
                                    passengerBookingInfo.put("Email", Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());
                                    passengerBookingInfo.put("Location Desc", inputEditText.getText().toString());
                                    passengerBookingInfo.put("Status", "Pending Approval");

                                    DocumentReference bookedByRef = db.collection("Rides").document(theRideDriverId).collection("Booked By").document(thePassengersId);
                                    DocumentReference bookedByRefToDriver = db.collection("Driver").document(theMainDriverId).collection("Pending Rides").document(theRideDriverId).collection("Booked By").document(thePassengersId);

                                    DocumentReference myBookedRides = db.collection("Passenger").document(thePassengersId).collection("Rides").document("Pending Rides");
                                    bookedByRef.set(passengerBookingInfo)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    //Get documents for all my pending rides
                                                    myBookedRides.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {

                                                            //Check if task was successful
                                                            if (task.isSuccessful()) {
                                                                Log.e("bookTask001", "Success");

                                                                //Check if I have ever booked a ride before
                                                                if (task.getResult().contains("BookedRides")) {
                                                                    Log.e("bookTask001", "Contains Booked Rides -True");

                                                                    //if yes, then get all my booked rides Id
                                                                    bookedRides = new ArrayList<>((Collection<? extends String>) task.getResult().get("BookedRides"));

                                                                    //add my current ride Id to it
                                                                    bookedRides.add(theRideDriverId);

                                                                    //Then update the all my booked rides with respect to the one I just booked
                                                                    task.getResult().getReference().update("BookedRides", bookedRides).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull @NotNull Task<Void> task002) {
                                                                            Log.e("Task002", "Completed");

                                                                            if (task002.isSuccessful()) {
                                                                                indicateBookedSuccess();
                                                                            }

                                                                        }
                                                                    })
                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {
                                                                                    MainActivity mainActivity = new MainActivity();
                                                                                    Intent intent = new Intent(getApplicationContext(), mainActivity.getClass());
                                                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                                    startActivity(intent);
                                                                                }
                                                                            })
                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    Toast.makeText(getApplicationContext(), "Couldn't add to driver", Toast.LENGTH_LONG).show();
                                                                                }
                                                                            });


                                                                } else {
                                                                    Log.e("bookTask001", "Contains Booked Rides -False");
                                                                    Map<String, Object> pendingRideData = new HashMap<>();
                                                                    bookedRides.add(theRideDriverId);
                                                                    pendingRideData.put("BookedRides", bookedRides);

                                                                    task.getResult().getReference().set(pendingRideData).addOnCompleteListener(new OnCompleteListener<Void>() {

                                                                        @Override
                                                                        public void onComplete(@NonNull @NotNull Task<Void> task) {

                                                                            if (task.isSuccessful()) {
                                                                                indicateBookedSuccess();
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                            } else {
                                                                Log.e("bookTask001", "Failed");
                                                            }


                                                        }
                                                    });


                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getApplicationContext(), "Couldn't add to Rides", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull @NotNull Exception e) {
                                            Toast.makeText(PickUpLocationActivity.this, "Profile not complete: Set Your Name", Toast.LENGTH_LONG).show();
                                            finish();
                                        }
                                    });


                        }
                    });

                    dlg.show();


                });
            }
        });

    }


    private void indicateBookedSuccess() {
        Log.e("Task002", "Successful");


        Snackbar.make(PickUpLocationActivity.this, selectLocationButton, "Booked successfully", 5000)
                .setTextColor(Color.WHITE)
                .setBackgroundTint(getResources().getColor(R.color.mapbox_plugins_green, null)).show();

        selectLocationButton.setBackgroundColor(
                ContextCompat.getColor(PickUpLocationActivity.this, R.color.mapbox_plugins_green));
        selectLocationButton.setText("Update pickup location");

        hoveringMarker.setImageResource(R.drawable.markerselecting);

        droppedMarkerLayer = mapboxMap.getStyle().getLayer("DROPPED_MARKER_LAYER_ID");
        if (droppedMarkerLayer != null) {
            droppedMarkerLayer.setProperties(visibility(Property.NONE));
        }
        Snackbar.make(PickUpLocationActivity.this, selectLocationButton, "Booked successfully", 5000)
                .setTextColor(Color.WHITE)
                .setBackgroundTint(getResources().getColor(R.color.mapbox_plugins_green, null)).show();

        selectLocationButton.setBackgroundColor(
                ContextCompat.getColor(PickUpLocationActivity.this, R.color.mapbox_plugins_green));
        selectLocationButton.setText("Update pickup location");

        hoveringMarker.setImageResource(R.drawable.markerselecting);

        droppedMarkerLayer = mapboxMap.getStyle().getLayer("DROPPED_MARKER_LAYER_ID");
        if (droppedMarkerLayer != null) {
            droppedMarkerLayer.setProperties(visibility(Property.NONE));
        }
    }


    private void addMarker(@NonNull Style style, @NotNull LatLng latLng, String name, int resource, @Nullable String text) {

        style.addImage(name, BitmapFactory.decodeResource(
                PickUpLocationActivity.this.getResources(), resource));

        SymbolOptions symbolOptions = new SymbolOptions()
                .withLatLng(new LatLng(latLng.getLatitude(), latLng.getLongitude()))
                .withIconImage(name)
                .withIconSize(0.5f)
                .withTextField(text)
                .withTextColor("#4688F1");

        symbolManager = new SymbolManager(mapView, mapboxMap, style);
        symbolManager.create(symbolOptions);

    }

    private void addDestinationIconSymbolLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addImage("destination-icon-id",
                BitmapFactory.decodeResource(this.getResources(), R.drawable.mapbox_marker_icon_default));
        GeoJsonSource geoJsonSource = new GeoJsonSource("destination-source-id");
        loadedMapStyle.addSource(geoJsonSource);
        SymbolLayer destinationSymbolLayer = new SymbolLayer("destination-symbol-layer-id", "destination-source-id");
        destinationSymbolLayer.withProperties(
                iconImage("destination-icon-id"),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
        );
        loadedMapStyle.addLayer(destinationSymbolLayer);
    }


    private void passengerPickUpLocMarker(Point point) {
        if (mapboxMap.getStyle() != null) {
            GeoJsonSource sanciangkoFlood1 = mapboxMap.getStyle().getSourceAs("destination-source-id");
            if (sanciangkoFlood1 != null) {
                sanciangkoFlood1.setGeoJson(FeatureCollection.fromFeature(
                        Feature.fromGeometry(Point.fromLngLat(point.longitude(), point.latitude()))
                ));

            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void setUpSource(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource("geojsonSourceLayerId"));
    }

    private void setupLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addLayer(new SymbolLayer("SYMBOL_LAYER_ID", "geojsonSourceLayerId").withProperties(
                iconImage(symbolIconId),
                iconOffset(new Float[]{0f, -8f})
        ));
    }


    private void getRoute(Point origin, Point destination) {
        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(@NotNull Call<DirectionsResponse> call, @NotNull Response<DirectionsResponse> response) {

                        if (response.body() == null) {
                            Timber.e("Routes not generated");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Timber.e("No routes found");
                            return;
                        }

                        currentRoute = response.body().routes().get(0);


                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        Timber.e(t.toString());
                    }
                });
    }



    /* selectLocationButton.setBackgroundColor(
             ContextCompat.getColor(PickUpLocationActivity.this, R.color.primaryColors));
                    selectLocationButton.setText("Pickup from this location");
                    hoveringMarker.setImageResource(R.drawable.markerselecting);*/


    // Add the mapView lifecycle to the activity's lifecycle methods
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
        sharedPreferences.edit().clear().apply();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        sharedPreferences.edit().clear().apply();

    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}