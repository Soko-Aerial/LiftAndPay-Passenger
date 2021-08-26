package com.example.liftandpay_passenger;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import nl.nos.imagin.Imagin;
import nl.nos.imagin.SingleTapHandler;

public class ImageViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        RelativeLayout parentView = findViewById(R.id.parentView);
        ImageView imageView = findViewById(R.id.fullImageControl);

        String text = getIntent().getStringExtra("image-view-control");

        zoomImage(parentView,imageView);


    }

    void zoomImage(View parentView, ImageView imageView){
        Imagin.Companion.with(parentView,imageView,1.0f,2f).
                enableDoubleTapToZoom()
                .enableSingleTap(new SingleTapHandler.OnSingleTapListener() {
                    @Override
                    public void onSingleTap(@NotNull MotionEvent motionEvent) {
                        Log.e("Tapped Message:","Hello Tapped");
                    }
                })
                .enablePinchToZoom()
                .enableScroll(true,true,20,null);

    }
}