package com.idank_elishevaa.myruns;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class Map implements OnMapReadyCallback {
    private final Context context;

    public GoogleMap googleMap;
    private final float zoom = 17.0f;
    private double maxLan = -200;
    private double minLan = 200;
    private double maxLong = -200;
    private double minLong = 200;

    private final List<LatLng> coordinates;
    private final PolylineOptions line;

    // constructor
    public Map(Context con, SupportMapFragment mapFragment){
        mapFragment.getMapAsync(this);

        this.context = con;

        //initializing the
        coordinates = new ArrayList<>();

        // initializing the line parameters
        line = new PolylineOptions();
        line.color(Color.RED);
        line.width(7f);
    }

    // gets the location from the phone and handles it
    public void location() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {

            // Called when a new location is found by the location provider.
            public void onLocationChanged(Location location) {

                // getting the latitude and longitude
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                LatLng coordinate = new LatLng(latitude, longitude);

                updateCoordinates(latitude, longitude);

                //showing the starting point
                if(coordinates.isEmpty() && googleMap != null){
                    googleMap.addMarker(new MarkerOptions().position(coordinate).title("Start"));
                }

                coordinates.add(coordinate);
                line.add(coordinate);

                // centering the map to the location
                if(googleMap != null){
                    googleMap.addPolyline(line);
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, zoom));
                }

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        final long MIN_TIME_FOR_UPDATE = 3000; // in milli seconds
        final long MIN_DIS_FOR_UPDATE = 5; // in meters

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                MIN_TIME_FOR_UPDATE,
                MIN_DIS_FOR_UPDATE,
                locationListener);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setMaxZoomPreference(zoom);
        googleMap.setMinZoomPreference(zoom);
    }

    // updating the min and max values to find the bounds
    private void updateCoordinates(double latitude , double longitude){
        if(latitude > maxLan)
            maxLan = latitude;

        if(latitude < minLan)
            minLan = latitude;

        if(longitude > maxLong)
            maxLong = longitude;

        if(longitude < minLong)
            minLong = longitude;
    }


    public List<LatLng> finish(){
        // showing the final result of the map
        if(googleMap!= null && !coordinates.isEmpty()){

            // creating the bounds to show the image
            LatLng southwest = new LatLng(maxLan, minLong);
            LatLng northeast = new LatLng(minLan, maxLong);

            LatLngBounds bounds = new LatLngBounds.Builder().include(southwest).
                    include(northeast).build();

            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        }

        return coordinates;
    }

}
