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
public class MappedPointManager implements IPointManager<MappedPoint>, IOnPointListener {

    IPointLayer drawingLayer;
    SimpleDeleteListAdapter<MappedPoint> listAdapter;
    IMappedPointsPersistence persistence;
    int schemaMapId;
    IOnPointChanged onPointChanged;

    List<MappedPoint> points;

    int highlightColor;
    int color;

    public MappedPointManager(IPointLayer drawingLayer, SimpleDeleteListAdapter<MappedPoint> listAdapter, IMappedPointsPersistence persistence, IOnPointChanged onPointChanged, int schemaMapId){
        this.drawingLayer = drawingLayer;
        this.listAdapter = listAdapter;
        this.persistence = persistence;
        this.schemaMapId = schemaMapId;

        this.points = new ArrayList<>();

        this.color = Color.BLUE;
        this.highlightColor = Color.RED;

        this.onPointChanged = onPointChanged;
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

        // 05) notify
        this.onPointChanged.onPointChanged();
    }

    public List<MappedPoint> getPointsFromDatabaseWithoutRefresh(){
        return this.persistence.getAllPoints(this.schemaMapId);
    }

    @Override
    public int getSchemaMapId() {
        return this.schemaMapId;
    }

    @Override
    public List<MappedPoint> getPoints() {
        return this.points;
    }

    @Override
    public int getRadius() {
        return 0;
    }

    @Override
    public void setRadius(int radius){
    }

    @Override
    public MappedPoint addPoint(float x, float y) {
        return null;
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

        // 05) notify
        this.onPointChanged.onPointChanged();
    }

    public void removePoints(List<MappedPoint> points){
        // 1) store in local list
        this.points.removeAll(points);

        // 2) store in DB
        for (MappedPoint point : points) {
            this.persistence.deleteMappedPoint(point);
            // 3) update GUI - list adapter
            this.listAdapter.remove(point);
        }

        // 4) update GUI - layer screen
        this.drawingLayer.clear();
        this.drawingLayer.drawPoints(this.getDrawingPointsOnly(this.points), this.color);

        // 05) notify
        this.onPointChanged.onPointChanged();
    }

    @Override
    public MappedPoint removeLastPoint(){
        if (points.size() > 0) {
            MappedPoint lastPoint = points.get(points.size() - 1);
            removePoint(lastPoint);
            return lastPoint;
        }

        return null;
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

        // 05) notify
        this.onPointChanged.onPointChanged();
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
    public void highlightPoint(MappedPoint point){
        this.setHighlightPoint(point, true);
    }

    @Override
    public void toggleHighlightPoint(MappedPoint point) {
        this.setHighlightPoint(point, !point.isHighlighted);
    }

    private void setHighlightPoint(MappedPoint point, boolean isHighlighted) {
        // 1) store in local list
        int pointIdx = this.points.indexOf(point);
        if (pointIdx == -1) return;
        this.points.get(pointIdx).isHighlighted = isHighlighted;

        // 2) store in DB
        // NOPE

        // 3) update GUI - list adapter
        this.listAdapter.notifyDataSetChanged();

        // 4) update GUI - layer screen
        this.drawingLayer.clear();
        this.drawingLayer.drawPoints(this.getDrawingPointsOnly(this.points), this.color);
        this.drawingLayer.drawPoints(this.getDrawingPointsOnly(this.getHighlightedPoints()), this.highlightColor);

        // 05) notify
        this.onPointChanged.onPointChanged();
    }

    @Override
    public void toggleHighlightPoint(float x, float y) {
        for (MappedPoint point : this.points){
            if (isPointInCircle(point.drawingPoint.x, point.drawingPoint.y, point.drawingPoint.radius, x, y)){
                setHighlightPoint(point, true);
            }
        }
    }

    private boolean isPointInCircle(double centerX, double centerY, double radius, double x, double y){
        return  Math.pow(x-centerX, 2) + Math.pow(y-centerY, 2) <= radius*radius;
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

        // 05) notify
        this.onPointChanged.onPointChanged();

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

    public void highlightDrawingPoint(float vX, float vY){
        if (!this.drawingLayer.isVisible()) return;
        this.toggleHighlightPoint(vX, vY);
    }

    @Override
    public DrawingPoint addDrawingPoint(float vX, float vY) {
        if (!this.drawingLayer.isVisible()) return null;
        return null;
    }

    @Override
    public DrawingPoint removeLastDrawingPoint() {
        if (!this.drawingLayer.isVisible()) return null;
        return null;
    }






    private List<DrawingPoint> getDrawingPointsOnly(List<MappedPoint> mappedPoints){
        List<DrawingPoint> result = new ArrayList<>();
        for(MappedPoint point : mappedPoints){
            result.add(point.drawingPoint);
        }
        return result;
    }
}
