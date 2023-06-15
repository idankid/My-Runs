package com.idank_elishevaa.myruns;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    private Switch darkMode;
    private Switch DoNotDisturb;
    private DataBase db;
    private BroadcastReceiver ign = SplashActivity.ign;
    private IntentFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        db = new DataBase(this);
        setUpDarkMode();
        setUpDoNotDisturb();

        ign = new IgnoreReceiver();
        filter = new IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");

    }

    //setting up a the dark mode switch
    private void setUpDarkMode(){
        darkMode = findViewById(R.id.DarkMode);
        darkMode.setChecked(db.getDarkMode()?db.getDarkMode():false);
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
        DoNotDisturb = findViewById(R.id.DoNotDisturb);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED){

            askPermissions();

            db.setDoNotDisturb(false);
            DoNotDisturb.setChecked(false);
            return;
        }

        DoNotDisturb.setChecked(db.getDoNotDisturb()?db.getDoNotDisturb():false);

        DoNotDisturb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    Log.d("receiver", "registered do not disturb");
                    registerReceiver(SplashActivity.ign,  filter);
                    db.setDoNotDisturb(true);
                } else {
                    try {
                        unregisterReceiver(SplashActivity.ign);
                        unMute(false);

                    }
                    catch (Exception e){
                        Log.d("receiver", "not registered ");
                    }
                    db.setDoNotDisturb(false);
                }
            }
        });
    }

    // asking for permission
    private  void askPermissions(){
        ActivityCompat.requestPermissions(this,
                new String[] {
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.RECEIVE_SMS
                },
                1);
    }

    // when the user answered a permission request
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0) {

                //permission was granted
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setContentView(R.layout.activity_settings);

                    setUpDarkMode();
                    setUpDoNotDisturb();
                }
                // permission was not granted
                else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {

                    setContentView(R.layout.request_permission);
                    TextView view = (TextView) findViewById(R.id.setting_text_view);

                    view.setText("Click on the button to give Phone and SMS Permissions to ignore calls and messages");
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

    //turning off the mute the call
    private void unMute(Boolean mute){
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            int adJustMute;
            if(mute)
                adJustMute = AudioManager.ADJUST_MUTE;
            else
                adJustMute = AudioManager.ADJUST_UNMUTE;
            audioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, adJustMute, 0);
            audioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, adJustMute, 0);
            audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, adJustMute, 0);
            audioManager.adjustStreamVolume(AudioManager.STREAM_RING, adJustMute, 0);
            audioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, adJustMute, 0);
        }
    }



}