package com.example.majo.GoogleMap;

import android.graphics.Color;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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



    private Polyline polyLine;

    private Marker lastHighlightedPoint;
    private Marker trackingMarker;

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

        putMarkerAndCenter(point);
    }

    @Override
    public void add(List<LatLng> points) {
        if (points == null) return;
        if (points.size() <= 0) return;

        this.addPoints(points);
        this.setTrackingMarker(getLastPoint());
        centerMap(getLastPoint(), this.lastZoomLevel);
    }

    @Override
    public void remove(LatLng point) {
        if (point == null) return;

        List<LatLng> tmpList = new ArrayList<>();
        tmpList.add(point);
        removePoints(tmpList);

        // enter on last point of the line
        LatLng lastPoint = getLastPoint();
        if (lastPoint != null){
            this.setTrackingMarker(lastPoint);
            centerMap(lastPoint, this.lastZoomLevel);
        }
    }

    @Override
    public void removeLast() {
        remove(getLastPoint());
    }

    @Override
    public void clear() {
        if (this.polyLine != null) this.polyLine.remove();
        this.polyLine = null;

        removeMarker(this.trackingMarker);
        hideLastLocation();
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
    public void putMarkerAndCenter(LatLng point) {
        if (point == null) return;
        this.setTrackingMarker(point);
        centerMap(point, this.lastZoomLevel);
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

    @Override
    public void showLocation(LatLng point) {
        this.removeMarker(this.lastHighlightedPoint);

        if (this.lastHighlightedPoint == null){
            this.lastHighlightedPoint = addMarker(point, BitmapDescriptorFactory.HUE_GREEN);
        } else {
            // if double click on the same point than remove it
            if ((this.lastHighlightedPoint.getPosition().latitude == point.latitude) && (this.lastHighlightedPoint.getPosition().longitude == point.longitude)) {
                this.lastHighlightedPoint = null;
            } else {
                this.lastHighlightedPoint = addMarker(point, BitmapDescriptorFactory.HUE_GREEN);
            }
        }
    }

    @Override
    public void hideLastLocation(){
        this.removeMarker(this.lastHighlightedPoint);
    }


    /* MARKER */
    private void setTrackingMarker(LatLng markPosition) {
        removeMarker(this.trackingMarker);
        this.trackingMarker = addMarker(markPosition, BitmapDescriptorFactory.HUE_AZURE);
    }




    private Marker addMarker(LatLng markPosition, float hue){
        return map.addMarker(new MarkerOptions().position(markPosition).icon(BitmapDescriptorFactory.defaultMarker(hue)));
    }

    private void removeMarker(Marker marker){
        if (marker != null){
            marker.remove();
        }
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
