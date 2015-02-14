package com.example.majo.GoogleMap;

import android.location.Location;

import java.util.List;

/**
 * Created by majo on 10-Jan-15.
 */
public interface IGpsTrackerService {
    void startTracking(int geoSessionId, long minTime, float minDistance);
    void stopTracking();

    boolean isTracking();
    int getTrackedGeoSessionId();

    int getLocationsCount();
    List<Location> getLocations();
}
