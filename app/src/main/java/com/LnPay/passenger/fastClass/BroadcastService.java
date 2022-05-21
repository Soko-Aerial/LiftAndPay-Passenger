package com.LnPay.passenger.fastClass;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.LnPay.passenger.MainActivities.MainFragment;
import com.LnPay.passenger.R;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;


@SuppressLint("SpecifyJobSchedulerIdRange")
public class BroadcastService extends JobService {
    private boolean jobCancelled;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        Log.i("Baguvix","Started");
        doBackgroundWork(jobParameters);
        return true;
    }

    private void doBackgroundWork(JobParameters params){

                db.collection("BroadcastMessage").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        Log.i("Baguvix", "Pointing at "+"work at the Background");

                        if (jobCancelled){
                            return;
                        }

                        for (DocumentChange documentChange : value.getDocumentChanges()){

                            String receipient =  documentChange.getDocument().getId();

                            if (documentChange.getType() == DocumentChange.Type.MODIFIED){

                                if (receipient.equals("Passenger"))
                                    buildNotification(documentChange.getDocument().getString("title"), documentChange.getDocument().getString("message"), 5);
                            }
                        }

                    }
                });

        jobFinished(params, true);

       /* new Thread(new Runnable() {
            @Override
            public void run() {
               for (int i = 0; i < 10; i++) {
                   Log.i("Baguvix", "Pointing at " + i);

                   if (jobCancelled) {
                       return;
                   }
                   try {
                       Thread.sleep(2000);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }

                   jobFinished(params, true);
               }
            }
        }).start();*/

    }


    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.i("Baguvix", "onStopJob: Pre-Cancelled");
        jobCancelled = true;
        return true;
    }


    private void buildNotification(String title, String contentText, int chanDesc) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getApplicationContext().getString(R.string.channel_name);
            String description = "Hello" + chanDesc;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
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
