package com.example.majo.BusinessObjects;

/**
 * Created by majo on 11-Dec-14.
 */
public class DrawingPoint extends PersistedObject {
    public float x;
    public float y;
    public float radius;

    public DrawingPoint(float x, float y, float radius){
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    @Override
    public String toString() {
        return String.format("x:%s y:%s r:%s", x, y, radius);
    }
}
