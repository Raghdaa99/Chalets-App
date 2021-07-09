package com.example.shalehatbooking.view.auth;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.shalehatbooking.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.auth.User;


public class UpdatePasswordDialog extends DialogFragment {
    private EditText password_email_editText;

    private FirebaseAuth mAuth;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        AlertDialog.Builder alBuilder = new AlertDialog.Builder(getContext());
        View view = getActivity().getLayoutInflater().inflate(R.layout.password_reset_dialog, null);
        password_email_editText = view.findViewById(R.id.password_email_editText);
        alBuilder.setView(view)
                .setTitle(getString(R.string.reset_password))
                .setMessage(getString(R.string.reset_password_message))
                .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (inputResetValidation()) {
                            String email = password_email_editText.getText().toString().trim();
                            mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getContext(), "Reset Email sent", Toast.LENGTH_SHORT).show();
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });

        return alBuilder.create();
    }


    private boolean inputResetValidation() {
        boolean valid;
        String Email = password_email_editText.getText().toString().trim();

        if (Email.isEmpty()) {
            valid = false;
            password_email_editText.setError("Please enter your Email Address");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            valid = false;
            password_email_editText.setError("Please enter a valid Email Address");
        } else {
            valid = true;
        }
        return valid;
    }
}
