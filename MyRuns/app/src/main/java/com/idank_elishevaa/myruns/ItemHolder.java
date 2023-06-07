package com.idank_elishevaa.myruns;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ItemHolder extends RecyclerView.ViewHolder {

    TextView date;
    TextView time;
    Button map;

    public ItemHolder(View itemView){
        super(itemView);

        date = itemView.findViewById(R.id.run_date);
        time = itemView.findViewById(R.id.run_time);
        map = itemView.findViewById(R.id.show_map);

    }
}
