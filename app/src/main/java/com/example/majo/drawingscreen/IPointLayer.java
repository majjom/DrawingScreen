package com.example.majo.drawingscreen;

import com.example.majo.BusinessObjects.DrawingPoint;

import java.util.List;

/**
 * Created by majo on 15-Jan-15.
 */
public interface IPointLayer {

    void drawPoints(List<DrawingPoint> points, int color);
    void addPoint(DrawingPoint point, int color);
    void clear();






}
