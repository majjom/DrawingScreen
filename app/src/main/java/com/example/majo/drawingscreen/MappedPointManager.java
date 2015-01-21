package com.example.majo.drawingscreen;

import android.graphics.Color;
import android.widget.ListView;

import com.example.majo.Adapters.SimpleDeleteListAdapter;
import com.example.majo.BusinessObjects.DrawingPoint;
import com.example.majo.BusinessObjects.MappedPoint;
import com.example.majo.persistence.IDrawingPointPersistence;
import com.example.majo.persistence.IMappedPointsPersistence;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moravekm on 21-Jan-15.
 */
public class MappedPointManager implements IMappedPointManager {

    IPointLayer drawingLayer;
    SimpleDeleteListAdapter<MappedPoint> listAdapter;
    IMappedPointsPersistence persistence;
    int schemaMapId;

    List<MappedPoint> points;

    int highlightColor;
    int color;

    public MappedPointManager(IPointLayer drawingLayer, SimpleDeleteListAdapter<MappedPoint> listAdapter, IMappedPointsPersistence persistence, int schemaMapId){
        this.drawingLayer = drawingLayer;
        this.listAdapter = listAdapter;
        this.persistence = persistence;
        this.schemaMapId = schemaMapId;

        this.points = new ArrayList<>();

        this.color = Color.BLUE;
        this.highlightColor = Color.RED;
    }

    @Override
    public int getSchemaMapId() {
        return this.schemaMapId;
    }

    @Override
    public void refreshPointsFromDb() {
        // 1) store in local list
        this.points = this.persistence.getAllPoints(this.schemaMapId);

        // 3) update GUI - list adapter
        this.listAdapter.clear();
        this.listAdapter.addAll(this.points);

        // 4) update GUI - layer screen
        this.drawingLayer.clear();
        this.drawingLayer.drawPoints(this.getDrawingPointsOnly(this.points), this.color);
    }

    @Override
    public List<MappedPoint> getPoints() {
        return this.points;
    }

    @Override
    public void removePoint(MappedPoint point) {
        // 1) store in local list
        this.points.remove(point);

        // 2) store in DB
        this.persistence.deleteMappedPoint(point);

        // 3) update GUI - list adapter
        this.listAdapter.remove(point);

        // 4) update GUI - layer screen
        this.drawingLayer.clear();
        this.drawingLayer.drawPoints(this.getDrawingPointsOnly(this.points), this.color);
    }

    @Override
    public void removeAllPoints(){
        // 1) store in local list
        this.points.clear();

        // 2) store in DB
        this.persistence.deleteAllPoints(this.schemaMapId);

        // 3) update GUI - list adapter
        this.listAdapter.clear();

        // 4) update GUI - layer screen
        this.drawingLayer.clear();
    }

    @Override
    public List<MappedPoint> getHighlightedPoints() {
        List<MappedPoint> result = new ArrayList<>();
        for (MappedPoint point : this.points){
            if (point.isHighlighted){
                result.add(point);
            }
        }

        return result;
    }

    @Override
    public void toggleHighlightPoint(MappedPoint point) {
        // 1) store in local list
        int pointIdx = this.points.indexOf(point);
        if (pointIdx == -1) return;
        this.points.get(pointIdx).isHighlighted = !this.points.get(pointIdx).isHighlighted;

        // 2) store in DB
        // NOPE

        // 3) update GUI - list adapter
        this.listAdapter.notifyDataSetChanged();

        // 4) update GUI - layer screen
        this.drawingLayer.clear();
        this.drawingLayer.drawPoints(this.getDrawingPointsOnly(this.points), this.color);
        this.drawingLayer.drawPoints(this.getDrawingPointsOnly(this.getHighlightedPoints()), this.highlightColor);

    }

    @Override
    public void toggleHighlightPoint(float x, float y) {
        for (MappedPoint point : this.points){
            if ((int)point.drawingPoint.x == (int)x && (int)point.drawingPoint.y == (int)y){
                toggleHighlightPoint(point);
            }
        }

    }

    @Override
    public void clearHighlight() {
        // 1) store in local list
        for (MappedPoint point : this.getHighlightedPoints()) {
            point.isHighlighted = false;
        }

        // 2) store in DB
        // NOPE

        // 3) update GUI - list adapter
        this.listAdapter.notifyDataSetChanged();

        // 4) update GUI - layer screen
        this.drawingLayer.clear();
        this.drawingLayer.drawPoints(this.getDrawingPointsOnly(this.points), this.color);
    }

    @Override
    public void setHighlightColor(int color) {
        this.highlightColor = color;
    }

    @Override
    public int getHighlightColor() {
        return this.highlightColor;
    }

    @Override
    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public int getColor() {
        return this.color;
    }



    private List<DrawingPoint> getDrawingPointsOnly(List<MappedPoint> mappedPoints){
        List<DrawingPoint> result = new ArrayList<>();
        for(MappedPoint point : mappedPoints){
            result.add(point.drawingPoint);
        }
        return result;
    }
}
