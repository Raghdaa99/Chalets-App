package com.example.shalehatbooking.admin.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.shalehatbooking.MainActivity;
import com.example.shalehatbooking.R;
import com.example.shalehatbooking.admin.activites.AdminMainActivity;
import com.example.shalehatbooking.model.Shalehats;
import com.example.shalehatbooking.view.adapter.ShalehatsAdapter;
import com.example.shalehatbooking.view.adapter.ShalehatsAdapterAdmin;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminHomeFragment extends Fragment implements ShalehatsAdapterAdmin.OnListItemClicked ,View.OnClickListener{

    private static final String TAG = "AdminHomeFragment";
    private RecyclerView recyclerView;
    private ShalehatsAdapterAdmin shalehatsAdapter;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference chalehatRef = firebaseFirestore.collection("Shalehats");
    private List<Shalehats> shalehatsList;
    private Query mQuery;
    private OnSelectedChaletUpdated listener;
    private FloatingActionButton floating_add_btn;

    public AdminHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnSelectedChaletUpdated) {
            listener = (OnSelectedChaletUpdated) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);
        recyclerView = view.findViewById(R.id.recycler_home);
        floating_add_btn = view.findViewById(R.id.floating_add_btn);
        floating_add_btn.setOnClickListener(this);
        initFirestore();
        initRecyclerView();

        return view;
    }

    private void initRecyclerView() {
        if (mQuery == null) {
            Log.w(TAG, "No query, not initializing RecyclerView");

        }
        System.out.println("-------");
        shalehatsAdapter = new ShalehatsAdapterAdmin(shalehatsList, this);

        recyclerView.setAdapter(shalehatsAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void initFirestore() {

        mQuery = chalehatRef.orderBy("rating", Query.Direction.DESCENDING);
        shalehatsList = new ArrayList<>();
        mQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, error.getMessage());
                    return;
                }
                for (DocumentChange dc : value.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.ADDED) {
                        shalehatsList.add(dc.getDocument().toObject(Shalehats.class));
                    }

                    shalehatsAdapter.notifyDataSetChanged();
                    System.out.println(shalehatsList.get(0).getName() + " 2222");
                }
            }
        });


    }

    @Override
    public void onItemClick(String idChalet) {
        listener.updateChalet(idChalet);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.floating_add_btn:
                moveToAddChalet();
                break;
        }
    }

    private void moveToAddChalet() {
        NewChaletFragment newChaletFragment = new NewChaletFragment();
        getActivity().getSupportFragmentManager()
                .beginTransaction().replace(R.id.fragment_containar, newChaletFragment).addToBackStack(null).commit();
    }

    public interface OnSelectedChaletUpdated {
        void updateChalet(String idChalet);
    }
    @Override
    public void onStart() {
        super.onStart();
        ((AdminMainActivity) getActivity()).getSupportActionBar().setTitle("Admin");
    }
}