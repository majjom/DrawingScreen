package com.example.majo.distance;

import android.location.Location;

import com.example.majo.BusinessObjects.MappedPoint;

/**
 * Created by majo on 14-Dec-14.
 */
public class MPLComposite {
    public Location location;
    public MappedPoint point;

    public MPLComposite(Location location, MappedPoint point){
        this.location = location;
        this.point = point;
    }
}
