package com.example.shalehatbooking.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shalehatbooking.R;
import com.example.shalehatbooking.model.Shalehats;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ShalehatsAdapterAdmin extends RecyclerView.Adapter<ShalehatsAdapterAdmin.ShalehatsViewHolder> {
    private List<Shalehats> mItems;
    private OnListItemClicked listItemClicked;
    CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("Shalehats");
    boolean isAdmin = false;

    public ShalehatsAdapterAdmin(List<Shalehats> mItems, OnListItemClicked listItemClicked) {
        this.mItems = mItems;
        this.listItemClicked = listItemClicked;
    }


    @NonNull
    @Override
    public ShalehatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_custom_shalet_admin, parent, false);
        ShalehatsViewHolder viewHolder = new ShalehatsViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ShalehatsViewHolder holder, int position) {
        Shalehats shalehats = mItems.get(position);

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
        return mItems.size();
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    class ShalehatsViewHolder extends RecyclerView.ViewHolder {
        private ImageView shalehat_img, ic_delete;
        private TextView shalehat_name, shalehat_location, shalehat_price;
        private RatingBar shalehat_rating;

        public ShalehatsViewHolder(@NonNull final View itemView) {
            super(itemView);
            shalehat_img = itemView.findViewById(R.id.shalehat_img);
            shalehat_name = itemView.findViewById(R.id.shalehat_name);
            shalehat_location = itemView.findViewById(R.id.shalehat_location);
            shalehat_price = itemView.findViewById(R.id.shalehat_price);
            shalehat_rating = itemView.findViewById(R.id.shalehat_rating);
            ic_delete = itemView.findViewById(R.id.ic_delete);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listItemClicked != null) {
                        listItemClicked.onItemClick((String) shalehat_img.getTag());
                    }
                }
            });

            ic_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Shalehats shalehats = (Shalehats) shalehat_name.getTag();
                    collectionReference.document(shalehats.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mItems.remove(shalehats);
                            notifyDataSetChanged();

                        //notifyItemRemoved(getAdapterPosition());

                        }
                    });
                }
            });
        }


    }

    public interface OnListItemClicked {
        void onItemClick(String position);
    }


}

