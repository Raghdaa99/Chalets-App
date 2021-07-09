package com.example.shalehatbooking.view.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.shalehatbooking.MainActivity;
import com.example.shalehatbooking.R;
import com.example.shalehatbooking.dbHelper.DatabaseHandler;
import com.example.shalehatbooking.model.Booking;
import com.example.shalehatbooking.model.Favourite;
import com.example.shalehatbooking.model.Rating;
import com.example.shalehatbooking.model.Shalehats;
import com.example.shalehatbooking.model.Slide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailsFragment extends Fragment implements RatingDialogListener, View.OnClickListener {
    private static final String TAG = "DetailsFragment";
    private static final String ARG_POSITION = "position";
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = firebaseFirestore.collection("Shalehats");
    // private String position;
    private List<Shalehats> shalehatsList;
    private TextView details_name_txt, details_location_txt, details_desc_txt, price_value, reviews_value, details_address_txt;
    private RatingBar details_rating;
    private FloatingActionButton floating_rating;
    private ImageView facebook, whatsapp, instagram, telephone;
    private String id_shalet;
    ImageSlider imageSlider;
    List<Slide> slideList;
    List<SlideModel> slideModels;
    Shalehats shalehats;
    String userId;
    private Button book_now_btn;
    private OnClickBookNext listener;
    String imgUrl, name, facebook_uri, insta_uri, phone;
    double price;

    public DetailsFragment() {
        // Required empty public constructor
    }

    public static DetailsFragment newInstance(String position) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnClickBookNext) {
            listener = (OnClickBookNext) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id_shalet = getArguments().getString(ARG_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        book_now_btn = view.findViewById(R.id.book_now_btn);
        ImageButton favorite_btn = view.findViewById(R.id.favorite_btn);
        initializeComponents(view);
        book_now_btn.setOnClickListener(this);
        favorite_btn.setOnClickListener(this);
        facebook.setOnClickListener(this);
        whatsapp.setOnClickListener(this);
        instagram.setOnClickListener(this);
        telephone.setOnClickListener(this);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        shalehatsList = new ArrayList<>();

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                System.out.println("//*****///");
                if (error != null) {
                    Log.e(TAG, error.getMessage());
                    return;
                }
                for (DocumentChange dc : value.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.ADDED) {
                        shalehatsList.add(dc.getDocument().toObject(Shalehats.class));
                    }

                }
                initializeValues();

                //initializeImageSlider();
            }
        });

        collectionReference.document(id_shalet).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                facebook_uri = (String) value.get("facebook");
                insta_uri = (String) value.get("instagram");
                phone = (String) value.get("phone");
            }
        });
        floating_rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingDialog();
            }
        });
        checkIfBooked();

        return view;
    }

    private void showRatingDialog() {

        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNeutralButtonText("Later")
                .setNoteDescriptions(Arrays.asList("Very Bad", "Not good", "Quite ok", "Very Good", "Excellent !!!"))
                .setDefaultRating(1)
                .setTitle("Rate this application")
                .setDescription("Please select some stars and give your feedback")
                .setCommentInputEnabled(true)
                .setStarColor(R.color.starColor)
//                    .setNoteDescriptionTextColor(R.color.noteDescriptionTextColor)
//                    .setTitleTextColor(R.color.titleTextColor)
//                    .setDescriptionTextColor(R.color.contentTextColor)
                .setHint("Please write your comment here ...")
//                    .setHintTextColor(R.color.hintTextColor)
//                    .setCommentTextColor(R.color.commentTextColor)
                .setCommentBackgroundColor(R.color.colorDarkGrey)
                .setWindowAnimation(R.style.MyDialogFadeAnimation)
                .setCancelable(false)
                .setCanceledOnTouchOutside(false)
                .create(getActivity())
                .setTargetFragment(this, 1) // only if listener is implemented by fragment
                .show();
    }

    private void initializeValues() {
        System.out.println(id_shalet);
        // shalehats = shalehatsList.get(position);

        DocumentReference dc = firebaseFirestore.collection("Shalehats").document(id_shalet);
        dc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                shalehats = documentSnapshot.toObject(Shalehats.class);
                imgUrl = shalehats.getImage();
                price = shalehats.getPrice();
                name = shalehats.getName();
                //id_shalet = shalehats.getId();
                getRatingChalets();
                details_name_txt.setText(shalehats.getName());
                details_location_txt.setText(shalehats.getLocation());
                details_desc_txt.setText(shalehats.getDescription());
                details_address_txt.setText(shalehats.getAddress());
                price_value.setText(shalehats.getPrice() + "");
                // details_rating.setRating((float) shalehats.getRating());
                System.out.println(id_shalet);
                slideList = new ArrayList<>();
                firebaseFirestore.collection("Shalehats").document(id_shalet).collection("pictures").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: ");
                            slideList = task.getResult().toObjects(Slide.class);
                            initializeImageSlider();
                            //
                        } else {
                            System.out.println(TAG + " " + task.getException());
                        }
                    }
                });
            }
        });


    }

    private void initializeImageSlider() {
        slideModels = new ArrayList<>();
        for (int i = 0; i < slideList.size(); i++) {
            Slide slide = slideList.get(i);
            slideModels.add(new SlideModel(slide.getImageUrl(), slide.getTitle()));

        }
        imageSlider.setImageList(slideModels, true);

    }

    private void initializeComponents(View view) {
        imageSlider = view.findViewById(R.id.slider);
        details_name_txt = view.findViewById(R.id.details_name_txt);
        details_location_txt = view.findViewById(R.id.details_location_txt);
        details_desc_txt = view.findViewById(R.id.details_desc_txt);
        price_value = view.findViewById(R.id.price_value);
        reviews_value = view.findViewById(R.id.reviews_value);
        details_rating = view.findViewById(R.id.details_rating);
        floating_rating = view.findViewById(R.id.floating_rating);
        details_address_txt = view.findViewById(R.id.details_address_txt);
        facebook = view.findViewById(R.id.facebook);
        whatsapp = view.findViewById(R.id.whatsapp);
        instagram = view.findViewById(R.id.instagram);
        telephone = view.findViewById(R.id.telephone);
    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onNeutralButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(int i, String s) {

        Rating rating = new Rating(String.valueOf(i), s);
        DocumentReference documentReference = firebaseFirestore.collection("Shalehats/" + id_shalet + "/Rating").document(userId);
        documentReference.set(rating).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                getRatingChalets();
                Log.d(TAG, "onSuccess: " + userId);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " + e.toString());
            }
        });

    }

    private void getRatingChalets() {
        firebaseFirestore.collection("Shalehats/" + id_shalet + "/Rating").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                int count = 0, sum = 0;
                float average = 0;
                if (error != null) {
                    Log.e(TAG, error.getMessage());
                    return;
                }
                for (DocumentChange dc : value.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.ADDED) {
                        Rating rating = dc.getDocument().toObject(Rating.class);
                        sum += Integer.parseInt(rating.getRateValue());
                        count++;
                    }

                }
                if (count != 0) {
                    average = sum / count;
                    details_rating.setRating(average);
                    reviews_value.setText(count + "");
                } else {
                    reviews_value.setText(count + "");
                }
                DocumentReference dc = firebaseFirestore.collection("Shalehats").document(id_shalet);
                shalehats.setRating(average);
