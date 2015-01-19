package com.example.majo.BusinessObjects;

/**
 * Created by majo on 14-Dec-14.
 */
public class MappedPoint extends PersistedObject {
    public float drawingX;
    public float drawingY;
    public float drawingRadius;

    public double geoLatitude;
    public double geoLongitude;
    public double geoAltitude;
    public double geoRadius;

    public int order;

    public MappedPoint(float drawingX, float drawingY, float drawingRadius
                       , double geoLatitude, double geoLongitude, double geoAltitude, double geoRadius){
        this.drawingX = drawingX;
        this.drawingY = drawingY;
        this.drawingRadius = drawingRadius;

        this.geoLatitude = geoLatitude;
        this.geoLongitude = geoLongitude;
        this.geoAltitude = geoAltitude;
        this.geoRadius = geoRadius;

        this.order = -1;
    }

    @Override
    public String toString() {
        return String.format("MPid:%s x:%s y:%s r:%s lat:%s lon:%s alt:%s r:%s", String.valueOf(this.id), String.valueOf(drawingX), String.valueOf(drawingY), String.valueOf(drawingRadius),
                String.valueOf(geoLatitude), String.valueOf(geoLatitude), String.valueOf(geoAltitude), String.valueOf(geoRadius));
    }
}
