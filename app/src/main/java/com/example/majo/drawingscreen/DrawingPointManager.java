package com.example.majo.drawingscreen;

import android.graphics.Color;

import com.example.majo.Adapters.SimpleDeleteListAdapter;
import com.example.majo.BusinessObjects.DrawingPoint;
import com.example.majo.persistence.IDrawingPointPersistence;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by majo on 20-Jan-15.
 */
public class DrawingPointManager implements IDrawingPointManager, IOnPointListener {

    IPointLayer drawingLayer;
    SimpleDeleteListAdapter<DrawingPoint> listAdapter;
    IDrawingPointPersistence persistence;
    int schemaMapId;

    List<DrawingPoint> points;

    int highlightColor;
    int color;
    int radius;

    public DrawingPointManager(IPointLayer drawingLayer, SimpleDeleteListAdapter<DrawingPoint> listAdapter, IDrawingPointPersistence persistence, int schemaMapId){
        this.drawingLayer = drawingLayer;
        this.listAdapter = listAdapter;
        this.persistence = persistence;
        this.schemaMapId = schemaMapId;

        this.points = new ArrayList<>();

        this.color = Color.BLUE;
        this.highlightColor = Color.RED;
        this.radius = 10;
    }


    public void refreshPointsFromDb(){
        // 1) store in local list
        this.points = this.persistence.getAllPoints(this.schemaMapId);

        // 3) update GUI - list adapter
        this.listAdapter.clear();
        this.listAdapter.addAll(this.points);

        // 4) update GUI - layer screen
        this.drawingLayer.clear();
        this.drawingLayer.drawPoints(this.points, this.color);
    }

    @Override
    public int getSchemaMapId() {
        return this.schemaMapId;
    }

    @Override
    public List<DrawingPoint> getPoints() {
        return this.points;
    }

    @Override
    public int getRadius() {
        return this.radius;
    }

    @Override
    public void setRadius(int radius){
        this.radius = radius;
    }

    @Override
    public DrawingPoint addPoint(float x, float y) {
        // first check if we dont have the one yet as the last point
        if (points.size() > 0) {
            DrawingPoint lastPoint = points.get(points.size() - 1);
            if ((int)lastPoint.x == (int)x && (int)lastPoint.y == (int)y){
                return null;
            }
        }

        // 1) store in local list
        DrawingPoint result = new DrawingPoint(x, y, this.radius);
        this.points.add(result);

        // 2) store in DB (will get DB id)
        this.persistence.addPoint(this.schemaMapId, result);

        // 3) update GUI - list
        this.listAdapter.add(result);

        // 4) update GUI - layer
        this.drawingLayer.addPoint(result, this.color);

        return result;
    }

    @Override
    public void removePoint(DrawingPoint point) {
        // 1) store in local list
        this.points.remove(point);

        // 2) store in DB
        this.persistence.deleteDrawingPoint(point);

        // 3) update GUI - list adapter
        this.listAdapter.remove(point);

        // 4) update GUI - layer screen
        this.drawingLayer.clear();
        this.drawingLayer.drawPoints(this.points, this.color);
    }

    @Override
    public void removePoints(List<DrawingPoint> points) {
        // 1) store in local list
        this.points.removeAll(points);

        // 2) store in DB
        for (DrawingPoint point : points) {
            this.persistence.deleteDrawingPoint(point);
            // 3) update GUI - list adapter
            this.listAdapter.remove(point);
        }

        // 4) update GUI - layer screen
        this.drawingLayer.clear();
        this.drawingLayer.drawPoints(this.points, this.color);
    }

    @Override
    public DrawingPoint removeLastPoint() {
        if (points.size() > 0) {
            DrawingPoint lastPoint = points.get(points.size() - 1);
            removePoint(lastPoint);
            return lastPoint;
        }

        return null;
    }

    @Override
    public void removeAllPoints() {
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
    public List<DrawingPoint> getHighlightedPoints() {
        List<DrawingPoint> result = new ArrayList<>();
        for (DrawingPoint point : this.points){
            if (point.isHighlighted){
                result.add(point);
            }
        }

        return result;
    }

    @Override
    public void toggleHighlightPoint(DrawingPoint point) {
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
        this.drawingLayer.drawPoints(this.points, this.color);
        this.drawingLayer.drawPoints(getHighlightedPoints(), this.highlightColor);
    }

    @Override
    public void toggleHighlightPoint(float x, float y){
        for (DrawingPoint point : this.points){
            if ((int)point.x == (int)x && (int)point.y == (int)y){
                toggleHighlightPoint(point);
            }
        }
    }

    @Override
    public void clearHighlight(){
            // 1) store in local list
            for (DrawingPoint point : this.getHighlightedPoints()) {
                point.isHighlighted = false;
            }

            // 2) store in DB
            // NOPE

            // 3) update GUI - list adapter
            this.listAdapter.notifyDataSetChanged();

            // 4) update GUI - layer screen
            this.drawingLayer.clear();
            this.drawingLayer.drawPoints(this.points, this.color);
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









    /******** IOnPointListener ***************/

    @Override
    public DrawingPoint addDrawingPoint(float vX, float vY) {
        return this.addPoint(vX, vY);
    }

    @Override
    public DrawingPoint removeLastDrawingPoint() {
        return this.removeLastPoint();
    }
}
