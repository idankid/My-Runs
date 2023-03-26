package com.Idan_Elisheva.myruns;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.button2);
        Intent jump = new Intent(this, MapActivity.class);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(jump);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        MenuItem about = menu.add("Exit");
        MenuItem settings = menu.add("Settings");
        Intent goToSettings = new Intent(this, SettingsActivity.class);
        settings.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                startActivity(goToSettings);
                return true;
            }
        });
//        about.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
//        {
//            @Override
//            public boolean onMenuItemClick(MenuItem item)
//            {
//                String str = "DEvice OS: Android" + Build.VERSION.RELEASE +"API " + Build.VERSION.SDK_INT;
//                AlertDialog.Builder dialog = new AlertDialog.Builder();
//                dialog.setMessage(str);
//                return true;
//            }
//        });
        return true;
    }

}