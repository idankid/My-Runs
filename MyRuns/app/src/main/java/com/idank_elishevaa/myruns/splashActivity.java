package com.idank_elishevaa.myruns;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        setupSettings();
        waitToLoad();

    }

    //setting up the settings
    private void setupSettings(){
        DataBase db = new DataBase(this);

        // applying the dark mode setting
        if(db.getDarkMode())
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

    }

    // giving enough time to see the splash screen
    private void waitToLoad(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Starting the main activity
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }, 2000); // Delay in milliseconds (e.g., 2000 = 2 seconds)
    }





}