package com.example.majo.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.majo.drawingscreen.R;

/**
 * Created by majo on 13-Jan-15.
 */
public class DrawingPointsViewHolder extends RecyclerView.ViewHolder {

    protected TextView title;

    public DrawingPointsViewHolder(View itemView) {
        super(itemView);

        /* for custom view
        this.title = (TextView)itemView.findViewById(R.id.drawingPointListItemText);
        */

        /*for default android build in view android.R.layout.simple_list_item_1 */
        this.title = (TextView)itemView.findViewById(android.R.id.text1);
    }
}
