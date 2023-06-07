package com.idank_elishevaa.myruns;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setup();

    }

    // setting up the buttons
    private void setup(){
        // setting up the start button
        Button start = (Button) findViewById(R.id.start);
        Intent goToRun  = new Intent(this, MapActivity.class);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(goToRun);
            }
        });

        // setting up the history button
        Button history = (Button) findViewById(R.id.history);
        Intent goToHistory  = new Intent(this, HistoryActivity.class);

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(goToHistory);
            }
        });



    }


    // creating the menu
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        //creating the menu items
        MenuItem about = menu.add("about");
        MenuItem settings = menu.add("Settings");
        MenuItem exit = menu.add("Exit");

        //setting the options
        setMenuItem(about, 1);
        setMenuItem(settings, 2);
        setMenuItem(exit, 3);

        return true;
    }


    // setting a menu item
    public void  setMenuItem(MenuItem item, int choice){
        //set the about option
        item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                switch (choice){
                    case 1:
                        showAbout();
                        break;
                    case 2:
                        goToSettings();
                        break;
                    case 3:
                        createExitAlert();
                        break;
                }

                return true;
            }
        });
    }

    // creating an Alert to ask whether to exit or not
    public void createExitAlert(){
        AlertDialog.Builder exitAlert = new AlertDialog.Builder(this);
        exitAlert.setIcon(R.drawable.ic_warning);
        exitAlert.setTitle("Exit App");
        exitAlert.setMessage("Do you really want to exit?");
        exitAlert.setCancelable(false);

        //setting the confirm button to exit the app
        exitAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Log.d("debug", "Yes");
                finish(); // destroy this activity
            }
        });

        // setting the decline button to not do anything
        exitAlert.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Log.d("debug", "No");
            }
        });
        exitAlert.show();

    }

    // jumping to the settings page
    public void goToSettings(){
        Intent settingsJump = new Intent(this, SettingsActivity.class);
        startActivity(settingsJump);
    }

    //showing the information about the app in a alert dialog
    public void showAbout(){
        AlertDialog.Builder about = new AlertDialog.Builder(this);
        about.setIcon(R.drawable.app_logo);
        about.setTitle("About");
        about.setMessage("App Name: MyRuns\n\n" +
                "Developers: \n Idan Kideckel,\n Elisheva Amar\n\n" +
                "Submission Date: 18/06/2023\n\n" +
                "OS: " + Build.VERSION.RELEASE + " SDK: " + Build.VERSION.SDK_INT);
        about.show();
    }
}