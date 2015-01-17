package com.example.majo.drawingscreen;

import com.example.majo.BusinessObjects.DrawingPoint;

import java.util.List;

/**
 * Created by majo on 15-Jan-15.
 */
public interface IPointsLayer {

    void setColor(int color);
    int getColor();


    List<LayerPoint> getPoints();
    LayerPoint getLastPoint();

    void addPoint(LayerPoint point);
    void addPoints(List<LayerPoint> points);


    void removePoint(LayerPoint point);
    void removePoints(List<LayerPoint> points);
    LayerPoint removeLastPoint();
    void removeAllPoints();






    void setHighlightColor(int color);
    int getHighlightColor();

    List<LayerPoint> getHighlightedPoints();
    void highlightPoint(LayerPoint point);
    void highlightPoints(List<LayerPoint> points);
    void undoHighlight();



}
