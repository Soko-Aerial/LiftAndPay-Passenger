package com.example.liftandpay_passenger.AVR_Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.liftandpay_passenger.AVR_Activities.Chats.ChatActivity_avr;
import com.example.liftandpay_passenger.CheckMapActivity;
import com.example.liftandpay_passenger.PickUpLocationActivity;
import com.example.liftandpay_passenger.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.mapboxsdk.plugins.places.picker.PlacePicker;

import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class AvailableRideDialog extends BottomSheetDialogFragment {

    TextView carDetails;
    TextView checkMap;
    TextView bookRide;
    TextView chatDriver;
    private String mainDriverId;
    private String rideDriverId;


    private static final int PLACE_SELECTION_REQUEST_CODE = 56789;

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String thePassengerId = mAuth.getUid();


    private Map<String, Object> ride;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.available_ride_dialog, container, false);

        carDetails = v.findViewById(R.id.carDetails_avrId);
        checkMap = v.findViewById(R.id.checkMap_avrId);
        bookRide = v.findViewById(R.id.bookRide_avrId);
        chatDriver = v.findViewById(R.id.chatDriver_avrId);

        carDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getContext(), CarDetailsActivity_avr.class);
                startActivity(intent);
            }
        });

        checkMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Objects.requireNonNull(getActivity()).getApplicationContext(), CheckMapActivity.class);
                intent.putExtra("DRIVER_ID",    mainDriverId);
                intent.putExtra("RIDEDRIVER_ID", rideDriverId);
               getActivity().startActivity(intent);
            }
        });

        chatDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ChatActivity_avr.class);
                startActivity(intent);
            }
        });

        bookRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), PickUpLocationActivity.class);
                startActivity(intent);

            /*    Intent intent = new PlacePicker.IntentBuilder()
                        .accessToken(Mapbox.getAccessToken())
                        .placeOptions(
                                PlacePickerOptions.builder()
                                        .statingCameraPosition(
                                                new CameraPosition.Builder()
                                                        .target(new LatLng(5.5958, -0.1518))
                                                        .zoom(14)
                                                        .build())
                                        .build())
                        .build(getActivity());
                startActivityForResult(intent, PLACE_SELECTION_REQUEST_CODE);*/

               /* ride = new HashMap<>();
                ride.put("Email", Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());
                ride.put("Phone", Objects.requireNonNull(mAuth.getCurrentUser()).getPhoneNumber());
                ride.put("Name", Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName());
                ride.put("Pickup Point", Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName());
                ride.put("Destination", Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName());

                CollectionReference pendingRidesDb = FirebaseFirestore.getInstance().collection("Driver").document(mainDriverId).collection("Pending Rides");

                db.collection("Driver").document(mainDriverId).collection("Pending Rides").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                assert thePassengerId != null;
                                db.collection("Driver").document(mainDriverId)
                                        .collection("Pending Rides").document(rideDriverId)
                                        .collection("Booked By").document(thePassengerId)
                                        .set(ride,SetOptions.merge())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                task.getResult().size();
                                                db.collection("Rides").document(rideDriverId)
                                                        .collection("Booked By").document(thePassengerId)
                                                        .set(ride, SetOptions.merge());
                                            }
                                        });

                                Toast.makeText(getActivity(), "Uploaded successfully",Toast.LENGTH_LONG).show();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Could not upload. Restart app and Try again",Toast.LENGTH_LONG).show();
                            }
                        });*/
            }
        });
        return v;
    }

    public void setMainDriverId(String mainDriverId) {
        this.mainDriverId = mainDriverId;
        /*the mainDriverId is the id on the firestore database. It has the Driver's id without an index attached to it. Can be accessed from the Drivers collection*/

    }

    public void setRideDriverId(String rideDriverId) {
        this.rideDriverId = rideDriverId;
        /*the rideDriverId is the id on the firestore database. It has the Driver's id with an index attached to it. Can be accessed from the Rides collection*/
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) { super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PLACE_SELECTION_REQUEST_CODE && resultCode == RESULT_OK){

            // Retrieve the information from the selected location's CarmenFeature

            CarmenFeature carmenFeature = PlacePicker.getPlace(data);
        }
    }
}
