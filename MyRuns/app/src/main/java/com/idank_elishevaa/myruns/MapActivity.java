package com.idank_elishevaa.myruns;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import android.os.Bundle;

import java.util.Calendar;
import java.util.List;

public class MapActivity extends AppCompatActivity implements runningTimer.TimerCallback{

    private runningTimer stopwatch;
    private Chronometer display;
    private Boolean running;
    private Map map;

    private Intent timerIntend;

    private final DataBase db = new DataBase(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 111);
        }
        else{
            setContentView(R.layout.activity_map);
            //setting everything up
            setup();
        }

    }

    //setting up all off the variables
    private void setup(){

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        map = new Map(this, mapFragment);
        map.location();

        waitToLoad();
        //setting up the finish button
        setUpFinish();

        display = findViewById(R.id.stopwatch);
        stopwatch = new runningTimer(this, display, this);


    }

    //setting up the finish button
    private void setUpFinish(){
        Button finish = findViewById(R.id.finish);


        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(running){
                    running = false;
                    try{
                        stopService(timerIntend);
                    }catch(Exception e){
                        Log.d("timer receiver", "no receiver active");
                    }

                    // adding the run to the database
                    String runTime = stopwatch.stop();
                    String runDate = getDate();
                    List<LatLng> coordinates =  map.finish();

                    if(!coordinates.isEmpty())
                        db.insertRun(runDate, runTime, coordinates);

                }

            }
        });

    }


    @Override
    // when the user answered a permission request
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 111) {
            if (grantResults.length > 0) {

                //permission was granted
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setContentView(R.layout.activity_map);
                    setup();
                }
                // permission was not granted
                else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {

                    setContentView(R.layout.request_permission);
                    Button permissionButton = (Button) findViewById(R.id.location_permission);

                    // asking for permission
                    permissionButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openAppSettings();
                        }
                    });

                }

            }
        }
    }

    // opening the settings to manually give permissions
    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }


    @Override
    // updating the timer on screen
    public void onTimerUpdate(final String time){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                display.setText(time);
            }
        });
    }

    //return the current date as a string
    private String getDate(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Months are 0-based
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return String.format("%02d-%02d-%04d" , day, month, year);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopwatch.stop();
    }

    // giving enough time for the location to load
    private void waitToLoad(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                stopwatch.start(-1);

                running = true;
            }
        }, 3000); // Delay in milliseconds (e.g., 2000 = 2 seconds)
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(running){
            timerIntend = new Intent(this, runningTimerService.class);
            timerIntend.putExtra("start_time", stopwatch.getStartTime());
            startService(timerIntend);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try{
            stopService(timerIntend);
        }catch(Exception e){
            Log.d("service", "cannot stop service");
        }
    }
}