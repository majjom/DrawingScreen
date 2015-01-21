package com.example.majo.drawingscreen;

/**
 * Created by majo on 15-Jan-15.
 */
public interface IDrawingScreenView {
    DrawingMode getDrawingMode();
    void setDrawingMode(DrawingMode drawingMode);

    int getStrokeWidth();
    void setStrokeWidth(int strokeWidth);

    void addLayer(IBitmapLayer layer);

    void setPointListener(IOnPointListener listener);
}
