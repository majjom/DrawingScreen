package com.example.majo.BusinessObjects;

/**
 * Created by majo on 14-Dec-14.
 */
public class MappedPoint extends PersistedObject {
    public DrawingPoint drawingPoint;
    public GeoLocation geoLocation;

    public int order;

    public MappedPoint(DrawingPoint drawingPoint, GeoLocation geoLocation){
        this.drawingPoint = drawingPoint;
        this.geoLocation = geoLocation;

        this.order = -1;
    }

    // not stored in DB
    public boolean isHighlighted = false;

    @Override
    public String toString() {
        return String.format("MPid:%s dp:%s mp:%s", String.valueOf(this.id), this.drawingPoint.toString(), this.geoLocation.toString());
    }
}
