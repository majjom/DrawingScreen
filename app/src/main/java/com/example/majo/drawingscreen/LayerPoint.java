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
        this.x = relatedDrawingPoint.x;
        this.y = relatedDrawingPoint.y;
        this.radius = relatedDrawingPoint.radius;

        this.isHighlighted = false;

        this.relatedMappedPoint = relatedPoint;
        this.relatedDrawingPoint = null;
    }

    @Override
    public String toString() {
        String result = "";
        if (this.relatedMappedPoint != null) {
            result += String.format("id:%s", this.relatedMappedPoint.id);
        }
        if (this.relatedDrawingPoint != null) {
            result += String.format("id:%s", this.relatedDrawingPoint.id);
        }
        result += String.format(" x:%s y:%s r:%s", this.x, this.y, this.radius);
        if (this.isHighlighted){
            result += "HIGHLIGHTED";
        }
        return result;

    }
}
