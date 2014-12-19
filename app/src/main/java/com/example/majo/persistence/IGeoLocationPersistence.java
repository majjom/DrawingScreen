package com.example.majo.persistence;

import com.example.majo.BusinessObjects.GeoLocation;
import com.example.majo.BusinessObjects.MappedPoint;

import java.util.ArrayList;

/**
 * Created by majo on 14-Dec-14.
 */
public interface IGeoLocationPersistence {
    public ArrayList<GeoLocation> getAllLocations(int geoSessionId);

    public void addLocations(int geoSessionId, ArrayList<GeoLocation> locations);

    public void deleteLocation(GeoLocation geoLocation);

    public void deleteAllLocations(int geoSessionId);
}
