package com.example.majo.persistence;

import com.example.majo.BusinessObjects.GeoLocation;
import com.example.majo.BusinessObjects.MappedPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by majo on 14-Dec-14.
 */
public interface IGeoLocationPersistence {

    public List<GeoLocation> getAllLocations(int geoSessionId);

    public int getLastLocationOrder(int geoSessionId);

    public void addLocation(int geoSessionId, GeoLocation location);

    public void addLocations(int geoSessionId, List<GeoLocation> locations);

    public void deleteLocation(GeoLocation geoLocation);

    public void deleteAllLocations(int geoSessionId);
}
