package com.idank_elishevaa.myruns;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {

    private Switch darkMode;
    private Switch DoNotDisturb;
    private DataBase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        db = new DataBase(this);
        setUpDarkMode();
        setUpDoNotDisturb();
    }

    //setting up a the dark mode switch
    private void setUpDarkMode(){
        darkMode = findViewById(R.id.DarkMode);
        darkMode.setChecked(db.getDarkMode());
        darkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    db.setDarkMode(true);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    db.setDarkMode(false);
                }
                recreate();
            }
        });
    }

    //setting up a the Do not disturb switch
    private void setUpDoNotDisturb(){
//        BroadcastReceiver ign = new IgnoreReceiver();
//        IntentFilter filter = new IntentFilter();
//        filter.addAction("android.intent.action.PHONE_STATE");
//        filter.addAction("android.provider.Telephony.SMS_RECEIVED");

        DoNotDisturb = findViewById(R.id.DoNotDisturb);
        DoNotDisturb.setChecked(db.getDoNotDisturb());

        DoNotDisturb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
//                    registerReceiver(ign,  filter);
                    db.setDoNotDisturb(true);
                } else {
//                    try {
//                        unregisterReceiver(ign);
//                    }
//                    catch (Exception e){
//                        Log.d("receiver", "not registered ");
//                    }
                    db.setDoNotDisturb(false);
                }
            }
        });
    }


}