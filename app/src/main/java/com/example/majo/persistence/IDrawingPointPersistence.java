package com.example.majo.persistence;

import com.example.majo.BusinessObjects.DrawingPoint;
import com.example.majo.BusinessObjects.GeoSession;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by majo on 11-Dec-14.
 */
public interface IDrawingPointPersistence {
    public List<DrawingPoint> getAllPoints(int mapId);

    public void addPoint(int mapId, DrawingPoint point);

    public void addPoints(int mapId, List<DrawingPoint> points);

    public void deleteAllPoints(int mapId);

    public void deleteDrawingPoint(DrawingPoint drawingPoint);

}
