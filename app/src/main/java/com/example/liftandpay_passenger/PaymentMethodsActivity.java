package com.example.liftandpay_passenger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.card.MaterialCardView;

public class PaymentMethodsActivity extends AppCompatActivity implements View.OnClickListener {
    public MaterialCardView card1,card2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_methods);
        card1 = (MaterialCardView) findViewById(R.id.cardPayment);
        card2 = (MaterialCardView) findViewById(R.id.cardPayment2);

        card1.setOnClickListener(this);

        card2.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        Intent i;

        switch (view.getId()){
            case R.id.cardPayment:
                i = new Intent( this,AddCardDetails.class);
                startActivity(i);
                break;


        }


    }
}