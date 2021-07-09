package com.example.shalehatbooking.view.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.shalehatbooking.MainActivity;
import com.example.shalehatbooking.R;
import com.example.shalehatbooking.dbHelper.DatabaseHandler;
import com.example.shalehatbooking.view.adapter.FavouriteAdapter;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class FavouriteFragment extends Fragment {
    private FavouriteAdapter adapter;

    public FavouriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);
        RecyclerView recycler_fav = view.findViewById(R.id.recycler_fav);
        DatabaseHandler databaseHandler = new DatabaseHandler(getContext());
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        adapter = new FavouriteAdapter(databaseHandler.getAllFavourite(userId), new FavouriteAdapter.OnItemClickFavListener() {
            @Override
            public void onClickFav(String idChalet) {
                DetailsFragment detailsFragment = DetailsFragment.newInstance(idChalet);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containar, detailsFragment)
                        .addToBackStack(null).commit();
            }
        });
        recycler_fav.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler_fav.setAdapter(adapter);
        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Favourite");
    }
}