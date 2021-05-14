package com.example.liftandpay_passenger.AVR_Activities.Chats;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.liftandpay_passenger.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity_avr extends AppCompatActivity {

    String driverId;
    RecyclerView recyclerView;
    ArrayList<MessageModel> messageModels;
    ImageButton sendBtn;
    EditText typedMessage;

    private HashMap<String, Object> chat = new HashMap<>();


    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String thePassengerId = mAuth.getUid();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_avr);



        Bundle extras = getIntent().getExtras();
        if (extras == null)
            Toast.makeText(ChatActivity_avr.this,"Bundle is empty",Toast.LENGTH_LONG).show();
        else
        {
            driverId = extras.get("DRIVER_ID").toString();
            if (driverId.equals("null")) {
                Toast.makeText(ChatActivity_avr.this, "Bundle is available", Toast.LENGTH_LONG).show();
                ChatActivity_avr.this.finish();
            }

        }

        sendBtn = findViewById(R.id.sendBtn);
        typedMessage = findViewById(R.id.typedMessage);

        recyclerView = findViewById(R.id.chatRecyler);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity_avr.this,LinearLayoutManager.VERTICAL,true));
        messageModels = new ArrayList<>();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!typedMessage.getText().equals("")){


                    chat.put("Message",typedMessage.getText().toString());
                    chat.put("Time","Not clear");
                    chat.put("ChatMode","2");
                    db.collection("Chat").document("driverId" ).collection(thePassengerId).add(chat)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
//                            Toast.makeText(ChatActivity_avr.this,"Added Successfully",Toast.LENGTH_LONG).show();
                            MessageModel messageModel = new MessageModel(typedMessage.getText().toString(),2);
                            messageModels.add(0,messageModel);

                            recyclerView.setAdapter(new MessageAdapter(messageModels));

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ChatActivity_avr.this,"Failed to add",Toast.LENGTH_LONG).show();

                        }
                    });

                    typedMessage.setText("");
                }
            }
        });






    }
}