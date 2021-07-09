package com.example.shalehatbooking.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.shalehatbooking.R;
import com.example.shalehatbooking.dbHelper.DatabaseHandler;
import com.example.shalehatbooking.model.Favourite;

import java.util.List;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder> {
    List<Favourite> mItems;
    DatabaseHandler databaseHandler;
    OnItemClickFavListener listener;

    public FavouriteAdapter(List<Favourite> mItems, OnItemClickFavListener listener) {
        this.mItems = mItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FavouriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_custom_fav, parent, false);
        FavouriteViewHolder viewHolder = new FavouriteViewHolder(v);
        databaseHandler = new DatabaseHandler(parent.getContext());
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteViewHolder holder, int position) {
        Favourite favourite = mItems.get(position);
        holder.fav_name_chalet_text.setText(favourite.getNameChalet());
        holder.fav_price_chalet_text.setText(favourite.getPrice() + "");
        Glide.with(holder.img_fav.getContext())
                .load(favourite.getImgChalet())
                .centerCrop()
                .into(holder.img_fav);
        holder.icon_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseHandler.deleteFavourite(favourite);
                mItems.remove(favourite);
                notifyItemRemoved(position);
            }
        });
        holder.fav_details_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickFav(favourite.getIdChalet());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }


    class FavouriteViewHolder extends RecyclerView.ViewHolder {
        ImageView img_fav, fav_details_icon,icon_fav;
        TextView fav_name_chalet_text, fav_price_chalet_text;

        public FavouriteViewHolder(@NonNull final View itemView) {
            super(itemView);
            img_fav = itemView.findViewById(R.id.img_fav);
            icon_fav = itemView.findViewById(R.id.icon_fav);
            fav_details_icon = itemView.findViewById(R.id.fav_details_icon);
            fav_name_chalet_text = itemView.findViewById(R.id.fav_name_chalet_text);
            fav_price_chalet_text = itemView.findViewById(R.id.fav_price_chalet_text);
        }
    }

    public interface OnItemClickFavListener {
        void onClickFav(String idChalet);
    }

}

