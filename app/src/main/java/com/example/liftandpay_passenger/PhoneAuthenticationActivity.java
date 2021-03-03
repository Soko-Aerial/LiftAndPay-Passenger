package com.example.liftandpay_passenger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.api.Authentication;

import org.jetbrains.annotations.Nullable;

public class PhoneAuthenticationActivity extends AppCompatActivity {
    EditText countryCode,phoneNumber;
    Button sendOTPBtn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_authentication);
        countryCode = findViewById(R.id.cCode);
        phoneNumber = findViewById(R.id.phoneNumber);
        sendOTPBtn = findViewById(R.id.sendOtpBtn);


        sendOTPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(countryCode.getText().toString().isEmpty()){
                    countryCode.setError("Required");
                    return;
                }
                if(phoneNumber.getText().toString().isEmpty()){
                    phoneNumber.setError("Phone number is Required");
                    return;
                }

             

            }
        });
    }
}