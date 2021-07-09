package com.example.shalehatbooking.view.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.shalehatbooking.MainActivity;
import com.example.shalehatbooking.R;
import com.example.shalehatbooking.Service.ListenOrder;
import com.example.shalehatbooking.model.Shalehats;
import com.example.shalehatbooking.view.adapter.ShalehatsAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment implements
        ShalehatsAdapter.OnListItemClicked, View.OnClickListener {
    private static final String TAG = "HomeFragment";
    private RecyclerView recyclerView;
    private ShalehatsAdapter shalehatsAdapter;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference chalehatRef = firebaseFirestore.collection("Shalehats");
    private List<Shalehats> shalehatsList;
    private OnItemClickSendToDetails listener;
    private ShowDialogListener dialogListener;
    private FilterDialogFragment mFilterDialog;
    private ProgressBar progress_loading;
    private Toolbar mToolbar;
    private TextView mCurrentSearchView;
    private TextView mCurrentSortByView;
    private ViewGroup mEmptyView;
    private Query mQuery;
    private Animation fade_in;
    private Animation fade_out;
    private SearchView searchView;
    private String query_search = "";

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnItemClickSendToDetails) {
            listener = (OnItemClickSendToDetails) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement");
        }
        if (context instanceof ShowDialogListener) {
            dialogListener = (ShowDialogListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        fade_in = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        fade_out = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
        recyclerView = view.findViewById(R.id.recycler_home);
        progress_loading = view.findViewById(R.id.progress_loading);
//        mToolbar = view.findViewById(R.id.toolbar);
//        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        setHasOptionsMenu(true);
        mCurrentSearchView = view.findViewById(R.id.text_current_search);
        mCurrentSortByView = view.findViewById(R.id.text_current_sort_by);

        view.findViewById(R.id.filter_bar).setOnClickListener(this);
        view.findViewById(R.id.button_clear_filter).setOnClickListener(this);


        initFirestore();
        initRecyclerView();

        // Filter Dialog
        mFilterDialog = new FilterDialogFragment();


        return view;
    }

    private void initRecyclerView() {
        if (mQuery == null) {
            Log.w(TAG, "No query, not initializing RecyclerView");

        }
        System.out.println("-------");
        //shalehatsAdapter = new ShalehatsAdapter(shalehatsList,this);

        shalehatsAdapter = new ShalehatsAdapter(shalehatsList, this);

        recyclerView.setAdapter(shalehatsAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    private void initFirestore() {

        mQuery = chalehatRef.orderBy("rating", Query.Direction.DESCENDING);
        shalehatsList = new ArrayList<>();
        setQuery(mQuery);
    }

    private void setQuery(Query mQuery) {
        mQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                System.out.println("////");
                if (error != null) {
                    Log.e(TAG, error.getMessage());
                    return;
                }

                recyclerView.startAnimation(fade_in);
                progress_loading.startAnimation(fade_out);
                for (DocumentChange dc : value.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.ADDED) {
                        shalehatsList.add(dc.getDocument().toObject(Shalehats.class));
                    }

                    shalehatsAdapter.notifyDataSetChanged();
                }


            }
        });

    }

    @Override
    public void onItemClick(String position) {
        listener.sendToDetails(position);
    }

    public void setQueryFromDialog(Query query, Filters filters) {
        //mQuery = query;
        //shalehatsAdapter.setQuery(query);
        shalehatsAdapter.clear();
        setQuery(query);
        // Set header
        mCurrentSearchView.setText(Html.fromHtml(filters.getSearchDescription(getContext())));
        mCurrentSortByView.setText(filters.getOrderDescription(getContext()));
    }


    public interface OnItemClickSendToDetails {
        void sendToDetails(String position);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.filter_bar:
                onFilterClicked();
                break;
            case R.id.button_clear_filter:
                onClearFilterClicked();
        }
    }

    private void onClearFilterClicked() {
        dialogListener.clearFilter();
    }

    private void onFilterClicked() {
        // Show the dialog containing filter options
        dialogListener.showDialog();
    }

    public interface ShowDialogListener {
        void showDialog();

        void clearFilter();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Chalets App");
    }



}