package com.example.majo.position;

import android.location.Location;

import com.example.majo.BusinessObjects.DrawingPoint;

/**
 * Created by majo on 13-Dec-14.
 */
public class DummyPositionService implements IPositionService {


    @Override
    public DrawingPoint getCurrentPosition() {
        return new DrawingPoint(100, 100, 40);
    }

    @Override
    public DrawingPoint getCurrentPosition(Location location) {
        return getCurrentPosition();
    }
}
