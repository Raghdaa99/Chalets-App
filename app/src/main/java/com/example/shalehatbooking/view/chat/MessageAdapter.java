package com.example.shalehatbooking.view.chat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.shalehatbooking.R;
import com.example.shalehatbooking.model.Message;
import com.example.shalehatbooking.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    List<Message> mItems;
    Context context;
    CollectionReference collectionReference =FirebaseFirestore.getInstance().collection("users");
    public MessageAdapter(List<Message> mItems) {
        this.mItems = mItems;

    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_item_message, parent, false);
        MessageViewHolder viewHolder = new MessageViewHolder(v);
        context = parent.getContext();
        return viewHolder;
    }
    public void addItems(List<Message> messageList){
        mItems.addAll(messageList);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = mItems.get(position);

        if(message.getUserEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
            holder.linear_sender.setVisibility(View.VISIBLE);
            holder.message_body_text_sender.setText(message.getMessageBody());
            holder.linear_receiver.setVisibility(View.GONE);
           // holder.root.setBackgroundColor(ContextCompat.getColor(context,R.color.colorSender));
        }else{
            holder.linear_sender.setVisibility(View.GONE);
            holder.linear_receiver.setVisibility(View.VISIBLE);
            holder.message_body_text_receiver.setText(message.getMessageBody());
            collectionReference.whereEqualTo("email",message.getUserEmail()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    List<User> users = value.toObjects(User.class);
                    User user =users.get(0);
                    holder.recevire_name.setText(user.getUsername());
                    Glide.with(context)
                            .load(user.getImage())
                            .placeholder(R.drawable.profile)
                            .centerCrop()
                            .into(holder.img_receiver);
                    Log.d("TAG", "onEvent: "+ users.get(0).getId());
                }
            });
//            Glide.with(context)
//                    .load(user.getImage())
//                    .centerCrop()
//                    .into(img_header);
//            holder.img_sender.setVisibility(View.GONE);
//            holder.img_receiver.setVisibility(View.VISIBLE);
//            holder.root.setBackgroundColor(ContextCompat.getColor(context,R.color.colorReceiver));
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }


    class MessageViewHolder extends RecyclerView.ViewHolder {
        private TextView message_body_text_sender,message_body_text_receiver,recevire_name;
        private ImageView img_sender, img_receiver;
        private LinearLayout linear_sender,linear_receiver;

        public MessageViewHolder(@NonNull final View itemView) {
            super(itemView);
            message_body_text_sender = itemView.findViewById(R.id.message_body_text_sender);
            message_body_text_receiver = itemView.findViewById(R.id.message_body_text_receiver);
            img_receiver = itemView.findViewById(R.id.message_receiver_img);
            linear_sender = itemView.findViewById(R.id.linear_sender);
            linear_receiver = itemView.findViewById(R.id.linear_receiver);
            recevire_name = itemView.findViewById(R.id.recevire_name);
        }

    }


}

