package com.example.majo.distance;

import com.example.majo.BusinessObjects.MappedPoint;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by majo on 20-Jan-15.
 */
public class DistancePoint {
    public MappedPoint point;
    public double distance;
    public DistancePoint(MappedPoint point, double distance){
        this.point = point;
        this.distance = distance;
    }

    @Override
    public String toString() {
        return String.format("%s MP:%s", String.valueOf(this.distance), this.point.toString());
    }
}
