package com.example.majo.drawingscreen;

import android.location.Location;

import com.example.majo.BusinessObjects.DrawingPoint;
import com.example.majo.BusinessObjects.MappedPoint;

/**
 * Created by majo on 17-Jan-15.
 */
public class LayerPoint {

    // todo - do it with inheritance
    public DrawingPoint relatedDrawingPoint;
    public MappedPoint relatedMappedPoint;


    public float x;
    public float y;
    public float radius;

    public boolean isHighlighted;

    public double distanceToLocation = -1;
    public Location location;

    public LayerPoint(DrawingPoint relatedPoint){
        this.x = relatedPoint.x;
        this.y = relatedPoint.y;
        this.radius = relatedPoint.radius;

        this.isHighlighted = false;

        this.relatedDrawingPoint = relatedPoint;
        this.relatedMappedPoint = null;
    }

    public LayerPoint(MappedPoint relatedPoint){
        this.x = relatedPoint.drawingPoint.x;
        this.y = relatedPoint.drawingPoint.y;
        this.radius = relatedPoint.drawingPoint.radius;

        this.isHighlighted = false;

        this.relatedMappedPoint = relatedPoint;
        this.relatedDrawingPoint = null;
    }

    @Override
    public String toString() {
        String result = "";

        if (this.isHighlighted){
            result += "<H>";
        }


        if (this.relatedMappedPoint != null) {
            result += this.relatedMappedPoint.toString();
        }
        if (this.relatedDrawingPoint != null) {
            result += this.relatedDrawingPoint.toString();
        }


        return result;

    }
}
