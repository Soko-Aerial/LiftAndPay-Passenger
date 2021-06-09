package com.example.liftandpay_passenger.SettingUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liftandpay_passenger.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignUp001 extends AppCompatActivity {

    private EditText usernameEdit,emailEdit;
    private TextView btnToSignUp002;
    private ProgressBar pBar;
    private TextView infoText;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        btnToSignUp002 = findViewById(R.id.btnTosignUp002);
        usernameEdit = findViewById(R.id.usernameId);
        emailEdit = findViewById(R.id.emailId);
        infoText = findViewById(R.id.signupInfoText001);
        pBar = findViewById(R.id.verifyProgressId);

        sharedPreferences = getSharedPreferences("PASSENGER_INFO",MODE_PRIVATE);




        btnToSignUp002.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                pBar.setVisibility(View.VISIBLE);
                if (usernameEdit.getText().toString().equals("") || emailEdit.getText().toString().equals(""))
                {
                    pBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(SignUp001.this,"Empty fields found",Toast.LENGTH_LONG).show();
//                    infoText.setText("Cannot process empty fields");
//                    infoText.setTextColor(R.color.red);
                }
                else {
                    sharedPreferences.edit().putString("TheUserName", usernameEdit.getText().toString()).apply();
                    sharedPreferences.edit().putString("TheEmail", emailEdit.getText().toString()).apply();

                    HashMap<String,String> map = new HashMap<>();
                    map.put("Name", mAuth.getUid());
                    map.put("Email","Not Set");
                    DocumentReference dRef = db.collection("Passenger").document(mAuth.getUid().toString());
                    dRef.set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            pBar.setVisibility(View.INVISIBLE);
                            Snackbar.make(SignUp001.this,pBar,"Profile Updated",5000)
                                    .setTextColor(Color.WHITE)
                                    .setBackgroundTint(getResources().getColor(R.color.mapbox_plugins_green)).show();
                            Intent intent = new Intent(SignUp001.this, SignUp003.class);
                            startActivity(intent);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pBar.setVisibility(View.INVISIBLE);
                        }
                    });

                }
            }
        });
    }
}