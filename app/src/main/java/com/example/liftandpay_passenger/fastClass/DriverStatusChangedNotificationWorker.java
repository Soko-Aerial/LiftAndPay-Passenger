package com.example.liftandpay_passenger.fastClass;

import static android.content.Context.MODE_PRIVATE;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.liftandpay_passenger.MainActivities.MainFragment;
import com.example.liftandpay_passenger.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class DriverStatusChangedNotificationWorker extends Worker {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String mUid = FirebaseAuth.getInstance().getUid();
    private static String passengerName;

    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("BACKGROUNDFILE", MODE_PRIVATE);


    public DriverStatusChangedNotificationWorker(@NonNull @NotNull Context context, @NonNull @NotNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @NotNull
    @Override
    public Result doWork() {

        Log.i("New Chat", "Chat Notification Started");

        db.collection("Chat").document(mUid).collection("Passengers").addSnapshotListener(
                new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                        Log.i("New Chat", "Checking For Notification");


                        for ( DocumentChange documentChange : value.getDocumentChanges()) {

                            db.collection("Passenger").document(documentChange.getDocument().getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {

                                    passengerName = documentSnapshot.getString("Name");

                                    switch (documentChange.getType()) {
                                        case MODIFIED:
                                            if (Objects.requireNonNull(documentChange.getDocument().get("ChatMode")).toString().equals("2"))
                                                buildNotification(passengerName, documentChange.getDocument().getString("Message"), documentChange.getNewIndex());

                                        case ADDED:
                                            int newDocumentSize = value.getDocuments().size();
                                            int oldDocumentSize = sharedPreferences.getInt("TheTotalNumberOfChats", -1);

                                            if (oldDocumentSize < newDocumentSize) {

                                                Log.i("New Chat", documentChange.getDocument().getString("Message"));
                                                Log.i("New Total old", "" + oldDocumentSize);
                                                Log.i("New Total new", "" + newDocumentSize);
                                                buildNotification(passengerName, documentChange.getDocument().getString("Message"), 200);
                                                sharedPreferences.edit().putInt("TheTotalNumberOfChats", newDocumentSize).apply();

                                            }
                                            oldDocumentSize = sharedPreferences.getInt("TheTotalNumberOfChats", -1);
                                            Log.i("New Total old 01", "" + oldDocumentSize);

                                        case REMOVED:
                                            newDocumentSize = value.getDocuments().size();
                                            sharedPreferences.edit().putInt("TheTotalNumberOfChats", newDocumentSize).apply();


                                    }

                                }
                            });
                        }
                    }
                }
        );

        return Result.success();

    }


    private void buildNotification(String title, String contentText, int chanDesc) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getApplicationContext().getString(R.string.channel_name);
            String description = "Hello" + chanDesc;
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("liftandpayId", name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);
            channel.shouldVibrate();
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(getApplicationContext(), MainFragment.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "liftandpayId")
                .setSmallIcon(R.drawable.img_circle)
                .setContentTitle(title)
                .setContentText(contentText)
                .setContentIntent(contentIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(contentText))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(chanDesc, builder.build());

    }
}