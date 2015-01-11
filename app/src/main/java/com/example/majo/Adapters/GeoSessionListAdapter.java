package com.example.majo.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.majo.BusinessObjects.GeoSession;
import com.example.majo.drawingscreen.R;

import java.text.DateFormat;
import java.util.List;

/**
 * Created by majo on 11-Jan-15.
 */
public class GeoSessionListAdapter extends ArrayAdapter<GeoSession> {

    private List<GeoSession> items;
    private int layoutResourceId;
    private Context context;


    public GeoSessionListAdapter(Context context, int layoutResourceId, List<GeoSession> items) {
        super(context, layoutResourceId, items);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        GeoSessionHolder holder = null;

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);

        holder = new GeoSessionHolder();
        holder.geoSession = items.get(position);
        holder.deleteButton = (ImageView)row.findViewById(R.id.geoSessionListItemDelete);
        holder.deleteButton.setTag(holder.geoSession);
        holder.geoSessionText = (TextView)row.findViewById(R.id.geoSessionListItemText);

        row.setTag(holder);

        populateItem(holder);
        return row;
    }

    private void populateItem(GeoSessionHolder holder){
        holder.geoSessionText.setText(String.format("id:%s name:%s dt:%s isTracked:%s count:%d", holder.geoSession.id, holder.geoSession.name, android.text.format.DateFormat.format("yyyy-MM-dd hh:mm:ss", holder.geoSession.dateCreated), String.valueOf(holder.geoSession.isBeingTracked), holder.geoSession.getGeoLocations().size()));
    }

    public static class GeoSessionHolder {
        GeoSession geoSession;
        TextView geoSessionText;
        ImageView deleteButton;
    }
}
