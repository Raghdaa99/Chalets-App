package com.example.shalehatbooking.view.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shalehatbooking.MainActivity;
import com.example.shalehatbooking.R;
import com.example.shalehatbooking.model.Booking;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;


public class ConfirmationFragment extends Fragment {

    private static final String TAG = "ConfirmationFragment";
    private static final String ARG_PRICE = "booking";
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    // TODO: Rename and change types of parameters
    private Booking booking;

    public ConfirmationFragment() {
        // Required empty public constructor
    }

    public static ConfirmationFragment newInstance(Booking booking) {
        ConfirmationFragment fragment = new ConfirmationFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PRICE, booking);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            booking = (Booking) getArguments().getSerializable(ARG_PRICE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_confirmation, container, false);
        Button confirm_btn = view.findViewById(R.id.confirm_btn);
        TextView confirm_price = view.findViewById(R.id.confirm_price);
        if (booking != null) {
            confirm_price.setText(booking.getTotalPrice() + "");
        }
        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (booking != null) {
                    uploadBookingToFirebase(booking);
                }
            }
        });
        return view;
    }

    private void uploadBookingToFirebase(Booking booking) {
        firestore.collection("Booking").document().set(booking).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: ");
                Toast.makeText(getActivity(), "Booking Chalet", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                getActivity().startActivity(intent);
                getActivity().finish();
//                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragment_containar, new HomeFragment())
//                        .commit();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: " + e.getMessage());
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Confirmation Booking");
    }

}