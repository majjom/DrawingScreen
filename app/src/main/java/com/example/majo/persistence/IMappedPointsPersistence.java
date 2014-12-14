package com.example.majo.persistence;

import com.example.majo.BusinessObjects.DrawingPoint;
import com.example.majo.BusinessObjects.MappedPoint;

import java.util.ArrayList;

/**
 * Created by majo on 14-Dec-14.
 */
public interface IMappedPointsPersistence {
    public ArrayList<MappedPoint> getAllPoints(int mapId);

    public void addPoints(int mapId, ArrayList<MappedPoint> points);

}
