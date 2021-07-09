package com.example.shalehatbooking.admin.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.shalehatbooking.R;
import com.example.shalehatbooking.Service.ListenOrder;
import com.example.shalehatbooking.model.Booking;
import com.example.shalehatbooking.model.User;
import com.example.shalehatbooking.sendNotification.APIService;
import com.example.shalehatbooking.sendNotification.Client;
import com.example.shalehatbooking.sendNotification.Data;
import com.example.shalehatbooking.sendNotification.MyResponse;
import com.example.shalehatbooking.sendNotification.NotificationSender;
import com.example.shalehatbooking.view.adapter.AdminBookingAdapter;
import com.example.shalehatbooking.view.adapter.BookingAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllBookingFragment extends Fragment implements AdminBookingAdapter.OnClickItemBooking {
    private RecyclerView recyclerView;
    private AdminBookingAdapter adapter;
    private List<Booking> bookingList;
    private APIService apiService;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference bookingRef = firebaseFirestore.collection("Booking");
    CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("users");
    private Query mQuery;
    private static final String TAG = "AllBookingFragment";
    private final String SENT = "SMS_SENT";
    private final String DELIVERED = "SMS_DELIVERED";
    private final int MY_PERMISSION_REQ_SMS = 1;
    private PendingIntent sendPi;
    private PendingIntent deliverdPi;
    private BroadcastReceiver smsSentReciever;
    private BroadcastReceiver smsDELIVEREDReciever;

    public AllBookingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sendPi = PendingIntent.getBroadcast(getActivity(), 0, new Intent(SENT), 0);
        deliverdPi = PendingIntent.getBroadcast(getActivity(), 0, new Intent(DELIVERED), 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_booking, container, false);

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        recyclerView = view.findViewById(R.id.recycler_booking);

        initFirestore();
        initRecyclerView();
        adapter.setOnClickItemBooking(this);

        return view;
    }

    private void initFirestore() {
        //  String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mQuery = bookingRef.orderBy("date", Query.Direction.DESCENDING);
        bookingList = new ArrayList<>();
        setQuery(mQuery);
    }

    private void initRecyclerView() {
        if (mQuery == null) {
            Log.w(TAG, "No query, not initializing RecyclerView");

        }
        System.out.println("-------");
        //shalehatsAdapter = new ShalehatsAdapter(shalehatsList,this);

        adapter = new AdminBookingAdapter(bookingList);

        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    private void setQuery(Query mQuery) {
        mQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, error.getMessage());
                    return;
                }
                for (DocumentChange dc : value.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.ADDED) {
                        Booking booking = dc.getDocument().toObject(Booking.class);
                        bookingList.add(booking);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onEditBook(int position, Booking booking) {
        AlertDialog.Builder alBuilder = new AlertDialog.Builder(getContext());
        View view = getActivity().getLayoutInflater().inflate(R.layout.change_status, null);
        Spinner spinner = view.findViewById(R.id.spinner_status);

        alBuilder.setView(view)
                .setTitle("Please Fill information")
                .setPositiveButton("updated", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String status = (String) spinner.getSelectedItem();
                        System.out.println(status);
                        booking.setStatus(status);
                        bookingRef.document(booking.getId()).set(booking).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                adapter.setBooking(position, booking);
                                String stat = "Your Booking " + booking.getNameChalet() + " was update status to " + status;

                                sendNotifications(booking.getToken(), stat);
                                collectionReference.document(booking.getUserId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        User user = documentSnapshot.toObject(User.class);
                                        sendBroadcast(user.getPhone());
                                    }
                                });

                            }
                        });
                    }
                }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alBuilder.create().show();
    }

    private void sendBroadcast(String phone) {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSION_REQ_SMS);
        } else {
            String message = "update your CHalet Booking";
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phone, null, message, sendPi, deliverdPi);
        }
    }

    private void sendNotifications(String token, String message) {
        String title = "Your Booking is Updated";
        Data data = new Data(title, message);
        NotificationSender sender = new NotificationSender(data, token);
        apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                Log.d(TAG, "onResponse: 33333");
                if (response.code() == 200) {
                    if (response.body().success != 1) {
                        Toast.makeText(getActivity(), "Failed ", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(smsSentReciever);
        getActivity().unregisterReceiver(smsDELIVEREDReciever);
    }

    @Override
    public void onResume() {
        super.onResume();
        smsSentReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "SMS sent Successfully", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(context, "No Service", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(context, "null PDU", Toast.LENGTH_SHORT).show();
                        break;
//                    case  SmsManager.RESULT_ERROR_RADIO_OFF:
//                        Toast.makeText(context, "No Service", Toast.LENGTH_SHORT).show();
//                        break;
                }
            }
        };
        smsDELIVEREDReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(context, "SMS Delivered Successfully", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(context, "SMS NOT Delivered", Toast.LENGTH_SHORT).show();
                        break;
                }
            }


        };
getActivity().registerReceiver(smsSentReciever,new IntentFilter(SENT));
getActivity().registerReceiver(smsDELIVEREDReciever,new IntentFilter(DELIVERED));
    }
}