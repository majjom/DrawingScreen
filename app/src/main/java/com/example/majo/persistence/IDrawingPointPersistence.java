package com.example.majo.persistence;

import com.example.majo.BusinessObjects.DrawingPoint;

import java.util.ArrayList;

/**
 * Created by majo on 11-Dec-14.
 */
public interface IDrawingPointPersistence {
    public ArrayList<DrawingPoint> getAllPoints();

    public void addPoints(ArrayList<DrawingPoint> points);

    public void deleteAllPoints();

}
