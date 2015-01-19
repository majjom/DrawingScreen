package com.example.majo.position;

import android.location.Location;

import com.example.majo.BusinessObjects.DrawingPoint;
import com.example.majo.BusinessObjects.MappedPoint;

/**
 * Created by majo on 13-Dec-14.
 */
public interface IPositionService {
    public MappedPoint getLastPosition();

    public MappedPoint getCurrentPosition(Location location);
}
