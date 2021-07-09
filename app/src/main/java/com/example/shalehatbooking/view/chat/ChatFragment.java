package com.example.shalehatbooking.view.chat;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shalehatbooking.MainActivity;
import com.example.shalehatbooking.R;
import com.example.shalehatbooking.model.Message;
import com.example.shalehatbooking.model.Shalehats;
import com.example.shalehatbooking.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class ChatFragment extends Fragment {
    private static final String TAG = "ChatFragment";

    private EditText ed_messageBody;
    private ImageView img_send;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private Message message;
    private String current_user_id;
    private String current_user_name;
    private List<Message> messageList;
    // TODO: Rename and change types of parameters
    private Query mQuery;

    public static final String COLLECTION_MESSAGES = "messages";
    public static final String USER_EMAIL = "userEmail";
    public static final String MESSAGE_BODY = "messageBody";
    public static final String MESSAGE_DATE = "date";

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Chatting");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        getActivity().setTitle("Chatting");
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getEmail();
        getUserName();

        ed_messageBody = view.findViewById(R.id.chat_write_editText);
        img_send = view.findViewById(R.id.chat_send_imageView);
        recyclerView = view.findViewById(R.id.chat_recycler_view);

        img_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
       getMessages();
        return view;
    }


    private void getMessages() {
        firebaseFirestore.collection(COLLECTION_MESSAGES).orderBy(MESSAGE_DATE).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    System.out.println(" ********");
                    Log.d(TAG, "onEvent: " + error.getMessage());
                    return;
                }

                messageList = new ArrayList<>();
                for (QueryDocumentSnapshot doc : value) {

                    if (doc.get(USER_EMAIL) != null && doc.get(MESSAGE_BODY) != null) {
                        messageList.add(new Message(doc.getString(USER_EMAIL),
                                doc.getString(MESSAGE_BODY), current_user_name, doc.getLong(MESSAGE_DATE)));
                        messageAdapter = new MessageAdapter(messageList);
                        recyclerView.setAdapter(messageAdapter);
                        recyclerView.scrollToPosition(messageList.size() - 1);
                        messageAdapter.notifyDataSetChanged();
                    }
                }


            }
        });
    }

    private void sendMessage() {
        String body_message = ed_messageBody.getText().toString();
        if (!TextUtils.isEmpty(body_message)) {
            if (mAuth.getCurrentUser() != null) {

                Map<String, Object> messages = new HashMap<>();
                messages.put(USER_EMAIL, current_user_id);
                messages.put("username", current_user_name);
                messages.put(MESSAGE_BODY, body_message);
                messages.put(MESSAGE_DATE, TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                firebaseFirestore.collection(COLLECTION_MESSAGES)
                        .add(messages).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "onSuccess: ");

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: ");
                    }
                });
            }
            ed_messageBody.setText("");
        }
    }

    public void getUserName() {
        String user_id = mAuth.getCurrentUser().getUid();
        firebaseFirestore.collection("users").document(user_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                User user = value.toObject(User.class);
                current_user_name = user.getUsername();
            }
        });

    }
}