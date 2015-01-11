package com.example.majo.GoogleMap;

import android.location.Location;

import com.example.majo.persistence.IGeoLocationPersistence;

import java.util.List;

/**
 * Created by majo on 10-Jan-15.
 */
public interface IGpsTrackerService {
    void startTracking(int geoSessionId, long minTime, float minDistance);
    void stopTracking();
    boolean isTracking();
    int getLocationsCount();
    List<Location> getLocations();
}
