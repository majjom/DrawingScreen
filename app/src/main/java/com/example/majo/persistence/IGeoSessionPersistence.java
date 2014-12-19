package com.example.majo.persistence;

import com.example.majo.BusinessObjects.DrawingPoint;
import com.example.majo.BusinessObjects.GeoLocation;
import com.example.majo.BusinessObjects.GeoSession;

import java.util.ArrayList;

/**
 * Created by moravekm on 16-Dec-14.
 */
public interface IGeoSessionPersistence {
    public ArrayList<GeoSession> getAllGeoSessions(int mapId);

    public void addSessions(int mapId, ArrayList<GeoSession> sessions);

    public void deleteAllSessions(int mapId);

    public void deleteSession(GeoSession geoSession);
}
