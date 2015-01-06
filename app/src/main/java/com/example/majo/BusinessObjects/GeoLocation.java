package com.example.majo.BusinessObjects;

/**
 * Created by majo on 14-Dec-14.
 */
public class GeoLocation extends PersistedObject {
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

    @Override
    public String toString() {
        return String.format("lat:%s lon:%s alt:%s rad:%s", latitude, longitude, altitude, radius);
    }
}
