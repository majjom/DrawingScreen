package com.example.majo.drawingscreen;

import com.example.majo.BusinessObjects.DrawingPoint;

/**
 * Created by majo on 17-Jan-15.
 * Interface used to listen for createing points on the screen
 */

public interface IOnPointListener {
    void highlightDrawingPoint(float vX, float vY);
    DrawingPoint addDrawingPoint(float vX, float vY);
    DrawingPoint removeLastDrawingPoint();
}
