package com.example.majo.position;

import com.example.majo.drawingscreen.DrawingPoint;

/**
 * Created by majo on 13-Dec-14.
 */
public class PositionService implements IPositionService {
    @Override
    public DrawingPoint getCurrentPosition() {
        return new DrawingPoint(100, 100, 40);
    }
}
