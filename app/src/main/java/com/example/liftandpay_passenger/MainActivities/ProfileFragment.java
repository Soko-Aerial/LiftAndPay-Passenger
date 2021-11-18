package com.example.liftandpay_passenger.MainActivities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.liftandpay_passenger.R;
import com.google.android.material.imageview.ShapeableImageView;

public class ProfileFragment extends AppCompatActivity {

    private ShapeableImageView profileImage;
    @Nullable
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);


    }
}