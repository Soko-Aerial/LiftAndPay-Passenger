package com.example.liftandpay_passenger.MainActivities;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.liftandpay_passenger.R;
import com.example.liftandpay_passenger.MainActivities.Rides.PendingRideMapActivity;

public class PayFragment extends Fragment {

    Button paymentButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_pay, container, false);
        paymentButton = v.findViewById(R.id.paymentButton);

        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), PendingRideMapActivity.class));
            }
        });


        return  v;


    }
}