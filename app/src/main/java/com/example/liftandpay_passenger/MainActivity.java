package com.example.liftandpay_passenger;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private BottomNavigationView bottomNavigationView;
    private Button payment_btn;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavMethod);
        getSupportFragmentManager().beginTransaction().replace(R.id.container,new MainFragment()).commit();



    }


    private final BottomNavigationView.OnNavigationItemSelectedListener bottomNavMethod = item -> {
        Fragment fragment = null;
        switch (item.getItemId())
        {
            case R.id.navigation_home:
                 fragment = new MainFragment();
                 break;

            case R.id.navigation_radar:
                fragment = new RidesFragment();
                break;

            case R.id.navigation_payment:
                fragment = new PayFragment();
                break;

            case R.id.navigation_profile:
                fragment = new ProfileFragment();
                break;


        }
        getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment).commit();

        return true;
    };

    
}