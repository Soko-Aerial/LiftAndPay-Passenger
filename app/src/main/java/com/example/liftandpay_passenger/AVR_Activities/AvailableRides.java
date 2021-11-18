package com.example.liftandpay_passenger.AVR_Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.ForeignKey;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.liftandpay_passenger.AVR_Activities.Chats.ChatActivity_avr;
import com.example.liftandpay_passenger.ImageViewActivity;
import com.example.liftandpay_passenger.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import nl.nos.imagin.Imagin;
import nl.nos.imagin.SingleTapHandler;

public class AvailableRides extends AppCompatActivity {

    TextView bookRide;
    ImageView chatDriver;
    private String mainDriverId, name, purpose;
    private double startLat;
    private double startLon;
    private double endLat;
    private double endLon;
    private String rideDriverId;
    private String startTime;
    private String distance;
    private String journey;

    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ImageView image001,image002,driverProfileImage;

    private TextView journeyText, nameText;

    private LinearLayout driverDetailsFooter;

    private TextView driverAbout, carAbout ,carModel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_rides);

        bookRide = findViewById(R.id.driverNxtBtnId);
        chatDriver = findViewById(R.id.messageBtn);
        journeyText =  findViewById(R.id.journeyId);
        driverDetailsFooter = findViewById(R.id.driverProfileFooter);

        nameText = findViewById(R.id.nameId);
        image001 = findViewById(R.id.image001);
        image002 = findViewById(R.id.image002);
        driverProfileImage = findViewById(R.id.driverProfileImage);

        driverAbout = findViewById(R.id.aboutId);
        carAbout = findViewById(R.id.numberPlateId);
        carModel = findViewById(R.id.carModelId);

//        from carBookAdapter.java or @MainFragment.java
        purpose = getIntent().getStringExtra("Purpose");
        mainDriverId = getIntent().getStringExtra("theDriverId");
        startLat = getIntent().getDoubleExtra("startLat",0.0);
        startLon =getIntent().getDoubleExtra("startLon",0.0);
        endLat =getIntent().getDoubleExtra("endLat",0.0);
        endLon =getIntent().getDoubleExtra("endLon",0.0);
        rideDriverId =getIntent().getStringExtra("theRideDriverId");
        startTime =getIntent().getStringExtra("theTime");
        journey = getIntent().getStringExtra("theJourney");
        distance =getIntent().getStringExtra("theDistance");
        name = getIntent().getStringExtra("theDriverName" );


        journeyText.setText(journey);
        nameText.setText(name);

        if (purpose.equals("ForView")){
            driverDetailsFooter.setVisibility(View.GONE);
        }

        chatDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AvailableRides.this, ChatActivity_avr.class);
                startActivity(intent);
            }
        });

        Log.i("TheDriverId",mainDriverId);


        storage.getReference().child("Driver").child(mainDriverId).child("profile.png").getDownloadUrl().addOnCompleteListener(
                new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Uri> task) {

                        if (task.isSuccessful()) {
                            Picasso.get().load(task.getResult().toString()).into(driverProfileImage);
                        }

                    }
                }
        );

        storage.getReference().child("Driver").child(mainDriverId).child("otherImage.png").getDownloadUrl().addOnCompleteListener(
                new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Uri> task) {

                        if (task.isSuccessful()) {
                            Picasso.get().load(task.getResult().toString()).into(image002);
                        }

                    }
                }
        );

        storage.getReference().child("Driver").child(mainDriverId).child("main.png").getDownloadUrl().addOnCompleteListener(
                new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Uri> task) {

                        if (task.isSuccessful()) {
                            Picasso.get().load(task.getResult().toString()).into(image001);
                        }

                    }
                }
        );

        db.collection("Driver").document(mainDriverId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){

                    if (task.getResult().contains("About")){

                        driverAbout.setText(task.getResult().getString("About"));

                    }

                    if (task.getResult().contains("Numberplate"))
                    {
                        carAbout.setText(task.getResult().getString("Numberplate"));
                    }


                    if (task.getResult().contains("Car Model"))
                    {
                        carModel.setText(task.getResult().getString("Car Model"));
                    }
                }

            }
        });


        image001.setOnClickListener(view->{
            Intent intent = new Intent(AvailableRides.this, ImageViewActivity.class);
            image001.buildDrawingCache();
            Bitmap image= image001.getDrawingCache();

            Bundle extras = new Bundle();
            extras.putParcelable("imagebitmap", image);
            intent.putExtras(extras);
            startActivity(intent);
        });



        image002.setOnClickListener(view->{
            Intent intent = new Intent(AvailableRides.this, ImageViewActivity.class);
            image002.buildDrawingCache();
            Bitmap image= image002.getDrawingCache();

            Bundle extras = new Bundle();
            extras.putParcelable("imagebitmap", image);
            intent.putExtras(extras);
            startActivity(intent);

        });


        bookRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AvailableRides.this, PickUpLocationActivity.class);
                intent.putExtra("endLat",endLat);
                intent.putExtra("endLon", endLon);
                intent.putExtra("startLat",startLat);
                intent.putExtra("startLon", startLon);
                intent.putExtra("distance", distance);
                intent.putExtra("journey", journey);
                intent.putExtra("startTime", startTime);
                startActivity(intent);
            }
        });
    }


}