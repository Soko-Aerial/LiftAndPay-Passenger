package com.example.liftandpay_passenger.menu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.liftandpay_passenger.MainActivities.ProfileFragment;
import com.example.liftandpay_passenger.R;

public class MenuListActivity extends AppCompatActivity {

    private ImageButton backBtn;

    //individual menu declaration
    private LinearLayout profileView;
    private LinearLayout messageView,logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //menu initiallization
        backBtn = findViewById(R.id.backButton_P1);
        profileView = findViewById(R.id.profileViewId);
        messageView = findViewById(R.id.messageViewId);


        profileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuListActivity.this, ProfileFragment.class);
                startActivity(intent);
            }
        });

        messageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(MenuListActivity.this, ChatList.class);
                startActivity(intent);*/
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}