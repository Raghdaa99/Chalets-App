package com.example.shalehatbooking.view.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.shalehatbooking.MainActivity;
import com.example.shalehatbooking.R;
import com.example.shalehatbooking.model.Booking;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditBookingDialogFragment extends DialogFragment implements View.OnFocusChangeListener{
    public static final String BOOKING = "booking";
    private EditText ed_date, ed_number_of_days;
    DatePickerDialog picker;
    OnEditListenerDialog listener;
    Booking booking;

    public EditBookingDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
           booking = (Booking) getArguments().getSerializable(BOOKING);
            System.out.println(booking.getDate());
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnEditListenerDialog) {
            listener = (OnEditListenerDialog) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder alBuilder = new AlertDialog.Builder(getContext());
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_edit_booking_dialog, null);

        ed_date = view.findViewById(R.id.ed_date);
        ed_number_of_days = view.findViewById(R.id.ed_number_of_days);
        ed_date.setOnFocusChangeListener(this);
        alBuilder.setView(view)
                .setTitle(getString(R.string.edit_booking))
                .setPositiveButton("update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String date = ed_date.getText().toString();
                        String number_of_days = ed_number_of_days.getText().toString();
                        if (!date.isEmpty() && !number_of_days.isEmpty()) {
                            listener.onEdit(date, number_of_days);
                        }
                    }
                }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
        if (booking != null){
            setValues(booking);
        }
        return alBuilder.create();
    }

    private void setValues(Booking booking) {
        ed_date.setText(booking.getDate());
        ed_number_of_days.setText(booking.getNumberOfDays()+"");
    }


    public void foucsDate() {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);
                picker = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                calendar.set(Calendar.YEAR, year);
                                calendar.set(Calendar.MONTH, monthOfYear);
                                Date selectedDate = calendar.getTime();
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                ed_date.setText(simpleDateFormat.format(selectedDate));
                            }
                        }, year, month, day);

                ed_date.clearFocus();
                picker.show();
            }



    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()){
            case R.id.ed_date:
                foucsDate();
                break;
        }
    }

    public interface OnEditListenerDialog {
        void onEdit(String date, String num_days);
    }
}