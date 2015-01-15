package com.example.majo.drawingscreen;

import android.graphics.Bitmap;

import com.example.majo.BusinessObjects.DrawingPoint;

import java.util.List;

/**
 * Created by majo on 15-Jan-15.
 */
public interface IBitmapLayer {
    void setVisibility(boolean isVisible);
    boolean isVisible();

    void setColor(int color);
    int getColor();

    void setRadius(int radius);
    int getRadius();

    Bitmap getBitmap();

    List<DrawingPoint> getPoints();
    void removeAllPoints();
    void addPoint(DrawingPoint point);
    void addPoints(List<DrawingPoint> points);
    void removePoint(DrawingPoint point);
    void removeLastPoint();
    void removeLastPoints(int count);

}
