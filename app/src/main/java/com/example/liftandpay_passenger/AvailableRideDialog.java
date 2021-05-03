package com.example.liftandpay_passenger;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class AvailableRideDialog extends BottomSheetDialogFragment {

    TextView carDetails;
    TextView checkMap;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.available_ride_dialog, container, false);

        carDetails = v.findViewById(R.id.carDetails_avrId);
        checkMap = v.findViewById(R.id.checkMap_avrId);

        carDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getContext(),CarDetailsActivity_avr.class);
                startActivity(intent);
            }
        });

        checkMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getContext(),CheckMapActivity.class);
                startActivity(intent);
            }
        });
        return v;
    }
}
