package com.idank_elishevaa.myruns;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class itemAdaptor extends RecyclerView.Adapter<ItemHolder> {

    private List<HistoryItem> data;
    private Context context;

    public itemAdaptor(List<HistoryItem> data){
        this.data = data;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int position){
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.history_item, parent, false);

        return new ItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position){
        HistoryItem item = data.get(position);
        holder.date.setText(item.getDate());
        holder.time.setText(item.getTime());

        // adding the onclick listener to the show map button
        holder.map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMap(item.getCoordinates());
            }
        });

    }

    @Override
    public int getItemCount(){
        return data.size();
    }


    private void showMap(List<LatLng> coordinates){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        // adding the map to the dialog
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_map, null);
        builder.setView(dialogView);

        // Initialize the map fragment and display the map
        MapView mapView = dialogView.findViewById(R.id.map_fragment);
        mapView.onCreate(null);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                googleMap.setMaxZoomPreference(17.0f);
                mapView.onResume();
                // initializing the line parameters
                PolylineOptions line = new PolylineOptions();
                line.color(Color.RED);
                line.width(7f);
                for(LatLng dot: coordinates){
                    line.add(dot);
                }

                googleMap.addPolyline(line);

                googleMap.addMarker(new MarkerOptions().position(coordinates.get(0)).title("Start"));
                googleMap.addMarker(new MarkerOptions().position(coordinates.get(coordinates.size()-1)).title("Finish"));

                // creating the bounds to show the image
                LatLng southwest = getSouthWest(coordinates);
                LatLng northeast = getNorthEast(coordinates);

                LatLngBounds bounds = new LatLngBounds.Builder().include(southwest).
                        include(northeast).build();

                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    // returns a southwest coordinate
    private LatLng getSouthWest(List<LatLng> coordinates){
        double minLat = 200;
        double minLng = 200;

        for(LatLng coordinate : coordinates){
            if(coordinate.latitude < minLat)
                minLat = coordinate.latitude;

            if(coordinate.longitude < minLng)
                minLng = coordinate.longitude;

        }

        return new LatLng(minLat, minLng);
    }

    // returns a northeast coordinate
    private LatLng getNorthEast(List<LatLng> coordinates){
        double maxLat = -200;
        double maxLng = -200;

        for(LatLng coordinate : coordinates){
            if(coordinate.latitude > maxLat)
                maxLat = coordinate.latitude;
            if(coordinate.longitude > maxLng)
                maxLng = coordinate.longitude;
        }

        return new LatLng(maxLat, maxLng);
    }

}
