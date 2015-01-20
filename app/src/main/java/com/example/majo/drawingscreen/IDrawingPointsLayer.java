package com.example.majo.drawingscreen;

/**
 * Created by majo on 17-Jan-15.
 */
public interface IDrawingPointsLayer extends IPointLayer {


    // returns NULL if point gets rejected!!!
    LayerPoint addPoint(float x, float y);
}
