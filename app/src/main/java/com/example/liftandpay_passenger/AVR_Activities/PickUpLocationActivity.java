package com.example.liftandpay_passenger.AVR_Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
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
import com.example.liftandpay_passenger.SettingUp.LogAuth;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.JsonElement;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.core.exceptions.ServicesException;
import com.mapbox.geojson.Feature;
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
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;


import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

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
    private final String mapBoxStyleUrl ="mapbox://styles/hubert-brako/cknk4g1t6031l17to153efhbs";

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

    private TextView mapTitleID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(PickUpLocationActivity.this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_check_map);

        selectLocationButton = findViewById(R.id.mapButtonId);
        selectLocationButton.setText("Pickup From This Location");

        mapTitleID = findViewById(R.id.mapTitleId);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(PickUpLocationActivity.this);

    }


    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {

        sharedPreferences =  getApplicationContext().getSharedPreferences("RIDEFILE",MODE_PRIVATE);
        sharedPreferencesAVR =  getApplicationContext().getSharedPreferences("AVRDialogFile",MODE_PRIVATE);
        sharedPreferences.edit().putString("TheOrderId","hELLO").apply();

        this.mapboxMap = mapboxMap;
        locationComponent = mapboxMap.getLocationComponent();


        mapboxMap.setStyle(new Style.Builder().fromUri(mapBoxStyleUrl), new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {

                myStyle = style;
                // Add the symbol layer icon to map for future use
                style.addImage(symbolIconId, BitmapFactory.decodeResource(
                        PickUpLocationActivity .this.getResources(), R.drawable.mapbox_logo_icon));

                style.addImage("origin",BitmapFactory.decodeResource(
                        PickUpLocationActivity .this.getResources(), R.drawable.markerselected));

                SymbolManager symbolManager = new SymbolManager(mapView, mapboxMap, style);

                symbolManager.setIconAllowOverlap(true);
                symbolManager.setTextAllowOverlap(true);

                SymbolOptions symbolOptions = new SymbolOptions()
                        .withLatLng(new LatLng(mapboxMap.getCameraPosition().target.getLatitude()+0.1, mapboxMap.getCameraPosition().target.getLongitude()+0.1))
                        .withIconImage("origin")
                        .withIconSize(0.5f)
                        .withTextField("Original Destination");

                symbolManager.create(symbolOptions);

                setUpSource(style);
                setupLayer(style);

                hoveringMarker = new ImageView(PickUpLocationActivity.this);
                hoveringMarker.setImageResource(R.drawable.markerselecting);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);

                params.bottomMargin = params.height/2;
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

//                            reverseGeocode(Point.fromLngLat(3.8105194993478397,51.204164723553774));
                        reverseGeocode(Point.fromLngLat(mapTargetLatLng.getLongitude(),mapTargetLatLng.getLatitude()));

                    }
                    else
                        {

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

                    //When Booking is completed
                    String theRideDriverId = sharedPreferencesAVR.getString("TheRideDriverId","Null");
                    String theMainDriverId = sharedPreferencesAVR.getString("TheMainDriverId","Null");
                    passengerBookingInfo = new HashMap<>();
                    final LatLng mapTargetLatLng = mapboxMap.getCameraPosition().target;

                    passengerBookingInfo.put("Lat",mapTargetLatLng.getLatitude());
                    passengerBookingInfo.put("Long",mapTargetLatLng.getLongitude());
                    passengerBookingInfo.put("Name", "Hubert");
                    passengerBookingInfo.put("Email", Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());
                   DocumentReference bookedByRef = db.collection("Rides").document(theRideDriverId).collection("Booked By").document(thePassengersId);
                   DocumentReference bookedByRefToDriver = db.collection("Driver").document(theMainDriverId).collection("Pending Rides").document(theRideDriverId).collection("Booked By").document(thePassengersId);

                   bookedByRef.set(passengerBookingInfo)
                          .addOnCompleteListener(new OnCompleteListener<Void>() {
                              @Override
                              public void onComplete(@NonNull Task<Void> task) {

                                  bookedByRefToDriver.set(passengerBookingInfo)
                                          .addOnCompleteListener(new OnCompleteListener<Void>() {
                                              @SuppressLint("ResourceAsColor")
                                              @Override
                                              public void onComplete(@NonNull Task<Void> task) {
                                                  Snackbar.make(PickUpLocationActivity.this,selectLocationButton,"Booked successfully",5000)
                                                          .setTextColor(Color.WHITE)
                                                          .setBackgroundTint(getResources().getColor(R.color.mapbox_plugins_green)).show();
                                              }
                                          })
                                  .addOnFailureListener(new OnFailureListener() {
                                      @Override
                                      public void onFailure(@NonNull Exception e) {
                                          Toast.makeText(getApplicationContext(), "Couldn't add to driver",Toast.LENGTH_LONG).show();
                                      }
                                  });
                              }
                          })
                   .addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                           Toast.makeText(getApplicationContext(), "Couldn't add to Rides",Toast.LENGTH_LONG).show();
                       }
                   });


                });
            }
        });



        fusedLocationProviderClient = getFusedLocationProviderClient(PickUpLocationActivity.this);

        if (ActivityCompat.checkSelfPermission(PickUpLocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(PickUpLocationActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        myLoc = new LatLng(location.getLatitude(), location.getLongitude());

                        sharedPreferences.edit().putString("TheDriverLatitude", (myLoc.getLatitude()+"")).apply();
                        sharedPreferences.edit().putString("TheDriverLongitude", (myLoc.getLongitude()+"")).apply();

                        String theCurrentLat = sharedPreferences.getString("TheDriverLatitude","Null");
                        String theCurrentLong = sharedPreferences.getString("TheDriverLongitude","Null");

                        if(!theCurrentLat.equals("Null") && !theCurrentLong.equals("Null")) {

                            double stLat = getIntent().getDoubleExtra("startLat",0.0);
                            double stLon = getIntent().getDoubleExtra("startLon",0.0);
                            double endLat = getIntent().getDoubleExtra("endLat",0.0);
                            double endLon = getIntent().getDoubleExtra("endLon",0.0);

                            if (stLat != 0.0 || stLon != 0.0 || endLat != 0.0 || endLon != 0.0 )
                            {
                                LatLng points = new LatLng( stLat, stLon);
                                LatLng pointd = new LatLng(endLat,endLon);

                                LatLngBounds latLngBounds = new LatLngBounds.Builder()
                                        .include(points)
                                        .include(pointd)
                                        .build();

                                mapboxMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 150));

                                Point originPoint = Point.fromLngLat(stLon, stLat);
                                Point destinationPoint = Point.fromLngLat(endLon, endLat);

                                getRoute(originPoint,destinationPoint);
                                addMarker(myStyle,points,"origin",R.drawable.markerselected,"Start");
                                addMarker(myStyle,pointd,"destination",R.drawable.markerselected,"Destination");
                            }
                            else
                            {
                                androidx.appcompat.app.AlertDialog.Builder builder
                                        = new androidx.appcompat.app.AlertDialog
                                        .Builder(PickUpLocationActivity.this);

                                // Set the message show for the Alert time
                                builder.setMessage("Unable to fetch cordinates");
                                builder.setTitle("Routing Error!");
                                builder.setCancelable(true);
                                builder.create().show();

                            }
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "ENABLE_YOUR_LOCATION",Toast.LENGTH_LONG).show();
                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {

                androidx.appcompat.app.AlertDialog.Builder builder
                        = new androidx.appcompat.app.AlertDialog
                        .Builder(PickUpLocationActivity.this);

                // Set the message show for the Alert time
                builder.setMessage("Unable to fetch data");
                builder.setTitle("Routing Error!");
                builder.setCancelable(true);
                builder.create().show();
            }
        });
    }


    private void addMarker(@NonNull Style style, @NotNull LatLng latLng, String name, int resource, @Nullable String text)
    {
        style.addImage(name,BitmapFactory.decodeResource(
                PickUpLocationActivity .this.getResources(), resource ));
        SymbolManager symbolManager = new SymbolManager(mapView, mapboxMap, style);

        symbolManager.setIconAllowOverlap(true);
        symbolManager.setTextAllowOverlap(true);

        SymbolOptions symbolOptions = new SymbolOptions()
                .withLatLng(new LatLng(latLng.getLatitude(), latLng.getLongitude()))
                .withIconImage(name)
                .withIconSize(0.5f)
                .withTextField(text)
                .withTextAnchor("Location")
                .withTextColor("#4688F1");

        symbolManager.create(symbolOptions);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void setUpSource(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addSource(new GeoJsonSource(geojsonSourceLayerId));
    }

    private void setupLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addLayer(new SymbolLayer("SYMBOL_LAYER_ID", geojsonSourceLayerId).withProperties(
                iconImage(symbolIconId),
                iconOffset(new Float[]{0f, -8f})
        ));
    }





    private void getRoute(Point origin, Point destination) {
        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(destination)
                .destination(origin)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {

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


    private void reverseGeocode(final Point point) {

        try {
            MapboxGeocoding client = MapboxGeocoding.builder()
                    .accessToken(getString(R.string.mapbox_access_token))
                    .query(Point.fromLngLat(point.longitude(), point.latitude()))
                    .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS)
                    .build();

            client.enqueueCall(new Callback<GeocodingResponse>() {
                @Override
                public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {

                    if (response.body() != null) {
                        List<CarmenFeature> results = response.body().features();
                        if (results.size() > 0) {
                            CarmenFeature feature = results.get(0);

                            Toast.makeText(PickUpLocationActivity.this,
                                    "Results: "+
                                            feature.placeName(), Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(PickUpLocationActivity.this,
                                    "No results", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<GeocodingResponse> call, Throwable throwable) {
                    Timber.e("Geocoding Failure: %s", throwable.getMessage());


                }
            });
        } catch (ServicesException servicesException) {
            Timber.e("Error geocoding: %s", servicesException.toString());
            servicesException.printStackTrace();
        }
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