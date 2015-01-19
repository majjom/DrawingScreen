package com.example.majo.drawingscreen;

import com.example.majo.BusinessObjects.DrawingPoint;
import com.example.majo.BusinessObjects.MappedPoint;

/**
 * Created by majo on 17-Jan-15.
 */
public class LayerPoint {

    // todo - do it with inheritance
    DrawingPoint relatedDrawingPoint;
    MappedPoint relatedMappedPoint;


    public float x;
    public float y;
    public float radius;
    public boolean isHighlighted;

    public LayerPoint(DrawingPoint relatedPoint){
        this.x = relatedPoint.x;
        this.y = relatedPoint.y;
        this.radius = relatedPoint.radius;

        this.isHighlighted = false;

        this.relatedDrawingPoint = relatedPoint;
        this.relatedMappedPoint = null;
    }

    public LayerPoint(MappedPoint relatedPoint){
        this.x = relatedPoint.drawingX;
        this.y = relatedPoint.drawingY;
        this.radius = relatedPoint.drawingRadius;

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
