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
import com.example.shalehatbooking.model.Slide;

import java.util.ArrayList;
import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {
    List<Slide> mItems;
    OnListItemClickedSlider listener;

    public SliderAdapter(List<Slide> mItems, OnListItemClickedSlider listener) {
        this.mItems = mItems;
        this.listener = listener;

    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_custom_slider, parent, false);
        SliderViewHolder viewHolder = new SliderViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        Slide slide = mItems.get(position);
        holder.title_slider.setText(slide.getTitle());
        holder.title_slider.setTag(slide);
        String url = slide.getImageUrl();
        Glide.with(holder.img_slider.getContext())
                .load(url)
                .centerCrop()
                .into(holder.img_slider);

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }


    class SliderViewHolder extends RecyclerView.ViewHolder {
        ImageView img_slider;
        TextView title_slider;

        public SliderViewHolder(@NonNull final View itemView) {
            super(itemView);
            img_slider = itemView.findViewById(R.id.img_slider);
            title_slider = itemView.findViewById(R.id.title_slider);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick((Slide) title_slider.getTag());
                }
            });
        }
    }

    public interface OnListItemClickedSlider {
        void onItemClick(Slide slide);
    }

}

