package com.example.shalehatbooking.admin.activites;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.shalehatbooking.AuthActivity;
import com.example.shalehatbooking.MainActivity;
import com.example.shalehatbooking.R;
import com.example.shalehatbooking.admin.fragments.AddDetailsFragment;
import com.example.shalehatbooking.admin.fragments.AdminHomeFragment;
import com.example.shalehatbooking.admin.fragments.AllBookingFragment;
import com.example.shalehatbooking.admin.fragments.DialogAddMoreFragment;
import com.example.shalehatbooking.admin.fragments.NewChaletFragment;
import com.example.shalehatbooking.model.Shalehats;
import com.example.shalehatbooking.model.Slide;
import com.example.shalehatbooking.model.User;
import com.example.shalehatbooking.view.chat.ChatFragment;
import com.example.shalehatbooking.view.fragments.DetailsFragment;
import com.example.shalehatbooking.view.fragments.HomeFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class AdminMainActivity extends AppCompatActivity implements
        NewChaletFragment.OnSelectedAddPictures, AddDetailsFragment.OnAddSliderPicture,
        AdminHomeFragment.OnSelectedChaletUpdated, DialogAddMoreFragment.OnSaveListener,
        FragmentManager.OnBackStackChangedListener, NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ;
    private DrawerLayout drawer;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    private boolean mToolBarNavigationListenerIsRegistered = false;
    private TextView username_header, email_header;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);
        navigationView = findViewById(R.id.navigation);
        View headerView = navigationView.inflateHeaderView(R.layout.nav_header);
        username_header = headerView.findViewById(R.id.username_header);
        email_header = headerView.findViewById(R.id.email_header);


        final Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        drawer = findViewById(R.id.drawer_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //لاظهار الزر
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        displayHomeUpOrHumburger();
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containar, new AdminHomeFragment()).commit();
            navigationView.setCheckedItem(R.id.home);
        }
        setValuesAccount();
    }

    private void setValuesAccount() {
        String userId = mAuth.getCurrentUser().getUid();
        firestore.collection("users").document(userId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                assert value != null;
                User user = value.toObject(User.class);
                assert user != null;
                System.out.println(user.getUsername());
                username_header.setText(user.getUsername());
                email_header.setText(user.getEmail());
            }
        });
    }

    private void displayHomeUpOrHumburger() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            //cant swipe left to open drawer
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            //remove hamburger
            toggle.setDrawerIndicatorEnabled(false);
            //need listener for up btn
            if (!mToolBarNavigationListenerIsRegistered) {
                toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Doesn't have to be onBackPressed
                        //onBackPressed();
                        getSupportFragmentManager().popBackStackImmediate();
                    }
                });
                mToolBarNavigationListenerIsRegistered = true;
            }

        } else {
            //  Display hamburger button:
            //swipe enabled
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            // Show hamburger
            toggle.setDrawerIndicatorEnabled(true);
            // Remove the/any drawer toggle listener
            toggle.setToolbarNavigationClickListener(null);
            mToolBarNavigationListenerIsRegistered = false;
        }
    }

    @Override
    public void onBackStackChanged() {
        displayHomeUpOrHumburger();
    }


    private void logout() {
        mAuth.signOut();
        sendToLoginPage();
    }

    private void sendToLoginPage() {
        Intent loginIntent = new Intent(AdminMainActivity.this, AuthActivity.class);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public void sendToDetails(String idChalet) {
        AddDetailsFragment detailsFragment = AddDetailsFragment.newInstance(idChalet);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containar, detailsFragment, "details").addToBackStack(null).commit();

    }

    @Override
    public void addSlider(String id_chalet) {
        DialogAddMoreFragment addMoreFragment = DialogAddMoreFragment.newInstance(id_chalet);
        addMoreFragment.show(getSupportFragmentManager(), null);
    }

    @Override
    public void updateSlider(String id_chalet, Slide slide) {
        DialogAddMoreFragment addMoreFragment = new DialogAddMoreFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id_chalet", id_chalet);
        bundle.putSerializable("slide", slide);
        addMoreFragment.setArguments(bundle);
        addMoreFragment.show(getSupportFragmentManager(), null);
    }

    @Override
    public void updateChalet(String idChalet) {
        NewChaletFragment newChaletFragment = NewChaletFragment.newInstance(idChalet);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containar, newChaletFragment).addToBackStack(null).commit();

    }

    @Override
    public void AddSlider(Slide slide) {
        AddDetailsFragment detailsFragment = (AddDetailsFragment) getSupportFragmentManager().findFragmentByTag("details");
        if (detailsFragment != null) {
            detailsFragment.addSlide(slide);
        } else {
            System.out.println("detailsFragment is null");
        }
    }

    @Override
    public void EditSlider(Slide slide) {
        AddDetailsFragment detailsFragment = (AddDetailsFragment) getSupportFragmentManager().findFragmentByTag("details");
        if (detailsFragment != null) {
            detailsFragment.editSlide(slide);
        } else {
            System.out.println("detailsFragment is null");
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.order:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containar, new AllBookingFragment()).addToBackStack(null).commit();
                break;
            case R.id.logout:
                logout();
                break;
        }
        return false;
    }
}