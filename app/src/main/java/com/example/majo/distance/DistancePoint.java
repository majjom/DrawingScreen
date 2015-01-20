package com.example.majo.distance;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by majo on 20-Jan-15.
 */
public class DistancePoint {
    public LatLng point;
    public double distance;
    public DistancePoint(LatLng point, double distance){
        this.point = point;
        this.distance = distance;
    }
}
