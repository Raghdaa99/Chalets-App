package com.example.shalehatbooking;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.shalehatbooking.model.User;
import com.example.shalehatbooking.view.auth.RegisterFragment;
import com.example.shalehatbooking.view.chat.ChatFragment;
import com.example.shalehatbooking.view.fragments.AboutUsFragment;
import com.example.shalehatbooking.view.fragments.AccountFragment;
import com.example.shalehatbooking.view.fragments.BookingFragment;
import com.example.shalehatbooking.view.fragments.DetailsFragment;
import com.example.shalehatbooking.view.fragments.EditBookingDialogFragment;
import com.example.shalehatbooking.view.fragments.FavouriteFragment;
import com.example.shalehatbooking.view.fragments.FilterDialogFragment;
import com.example.shalehatbooking.view.fragments.Filters;
import com.example.shalehatbooking.view.fragments.HomeFragment;
import com.example.shalehatbooking.view.fragments.MyBookingFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import javax.security.auth.Subject;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements FragmentManager.OnBackStackChangedListener, HomeFragment.OnItemClickSendToDetails,
        DetailsFragment.OnClickBookNext, NavigationView.OnNavigationItemSelectedListener
        , EditBookingDialogFragment.OnEditListenerDialog , FilterDialogFragment.FilterListener,HomeFragment.ShowDialogListener{
    private DrawerLayout drawer;
    private FirebaseAuth mAuth;
    NavigationView navigationView;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    // private final static String TAG_FRAGMENT = "TAG_FRAGMENT";
    ActionBarDrawerToggle toggle;
    private static final String TAG = "MainActivity";
    private boolean mToolBarNavigationListenerIsRegistered = false;

    private TextView username_header, email_header;
    private CircleImageView img_header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        navigationView = findViewById(R.id.navigation);
        View headerView = navigationView.inflateHeaderView(R.layout.nav_header);
        username_header = headerView.findViewById(R.id.username_header);
        email_header = headerView.findViewById(R.id.email_header);
        img_header = headerView.findViewById(R.id.img_header);
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
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containar, new HomeFragment(),"home_page").commit();
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
                Log.d(TAG, "img_header: "+user.getImage());
                Glide.with(getBaseContext())
                        .load(user.getImage())
                        .centerCrop()
                        .into(img_header);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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

    @Override
    public void sendToDetails(String position) {
        DetailsFragment detailsFragment = DetailsFragment.newInstance(position);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containar, detailsFragment).addToBackStack(null).commit();
    }



    private void logout() {
        AlertDialog.Builder alBuilder = new AlertDialog.Builder(this);
        alBuilder.setTitle("logout")
                .setMessage("Are you sure want to logout")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signOut();
                        sendToLoginPage();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        }).create().show();

    }

    private void sendToLoginPage() {
        Intent loginIntent = new Intent(MainActivity.this, AuthActivity.class);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public void onClick(String idChalet) {
        BookingFragment bookingFragment = BookingFragment.newInstance(idChalet);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_containar, bookingFragment).addToBackStack(null).commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.booking:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_containar, new MyBookingFragment(), "booking").addToBackStack(null).commit();
                break;
            case R.id.account:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_containar, new AccountFragment() ).addToBackStack(null).commit();
                break;
            case R.id.favorite:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_containar, new FavouriteFragment()).addToBackStack(null).commit();
                break;
            case R.id.chat:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_containar, new ChatFragment()).addToBackStack(null).commit();
                break;
            case R.id.logout:
                logout();
                break;
            case  R.id.nav_share:
                shareApp();
                break;
            case  R.id.about:
                About_us();
                break;
        }
        return false;
    }

    private void About_us() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_containar, new AboutUsFragment()).addToBackStack(null).commit();
    }

    private void shareApp() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Chalets App");
            String shareMessage= "\nLet me recommend you this application\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch(Exception e) {
            //e.toString();
        }
    }

    @Override
    public void onEdit(String date, String num_days) {
        MyBookingFragment bookingFragment = (MyBookingFragment) getSupportFragmentManager().findFragmentByTag("booking");
        if (bookingFragment != null) {
            bookingFragment.update(date, num_days);
        }

    }

    @Override
    public void onFilter(Filters filters) {
     HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag("home_page");
        Query query = firestore.collection("Shalehats");

        // City (equality filter)
        if (filters.hasCity()) {
            Log.e(TAG, "onFilter: "+ filters.getCity());
            query = query.whereEqualTo("location", filters.getCity());
        }

        // Price (equality filter)
//        if (filters.hasPrice()) {
//            query = query.whereEqualTo("price", filters.getPrice());
//        }

        // Sort by (orderBy with direction)
        if (filters.hasSortBy()) {
            query = query.orderBy(filters.getSortBy(), filters.getSortDirection());
        }
        homeFragment.setQueryFromDialog(query,filters);
    }

    @Override
    public void showDialog() {
        FilterDialogFragment mFilterDialog = new FilterDialogFragment();
        mFilterDialog.show(getSupportFragmentManager(), FilterDialogFragment.TAG);
    }

    @Override
    public void clearFilter() {
        FilterDialogFragment mFilterDialog = new FilterDialogFragment();
        mFilterDialog.resetFilters();
       onFilter(Filters.getDefault());
    }
}