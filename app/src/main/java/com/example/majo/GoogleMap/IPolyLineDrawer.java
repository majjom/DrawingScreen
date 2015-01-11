package com.example.majo.GoogleMap;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by majo on 09-Jan-15.
 */
public interface IPolyLineDrawer {
    List<LatLng> getPoints();

    void add(LatLng point);
    void add(List<LatLng> points);

    void remove(LatLng point);
    void removeLast();
    void clear();

    void setZoom(int zoomLevel);
    int getZoom();

    void putMarkerAndCenter(LatLng point);

    void reCenterMap();
    void centerMapVisiblePath();
}
