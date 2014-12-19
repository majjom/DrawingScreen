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

    public MappedPoint(float drawingX, float drawingY, float drawingRadius
                       , double geoLatitude, double geoLongitude, double geoAltitude, double geoRadius){
        this.drawingX = drawingX;
        this.drawingY = drawingY;
        this.drawingRadius = drawingRadius;

        this.geoLatitude = geoLatitude;
        this.geoLongitude = geoLongitude;
        this.geoAltitude = geoAltitude;
        this.geoRadius = geoRadius;
    }
}
