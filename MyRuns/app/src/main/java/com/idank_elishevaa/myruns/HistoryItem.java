package com.idank_elishevaa.myruns;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class HistoryItem {
    private final String date;
    private final String time;
    private final List<LatLng> coordinates;

    public HistoryItem(String date, String time, List<LatLng> coordinates){
        this.date = date;
        this.time = time;
        this.coordinates = coordinates;
    }

    //getters
    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public List<LatLng> getCoordinates() {
        return coordinates;
    }
}
