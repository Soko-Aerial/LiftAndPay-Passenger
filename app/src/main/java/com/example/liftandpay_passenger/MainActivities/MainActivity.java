package com.example.liftandpay_passenger.MainActivities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.example.liftandpay_passenger.R;
import com.example.liftandpay_passenger.ProfileSetup.SignUp001;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;

import nl.joery.animatedbottombar.AnimatedBottomBar;


public class MainActivity extends AppCompatActivity {
    private AnimatedBottomBar animatedBottomBar;
    private FragmentManager fragmentManager;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart(){
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser==null)
        {
            startSignIn();

     /*       Intent intent  = new Intent(MainActivity.this, LogAuth.class);
            startActivity(intent);
            finish();*/
        }
        else
        {

            Intent intent  = new Intent(MainActivity.this, MainFragment.class);
            startActivity(intent);
            finish();
        }
    }

    private ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            (result) -> {

                HashMap<String,String> map = new HashMap<>();
                map.put("Name",result.getIdpResponse().getPhoneNumber());
                map.put("Email","Not Set");
                DocumentReference dRef = db.collection("Passenger").document(mAuth.getUid().toString());
                dRef.set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent  = new Intent(MainActivity.this, SignUp001.class);
                        startActivity(intent);
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this,"Details not set",Toast.LENGTH_LONG).show();
                                mAuth.signOut();
                            }
                        });

            });


    private void startSignIn() {
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(Arrays.asList(
                        new AuthUI.IdpConfig.PhoneBuilder().build()
                     ))
                .setTheme(R.style.FirebaseUITheme)
                .build();

        signInLauncher.launch(signInIntent);
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