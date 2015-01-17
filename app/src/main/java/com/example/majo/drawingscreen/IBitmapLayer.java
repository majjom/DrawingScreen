package com.example.majo.drawingscreen;

import android.graphics.Bitmap;

/**
 * Created by majo on 17-Jan-15.
 */
public interface IBitmapLayer {
    void setVisibility(boolean isVisible);
    boolean isVisible();

    Bitmap getBitmap();

    void attachToRedrawListener(IOnBitmapLayerRedraw listener);
}
