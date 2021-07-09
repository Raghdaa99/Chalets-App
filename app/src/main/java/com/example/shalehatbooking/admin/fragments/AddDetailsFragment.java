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

import com.example.shalehatbooking.R;
import com.example.shalehatbooking.model.Shalehats;
import com.example.shalehatbooking.model.Slide;
import com.example.shalehatbooking.view.adapter.SliderAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddDetailsFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "AddDetailsFragment";
    private RecyclerView recyclerView;
    private FloatingActionButton floating_add_btn;
    private SliderAdapter adapter;
    private List<Slide> slideList;
    private static final String ARG_PARAM1 = "idChalet";
    private OnAddSliderPicture listener;
    // TODO: Rename and change types of parameters
    private String idChalet;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private Query mQuery;

    public AddDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnAddSliderPicture) {
            listener = (OnAddSliderPicture) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement");
        }
    }

    public static AddDetailsFragment newInstance(String idChalet) {
        AddDetailsFragment fragment = new AddDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, idChalet);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            idChalet = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_details, container, false);
        recyclerView = view.findViewById(R.id.recycler_details_slider);
        floating_add_btn = view.findViewById(R.id.floating_add_btn);
        floating_add_btn.setOnClickListener(this);

        initFirestore();
        initRecyclerView();
        adapter = new SliderAdapter(slideList, new SliderAdapter.OnListItemClickedSlider() {
            @Override
            public void onItemClick(Slide slide) {
                listener.updateSlider(idChalet, slide);
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    private void initRecyclerView() {
    }

    private void initFirestore() {
        slideList = new ArrayList<>();
        firebaseFirestore.collection("Shalehats/" + idChalet + "/pictures")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e(TAG, error.getMessage());
                            return;
                        }
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                slideList.add(dc.getDocument().toObject(Slide.class));
                            }

                            adapter.notifyDataSetChanged();
                            //System.out.println(shalehatsList.get(0).getName()+" 2222");
                        }
                    }

                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floating_add_btn:
                listener.addSlider(idChalet);
                break;
        }
    }

    public void editSlide(Slide slide) {
        Map<String, Object> mapSlide = new HashMap<>();
        mapSlide.put("title", slide.getTitle());
        mapSlide.put("imageUrl", slide.getImageUrl());
        updatePicToFirebase(mapSlide, slide);
    }

    private void updatePicToFirebase(Map<String, Object> mapSlide, Slide slide) {
        System.out.println(slide.getId()+" slide.getId");
        System.out.println(idChalet + " idChalet");
        firebaseFirestore.collection("Shalehats/" + idChalet + "/pictures").document(slide.getId()).update(mapSlide)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Toast.makeText(getContext(), "onSuccess", Toast.LENGTH_SHORT).show();
                        adapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(getContext(), "onFailure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addSlide(Slide slide) {
        DocumentReference documentReference = firebaseFirestore.collection("Shalehats/" + idChalet + "/pictures")
                .document();
        documentReference.set(slide).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Log.d(TAG, "onSuccess: " + idChalet);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e.toString());
            }
        });
    }

    public interface OnAddSliderPicture {
        void addSlider(String id_chalet);

        void updateSlider(String id_chalet, Slide slide);
    }
}