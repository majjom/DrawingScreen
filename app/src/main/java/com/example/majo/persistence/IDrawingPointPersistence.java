package com.example.majo.persistence;

import com.example.majo.BusinessObjects.DrawingPoint;
import com.example.majo.BusinessObjects.GeoSession;

import java.util.ArrayList;

/**
 * Created by majo on 11-Dec-14.
 */
public interface IDrawingPointPersistence {
    public ArrayList<DrawingPoint> getAllPoints(int mapId);

    public void addPoints(int mapId, ArrayList<DrawingPoint> points);

    public void deleteAllPoints(int mapId);

    public void deleteDrawingPoint(DrawingPoint drawingPoint);

}
