package com.idank_elishevaa.myruns;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;

public class SplashActivity extends AppCompatActivity {

    public static IgnoreReceiver ign = new IgnoreReceiver();

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

        if(db.getDoNotDisturb()){
            ign = new IgnoreReceiver();
            IntentFilter  filter = new IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
            filter.addAction("android.provider.Telephony.SMS_RECEIVED");
            registerReceiver(ign, filter);
        }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            unregisterReceiver(ign);
        }
        catch (Exception e){
            Log.d("receiver", "not registered ");
        }
    }
}