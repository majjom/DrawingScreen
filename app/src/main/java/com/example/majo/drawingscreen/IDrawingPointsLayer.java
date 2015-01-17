package com.example.majo.drawingscreen;

import com.example.majo.BusinessObjects.DrawingPoint;

/**
 * Created by majo on 17-Jan-15.
 */
public interface IDrawingPointsLayer extends IPointsLayer {
    void setRadius(int radius);
    int getRadius();

    // returns NULL if point gets rejected!!!
    LayerPoint addPoint(float x, float y);
}
