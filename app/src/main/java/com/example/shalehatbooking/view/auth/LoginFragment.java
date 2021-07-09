package com.example.shalehatbooking.view.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shalehatbooking.MainActivity;
import com.example.shalehatbooking.R;
import com.example.shalehatbooking.admin.activites.AdminMainActivity;
import com.example.shalehatbooking.admin.fragments.AdminHomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class LoginFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "LoginFragment";
    private TextInputEditText login_editText_email, login_editText_password;
    private Button login_btn_sign_in;
    private TextView login_forget_password, login_txt_sign_up;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private ProgressBar progressBar;
    boolean isAdmin = false ;
    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        firestore = FirebaseFirestore.getInstance();
        initializeComponents(view);
        initializeListeners();
        return view;
    }

    private void initializeListeners() {
        login_btn_sign_in.setOnClickListener(this);
        login_forget_password.setOnClickListener(this);
        login_txt_sign_up.setOnClickListener(this);
    }

    private void initializeComponents(View view) {
        firestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        login_editText_email = view.findViewById(R.id.login_editText_email);
        login_editText_password = view.findViewById(R.id.login_editText_password);
        login_btn_sign_in = view.findViewById(R.id.login_btn_sign_in);
        login_forget_password = view.findViewById(R.id.login_forget_password);
        login_txt_sign_up = view.findViewById(R.id.login_txt_sign_up);
        progressBar = view.findViewById(R.id.progressBar);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn_sign_in:
               login();
                break;
            case R.id.login_forget_password:
                showDialogResetPassword();
                break;
            case R.id.login_txt_sign_up:
                getActivity().getSupportFragmentManager().beginTransaction().
                        replace(R.id.fragment_container, new RegisterFragment()).addToBackStack(null).commit();
                break;
        }
    }

    private void login() {
        String loginEmail = login_editText_email.getText().toString();
        String loginPassword = login_editText_password.getText().toString();
        if (!TextUtils.isEmpty(loginEmail) && !TextUtils.isEmpty(loginPassword)) {
            mAuth.signInWithEmailAndPassword(loginEmail, loginPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                             //   if (mAuth.getCurrentUser().isEmailVerified()){
                                progressBar.setVisibility(View.VISIBLE);
                                    checkIsAdmin(mAuth.getCurrentUser().getUid());
//                                }else{
//                                    Toast.makeText(getActivity(), "Email not Verified", Toast.LENGTH_SHORT).show();
//                                }


                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(getContext(), task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

        }
        else {
            Toast.makeText(getActivity(), "Empty Fields", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkIsAdmin(String uid) {

        DocumentReference dc = firestore.collection("users").document(uid);
        dc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.getString("isAdmin") != null) {
                    sendToMainAdmin();
                }else{
                    sendToMain();
                }
            }
        });
    }

    private void sendToMainAdmin() {
        Intent intent = new Intent(getContext(), AdminMainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void showDialogResetPassword() {
        UpdatePasswordDialog updatePasswordDialog = new UpdatePasswordDialog();
        updatePasswordDialog.show(getActivity().getSupportFragmentManager(), null);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            checkIsAdmin(currentUser.getUid());

        }
    }

    private void sendToMain() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}