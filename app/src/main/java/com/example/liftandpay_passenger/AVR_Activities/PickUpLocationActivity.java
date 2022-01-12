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
import android.location.Location;
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
import com.example.liftandpay_passenger.R;
import com.example.liftandpay_passenger.search.SearchActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.core.permissions.PermissionsListener;
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
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.LocationComponentOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.example.liftandpay_passenger.fastClass.DistanceCalc.distanceBtnCoordinates;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
import static com.mapbox.mapboxsdk.style.layers.Property.VISIBLE;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.visibility;

public class PickUpLocationActivity extends FragmentActivity implements OnMapReadyCallback, PermissionsListener {

    //Shared prefferences
    private SharedPreferences sharedPreferences;
    private SharedPreferences sharedPreferencesAVR;

    private double infoLonD, infoLatD;
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

    private Point pickUpLocationPoint;

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
//    private TextView startTimeText, distanceText, journeyText;

    private TextView mapTitleID;

    private TextInputEditText inputEditText;

    private SymbolManager symbolManager;

    private List<Feature> symbolLayerIconFeatureList = new ArrayList<>();
    private LatLng points, pointd;

    private Point originPoint, destinationPoint;
    //Overpass Retrofit Variable declaration

    private TextView pickupSearchBox;
    private String myName;
    private ArrayList<String> bookedRides = new ArrayList<>();
    private FloatingActionButton useLocationFltBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(PickUpLocationActivity.this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_check_map);

        selectLocationButton = findViewById(R.id.mapButtonId);
        selectLocationButton.setText("Pickup From This Location");

        useLocationFltBtn = findViewById(R.id.useLocationBtn);

        pickupSearchBox = findViewById(R.id.searchPickupId);


        pickupSearchBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PickUpLocationActivity.this, SearchActivity.class);
                startActivityIfNeeded(i, 200);
            }
        });



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

                enableLocationComponent(style);


                hoveringMarker = new ImageView(PickUpLocationActivity.this);
                hoveringMarker.setImageResource(R.drawable.markerselecting);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);


                hoveringMarker.setLayoutParams(params);
                mapView.addView(hoveringMarker);

                   useLocationFltBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final LatLng mapTargetLatLng = mapboxMap.getCameraPosition().target;

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



                            pickUpLocationPoint = passengerPickUpLocMarker(Point.fromLngLat(mapTargetLatLng.getLongitude(), mapTargetLatLng.getLatitude()));

                            infoLatD = pickUpLocationPoint.latitude();
                            infoLonD = pickUpLocationPoint.longitude();
                        }
                    });

                   /*SetLocation click*/
                selectLocationButton.setOnClickListener(view -> {
                 /*   if (hoveringMarker.getVisibility() == View.VISIBLE) {
                        hoveringMarker.setImageResource(R.drawable.markerselected);
                        selectLocationButton.setBackgroundColor(
                                ContextCompat.getColor(PickUpLocationActivity.this, R.color.black));
                        selectLocationButton.setText("Generating location ...");




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
                    }*/



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
                                    passengerBookingInfo.put("Lat", infoLatD);
                                    passengerBookingInfo.put("Long",infoLonD);
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


    private Point passengerPickUpLocMarker(Point point) {
        if (mapboxMap.getStyle() != null) {
            GeoJsonSource sanciangkoFlood1 = mapboxMap.getStyle().getSourceAs("destination-source-id");
            if (sanciangkoFlood1 != null) {
                sanciangkoFlood1.setGeoJson(FeatureCollection.fromFeature(
                        Feature.fromGeometry(Point.fromLngLat(point.longitude(), point.latitude()))
                ));

            }
        }

        return point;
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

    @SuppressLint("WrongConstant")
    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
// Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
// Activate the MapboxMap LocationComponent to show user location
// Adding in LocationComponentOptions is also an optional parameter

            locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(PickUpLocationActivity.this, loadedMapStyle).build());
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setRenderMode(RenderMode.NORMAL);

// Set the component's camera mode



        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }

    }

    /* selectLocationButton.setBackgroundColor(
             ContextCompat.getColor(PickUpLocationActivity.this, R.color.primaryColors));
                    selectLocationButton.setText("Pickup from this location");
                    hoveringMarker.setImageResource(R.drawable.markerselecting);*/


    // Add the mapView lifecycle to the activity's lifecycle methods


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("onActivity001", "started");


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


            pickupSearchBox.setText(data.getExtras().getString("theLocationName"));

            Log.e("Result", data.getExtras().getString(new SearchActivity().theNameID));
            String infoNameD = data.getExtras().getString("theLocationName");
            infoLonD = (data.getExtras().getDouble("theLon", 0.0));
            infoLatD = (data.getExtras().getDouble("theLat", 0.0));

            hoveringMarker.setVisibility(View.INVISIBLE);
            pickUpLocationPoint = passengerPickUpLocMarker(Point.fromLngLat(infoLonD, infoLatD));


        }



    }






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

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {

    }
}