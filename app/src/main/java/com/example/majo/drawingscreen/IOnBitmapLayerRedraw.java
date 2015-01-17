package com.example.majo.drawingscreen;

/**
 * Created by majo on 15-Jan-15.
 * This is a callback inteface. The method is called when the bitmap layer nees to be redrawn (e.g. point is added/removed...)
 */
public interface IOnBitmapLayerRedraw {
    void onRedrawRequest();
}
