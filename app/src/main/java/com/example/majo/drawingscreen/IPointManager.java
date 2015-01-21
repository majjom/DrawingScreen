package com.example.majo.drawingscreen;

import com.example.majo.BusinessObjects.DrawingPoint;

import java.util.List;

/**
 * Created by majo on 20-Jan-15.
 */
public interface IPointManager<T> {

    // data
    int getSchemaMapId();

    void refreshPointsFromDb();

    List<T> getPoints();
    List<T> getPointsFromDatabaseWithoutRefresh();

    int getRadius();
    void setRadius(int radius);
    T addPoint(float x, float y);

    void removePoint(T point);
    void removePoints(List<T> points);
    T removeLastPoint();
    void removeAllPoints();

    // highlighting
    List<T> getHighlightedPoints();
    void toggleHighlightPoint(T point);
    void toggleHighlightPoint(float x, float y);
    void clearHighlight();


    // color management
    void setHighlightColor(int color);
    int getHighlightColor();

    void setColor(int color);
    int getColor();

}
