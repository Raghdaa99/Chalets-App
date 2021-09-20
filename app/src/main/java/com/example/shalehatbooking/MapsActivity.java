package com.example.shalehatbooking;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String Latitude, Longitude, title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        Latitude = intent.getStringExtra("Latitude");
        Longitude = intent.getStringExtra("Longitude");
//
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        double LatitudeD = Double.parseDouble(Latitude);
        double LongitudeD = Double.parseDouble(Longitude);
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(LatitudeD, LongitudeD);
        mMap.addMarker(new MarkerOptions().position(sydney).title(title));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(20.29f));
    }
}