package com.example.shalehatbooking.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shalehatbooking.R;
import com.example.shalehatbooking.model.Booking;
import com.example.shalehatbooking.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.List;

public class AdminBookingAdapter extends RecyclerView.Adapter<AdminBookingAdapter.AdminBookingHolder> {
   List<Booking> mItems;
    CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("users");
   OnClickItemBooking listener;
    public AdminBookingAdapter(List<Booking> mItems) {
        this.mItems = mItems;

    }
    public void setOnClickItemBooking(OnClickItemBooking listener) {
        this.listener = listener;
    }
    @NonNull
    @Override
    public AdminBookingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_custom_admin_book, parent, false);
        AdminBookingHolder viewHolder = new AdminBookingHolder(v);
        return viewHolder;
    }
    public void setBooking(int position ,Booking booking) {
        mItems.set(position,booking);
        notifyItemChanged(position,booking);

    }
    @Override
    public void onBindViewHolder(@NonNull AdminBookingHolder holder, int position) {
        Booking booking = mItems.get(position);
        collectionReference.document(booking.getUserId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                holder.booking_name_chalet_text.setText(booking.getNameChalet());
                holder.booking_name_chalet_text.setTag(booking);
                holder.booking_status_chalet_text.setText(booking.getStatus());
                holder.booking_username_chalet_text.setText(user.getUsername());
                holder.booking_phone_chalet_text.setText(user.getPhone());
            }
        });

    }


    @Override
    public int getItemCount() {
        return mItems.size();
    }


    class AdminBookingHolder extends RecyclerView.ViewHolder {
        private TextView booking_name_chalet_text,
                booking_status_chalet_text, booking_username_chalet_text, booking_phone_chalet_text;

        public AdminBookingHolder(@NonNull final View itemView) {
            super(itemView);
            booking_name_chalet_text = itemView.findViewById(R.id.booking_name_chalet_text);
            booking_status_chalet_text = itemView.findViewById(R.id.booking_status_chalet_text);
            booking_username_chalet_text = itemView.findViewById(R.id.booking_username_chalet_text);
            booking_phone_chalet_text = itemView.findViewById(R.id.booking_phone_chalet_text);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onEditBook(getAdapterPosition(),(Booking) booking_name_chalet_text.getTag());
                }
            });
        }

    }

    public interface OnClickItemBooking {
        void onEditBook(int position,Booking booking);
    }
}

