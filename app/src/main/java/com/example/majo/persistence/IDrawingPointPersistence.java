package com.example.majo.persistence;

import com.example.majo.drawingscreen.DrawingPoint;

import java.util.ArrayList;

/**
 * Created by majo on 11-Dec-14.
 */
public interface IDrawingPointPersistence {
    public ArrayList<DrawingPoint> getDrawingPoints();
    public DrawingPoint getDrawingPoint();
}
