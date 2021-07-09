package com.example.shalehatbooking.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.shalehatbooking.MainActivity;
import com.example.shalehatbooking.R;
import com.example.shalehatbooking.admin.fragments.AllBookingFragment;
import com.example.shalehatbooking.model.Booking;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import static com.example.shalehatbooking.Service.App.CHANNEL_ID;

public class ListenOrder extends Service {
    CollectionReference collectionReference;

    public ListenOrder() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        collectionReference = FirebaseFirestore.getInstance().collection("Booking");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String idBooking = intent.getStringExtra("idBooking");
        collectionReference.document(idBooking).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                Booking booking = (Booking) value.toObject(Booking.class);
                showNotification(booking);
            }
        });
      //  return super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    private void showNotification(Booking booking) {
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        intent.putExtra("userId", booking.getUserId());
        PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getBaseContext());
//        mBuilder.setAutoCancel(true)
//                .setDefaults(NotificationCompat.DEFAULT_ALL)
//                .setWhen(System.currentTimeMillis())
//                .setTicker("Chalets App")
//                .setContentInfo("Your Booking is Updated")
//                .setContentText("Your Booking #" + booking.getId() + " was update status to " + booking.getStatus())
//                .setContentIntent(contentIntent)
//        .setSmallIcon(R.drawable.ic_favorite);
//        // mBuilder.build();
//        NotificationManager notificationmanagr  = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationmanagr.notify(1,mBuilder.build());

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setTicker("Chalets App")
                .setContentInfo("Your Booking is Updated")
                .setContentText("Your Booking #" + booking.getId() + " was update status to " + booking.getStatus())
                .setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_favorite)
                .build();
        startForeground(1, notification);
    }
}