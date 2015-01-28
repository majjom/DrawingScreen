package com.example.majo.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.majo.BusinessObjects.PersistedObject;
import com.example.majo.drawingscreen.R;
import com.example.majo.maps.MapManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by majo on 28-Jan-15.
 */
public class ImageListAdapter<T extends PersistedObject> extends ArrayAdapter<T>   {

    private List<T> items;
    private int layoutResourceId;
    private Context context;
    private Map<Integer, Bitmap> bitmaps;

    public ImageListAdapter(Context context, int layoutResourceId, List<T> items) {
        super(context, layoutResourceId, items);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.items = items;
        this.bitmaps = new HashMap<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // inflate
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View row = inflater.inflate(layoutResourceId, parent, false);

        T item = items.get(position);

        // load bitmap if not already loaded (but only if the file exists)
        Integer mapId = new Integer(item.id);
        if (!this.bitmaps.containsKey(mapId)){
            Bitmap bmp = MapManager.loadSmallBitmap(item.id, context);
            if (bmp != null) {
                this.bitmaps.put(mapId, bmp);
            }
        }
        if (this.bitmaps.containsKey(mapId)){
            ImageView image = (ImageView)row.findViewById(R.id.imageItem);
            image.setImageBitmap(this.bitmaps.get(mapId));
        }

        // associate context item with delete button
        ImageView deleteButton = (ImageView)row.findViewById(R.id.imageDeleteItemButton);
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
