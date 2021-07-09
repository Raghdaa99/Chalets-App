package com.example.shalehatbooking.view.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shalehatbooking.MainActivity;
import com.example.shalehatbooking.R;
import com.example.shalehatbooking.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;


public class AccountFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("users");
    private TextInputEditText account_editText_username, account_editText_email,
            account_editText_phone;
    private TextView change_password_txt;
    private Button account_btn_set_up;
    private ImageView icon_edit;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Account");
    }

    public static AccountFragment newInstance(String param1, String param2) {
        AccountFragment fragment = new AccountFragment();
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

        View view = inflater.inflate(R.layout.fragment_account, container, false);

        account_editText_username = view.findViewById(R.id.account_editText_username);
        account_editText_email = view.findViewById(R.id.account_editText_email);
        account_editText_phone = view.findViewById(R.id.account_editText_phone);
        change_password_txt = view.findViewById(R.id.change_password_txt);
        account_btn_set_up = view.findViewById(R.id.account_btn_set_up);
        icon_edit = view.findViewById(R.id.icon_edit);
        change_password_txt.setOnClickListener(this);
        icon_edit.setOnClickListener(this);
        account_btn_set_up.setOnClickListener(this);
        setComponentsFocuseFalse();
        setValuesComponents();

        return view;
    }

    private void setValuesComponents() {
        String userId = auth.getCurrentUser().getUid();
        collectionReference.document(userId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                assert value != null;
                User user = value.toObject(User.class);
                assert user != null;
                account_editText_username.setText(user.getUsername());
                account_editText_email.setText(user.getEmail());
                account_editText_phone.setText(user.getPhone());
            }
        });

    }

    private void setComponentsFocuseFalse() {
        account_editText_username.setEnabled(false);
        account_editText_email.setEnabled(false);
        account_editText_phone.setEnabled(false);
    }

    private void setComponentsFocuseTrue() {
        account_editText_username.setEnabled(true);
        account_editText_phone.setEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change_password_txt:
                showChangePasswordDialog();
                break;
            case R.id.account_btn_set_up:
                updatePersonalInfo();
                break;
            case R.id.icon_edit:
                setComponentsFocuseTrue();
                break;
        }
    }

    private void updatePersonalInfo() {
        if (inputValidation()) {
            String userId = auth.getCurrentUser().getUid();
            String UserName = account_editText_username.getText().toString().trim();
            String UserPhone = account_editText_phone.getText().toString().trim();
            Map<String, Object> mapUser = new HashMap<>();
            mapUser.put("phone",UserPhone);
            mapUser.put("username",UserName);
            collectionReference.document(userId).update(mapUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getActivity(), "updated...", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder alBuilder = new AlertDialog.Builder(getContext());
        View view = getActivity().getLayoutInflater().inflate(R.layout.change_password, null);
        TextInputEditText current_editText_password = view.findViewById(R.id.current_editText_password);
        TextInputEditText new_editText_password = view.findViewById(R.id.new_editText_password);
        alBuilder.setView(view)
                .setTitle("Update Password")
                .setPositiveButton("update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String current_password = current_editText_password.getText().toString();
                        String new_password = new_editText_password.getText().toString();
                        if (TextUtils.isEmpty(current_password)) {
                            Toast.makeText(getActivity(), "Enter Your current password", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (new_password.length() < 6) {
                            Toast.makeText(getActivity(), "Password length must at least 6 characters ", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        dialog.dismiss();
                        updatePassword(current_password, new_password);
                    }
                }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alBuilder.create().show();
    }

    private void updatePassword(String current_password, String new_password) {
        final FirebaseUser user = auth.getCurrentUser();

        AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail(), current_password);
        user.reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                user.updatePassword(new_password)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getActivity(), "Password updated ... ", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean inputValidation() {
        boolean valid;
        String UserName = account_editText_username.getText().toString().trim();
        String UserEmail = account_editText_email.getText().toString().trim();
        String UserPhone = account_editText_phone.getText().toString().trim();
        if (UserName.isEmpty()) {
            valid = false;
            account_editText_username.setError("Please enter your  Name");
        } else if (UserPhone.isEmpty()) {
            valid = false;
            account_editText_phone.setError("Please enter Mobile Number");
        } else if (UserEmail.isEmpty()) {
            valid = false;
            account_editText_email.setError("Please enter your Email Address");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(UserEmail).matches()) {
            valid = false;
            account_editText_email.setError("Please enter  a valid Email Address");
        } else {
            valid = true;
        }
        return valid;
    }
}