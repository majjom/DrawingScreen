package com.example.majo.drawingscreen;

import android.content.Context;

import com.example.majo.BusinessObjects.DrawingPoint;

/**
 * Created by majo on 17-Jan-15.
 */
public class DrawingPointsLayer extends PointsLayer implements IDrawingPointsLayer {
    private int radius = 10;

    public DrawingPointsLayer(Context context, String assetName) {
        super(context, assetName);
    }

    public DrawingPointsLayer(int width, int height) {
        super(width, height);
    }

    @Override
    public void setRadius(int radius) {
        this.radius = radius;
    }

    @Override
    public int getRadius() {
        return this.radius;
    }

    @Override
    public LayerPoint addPoint(float x, float y) {
        // rejecting point if it is the same as the last one!!
        LayerPoint lp = getLastPoint();
        if (lp!= null) {
            if ((Math.round(lp.x) == Math.round(x)) && (Math.round(lp.y) == Math.round(y))) {
                return null;
            }
        }

        LayerPoint dp = new LayerPoint(new DrawingPoint(x, y, this.radius));
        addPoint(dp);
        return dp;
    }
}
