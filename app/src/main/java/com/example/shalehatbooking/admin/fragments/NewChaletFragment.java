package com.example.shalehatbooking.admin.fragments;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.shalehatbooking.MainActivity;
import com.example.shalehatbooking.R;
import com.example.shalehatbooking.admin.activites.AdminMainActivity;
import com.example.shalehatbooking.model.Model;
import com.example.shalehatbooking.model.Shalehats;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;


public class NewChaletFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final int PICK_IMG_REQ_CODE = 1;
    private static final String TAG = "NewChaletFragment";
    // TODO: Rename and change types of parameters
    private String idChalet;

    private ImageView new_img_chalet;
    private EditText ed_new_name, ed_new_description, ed_new_price, ed_new_address;
    private Spinner ed_new_location;
    private Button next_btn, update_btn;
    private Uri img_uri;
    private ImageButton img_btn_more_details;
    private StorageReference reference = FirebaseStorage.getInstance().getReference();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private OnSelectedAddPictures listener;
    Shalehats shalehats;
    private ProgressBar progressBar;

    public NewChaletFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnSelectedAddPictures) {
            listener = (OnSelectedAddPictures) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement");
        }
    }

    public static NewChaletFragment newInstance(String idChalet) {
        NewChaletFragment fragment = new NewChaletFragment();
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
        View view = inflater.inflate(R.layout.fragment_new_chalet, container, false);
        new_img_chalet = view.findViewById(R.id.new_img_chalet);
        ed_new_name = view.findViewById(R.id.ed_new_name);
        ed_new_description = view.findViewById(R.id.ed_new_description);
        ed_new_price = view.findViewById(R.id.ed_new_price);
        ed_new_location = view.findViewById(R.id.ed_new_location);
        ed_new_address = view.findViewById(R.id.ed_new_address);
        next_btn = view.findViewById(R.id.next_btn);
        update_btn = view.findViewById(R.id.update_btn);
        img_btn_more_details = view.findViewById(R.id.img_btn_more_details);
        progressBar = view.findViewById(R.id.progress_new_chalet);
        new_img_chalet.setOnClickListener(this);
        img_btn_more_details.setOnClickListener(this);
        next_btn.setOnClickListener(this);
        update_btn.setOnClickListener(this);
        if (idChalet != null) {
            update_btn.setVisibility(View.VISIBLE);
            img_btn_more_details.setVisibility(View.VISIBLE);
            next_btn.setVisibility(View.GONE);
            UpdatedChalet(idChalet);
        } else {

        }

        return view;
    }

    private boolean inputValidation() {
        boolean valid;
        String name = ed_new_name.getText().toString().trim();
        String description = ed_new_description.getText().toString().trim();
        String price = ed_new_price.getText().toString().trim();
       // String location = ed_new_location.getText().toString().trim();

        if (name.isEmpty()) {
            valid = false;
            ed_new_name.setError("Please enter Name");
        } else if (description.isEmpty()) {
            valid = false;
            ed_new_description.setError("Please enter Description");
        } else if (price.isEmpty()) {
            valid = false;
            ed_new_price.setError("Please enter Price");
        }
//        else if (location.isEmpty()) {
//            valid = false;
//            ed_new_location.setError("Please enter location");
//        }
        else if (img_uri == null) {
            valid = false;
            Toast.makeText(getActivity(), "Empty picture", Toast.LENGTH_SHORT).show();
        } else {
            valid = true;
        }
        return valid;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_img_chalet:
                cropImage();
                break;
            case R.id.next_btn:
                uploadToFirebase(img_uri);
                break;
            case R.id.update_btn:
                update();
                break;
            case R.id.img_btn_more_details:
                moveToDetails();
                break;
        }
    }

    private void moveToDetails() {
        AddDetailsFragment detailsFragment = AddDetailsFragment.newInstance(idChalet);
        getActivity().getSupportFragmentManager()
                .beginTransaction().replace(R.id.fragment_containar, detailsFragment, "details").addToBackStack(null).commit();
    }


    private void uploadToFirebase(Uri img_uri) {
        String name = ed_new_name.getText().toString();
        String description = ed_new_description.getText().toString();
        String price = ed_new_price.getText().toString();
        String location = (String) ed_new_location.getSelectedItem();
        String address = ed_new_address.getText().toString();
        if (inputValidation()) {
            final String randomName = UUID.randomUUID().toString();
            final StorageReference filePath = reference.child("chalet_images").child(randomName + ".jpg");
            UploadTask uploadTask = filePath.putFile(img_uri);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        String error = task.getException().getMessage();
                        System.out.println(img_uri + " ///////");
                        Toast.makeText(getActivity(), "Image Error ..." + error, Toast.LENGTH_SHORT).show();
                    }

                    // Continue with the task to get the download URL
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();

                        progressBar.setVisibility(View.VISIBLE);
                        Shalehats shalehats = new Shalehats(name, location, description, downloadUri.toString(), Double.parseDouble(price), 0, address);
                        DocumentReference documentReference = firestore.collection("Shalehats").document();
                        documentReference.set(shalehats).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                String id_chalet = documentReference.getId();
                                //sendTodetails(id_chalet);
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getActivity(), "Chalet is added", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), AdminMainActivity.class);
                                getActivity().startActivity(intent);
                                // listener.sendToDetails(id_chalet);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

                    }
                }
            });
        }
    }

    private void cropImage() {
        Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(cameraIntent, PICK_IMG_REQ_CODE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMG_REQ_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                img_uri = data.getData();
                new_img_chalet.setImageURI(img_uri);
            }
        }
    }

    private void UpdatedChalet(String idChalet) {
        if (idChalet != null) {
            firestore.collection("Shalehats").document(idChalet).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    shalehats = documentSnapshot.toObject(Shalehats.class);
                    setValuesComponents(shalehats);
                }
            });
        }
    }

    private void setValuesComponents(Shalehats shalehats) {
        Glide.with(getActivity())
                .load(shalehats.getImage())
                .centerCrop()
                .into(new_img_chalet);
        ed_new_name.setText(shalehats.getName());
        ed_new_description.setText(shalehats.getDescription());
        ed_new_location.getSelectedItemPosition();
        //ed_new_location.setSelection(shalehats.getLocation());
        setSelectionSpinner(shalehats.getLocation());
        ed_new_price.setText(shalehats.getPrice() + "");
        ed_new_address.setText(shalehats.getAddress());

    }

    private void setSelectionSpinner(String location) {
        String compareValue = location;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.location, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ed_new_location.setAdapter(adapter);
        if (compareValue != null) {
            int spinnerPosition = adapter.getPosition(compareValue);
            ed_new_location.setSelection(spinnerPosition);

        }
    }

    private void update() {
        String name = ed_new_name.getText().toString();
        String description = ed_new_description.getText().toString();
        String price = ed_new_price.getText().toString();
        String location = (String) ed_new_location.getSelectedItem();
        String address = ed_new_address.getText().toString();

        //shalehats = new Shalehats(img_uri.toString(), name, location, description, Double.parseDouble(price), shalehats.getRating());
        Map<String, Object> mapChalet = new HashMap<>();
        mapChalet.put("name", name);
        mapChalet.put("address", address);
        mapChalet.put("description", description);
        mapChalet.put("price", Double.parseDouble(price));
        mapChalet.put("location", location);
        mapChalet.put("rating", shalehats.getRating());
        if (img_uri != null) {
            final String randomName = UUID.randomUUID().toString();
            final StorageReference filePath = reference.child("chalet_images").child(randomName + ".jpg");
            UploadTask uploadTask = filePath.putFile(img_uri);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        String error = task.getException().getMessage();
                        System.out.println(img_uri + " ///////");
                        Toast.makeText(getActivity(), "Image Error ..." + error, Toast.LENGTH_SHORT).show();
                    }
                    System.out.println(filePath.getDownloadUrl().toString() + "  *********filePath");
                    // Continue with the task to get the download URL
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        System.out.println(downloadUri.toString() + "  *********downloadUri");
                        mapChalet.put("image", downloadUri.toString());
                        updatePicToFirebase(mapChalet);
                        System.out.println("***********************************");
                    }
                }
            });

        } else {
            mapChalet.put("image", shalehats.getImage());
            System.out.println("++++++++");
            updatePicToFirebase(mapChalet);
        }


    }

    private void updatePicToFirebase(Map<String, Object> mapChalet) {
        firestore.collection("Shalehats").document(idChalet).update(mapChalet)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "onSuccess", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "onFailure", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public interface OnSelectedAddPictures {
        void sendToDetails(String idChalet);
    }

}