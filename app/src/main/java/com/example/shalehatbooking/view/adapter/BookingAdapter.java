package com.example.shalehatbooking.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shalehatbooking.R;
import com.example.shalehatbooking.model.Booking;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {
    List<Booking> mItems;
    CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("Booking");
    OnClickItemBooking listener;

    public BookingAdapter(List<Booking> mItems) {
        this.mItems = mItems;

    }

    public void setOnClickItemBooking(OnClickItemBooking listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_custom_book, parent, false);
        BookingViewHolder viewHolder = new BookingViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = mItems.get(position);
        holder.booking_name_chalet_text.setText(booking.getNameChalet());
        holder.booking_date_chalet_text.setText(booking.getDate());
        holder.booking_days_chalet_text.setText(booking.getNumberOfDays() + "");
        holder.booking_status_chalet_text.setText(booking.getStatus());
        holder.booking_price_chalet_text.setText(booking.getTotalPrice() + "");
        holder.booking_status_chalet_text.setText(booking.getStatus());
        holder.booking_name_chalet_text.setTag(booking);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setBooking(int position ,Booking booking) {
        mItems.set(position,booking);
        notifyItemChanged(position,booking);

    }


    class BookingViewHolder extends RecyclerView.ViewHolder {
        private TextView booking_name_chalet_text, booking_date_chalet_text,
                booking_days_chalet_text, booking_status_chalet_text, booking_price_chalet_text,
                booking_remove, booking_edit, booking_details;

        public BookingViewHolder(@NonNull final View itemView) {
            super(itemView);
            booking_name_chalet_text = itemView.findViewById(R.id.booking_name_chalet_text);
            booking_date_chalet_text = itemView.findViewById(R.id.booking_date_chalet_text);
            booking_days_chalet_text = itemView.findViewById(R.id.booking_days_chalet_text);
            booking_status_chalet_text = itemView.findViewById(R.id.booking_status_chalet_text);
            booking_price_chalet_text = itemView.findViewById(R.id.booking_price_chalet_text);
            booking_remove = itemView.findViewById(R.id.booking_remove);
            booking_edit = itemView.findViewById(R.id.booking_edit);
            booking_details = itemView.findViewById(R.id.booking_details);
            booking_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onEditBook(getAdapterPosition(),(Booking) booking_name_chalet_text.getTag());
                }
            });
            booking_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Booking booking =(Booking) booking_name_chalet_text.getTag();
                    collectionReference.document(booking.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mItems.remove(booking);
                            notifyItemRemoved(getAdapterPosition());
                        }
                    });
                }
            });
        }
    }

    public interface OnClickItemBooking {
        void onEditBook(int position,Booking booking);
    }
}

