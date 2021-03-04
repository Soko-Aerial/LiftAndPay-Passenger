package com.example.liftandpay_passenger;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PassengerSignUpActivity extends AppCompatActivity {
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.passengersign_up);
//title name color change
        TextView textView = findViewById(R.id.app_name);
        String text = "LiftnPay";
        SpannableString ss = new SpannableString(text);
        ForegroundColorSpan fcsBlue = new ForegroundColorSpan(Color.rgb(1, 86, 245));
        ss.setSpan(fcsBlue,0,4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        ForegroundColorSpan fcsYellow = new ForegroundColorSpan(Color.rgb (255, 190, 11));
        ss.setSpan(fcsYellow,4,5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(ss);

        ForegroundColorSpan fcsRed = new ForegroundColorSpan(Color.RED);
        ss.setSpan(fcsRed,5,8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

       textView.setText(ss);


       TextView textView1 = findViewById(R.id.passengerId);
        String text1 = "Passenger";

        SpannableString pp = new SpannableString(text1);
        ForegroundColorSpan passBlue = new ForegroundColorSpan(Color.rgb(1, 86, 245));
        pp.setSpan(passBlue,0,3,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView1.setText(pp);

        ForegroundColorSpan passYellow = new ForegroundColorSpan(Color.rgb(255, 190, 11));
        pp.setSpan(passYellow,3,6,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ForegroundColorSpan passRed = new ForegroundColorSpan(Color.RED);
        pp.setSpan(passRed,6,9,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView1.setText(pp);





    }
}
