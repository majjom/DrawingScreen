package com.example.majo.BusinessObjects;

/**
 * Created by majo on 14-Dec-14.
 */
public class GeoLocation extends PersistedObject {
    public double latitude;
    public double longitude;
    public double altitude;

    public double radius;

    public GeoLocation(int id, double latitude, double longitude, double altitude, double radius){
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.radius = radius;
    }
}
