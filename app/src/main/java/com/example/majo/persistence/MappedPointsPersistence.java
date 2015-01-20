package com.example.majo.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.majo.BusinessObjects.DrawingPoint;
import com.example.majo.BusinessObjects.GeoLocation;
import com.example.majo.BusinessObjects.MappedPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by majo on 14-Dec-14.
 */
public class MappedPointsPersistence implements IMappedPointsPersistence {

    IDatabaseConnection connection;

    public MappedPointsPersistence(IDatabaseConnection connection){
        this.connection = connection;
    }

    @Override
    public ArrayList<MappedPoint> getAllPoints(int mapId) {
        ArrayList<MappedPoint> result = new ArrayList<>();

        String[] columns = new String[] { MyDatabaseHelper.COL_MP_ID, MyDatabaseHelper.COL_MP_DRAWING_X, MyDatabaseHelper.COL_MP_DRAWING_Y, MyDatabaseHelper.COL_MP_DRAWING_RADIUS
                , MyDatabaseHelper.COL_MP_GEO_LAT, MyDatabaseHelper.COL_MP_GEO_LON, MyDatabaseHelper.COL_MP_GEO_ALT, MyDatabaseHelper.COL_MP_GEO_RAD, MyDatabaseHelper.COL_MP_ORDERING};
        Cursor cur = connection.getDb().query(MyDatabaseHelper.TAB_MAPPED_POINTS, columns, String.format("%s=?", MyDatabaseHelper.COL_MP_MAP_ID), new String[] { String.valueOf(mapId) }, null, null, MyDatabaseHelper.COL_MP_ORDERING);

        while(cur.moveToNext()){
            int id = cur.getInt(cur.getColumnIndex(MyDatabaseHelper.COL_MP_ID));

            float drawingX = ConversionHelper.intToDrawingPoint(cur.getInt(cur.getColumnIndex(MyDatabaseHelper.COL_MP_DRAWING_X)));
            float drawingY = ConversionHelper.intToDrawingPoint(cur.getInt(cur.getColumnIndex(MyDatabaseHelper.COL_MP_DRAWING_Y)));
            float drawingRadius = ConversionHelper.intToDrawingPoint(cur.getInt(cur.getColumnIndex(MyDatabaseHelper.COL_MP_DRAWING_RADIUS)));

            double geoLat = ConversionHelper.intToGeoLocation(cur.getInt(cur.getColumnIndex(MyDatabaseHelper.COL_MP_GEO_LAT)));
            double geoLon = ConversionHelper.intToGeoLocation(cur.getInt(cur.getColumnIndex(MyDatabaseHelper.COL_MP_GEO_LON)));
            double geoAlt = ConversionHelper.intToGeoLocation(cur.getInt(cur.getColumnIndex(MyDatabaseHelper.COL_MP_GEO_ALT)));
            double geoRad = ConversionHelper.intToGeoLocation(cur.getInt(cur.getColumnIndex(MyDatabaseHelper.COL_MP_GEO_RAD)));

            int order = cur.getInt(cur.getColumnIndex(MyDatabaseHelper.COL_MP_ORDERING));

            MappedPoint mp = new MappedPoint(new DrawingPoint(drawingX, drawingY, drawingRadius), new GeoLocation(geoLat, geoLon, geoAlt, geoRad));
            mp.id = id;
            mp.order = order;

            result.add(mp);
        }

        cur.close();
        return result;
    }

    private int getMaxMappedPointOrder(int mapId){
        String[] columns = new String[] { String.format("MAX(%s)", MyDatabaseHelper.COL_MP_ORDERING) };
        Cursor cur = connection.getDb().query(MyDatabaseHelper.TAB_MAPPED_POINTS, columns, String.format("%s=?", MyDatabaseHelper.COL_MP_MAP_ID), new String[] { String.valueOf(mapId) }, null, null, null);
        int result = 0;
        while(cur.moveToNext()){
            result = cur.getInt(0);
        }
        return result;
    }

    @Override
    public void addPoint(int mapId, MappedPoint point) {
        addPoint(mapId, point, getMaxMappedPointOrder(mapId) + 1);
    }

    private void addPoint(int mapId, MappedPoint point, int order) {

        ContentValues cv = new ContentValues(9);

        int drawingX = ConversionHelper.drawingPointToInt(point.drawingPoint.x);
        int drawingY = ConversionHelper.drawingPointToInt(point.drawingPoint.y);
        int drawingRadius = ConversionHelper.drawingPointToInt(point.drawingPoint.radius);

        int geoLat = ConversionHelper.geoLocationToInt(point.geoLocation.latitude);
        int geoLon = ConversionHelper.geoLocationToInt(point.geoLocation.longitude);
        int geoAlt = ConversionHelper.geoLocationToInt(point.geoLocation.altitude);
        int geoRad = ConversionHelper.geoLocationToInt(point.geoLocation.radius);

        cv.put(MyDatabaseHelper.COL_MP_MAP_ID, mapId);
        cv.put(MyDatabaseHelper.COL_MP_DRAWING_X, drawingX);
        cv.put(MyDatabaseHelper.COL_MP_DRAWING_Y, drawingY);
        cv.put(MyDatabaseHelper.COL_MP_DRAWING_RADIUS, drawingRadius);

        cv.put(MyDatabaseHelper.COL_MP_GEO_LAT, geoLat);
        cv.put(MyDatabaseHelper.COL_MP_GEO_LON, geoLon);
        cv.put(MyDatabaseHelper.COL_MP_GEO_ALT, geoAlt);
        cv.put(MyDatabaseHelper.COL_MP_GEO_RAD, geoRad);

        cv.put(MyDatabaseHelper.COL_MP_ORDERING, order);

        long id = connection.getDb().insert(MyDatabaseHelper.TAB_MAPPED_POINTS, null, cv);
        point.id = (int)id;

    }

    @Override
    public void addPoints(int mapId, List<MappedPoint> points) {
        int order = getMaxMappedPointOrder(mapId) + 1;
        for (MappedPoint point : points){
            addPoint(mapId, point, order);
            order++;
        }
    }

    @Override
    public void deleteAllPoints(int mapId) {
        connection.getDb().delete(MyDatabaseHelper.TAB_MAPPED_POINTS, String.format("%s=?", MyDatabaseHelper.COL_MP_MAP_ID), new String[] { String.valueOf(mapId) });
    }

    @Override
    public void deleteMappedPoint(MappedPoint mappedPoint){
        connection.getDb().delete(MyDatabaseHelper.TAB_MAPPED_POINTS, String.format("%s=?", MyDatabaseHelper.COL_MP_ID), new String[] { String.valueOf(mappedPoint.id) });
    }
}
