package com.example.majo.persistence;

import com.example.majo.BusinessObjects.GeoLocation;
import com.example.majo.BusinessObjects.MappedPoint;

import java.util.ArrayList;

/**
 * Created by majo on 14-Dec-14.
 */
public interface IGeoLocationPersistence {
    public ArrayList<GeoLocation> getAllPoints(int geoSessionId);

    public void addPoints(int geoSessionId, ArrayList<GeoLocation> points);

    public void deleteAllPoints(int geoSessionId);
}
