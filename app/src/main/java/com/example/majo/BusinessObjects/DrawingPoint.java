package com.example.majo.BusinessObjects;

/**
 * Created by majo on 11-Dec-14.
 */
public class DrawingPoint extends PersistedObject {
    public float x;
    public float y;
    public float radius;

    public int order;


    // not saved to DB
    public boolean isHighlighted = false;

    public DrawingPoint(float x, float y, float radius){
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.order = -1;
    }

    @Override
    public String toString() {
        return String.format("id:%s x:%s y:%s r:%s %s", id, x, y, radius, isHighlighted ? "HIGH" : "");
    }
}
