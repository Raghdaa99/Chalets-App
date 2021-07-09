package com.example.shalehatbooking.view.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shalehatbooking.R;

import com.example.shalehatbooking.model.Shalehats;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ShalehatsAdapter extends RecyclerView.Adapter<ShalehatsAdapter.ShalehatsViewHolder> {
    private List<Shalehats> mItemsOriginal;

    private OnListItemClicked listItemClicked;
  
    CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("Shalehats");

    public ShalehatsAdapter(List<Shalehats> mItemsOriginal, OnListItemClicked listItemClicked) {
        this.mItemsOriginal = mItemsOriginal;

        this.listItemClicked = listItemClicked;

    }


    @NonNull
    @Override
    public ShalehatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_custom_shalet, parent, false);
        ShalehatsViewHolder viewHolder = new ShalehatsViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ShalehatsViewHolder holder, int position) {
        Shalehats shalehats = mItemsOriginal.get(position);

        holder.shalehat_name.setText(shalehats.getName());
        holder.shalehat_name.setTag(shalehats);
        holder.shalehat_location.setText(shalehats.getLocation());
        holder.shalehat_price.setText(shalehats.getPrice() + "");
        holder.shalehat_rating.setRating((float) shalehats.getRating());
        holder.shalehat_img.setTag(shalehats.getId());
        String url = shalehats.getImage();
        Glide.with(holder.shalehat_img.getContext())
                .load(url)
                .centerCrop()
                .into(holder.shalehat_img);


    }

    @Override
    public int getItemCount() {
        return mItemsOriginal.size();
    }

    public void clear() {
        mItemsOriginal.clear();
        notifyDataSetChanged();
    }

    class ShalehatsViewHolder extends RecyclerView.ViewHolder {
        private ImageView shalehat_img;
        private TextView shalehat_name, shalehat_location, shalehat_price;
        private RatingBar shalehat_rating;

        public ShalehatsViewHolder(@NonNull final View itemView) {
            super(itemView);
            shalehat_img = itemView.findViewById(R.id.shalehat_img);
            shalehat_name = itemView.findViewById(R.id.shalehat_name);
            shalehat_location = itemView.findViewById(R.id.shalehat_location);
            shalehat_price = itemView.findViewById(R.id.shalehat_price);
            shalehat_rating = itemView.findViewById(R.id.shalehat_rating);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listItemClicked != null) {
                        listItemClicked.onItemClick((String) shalehat_img.getTag());
                    }
                }
            });


        }


    }

    public interface OnListItemClicked {
        void onItemClick(String position);
    }


}

