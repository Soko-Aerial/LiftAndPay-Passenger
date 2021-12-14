package com.example.liftandpay_passenger.MainActivities.Rides;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liftandpay_passenger.AVR_Activities.Chats.ChatActivity_avr;
import com.example.liftandpay_passenger.MainActivities.MainActivity;
import com.example.liftandpay_passenger.MainActivities.MainFragment;
import com.example.liftandpay_passenger.R;
import com.example.liftandpay_passenger.fastClass.Ratings;
import com.example.liftandpay_passenger.fastClass.StringFunction;
import com.google.android.gms.location.FusedLocationProviderClient;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
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
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.visibility;

import org.jetbrains.annotations.NotNull;

public class PendingRideMapActivity extends FragmentActivity implements OnMapReadyCallback, PermissionsListener {

    private TextView actionBtn;
    private ImageView callBtn, chatBtn;
    private ProgressBar progressBar;

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private ShapeableImageView profileImg;

    private final String geojsonSourceLayerId = "geojsonSourceLayerId";
    private final String destinationSource = "destination-source-id";
    private ValueAnimator animator;

    private final String symbolIconId = "symbolIconId";
    private final String mapBoxStyleUrl = "mapbox://styles/hubert-brako/cknk4g1t6031l17to153efhbs";
    private final String carIconId = "car-icon-id";

