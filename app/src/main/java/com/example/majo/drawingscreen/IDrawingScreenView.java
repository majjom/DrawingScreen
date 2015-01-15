package com.example.majo.drawingscreen;

import com.example.majo.BusinessObjects.DrawingPoint;

import java.util.List;

/**
 * Created by majo on 15-Jan-15.
 */
public interface IDrawingScreenView {

    /*******************************/
    /*for the drawing points layer*/
    /*******************************/

    /**
     * Figures out the drawing mode.
     * @return TRUE means that drawing is enabled, FALSE that dragging is enabled = finger does not leave points.
     */
    boolean isDrawingMode();
    void setDrawingMode(boolean isDrawingMode);

    /* how far I paint points when drawing */
    int getStrokeWidth();
    void setStrokeWidth(int strokeWidth);

    /* general radius for points */
    void setRadius(int radius);
    int getRadius();

    void setColor(int color);
    int getColor();

    List<DrawingPoint> getPoints();
    void addPoint(DrawingPoint point);
    void addPoints(List<DrawingPoint> points);
    void removePoint(DrawingPoint point);
    void removeAllPoints();

    /*visibility of the layer*/
    void show();
    void hide();

    /*******************************/
    /*for the mapped points layer*/
    /*******************************/
    void loadMappedPoints(List<DrawingPoint> points);
    void showMappedPoints(int color, int radius);
    void showMappedPoints();
    void hideMappedPoints();
}
