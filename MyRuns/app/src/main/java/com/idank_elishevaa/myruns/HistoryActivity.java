package com.idank_elishevaa.myruns;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private DataBase db;
    private List<HistoryItem> runs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        db = new DataBase(this);

        runs = db.getRuns();

        //getting the fragment to put the map in
        RecyclerView recyclerView = findViewById(R.id.history_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        itemAdaptor adaptor = new itemAdaptor(runs);
        recyclerView.setAdapter(adaptor);
        recyclerView.setLayoutManager(layoutManager);
    }
}