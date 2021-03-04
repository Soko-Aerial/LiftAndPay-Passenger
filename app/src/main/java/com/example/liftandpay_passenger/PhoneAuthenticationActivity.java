package com.example.liftandpay_passenger;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

public class PhoneAuthenticationActivity extends AppCompatActivity {
    EditText countryCode,phoneNumber,enterOTPField;
    Button sendOTPBtn,verifyBTN,resendBTN;
    String userPhoneNumber,verificationId;
    PhoneAuthProvider.ForceResendingToken token;
    FirebaseAuth firebaseAuth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_authentication);
        countryCode = findViewById(R.id.cCode);
        phoneNumber = findViewById(R.id.phoneNumber);
        sendOTPBtn = findViewById(R.id.sendOtpBtn);
        enterOTPField = findViewById(R.id.enterOTPfield);
        verifyBTN = findViewById(R.id.verifyOTP);
        resendBTN = findViewById(R.id.resendButton);
        firebaseAuth = FirebaseAuth.getInstance();


        sendOTPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (countryCode.getText().toString().isEmpty()) {
                    countryCode.setError("Required");
                    return;
                }
                if (phoneNumber.getText().toString().isEmpty()) {
                    phoneNumber.setError("Phone number is Required");
                    return;
                }
                userPhoneNumber = "+" + countryCode.getText().toString() + phoneNumber.getText().toString();
                verifyphoneNumber(userPhoneNumber);

                Toast.makeText(PhoneAuthenticationActivity.this, userPhoneNumber, Toast.LENGTH_SHORT).show();

            }
        });

        verifyBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(enterOTPField.getText().toString().isEmpty()){
                    enterOTPField.setError("Enter OTP First");
                    return;
                }
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,enterOTPField.getText().toString());
                authenticateUser(credential);
            }
        });
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                authenticateUser(phoneAuthCredential);



            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(PhoneAuthenticationActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationId = s;
                token = forceResendingToken;

                countryCode.setVisibility(View.GONE);
                phoneNumber.setVisibility(View.GONE);
                sendOTPBtn.setVisibility(View.GONE);

                enterOTPField.setVisibility(View.VISIBLE);
                verifyBTN.setVisibility(View.VISIBLE);
                resendBTN.setVisibility(View.VISIBLE);
                resendBTN.setEnabled(true);
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                resendBTN.setEnabled(false);
            }
        };



        }


    public void verifyphoneNumber(String phoneNum){
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setActivity(this)
                .setPhoneNumber(phoneNum)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setCallbacks(callbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }
    public void authenticateUser(PhoneAuthCredential credential){
        firebaseAuth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(PhoneAuthenticationActivity.this,"Success",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),MainFragment.class));
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PhoneAuthenticationActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();


            }
        });

    }
}