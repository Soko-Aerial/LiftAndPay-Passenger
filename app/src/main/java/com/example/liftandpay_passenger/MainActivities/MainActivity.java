package com.example.liftandpay_passenger.MainActivities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.example.liftandpay_passenger.R;
import com.example.liftandpay_passenger.SettingUp.LogAuth;
import com.example.liftandpay_passenger.SplashScreen;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import nl.joery.animatedbottombar.AnimatedBottomBar;


public class MainActivity extends AppCompatActivity {
    private AnimatedBottomBar animatedBottomBar;
    private FragmentManager fragmentManager;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent  = new Intent(MainActivity.this, MainFragment.class);
        startActivity(intent);
        finish();

    }

    @Override
    protected void onStart(){
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser==null)
        {
            Intent intent  = new Intent(MainActivity.this, LogAuth.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    }
                } else {

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    AlertDialog.Builder builder
                            = new AlertDialog
                            .Builder(MainActivity.this);

                    // Set the message show for the Alert time
                    builder.setMessage("Unable to fetch data");
                    builder.setTitle("Permissions not enabled");
                    builder.setCancelable(true);
                    builder.create().show();
                    Toast.makeText(this, "PERMISSION_NOT_GRANTED_002", Toast.LENGTH_SHORT).show();

                }
            }
        }
    }
}