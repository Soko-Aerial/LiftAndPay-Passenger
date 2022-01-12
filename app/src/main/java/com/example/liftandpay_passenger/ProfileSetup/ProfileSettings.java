package com.example.liftandpay_passenger.ProfileSetup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liftandpay_passenger.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;

import timber.log.Timber;

public class ProfileSettings extends AppCompatActivity {

    private EditText usernameEdit, emailEdit;
    private TextView btnToSignUp002;
    private ProgressBar pBar;
    private TextView infoText;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    public final int SELECT_PICTURE = 111;
    private ShapeableImageView profileImg;
    private FirebaseStorage storage =  FirebaseStorage.getInstance();
    private String mUid = FirebaseAuth.getInstance().getUid();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        fillInfo();


        profileImg = findViewById(R.id.profileImgId);

        btnToSignUp002 = findViewById(R.id.btnTosignUp002);
        usernameEdit = findViewById(R.id.usernameId);
        emailEdit = findViewById(R.id.emailId);
        infoText = findViewById(R.id.signupInfoText001);
        pBar = findViewById(R.id.verifyProgressId);

        sharedPreferences = getSharedPreferences("PASSENGER_INFO", MODE_PRIVATE);




        StorageReference imageRef = storage.getReference().child("Passenger").child(mUid).child("profile.png");
        profileImg.setDrawingCacheEnabled(true);
        profileImg.buildDrawingCache();


        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
//                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }
        });



        btnToSignUp002.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                pBar.setVisibility(View.VISIBLE);
                if (usernameEdit.getText().toString().equals("") || emailEdit.getText().toString().equals("")) {
                    pBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(ProfileSettings.this, "Empty fields found", Toast.LENGTH_LONG).show();
//                    infoText.setText("Cannot process empty fields");
//                    infoText.setTextColor(R.color.red);
                } else {
                    sharedPreferences.edit().putString("TheUserName", usernameEdit.getText().toString()).apply();
                    sharedPreferences.edit().putString("TheEmail", emailEdit.getText().toString()).apply();

                    HashMap<String, String> map = new HashMap<>();
                    map.put("Name", usernameEdit.getText().toString());
                    map.put("Email", emailEdit.getText().toString());
                    DocumentReference dRef = db.collection("Passenger").document(mAuth.getUid().toString());
                    dRef.set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            pBar.setVisibility(View.INVISIBLE);
                            Snackbar.make(ProfileSettings.this, pBar, "Profile Updated", 5000)
                                    .setTextColor(Color.WHITE)
                                    .setBackgroundTint(getResources().getColor(R.color.success)).show();


                            /*Upload Image*/
                            Bitmap bitmap = ((BitmapDrawable) profileImg.getDrawable()).getBitmap();
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] data = baos.toByteArray();

                            UploadTask uploadTask = imageRef.putBytes(data);
                            String d = Arrays.toString(data);
                            data = d.getBytes(StandardCharsets.UTF_8);


                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    Timber.e(exception);
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Timber.e("Profile Upload Succesful");
                                }
                            });
//                            finish();
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

    void imageChooser() {

        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        startActivityIfNeeded(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    profileImg.setImageURI(selectedImageUri);
                }
            }
        }
    }


    private void fillInfo(){

        db.collection("Passenger").document(mUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                Log.i("PassengerProfileCheck","Completed");
                if (task.isSuccessful()){
                    Log.i("PassengerProfileCheck","Successful");

                    storage.getReference().child("Passenger").child(mUid).child("profile.png").getDownloadUrl().addOnCompleteListener(
                            new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Uri> task) {

                                    if (task.isSuccessful()) {
                                        Picasso.get().load(task.getResult().toString()).into(profileImg);
                                    }

                                }
                            }
                    );


                    if (task.getResult().exists()){

                        if (task.getResult().contains("Name"))
                        {
                            Log.i("PassengerProfileCheck","Name exists");
                            usernameEdit.setText(task.getResult().getString("Name"));
                        }
                        if (task.getResult().contains("Email"))
                        {
                            Log.i("PassengerProfileCheck","Email exists");
                            emailEdit.setText(task.getResult().getString("Email"));
                        }

                    }
                    else
                    {
                        Log.i("PassengerProfileCheck","Details don't exist");

                    }

                }
                else
                {
                    Log.i("PassengerProfileCheck","Failed");

                }
            }
        });
    }
}