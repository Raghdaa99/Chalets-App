package com.example.shalehatbooking.view.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.shalehatbooking.MainActivity;
import com.example.shalehatbooking.R;
import com.example.shalehatbooking.model.Booking;
import com.example.shalehatbooking.model.Shalehats;
import com.example.shalehatbooking.view.adapter.BookingAdapter;
import com.example.shalehatbooking.view.adapter.ShalehatsAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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


public class MyBookingFragment extends Fragment implements BookingAdapter.OnClickItemBooking {
    private static final String TAG = "MyBookingFragment";
    private SearchView searchView;
    private RecyclerView recyclerView;
    private BookingAdapter adapter;
    private List<Booking> bookingList;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference bookingRef = firebaseFirestore.collection("Booking");
    private Query mQuery;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String id_booking;
    private Booking booking;
    private int position;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyBookingFragment() {
        // Required empty public constructor
    }

    public static MyBookingFragment newInstance(String param1, String param2) {
        MyBookingFragment fragment = new MyBookingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_booking, container, false);
        recyclerView = view.findViewById(R.id.recycler_booking);

        initFirestore();
        initRecyclerView();
        adapter.setOnClickItemBooking(this);

        return view;
    }

    private void initFirestore() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mQuery = bookingRef.orderBy("date", Query.Direction.DESCENDING).whereEqualTo("userId", userId);
        bookingList = new ArrayList<>();
        setQuery(mQuery);
    }

    private void initRecyclerView() {
        if (mQuery == null) {
            Log.w(TAG, "No query, not initializing RecyclerView");

        }
        System.out.println("-------");
        //shalehatsAdapter = new ShalehatsAdapter(shalehatsList,this);

        adapter = new BookingAdapter(bookingList);

        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void setQuery(Query mQuery) {
        mQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e(TAG, error.getMessage());
                    return;
                }
                for (DocumentChange dc : value.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.ADDED) {
                        Booking booking = dc.getDocument().toObject(Booking.class);
                        bookingList.add(booking);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                query_search = query;
//                foodAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                query_search = newText;
//                foodAdapter.getFilter().filter(newText);
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                //  foodAdapter.getFilter().filter("");
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onEditBook(int position, Booking booking) {
        this.booking = booking;
        this.position = position;
        id_booking = booking.getId();
        EditBookingDialogFragment editBookingDialogFragment = new EditBookingDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("booking", booking);
        System.out.println(booking);
        editBookingDialogFragment.setArguments(bundle);
        editBookingDialogFragment.show(getActivity().getSupportFragmentManager(), null);
    }

    public void update(String date, String num_days) {
        firebaseFirestore.collection("Shalehats").document(booking.getChaletId()).
                addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        Shalehats shalehats = value.toObject(Shalehats.class);
                        double price = shalehats.getPrice();
                        booking.setDate(date);
                        int days = Integer.parseInt(num_days);
                        booking.setNumberOfDays(days);
                        booking.setTotalPrice(days*price);
                        bookingRef.document(id_booking).set(booking).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                adapter.setBooking(position, booking);
                            }
                        });
                    }
                });

    }
    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("My Booking");
    }
}