package com.example.majo.GoogleMap;

import android.graphics.Color;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by majo on 09-Jan-15.
 */
public class PolyLineDrawer implements IPolyLineDrawer {

    private final int COLOR = Color.MAGENTA;
    private final int WIDTH = 5;

    private GoogleMap map;

    private Marker marker;

    private Polyline polyLine;

    int lastZoomLevel;
    LatLng lastCenteredPoint;

    public PolyLineDrawer(GoogleMap map){
        this.map = map;
        this.lastZoomLevel = 13;
    }


    @Override
    public List<LatLng> getPoints() {
        if (this.polyLine == null){
            return new ArrayList<>();
        }
        return this.polyLine.getPoints();
    }

    @Override
    public void add(LatLng point) {
        if (point == null) return;

        List<LatLng> tmpList = new ArrayList<>();
        tmpList.add(point);
        this.addPoints(tmpList);
        this.setMarker(point);
        centerMap(point, this.lastZoomLevel);
    }

    @Override
    public void add(List<LatLng> points) {
        if (points == null) return;
        if (points.size() <= 0) return;

        this.addPoints(points);
        this.setMarker(getLastPoint());
        centerMap(getLastPoint(), this.lastZoomLevel);
    }

    @Override
    public void remove(LatLng point) {
        if (point == null) return;

        List<LatLng> tmpList = new ArrayList<>();
        tmpList.add(point);
        removePoints(tmpList);
        this.setMarker(getLastPoint());
        centerMap(getLastPoint(), this.lastZoomLevel);
    }

    @Override
    public void removeLast() {
        remove(getLastPoint());
    }

    @Override
    public void clear() {
        if (this.polyLine != null) this.polyLine.remove();
        this.polyLine = null;

        clearMarker();
    }

    @Override
    public void setZoom(int zoomLevel) {
        centerMap(this.lastCenteredPoint, zoomLevel);
    }

    @Override
    public int getZoom() {
        return this.lastZoomLevel;
    }

    @Override
    public void reCenterMap() {
        centerMap(this.lastCenteredPoint, this.lastZoomLevel);
    }

    @Override
    public void centerMapVisiblePath(){
        if (this.polyLine.getPoints().size() < 2) return;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng point : this.polyLine.getPoints()){
            builder.include(point);
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate newCamera = CameraUpdateFactory.newLatLngBounds(bounds, 10);
        map.moveCamera(newCamera);
        map.animateCamera(newCamera);
    }

    /* MARKER */
    private void clearMarker(){
        if (marker != null){
            marker.remove();
        }
    }
    private void setMarker(LatLng markPosition){
        clearMarker();
        marker = map.addMarker(new MarkerOptions().position(markPosition));
    }

    /* POINTS */
    private void addPoints(List<LatLng> points) {
        if (this.polyLine == null){
            // fist two points, stating the line
            this.polyLine = map.addPolyline(new PolylineOptions().addAll(points)
                    .width(WIDTH).color(COLOR).geodesic(true));
        } else {
            // continuing the line
            List<LatLng> pointsTmp = this.polyLine.getPoints();
            pointsTmp.addAll(points);
            this.polyLine.setPoints(pointsTmp);
        }
    }

    private void removePoints(List<LatLng> points) {
        if (this.polyLine == null) return;
        List<LatLng> pointsTmp = this.polyLine.getPoints();
        pointsTmp.removeAll(points);
        this.polyLine.setPoints(pointsTmp);
    }

    /* CENTER & ZOOM */
    private void centerMap(LatLng mapCenter, int mapZoom){
        this.lastCenteredPoint = mapCenter;
        this.lastZoomLevel = mapZoom;
        CameraUpdate newCamera = CameraUpdateFactory.newLatLngZoom(mapCenter, mapZoom);
        map.moveCamera(newCamera);
        map.animateCamera(newCamera);
    }

    private LatLng getLastPoint(){
        if (this.polyLine == null) return null;
        if (this.polyLine.getPoints().size() == 0) return null;

        return this.polyLine.getPoints().get(this.polyLine.getPoints().size() - 1);
    }
}
