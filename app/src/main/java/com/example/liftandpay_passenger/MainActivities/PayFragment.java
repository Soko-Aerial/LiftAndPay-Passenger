package com.example.liftandpay_passenger.MainActivities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.liftandpay_passenger.R;
import com.example.liftandpay_passenger.MainActivities.Rides.PendingRideMapActivity;

public class PayFragment extends AppCompatActivity {

    Button paymentButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pay);

        paymentButton = findViewById(R.id.paymentButton);

        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PayFragment.this, PendingRideMapActivity.class));
            }
        });

    }
}