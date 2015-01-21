package com.example.majo.drawingscreen;

import com.example.majo.BusinessObjects.DrawingPoint;

import java.util.List;

/**
 * Created by majo on 20-Jan-15.
 */
public interface IDrawingPointManager {

    // data
    int getSchemaMapId();

    void refreshPointsFromDb();

    List<DrawingPoint> getPoints();

    int getRadius();
    void setRadius(int radius);
    DrawingPoint addPoint(float x, float y);

    void removePoint(DrawingPoint point);
    void removePoints(List<DrawingPoint> points);
    DrawingPoint removeLastPoint();
    void removeAllPoints();

    // highlighting
    List<DrawingPoint> getHighlightedPoints();
    void toggleHighlightPoint(DrawingPoint point);
    void toggleHighlightPoint(float x, float y);
    void clearHighlight();


    // color management
    void setHighlightColor(int color);
    int getHighlightColor();

    void setColor(int color);
    int getColor();

}
