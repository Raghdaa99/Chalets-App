package com.example.shalehatbooking.view.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shalehatbooking.MainActivity;
import com.example.shalehatbooking.R;
import com.example.shalehatbooking.model.Booking;
import com.example.shalehatbooking.model.Shalehats;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.installations.InstallationTokenResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BookingFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "BookingFragment";
    private static final String CHALET_ID = "param1";

    private CalendarView simpleCalendarView;
    private TextView date_txt;
    private EditText ed_number_of_days;
    private Button booking_next_btn;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    // TODO: Rename and change types of parameters
    private String chaletId;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public BookingFragment() {
        // Required empty public constructor
    }

    public static BookingFragment newInstance(String chaletId) {
        BookingFragment fragment = new BookingFragment();
        Bundle args = new Bundle();
        args.putString(CHALET_ID, chaletId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            chaletId = getArguments().getString(CHALET_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_booking, container, false);
        simpleCalendarView = view.findViewById(R.id.simpleCalendarView);
        date_txt = view.findViewById(R.id.date_txt);
        ed_number_of_days = view.findViewById(R.id.ed_number_of_days);
        booking_next_btn = view.findViewById(R.id.booking_next_btn);
        booking_next_btn.setOnClickListener(this);
        if (date_txt.getText().toString().equals("")) {
            final Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);
            date_txt.setText(day + "/" + month + "/" + year);
        }
        simpleCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = dayOfMonth + "/" + month + "/" + year;
                date_txt.setText(date);
            }
        });
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.booking_next_btn:
                bookingChalet();
                break;
        }
    }

    private void bookingChalet() {
        firestore.collection("Shalehats").document(chaletId).
                addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        Shalehats shalehats = value.toObject(Shalehats.class);
                        double price = shalehats.getPrice();
                        String name_chalet = shalehats.getName();
                        String userId = getCurrentUserId();
                        String id_chalet = chaletId;
                        String date = date_txt.getText().toString();
                        String num_days = ed_number_of_days.getText().toString();
                        String status = "waiting";

                        if (!TextUtils.isEmpty(num_days) && !TextUtils.isEmpty(date)
                                && !TextUtils.isEmpty(id_chalet) && !TextUtils.isEmpty(userId)) {
                            int number_of_days = Integer.parseInt(num_days);
                            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                                @Override
                                public void onComplete(@NonNull Task<String> task) {
                                    String token = task.getResult();
                                    Booking booking = new Booking(name_chalet, userId, id_chalet, date, status,token, number_of_days, price * number_of_days);
                                    moveToConfirmFragment(booking);
                                }
                            });
//                            FirebaseInstallations.getInstance().getToken(true).addOnCompleteListener(new OnCompleteListener<InstallationTokenResult>() {
//                                @Override
//                                public void onComplete(@NonNull Task<InstallationTokenResult> task) {
//
//                                }
//                            });

                        } else {
                            Toast.makeText(getActivity(), "Enter data...", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    private void moveToConfirmFragment(Booking booking) {
        ConfirmationFragment confirmationFragment = ConfirmationFragment.newInstance(booking);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containar, confirmationFragment)
                .addToBackStack(null).commit();

    }

    private String getCurrentUserId() {
        return mAuth.getCurrentUser().getUid();
    }


//    private void UpdateToken(){
//        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
//        String refreshToken= FirebaseInstanceId.getInstance().getToken();
//        Token token= new Token(refreshToken);
//        FirebaseDatabase.getInstance().getReference("Tokens").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(token);
//    }
@Override
public void onStart() {
    super.onStart();
    ((MainActivity) getActivity()).getSupportActionBar().setTitle("Booking");
}


}