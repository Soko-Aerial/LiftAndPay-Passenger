package com.example.liftandpay_passenger.AVR_Activities.Chats;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.liftandpay_passenger.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class ChatActivity_avr extends AppCompatActivity {

    String driverId;
    RecyclerView recyclerView;
    ArrayList<MessageModel> messageModels;
    ImageButton sendBtn;
    EditText typedMessage;
    MessageModel messageModel;

    private TextView driverName;
    private ShapeableImageView driverImage;
    private HashMap<String, Object> driverDetail = new HashMap<>();
    private HashMap<String, Object> chat = new HashMap<>();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String thePassengerId = mAuth.getUid();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_avr);


        driverName = findViewById(R.id.driverNameId);
        driverImage = findViewById(R.id.driverImageId);


        SharedPreferences sharedPreferences = getSharedPreferences("AVRDialogFile", Context.MODE_PRIVATE);
        driverId = sharedPreferences.getString("TheMainDriverId", "Null");
        if (driverId.equals("Null")) {
            Toast.makeText(ChatActivity_avr.this, "Couldn't fetch driver's details", Toast.LENGTH_LONG).show();
            ChatActivity_avr.this.finish();
        }


        storage.getReference().child("Passengers").child(driverId).child("profile.png");
        sendBtn = findViewById(R.id.sendBtn);
        typedMessage = findViewById(R.id.typedMessage);

        recyclerView = findViewById(R.id.chatRecyler);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity_avr.this, LinearLayoutManager.VERTICAL, true));
        messageModels = new ArrayList<>();



        storage.getReference().child("Driver").child(driverId).child("profile.png").getDownloadUrl().addOnCompleteListener(
                new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Uri> task) {

                        if (task.isSuccessful()) {
                            Picasso.get().load(task.getResult().toString()).into(driverImage);
                        }

                    }
                }
        );


        db.collection("Driver").document(driverId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

               if(documentSnapshot.contains("Name")){
                   driverName.setText(documentSnapshot.getString("Name"));
               }else{
                   driverName.setText("name not available");
               }
            }
        });



        CollectionReference chatCollection = db.collection("Chat").document(driverId).collection("Passengers").document(thePassengerId).collection("messages");
        DocumentReference passengerRef = db.collection("Chat").document(driverId).collection("Passengers").document(thePassengerId);
        DocumentReference driverRef = db.collection("Chat").document(driverId);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if (!typedMessage.getText().equals("")) {

                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    System.out.println(dtf.format(now));

                    chat.put("Message", typedMessage.getText().toString());
                    chat.put("Time", new Timestamp(new Date()));
                    chat.put("ChatMode", "2");
                    driverDetail.put("ID", driverId);

                    chatCollection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            int size = queryDocumentSnapshots.size();
                            chatCollection.document(size + "pA").set(chat)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            passengerRef.set(chat).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                    driverRef.set(driverDetail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull @NotNull Task<Void> task) {

                                                        }
                                                    });
                                                    Timber.tag("Add Phone").e("Phone number added successfully");
                                                }
                                            });


                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ChatActivity_avr.this, "Failed to add", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    });


                    typedMessage.setText("");
                }
            }
        });


        chatCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                assert value != null;
                for (DocumentChange ds : value.getDocumentChanges()) {

                    String mode = ds.getDocument().getString("ChatMode");

                    assert mode != null;
                    if (mode.equals("2")) {
                        messageModel = new MessageModel(ds.getDocument().getString("Message"), 2);
                    }
                    if (mode.equals("1")) {
                        messageModel = new MessageModel(ds.getDocument().getString("Message"), 1);
                    }
                    if (!mode.equals("2") && !mode.equals("1"))
                        Snackbar.make(ChatActivity_avr.this, recyclerView, "Some messages are missing", 6000).show();

                    messageModels.add(0, messageModel);
                }
                recyclerView.setAdapter(new MessageAdapter(messageModels));

            }
        });


    }
}