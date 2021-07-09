package com.example.shalehatbooking;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.shalehatbooking.view.auth.LoginFragment;
import com.example.shalehatbooking.view.auth.RegisterFragment;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new LoginFragment()).addToBackStack(null).commit();
    }
}