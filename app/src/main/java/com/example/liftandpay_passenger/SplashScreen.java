package com.example.liftandpay_passenger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.airbnb.lottie.LottieAnimationView;
import com.example.liftandpay_passenger.MainActivities.MainActivity;

public class SplashScreen extends AppCompatActivity {

   private LottieAnimationView lottieAnimationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        lottieAnimationView =findViewById(R.id.lottie);
        lottieAnimationView.animate().setDuration(4000).setStartDelay(4000);

        new Handler().postDelayed(new Runnable() {
// Using handler with postDelayed called runnable run method

            @Override

            public void run() {
                // close this activity
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);
                finish();

            }

        }, 4*1000); // wait for 2 seconds

    }

}