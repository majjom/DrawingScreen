package com.example.majo.persistenceLocalDatabase;

import com.example.majo.BusinessObjects.MappedPoint;

import java.util.List;

/**
 * Created by majo on 14-Dec-14.
 */
public interface IMappedPointsPersistence {
    public List<MappedPoint> getAllPoints(int mapId);

    public void addPoint(int mapId, MappedPoint point);

    public void addPoints(int mapId, List<MappedPoint> points);

    public void deleteAllPoints(int mapId);

    public void deleteMappedPoint(MappedPoint mappedPoint);

}
