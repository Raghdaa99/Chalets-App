package com.example.shalehatbooking.view.auth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shalehatbooking.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class RegisterFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "RegisterFragment";
    private static final int PICK_IMG_REQ_CODE = 22;
    private TextInputEditText register_editText_username, register_editText_email,
            register_editText_phone, register_editText_password, register_editText_password_confirm;
    private Button register_btn_sign_in;
    private ImageView register_camera;
    private CircleImageView img_cover;
    private TextView register_txt_sign_in;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private String userId;
    private Uri img_uri;
    private StorageReference reference = FirebaseStorage.getInstance().getReference();
    private FirebaseAuth fAuth = FirebaseAuth.getInstance();

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        initializeComponents(view);
        initializeListeners();
        return view;
    }

    private void initializeListeners() {
        register_btn_sign_in.setOnClickListener(this);
        register_txt_sign_in.setOnClickListener(this);
        register_camera.setOnClickListener(this);
    }

    private void initializeComponents(View view) {
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        register_editText_username = view.findViewById(R.id.register_editText_username);
        register_editText_email = view.findViewById(R.id.register_editText_email);
        register_editText_phone = view.findViewById(R.id.register_editText_phone);
        register_editText_password = view.findViewById(R.id.register_editText_password);
        register_editText_password_confirm = view.findViewById(R.id.register_editText_password_confirm);
        register_btn_sign_in = view.findViewById(R.id.register_btn_sign_in);
        register_txt_sign_in = view.findViewById(R.id.register_txt_sign_in);
        register_camera = view.findViewById(R.id.register_camera);
        img_cover = view.findViewById(R.id.img_cover);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_btn_sign_in:
                registerUser();
                break;
            case R.id.register_camera:
                cropImage();
                break;
        }
    }

    private void cropImage() {
        Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(cameraIntent, PICK_IMG_REQ_CODE);
    }


    private void registerUser() {
        String loginEmail = register_editText_email.getText().toString();
        String loginUsername = register_editText_username.getText().toString();
        String loginPhone = register_editText_phone.getText().toString();
        String loginPassword = register_editText_password.getText().toString();
        String confirmPassword = register_editText_password_confirm.getText().toString();
        if (!TextUtils.isEmpty(loginEmail) && !TextUtils.isEmpty(loginPassword) && !TextUtils.isEmpty(confirmPassword)) {
            if (loginPassword.equals(confirmPassword)) {
                mAuth.createUserWithEmailAndPassword(loginEmail, loginPassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    final String randomName = UUID.randomUUID().toString();
                                    final StorageReference filePath = reference.child("users_images").child(randomName + ".jpg");
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
                                                userId = mAuth.getCurrentUser().getUid();
                                                DocumentReference documentReference = firestore.collection("users").document(userId);
                                                Map<String, Object> users = new HashMap<>();
                                                users.put("username", loginUsername);
                                                users.put("email", loginEmail);
                                                users.put("phone", loginPhone);
                                                users.put("image", downloadUri.toString());
                                                documentReference.set(users).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "onSuccess: " + userId);
                                                        Toast.makeText(getActivity(), "Register Success", Toast.LENGTH_SHORT).show();
                                                        FirebaseUser fuser = fAuth.getCurrentUser();
                                                        fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Toast.makeText(getActivity(), "Verification Email Has been Sent.", Toast.LENGTH_SHORT).show();
                                                                moveTOLoginFragment();
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.d(TAG, "onFailure: Email not sent " + e.getMessage());
                                                            }
                                                        });

                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.d(TAG, "onFailure: " + e.toString());
                                                    }
                                                });
                                            } else {
                                                // If sign in fails, display a message to the user.
                                                Toast.makeText(getContext(), task.getException().getMessage(),
                                                        Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });

                                } else {
                                    register_editText_password_confirm.setError("confirm Password and password not matched");
                                }
                            }
                        });

            }

        }
    }

    private void moveTOLoginFragment() {
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LoginFragment())
                .addToBackStack(null).commit();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMG_REQ_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                img_uri = data.getData();
                img_cover.setImageURI(img_uri);
            }
        }
    }
}