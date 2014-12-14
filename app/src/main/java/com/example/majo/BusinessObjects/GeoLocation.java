package com.example.majo.BusinessObjects;

/**
 * Created by majo on 14-Dec-14.
 */
public class GeoLocation {
    public double latitude;
    public double longitude;
    public double altitude;

    public double radius;

    public GeoLocation(double latitude, double longitude, double altitude, double radius){
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.radius = radius;
    }
}
