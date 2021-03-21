package com.example.liftandpay_passenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneAuthenticationActivity extends AppCompatActivity {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_authentication);


        EditText phoneNumberText = findViewById(R.id.phoneNumberId);
        EditText countryCodeText = findViewById(R.id.countryCodeId);
        Button verifyNumberBtn = findViewById(R.id.verifyPhoneNumberBtnId);
        progressBar = findViewById(R.id.verifyProgressId);




        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                Toast.makeText(PhoneAuthenticationActivity.this,"Successful",Toast.LENGTH_LONG).show();

                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(PhoneAuthenticationActivity.this,e.toString(),Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.


                Toast.makeText(PhoneAuthenticationActivity.this,"Code sent",Toast.LENGTH_LONG).show();


                // Save verification ID and resending token so we can use them later
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, token.toString());


                // ...
            }
        };


        verifyNumberBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                setVerificationCode(PhoneNumber);
                String number  = phoneNumberText.getText().toString();
                String countryCode = countryCodeText.getText().toString();
                String phoneNumber =  countryCode + number;
                Toast.makeText(getBaseContext(), "This is you phone number " + phoneNumber , Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.VISIBLE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }, 8000);

                if(!number.isEmpty() && !countryCode.isEmpty()) {
                    PhoneAuthOptions options =
                            PhoneAuthOptions.newBuilder(mAuth)
                                    .setPhoneNumber(phoneNumber)       // Phone number to verify
                                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                    .setActivity(PhoneAuthenticationActivity.this)                 // Activity (for callback binding)
                                    .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                    .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);
                }
                else
                {
                    Toast.makeText(PhoneAuthenticationActivity.this, "Enter You Phone Number", Toast.LENGTH_LONG).show();
                }

            }
        });





    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null)
        {
            sendToUploadedPassenger();
        }
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(PhoneAuthenticationActivity.this,"Successfully Signed Up",Toast.LENGTH_LONG).show();


                            sendToUploadedPassenger();

                            FirebaseUser user = task.getResult().getUser();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }


    private void sendToUploadedPassenger(){
        Intent intent = new Intent(PhoneAuthenticationActivity.this , PayFragment.class);
        startActivity(intent);
        finish();
    }
}