//                Map<String,Object> map = new HashMap<>();
//                map.put();
                dc.update("rating", shalehats.getRating()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG, "onComplete: success");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: " + e.getMessage());
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.book_now_btn:
                listener.onClick(id_shalet);
                break;
            case R.id.favorite_btn:
                saveToFavourite();
                break;
            case R.id.facebook:
                moveToFacebook();
                break;
            case R.id.whatsapp:
                moveToWhatsapp();
                break;
            case R.id.instagram:
                saveToInstagram();
                break;
            case R.id.telephone:
                saveToTelephone();
                break;
        }
    }

    private void moveToFacebook() {
        if (facebook_uri != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(facebook_uri));
            startActivity(intent);
        }
    }

    private void moveToWhatsapp() {
        if (phone != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://api.whatsapp.com/send?phone=" + phone));
            startActivity(intent);
        }
    }

    private void saveToInstagram() {
        if (insta_uri != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(insta_uri));
            startActivity(intent);
        }
    }

    private void saveToTelephone() {
        if (phone != null) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phone));
            startActivity(intent);
        }
    }

    private void checkIfBooked() {
        Query query = firebaseFirestore.collection("Booking").whereEqualTo("chaletId", id_shalet);

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange dc : value.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.ADDED) {
                        Booking booking = dc.getDocument().toObject(Booking.class);
                        if (booking.getUserId().equals(userId)) {
                            book_now_btn.setEnabled(false);
                            break;
                        }
                    }
                }
            }
        });


    }

    private void saveToFavourite() {
        DatabaseHandler databaseHandler = new DatabaseHandler(getActivity());
        Favourite favourite = new Favourite(id_shalet, userId, imgUrl, name, price);
        if (databaseHandler.insertFavourite(favourite)) {
            Toast.makeText(getActivity(), "Added to Favourite..", Toast.LENGTH_SHORT).show();
        }
    }

    public interface OnClickBookNext {
        void onClick(String idChalet);
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Chalets App");
    }
}
