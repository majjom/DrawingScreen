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
public class SimpleDeleteListAdapter<T> extends ArrayAdapter<T> {

    private List<T> items;
    private int layoutResourceId;
    private Context context;

    public SimpleDeleteListAdapter(Context context, int layoutResourceId, List<T> items) {
        super(context, layoutResourceId, items);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // inflate
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View row = inflater.inflate(layoutResourceId, parent, false);

        T item = items.get(position);

        // associate context item with delete button
        ImageView deleteButton = (ImageView)row.findViewById(R.id.simpleDeleteListButton);
        deleteButton.setTag(item);

        // populate text
        TextView textView = (TextView)row.findViewById(R.id.simpleDeleteListText);
        textView.setText(item.toString());

        return row;
    }

    public List<T> getItems(){
        return this.items;
    }
}
