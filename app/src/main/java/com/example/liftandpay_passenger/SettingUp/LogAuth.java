package com.example.liftandpay_passenger.SettingUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liftandpay_passenger.MainActivities.MainActivity;
import com.example.liftandpay_passenger.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class LogAuth extends AppCompatActivity {

    private TextView btnToSignUp003;
    private TextView countryCodeText, phoneNumberText;
    private ProgressBar pBar;
    private String phoneNumber;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private TextView infoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logauth);

        btnToSignUp003 = findViewById(R.id.btnTosignUp003);
        countryCodeText = findViewById(R.id.countryCodeId);
        phoneNumberText = findViewById(R.id.phoneNumberId);
        pBar = findViewById(R.id.verifyProgressId);
        infoText = findViewById(R.id.infoText);

            /*    mAuth.signInWithEmailAndPassword("passenger@gmail.com", "123456")
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(LogAuth.this,"Successful",Toast.LENGTH_LONG).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LogAuth.this,"Failed",Toast.LENGTH_LONG).show();
                        }
                    }
                });*/


        btnToSignUp003.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pBar.setVisibility(View.VISIBLE);
                btnToSignUp003.setVisibility(View.GONE);

                if(!countryCodeText.getText().toString().isEmpty()) {
                    phoneNumber = "+" + countryCodeText.getText().toString() + phoneNumberText.getText().toString();
                    PhoneAuthOptions options =
                            PhoneAuthOptions.newBuilder(mAuth)
                                    .setPhoneNumber(phoneNumber)       // Phone number to verify
                                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                    .setActivity(LogAuth.this)                 // Activity (for callback binding)
                                    .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                    .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);
                }
                else
                {
                    Toast.makeText(LogAuth.this,"Empty",Toast.LENGTH_LONG).show();
                    pBar.setVisibility(View.INVISIBLE);
                }
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                Snackbar.make(LogAuth.this,pBar,"Verified",3000)
                .setTextColor(Color.WHITE)
                .setBackgroundTint(getResources().getColor(R.color.mapbox_plugins_green)).show();

                mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            HashMap<String,String> map = new HashMap<>();
                            map.put("Name", mAuth.getUid());
                            map.put("Email","Not Set");
                            DocumentReference dRef = db.collection("Passenger").document(mAuth.getUid().toString());
                            dRef.set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Intent intent  = new Intent(LogAuth.this, SignUp001.class);
                                    startActivity(intent);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(LogAuth.this,"Details not set",Toast.LENGTH_LONG).show();
                                    mAuth.signOut();
                                }
                            });

                        }
                    }
                });
                pBar.setVisibility(View.GONE);
                btnToSignUp003.setVisibility(View.VISIBLE);

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(LogAuth.this,"Error Message /n/n"+e.getMessage(),Toast.LENGTH_LONG).show();
            infoText.setText(e.getMessage());
                pBar.setVisibility(View.GONE);
                btnToSignUp003.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Toast.makeText(LogAuth.this,"Code is sent",Toast.LENGTH_LONG).show();
                infoText.setText("Code sent");

            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!=null)
        {
            Intent intent  = new Intent(LogAuth.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!=null)
        {
            Intent intent  = new Intent(LogAuth.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}