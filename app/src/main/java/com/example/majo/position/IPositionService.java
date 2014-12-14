package com.example.majo.position;

import android.location.Location;

import com.example.majo.BusinessObjects.DrawingPoint;

/**
 * Created by majo on 13-Dec-14.
 */
public interface IPositionService {
    public DrawingPoint getCurrentPosition();

    public DrawingPoint getCurrentPosition(Location location);
}
