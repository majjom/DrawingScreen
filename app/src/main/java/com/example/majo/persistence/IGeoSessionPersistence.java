package com.example.majo.persistence;

import com.example.majo.BusinessObjects.DrawingPoint;
import com.example.majo.BusinessObjects.GeoLocation;
import com.example.majo.BusinessObjects.GeoSession;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moravekm on 16-Dec-14.
 */
public interface IGeoSessionPersistence {
    public List<GeoSession> getAllGeoSessions(int mapId);

    public void addSession(int mapId, GeoSession session);

    public void addSessions(int mapId, List<GeoSession> sessions);

    public void deleteAllSessions(int mapId);

    public void deleteSession(GeoSession geoSession);
}
