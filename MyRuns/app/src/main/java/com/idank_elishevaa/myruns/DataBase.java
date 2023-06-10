package com.idank_elishevaa.myruns;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataBase extends SQLiteOpenHelper {

    public DataBase(Context context){
        super(context, "MyRuns.db", null, 1 );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE runs(date TEXT, time TEXT, coordinates TEXT)");
        db.execSQL("CREATE TABLE settings(name TEXT, active TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    // insert a run into the database
    public void insertRun(String date, String time,  List<LatLng> coordinates){
        SQLiteDatabase db = this.getWritableDatabase();

        // creating the values
        ContentValues values = new ContentValues();
        values.put("date", date);
        values.put("time", time);
        values.put("coordinates", coordinates.toString());

        // adding to the data base
        db.insert("runs", null, values);

    }

    // returns the history of runs
    public List<HistoryItem> getRuns(){
        List<HistoryItem> runs = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM runs", null);

        if (cursor.moveToFirst()) {
            int dateIndex = cursor.getColumnIndex("date");
            int timeIndex = cursor.getColumnIndex("time");
            int coordinatesIndex = cursor.getColumnIndex("coordinates");

            do {
                // getting the data for every row
                String date = cursor.getString(dateIndex);
                String time = cursor.getString(timeIndex);
                String coordinatesString = cursor.getString(coordinatesIndex);

                // parsing the string to get a list of lat long coordinates
                List<LatLng> coordinates = parseCoordinates(coordinatesString);

                if(coordinates.isEmpty()) continue;

                // creating a item that holds the history and adding it to the list
                HistoryItem item = new HistoryItem(date, time, coordinates);
                runs.add(item);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return runs;
    }

    // parsing the coordinates string to a list
    private List<LatLng> parseCoordinates(String str){
        List<LatLng> coordinates = new ArrayList<>();

        // defining the regular expression pattern to match the latitude and longitude values
        Pattern pattern = Pattern.compile("\\((-?\\d+\\.\\d+),(-?\\d+\\.\\d+)\\)");

        // Create a matcher object to find matches in the input string
        Matcher matcher = pattern.matcher(str);

        // Iterate through the matches and extract the latitude and longitude values
        while (matcher.find()) {
            double latitude = Double.parseDouble(matcher.group(1));
            double longitude = Double.parseDouble(matcher.group(2));
            LatLng latLng = new LatLng(latitude, longitude);
            coordinates.add(latLng);
        }

        return coordinates;
    }

    // getting the dark mode setting
    public Boolean getDarkMode(){
        String active = "false";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM settings Where name='DarkMode'", null);

        if (cursor.moveToFirst()) {
            int activeInt = cursor.getColumnIndex("active");

            do {
                // getting the data for every row
                active = cursor.getString(activeInt);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return active.equals("true");
    }

    // setting the dark mode setting
    public void setDarkMode(Boolean mode){
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM settings WHERE name='DarkMode' ", null);
        if(cursor.moveToFirst()){
            if(mode)
                db.execSQL("UPDATE settings SET active='true' WHERE name='DarkMode' ");
            else
                db.execSQL("UPDATE settings SET active='false' WHERE name='DarkMode' ");
        }
        else{
            // creating the values
            ContentValues values = new ContentValues();
            values.put("name", "DarkMode");
            values.put("active", mode?"true":"false");
            db.insert("settings", null, values);
        }

        cursor.close();
        db.close();


    }

    // getting the do not disturb setting
    public Boolean getDoNotDisturb(){
        String active = "false";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM settings Where name='Disturb'", null);

        if (cursor.moveToFirst()) {
            int activeInt = cursor.getColumnIndex("active");

            do {
                // getting the data for every row
                active = cursor.getString(activeInt);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return active.equals("true");
    }

    // setting the dark mode setting
    public void setDoNotDisturb(Boolean mode){
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM settings WHERE name='Disturb' ", null);
        if(cursor.moveToFirst()){
            if(mode)
                db.execSQL("UPDATE settings SET active='true' WHERE name='Disturb' ");
            else
                db.execSQL("UPDATE settings SET active='false' WHERE name='Disturb' ");
        }
        else{
            // creating the values
            ContentValues values = new ContentValues();
            values.put("name", "Disturb");
            values.put("active", mode?"true":"false");
            db.insert("settings", null, values);
        }

        cursor.close();
        db.close();


    }


}
