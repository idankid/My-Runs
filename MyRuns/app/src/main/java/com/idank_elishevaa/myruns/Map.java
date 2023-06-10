package com.idank_elishevaa.myruns;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class Map implements OnMapReadyCallback {
    private final Context context;
    private LocationManager locationManager = null;
    private LocationListener locationListener = null;

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
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // Define a listener that responds to location updates
        locationListener = new LocationListener() {

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
        //stop listening to the location
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }

        // showing the final result of the map
        if(googleMap!= null && !coordinates.isEmpty()){
            // creating the bounds to show the image
            LatLng southwest = new LatLng(minLan, minLong);
            LatLng northeast = new LatLng(maxLan, maxLong);

            LatLngBounds bounds = new LatLngBounds.Builder().include(southwest).
                    include(northeast).build();

            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Save Map");
            builder.setMessage("do you want to save the map to the gallery?");
            builder.setCancelable(false);

            //setting the confirm button to save the map
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    saveToGallery();
                    goToMain();
                }

            });

            // setting the decline button to not do anything
            builder.setNegativeButton("No", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    goToMain();
                }
            });

            builder.show();

        }

        return coordinates;
    }

    private void goToMain(){
        Intent goToMain  = new Intent(context, MainActivity.class);
        context.startActivity(goToMain);
    }

    // saving the map to the gallery
    public void saveToGallery(){
        // Create a Bitmap of the map view
        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                // Save the snapshot to the gallery
                saveMapSnapshotToGallery(snapshot);
            }
        };
        googleMap.snapshot(callback);
    }

    // the actual saving to the gallery
    private void saveMapSnapshotToGallery(Bitmap snapshot) {
        // Get the current timestamp as the image filename
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

        // Generate the image filename with the timestamp
        String imageFileName = "myRuns" + timeStamp + ".jpg";

        // Get the content resolver
        ContentResolver contentResolver = context.getContentResolver();

        // Insert the image into the MediaStore content provider
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName);
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        contentValues.put(MediaStore.Images.Media.WIDTH, snapshot.getWidth());
        contentValues.put(MediaStore.Images.Media.HEIGHT, snapshot.getHeight());
        Uri imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        // Open an output stream to write the image data
        try (OutputStream outputStream = contentResolver.openOutputStream(imageUri)) {
            // Compress and save the snapshot bitmap to the output stream
            snapshot.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Notify the gallery about the new image
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(imageUri);
        context.sendBroadcast(mediaScanIntent);
    }

}
