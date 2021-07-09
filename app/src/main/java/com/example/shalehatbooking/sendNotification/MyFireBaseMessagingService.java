package com.example.shalehatbooking.sendNotification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.shalehatbooking.MainActivity;
import com.example.shalehatbooking.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFireBaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFireBaseMessagingServ";
    public static final String CHANNEL_ID = "exampleServiceChannel";
    String message,title;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
        Log.d(TAG, "onMessageReceived: NotificationManager");
        title=remoteMessage.getData().get("Title");
        message=remoteMessage.getData().get("Message");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_calendar)
                        .setContentTitle("Your Booking is Updated")
                        .setContentIntent(pendingIntent)
                        .setContentText(message);
        NotificationManagerCompat manager = NotificationManagerCompat.from(getBaseContext());
        manager.notify(10, builder.build());

    }
}
