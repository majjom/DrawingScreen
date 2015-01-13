package com.example.majo.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.majo.BusinessObjects.DrawingPoint;
import com.example.majo.drawingscreen.R;

import java.util.List;

/**
 * Created by majo on 13-Jan-15.
 */
public class DrawingPointsAdapter extends RecyclerView.Adapter<DrawingPointsViewHolder> {

    private List<DrawingPoint> list;
    private Context context;


    public DrawingPointsAdapter(Context context, List<DrawingPoint> list){
        this.list = list;
        this.context = context;
    }

    @Override
    public DrawingPointsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        DrawingPointsViewHolder sp = new DrawingPointsViewHolder(v);
        return sp;
    }

    @Override
    public void onBindViewHolder(DrawingPointsViewHolder holder, int position) {
        DrawingPoint dp = list.get(position);
        holder.title.setText("rec " + dp.toString());
    }

    @Override
    public int getItemCount() {
        return (null != list ? list.size() : 0);
    }
}
