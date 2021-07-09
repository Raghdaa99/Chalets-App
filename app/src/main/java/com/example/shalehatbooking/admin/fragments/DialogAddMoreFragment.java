package com.example.shalehatbooking.admin.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.shalehatbooking.R;
import com.example.shalehatbooking.model.Rating;
import com.example.shalehatbooking.model.Slide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class DialogAddMoreFragment extends DialogFragment implements View.OnClickListener {
    private static final String TAG = "DialogAddMoreFragment";

    private static final String ARG_PARAM1 = "id_chalet";
    private static final String SLIDER = "slide";

    private static final int PICK_IMG_REQ_CODE = 22;

    // TODO: Rename and change types of parameters
    private String id_chalet;
    private Slide slide;

    private ImageView dialog_img;
    private EditText dialog_ed_desc;
    private Uri img_uri;

    private StorageReference reference = FirebaseStorage.getInstance().getReference();
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private OnSaveListener listener;

    public DialogAddMoreFragment() {
        // Required empty public constructor
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnSaveListener) {
            listener = (OnSaveListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement");
        }
    }

    public static DialogAddMoreFragment newInstance(String id_chalet) {
        DialogAddMoreFragment fragment = new DialogAddMoreFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, id_chalet);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id_chalet = getArguments().getString(ARG_PARAM1);
            slide = (Slide) getArguments().getSerializable(SLIDER);
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder alBuilder = new AlertDialog.Builder(getContext());
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_dialog_add_more, null);
        dialog_img = view.findViewById(R.id.dialog_img);
        dialog_ed_desc = view.findViewById(R.id.dialog_ed_desc);
        dialog_img.setOnClickListener(this);
        alBuilder.setView(view)
                .setTitle(getString(R.string.add_pictures))
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (slide != null) {
                            update(slide);
                        } else {
                            add();
                        }
                    }
                }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
        if (slide != null) {
            setValuesComponent(slide);
            //update(id_chalet, id_slider);
        }
        return alBuilder.create();

    }

    private void add() {
        Slide slide = new Slide();
        String title = dialog_ed_desc.getText().toString();
            if (img_uri != null && title != "") {

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
                            slide.setImageUrl(downloadUri.toString());
                            slide.setTitle(title);
                            listener.AddSlider(slide);
                        }
                    }
                });

            }else{
                Log.e(TAG, "add: empty field");
                Toast.makeText(getContext(), "empty field", Toast.LENGTH_SHORT).show();
            }
        }

    private void setValuesComponent(Slide slide) {
        dialog_ed_desc.setText(slide.getTitle());
        Glide.with(getContext())
                .load(slide.getImageUrl())
                .centerCrop()
                .into(dialog_img);
    }

    private void update(Slide slide) {
        String title = dialog_ed_desc.getText().toString();
        if (!TextUtils.isEmpty(title)) {
            slide.setTitle(title);
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
                            // mapSlide.put("imageUrl", downloadUri.toString());
                            //updatePicToFirebase(mapSlide);
                            slide.setImageUrl(downloadUri.toString());
                            sendListener(slide);
                        }
                    }
                });

            } else {
                //mapSlide.put("imageUrl", slide.getImageUrl());
                //  updatePicToFirebase(mapSlide);
                slide.setImageUrl(slide.getImageUrl());
                sendListener(slide);
            }
        }else{

        }
    }

    private void sendListener(Slide slide) {
        listener.EditSlider(slide);
    }


    private void uploadToFirebase() {
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
                Uri downloadUri = task.getResult();
                String title = dialog_ed_desc.getText().toString();
                Slide slide = new Slide(downloadUri.toString(), title);
                DocumentReference documentReference = firestore.collection("Shalehats/" + id_chalet + "/pictures")
                        .document();
                documentReference.set(slide).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Log.d(TAG, "onSuccess: " + id_chalet);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.toString());
                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_img:
                cropImage();
                break;
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
//                imageUri = data.getData();
//                new_food_img.setImageURI(imageUri);
                img_uri = data.getData();
                dialog_img.setImageURI(img_uri);
            }
        }
    }

    public interface OnSaveListener {
        void AddSlider(Slide slide);

        void EditSlider(Slide slide);
    }
}