    private LatLng myLoc;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationComponent locationComponent;
    private LocationEngine locationEngine;
    private PermissionsManager permissionsManager;
    private SharedPreferences sharedPreferences;
    private ProgressBar routeProgress;

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {Manifest.permission.CALL_PHONE};
    String[] LOCATIONPERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};

    private DirectionsRoute currentRoute;
    private static final String TAG = "DirectionsActivity";
    private NavigationMapRoute navigationMapRoute;
    private String rideId;
    private String driverId;
    private String rideStatus, passengerStatus;

    private Vibrator vibrator;
    private TextToSpeech textToSpeech;

    private TextView driverName, carNumberPlate;
    private String mUid = FirebaseAuth.getInstance().getUid();
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    private Map<String, Object> passengerLoc = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(PendingRideMapActivity.this, getString(R.string.mapbox_access_token));

        setContentView(R.layout.activity_pending_ridemap);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        actionBtn = findViewById(R.id.actionBtn);
        callBtn = findViewById(R.id.callBtn);
        chatBtn = findViewById(R.id.chatBtn);
        profileImg = findViewById(R.id.driverImageId);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        textToSpeech = new TextToSpeech(PendingRideMapActivity.this,
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        textToSpeech.setLanguage(Locale.UK);
                    }
                });

        rideId = getIntent().getStringExtra("rideId");
        driverId = getIntent().getStringExtra("theDriverId");

        driverName = findViewById(R.id.driverNameId);
        driverName.setText(getIntent().getStringExtra("driverName"));

        carNumberPlate = findViewById(R.id.carNumberPlate);
        carNumberPlate.setText(getIntent().getStringExtra("plate"));

        if (!hasPermissions(PendingRideMapActivity.this, LOCATIONPERMISSIONS)) {
            ActivityCompat.requestPermissions(PendingRideMapActivity.this, LOCATIONPERMISSIONS, PERMISSION_ALL);
//            Toast.makeText(PendingRideMapActivity.this,"Location permission not enabled", Toast.LENGTH_LONG).show();
            finish();
        }else {

        }

        dialogBuilder = new AlertDialog.Builder(PendingRideMapActivity.this);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(PendingRideMapActivity.this);


    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {


        this.mapboxMap = mapboxMap;
        locationComponent = mapboxMap.getLocationComponent();

        mapboxMap.setStyle(new Style.Builder().fromUri(mapBoxStyleUrl), new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                // Add the symbol layer icon to map for future use
                style.addImage(symbolIconId, BitmapFactory.decodeResource(
                        PendingRideMapActivity.this.getResources(), R.drawable.mapbox_logo_icon));
                // Create an empty GeoJSON source using the empty feature collection
                setUpSource(style);
                // Set up a new symbol layer for displaying the searched location's feature coordinates
                setupLayer(style);

                addDestinationIconSymbolLayer(style);


                callBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!hasPermissions(PendingRideMapActivity.this, PERMISSIONS)) {
                            ActivityCompat.requestPermissions(PendingRideMapActivity.this, PERMISSIONS, PERMISSION_ALL);
                        } else {
                            db.collection("Driver")
                                    .document(new StringFunction(rideId).splitStringWithAndGet(" ", 0))
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                            String phone;
                                            if (task.getResult().get("Phone") != null)
                                                phone = String.valueOf(task.getResult().get("Phone"));
                                            else
                                                phone = " No Number";
                                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                                            callIntent.setData(Uri.parse("tel:" + phone));
                                            startActivity(callIntent);

                                        }
                                    });

                        }
                    }
                });

                storage.getReference().child("Driver").child(driverId).child("profile.png").getDownloadUrl().addOnCompleteListener(
                        new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<Uri> task) {

                                if (task.isSuccessful()) {
                                    Picasso.get().load(task.getResult().toString()).into(profileImg);
                                }

                            }
                        }
                );

                chatBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(PendingRideMapActivity.this, ChatActivity_avr.class);
                        startActivity(i);
                    }
                });

            }
        });



        db.collection("Rides").document(rideId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value001, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {

                //Switch among passenger's status
                value001.getReference().
                        collection("Booked By").
                        document(mUid)
                        .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {


                                if (value.getString("Status").equals("Approved")) {
                                    switchAmongDriversStatuses(value001);
                                }
                                if (value.getString("Status").equals("Declined")) {

                                }
                                if (value.getString("Status").equals("Driver almost there")) {
                                    vibrator.vibrate(3000);
                                    textToSpeech.speak(value.getString("Status"), TextToSpeech.QUEUE_FLUSH, null, value.getString("Status"));
                                    dialogBuilder.setView(LayoutInflater.from(PendingRideMapActivity.this).inflate(R.layout.dialog_status_dialog, null));
                                    dialogBuilder.setCancelable(false);
                                    dialogBuilder.setPositiveButton("Alright", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    dialog = dialogBuilder.show();
                                }
                                if (value.getString("Status").equals("Cancelled")) {
                                    //don't show driver's location
                                    switchActionBtn("Oww, You cancelled the ride", R.color.orange, true);

                                }
                                if (value.getString("Status").equals("Picked")) {
                                    switchAmongDriversStatuses(value001);
                                    switchActionBtn("You have been picked", R.color.success, false);

                                    //Confirm pickup

                                }
                                if (value.getString("Status").equals("Dropped")) {
                                    //Confirm drop off
                                    switchActionBtn("You have arrived at your destination", R.color.success, true);

                                }
                            }
                        });
            }
        });


        fusedLocationProviderClient = getFusedLocationProviderClient(PendingRideMapActivity.this);

        if (ActivityCompat.checkSelfPermission(PendingRideMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                .addOnSuccessListener(PendingRideMapActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        Log.i("Fire","Location");

                        myLoc = new LatLng(location.getLatitude(), location.getLongitude());

                        double stLat = getIntent().getDoubleExtra("stLat", 0);
                        double stLon = getIntent().getDoubleExtra("stLon", 0);
                        double endLat = getIntent().getDoubleExtra("endLat", 0);
                        double endLon = getIntent().getDoubleExtra("endLon", 0);

//                        actionBtn.setText(stLat +"\n"+ stLon+"\n"+ endLat+"\n"+ endLon);
                        if (stLat != 0 && stLon != 0 && endLat != 0 && endLon != 0) {

                            LatLng points = new LatLng(stLat, stLon);
                            LatLng pointd = new LatLng(endLat, endLon);


                            LatLngBounds latLngBounds = new LatLngBounds.Builder()
                                    .include(points)
                                    .include(pointd)
                                    .include(myLoc)
                                    .build();
                            mapboxMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 150));

                            Point destinationPoint = Point.fromLngLat(pointd.getLongitude(), pointd.getLatitude());
                            Point originPoint = Point.fromLngLat(points.getLongitude(), points.getLatitude());

                            getRoute(originPoint, destinationPoint);
                        } else {
                            Toast.makeText(getApplicationContext(), "The Cordinates are null, Route could not render", Toast.LENGTH_LONG).show();
                        }

                    }
                });


    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    private void switchAmongDriversStatuses(DocumentSnapshot value) {
        //Switch among drivers status
        if (Objects.equals(value.getString("driversStatus"), "Started")) {
            Toast.makeText(PendingRideMapActivity.this, "Started", Toast.LENGTH_LONG).show();
            addMarkerToDestination(value.getDouble("driversLat"), value.getDouble("driversLon"));
        }

        if (Objects.equals(value.getString("driversStatus"), "Cancelled")) {

            switchActionBtn("Sorry driver has cancelled the Ride", R.color.orange, true);
            //don't read driver's location
        }
    }


    @SuppressLint("WrongConstant")
    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
// Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
// Activate the MapboxMap LocationComponent to show user location
// Adding in LocationComponentOptions is also an optional parameter

            locationComponent.activateLocationComponent(this, loadedMapStyle);
            locationComponent.setLocationComponentEnabled(true);
            locationComponent.setRenderMode(RenderMode.GPS);


// Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING_GPS);
            locationComponent.tiltWhileTracking(45);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }

    }

    private void switchActionBtn(String actionText, int actionColor, Boolean reviewRequired) {
        actionBtn.setText(actionText);
        actionBtn.setTextColor(ContextCompat.getColor(PendingRideMapActivity.this, actionColor));

        if (reviewRequired) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Ratings ratings = new Ratings(PendingRideMapActivity.this,
                            new StringFunction(rideId).splitStringWithAndGet(" ", 0),
                            passengerStatus,
                            rideStatus);
                }
            }, 5000);
        }

    }


    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "R.string.user_location_permission_explanation", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationComponent(mapboxMap.getStyle());
        } else {
            Toast.makeText(this, "R.string.user_location_permission_not_granted", Toast.LENGTH_LONG).show();
            finish();
        }
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


    private void addDestinationIconSymbolLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addImage("car-icon-id",
                BitmapFactory.decodeResource(this.getResources(), R.drawable.img_car));

        GeoJsonSource geoJsonSource = new GeoJsonSource("destination-source-id");

        SymbolLayer destinationSymbolLayer = new SymbolLayer("destination-symbol-layer-id", "destination-source-id");
        destinationSymbolLayer.withProperties(
                iconImage("car-icon-id"),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
        );

        loadedMapStyle.addLayer(destinationSymbolLayer);
        loadedMapStyle.addSource(geoJsonSource);

    }

    private void addMarkerToDestination(double lat, double lon) {

        GeoJsonSource theMainStyle = mapboxMap.getStyle().getSourceAs(destinationSource);
        if (theMainStyle != null) {
            theMainStyle.setGeoJson(FeatureCollection.fromFeature(
                    Feature.fromGeometry(Point.fromLngLat(lon, lat))
            ));
            animateMarker(new LatLng(lat, lon), new LatLng(lat, lon));

        }

    }


    private void animateMarker(LatLng currentPosition, LatLng point) {

        animator = ObjectAnimator
                .ofObject(latLngEvaluator, currentPosition, point)
                .setDuration(2000);
        animator.addUpdateListener(animatorUpdateListener);
        animator.start();

    }

    private static final TypeEvaluator<LatLng> latLngEvaluator = new TypeEvaluator<LatLng>() {

        private final LatLng latLng = new LatLng();

        @Override
        public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
            latLng.setLatitude(startValue.getLatitude()
                    + ((endValue.getLatitude() - startValue.getLatitude()) * fraction));
            latLng.setLongitude(startValue.getLongitude()
                    + ((endValue.getLongitude() - startValue.getLongitude()) * fraction));
            return latLng;
        }
    };

    private final ValueAnimator.AnimatorUpdateListener animatorUpdateListener =
            new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    LatLng animatedPosition = (LatLng) valueAnimator.getAnimatedValue();
                    GeoJsonSource theMainStyle = mapboxMap.getStyle().getSourceAs(destinationSource);
                    theMainStyle.setGeoJson(Point.fromLngLat(animatedPosition.getLongitude(), animatedPosition.getLatitude()));
                }
            };


    private void getRoute(Point origin, Point destination) {
        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
// You can get the generic HTTP info about the response
                        Log.d("TAG", "Response code: " + response.code());
                        if (response.body() == null) {
                            Timber.e("No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Toast.makeText(PendingRideMapActivity.this, "Couldn't generate route", Toast.LENGTH_LONG).show();
                            return;
                        }

                        currentRoute = response.body().routes().get(0);

// Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap, R.style.NavigationMapRoute);
                            navigationMapRoute.addRoute(currentRoute);
                        }
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
//                        routeProgress.setVisibility(View.INVISIBLE);
                        Toast.makeText(PendingRideMapActivity.this, "002:" + t.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(PendingRideMapActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    }
                }
                return;
            }
        }
    }

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


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}