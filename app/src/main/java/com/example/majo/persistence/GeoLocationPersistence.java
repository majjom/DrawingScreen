package com.example.majo.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.majo.BusinessObjects.GeoLocation;
import com.example.majo.BusinessObjects.MappedPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by majo on 14-Dec-14.
 */
public class GeoLocationPersistence implements IGeoLocationPersistence {

    IDatabaseConnection connection;

    public GeoLocationPersistence(IDatabaseConnection connection){
        this.connection = connection;
    }

    @Override
    public List<GeoLocation> getAllLocations(int geoSessionId) {
        List<GeoLocation> result = new ArrayList<>();

        String[] columns = new String[] { MyDatabaseHelper.COL_GL_ID, MyDatabaseHelper.COL_GL_LATITUDE, MyDatabaseHelper.COL_GL_LONGITUDE, MyDatabaseHelper.COL_GL_ALTITUDE, MyDatabaseHelper.COL_GL_RADIUS };
        Cursor cur = connection.getDb().query(MyDatabaseHelper.TAB_GEO_LOCATIONS, columns, String.format("%s=?", MyDatabaseHelper.COL_GL_GEO_SESSION_ID), new String[] { String.valueOf(geoSessionId) }, null, null, MyDatabaseHelper.COL_GL_ORDERING);


        while(cur.moveToNext()){
            int id = cur.getInt(cur.getColumnIndex(MyDatabaseHelper.COL_GL_ID));
            double geoLat = ConversionHelper.intToGeoLocation(cur.getInt(cur.getColumnIndex(MyDatabaseHelper.COL_GL_LATITUDE)));
            double geoLon = ConversionHelper.intToGeoLocation(cur.getInt(cur.getColumnIndex(MyDatabaseHelper.COL_GL_LONGITUDE)));
            double geoAlt = ConversionHelper.intToGeoLocation(cur.getInt(cur.getColumnIndex(MyDatabaseHelper.COL_GL_ALTITUDE)));
            double geoRad = ConversionHelper.intToGeoLocation(cur.getInt(cur.getColumnIndex(MyDatabaseHelper.COL_GL_RADIUS)));

            GeoLocation gl = new GeoLocation(geoLat, geoLon, geoAlt, geoRad);
            gl.id = id;
            result.add(gl);
        }

        cur.close();
        return result;
    }

    @Override
    public int getLastLocationOrder(int geoSessionId) {
        // todo finish this
        return 0;
    }


    @Override
    public void addLocation(int geoSessionId, GeoLocation location) {
        ContentValues cv = new ContentValues(6);

        int geoLat = ConversionHelper.geoLocationToInt(location.latitude);
        int geoLon = ConversionHelper.geoLocationToInt(location.longitude);
        int geoAlt = ConversionHelper.geoLocationToInt(location.altitude);
        int geoRad = ConversionHelper.geoLocationToInt(location.radius);

        cv.put(MyDatabaseHelper.COL_GL_GEO_SESSION_ID, geoSessionId);
        cv.put(MyDatabaseHelper.COL_GL_LATITUDE, geoLat);
        cv.put(MyDatabaseHelper.COL_GL_LONGITUDE, geoLon);
        cv.put(MyDatabaseHelper.COL_GL_ALTITUDE, geoAlt);
        cv.put(MyDatabaseHelper.COL_GL_RADIUS, geoRad);

        cv.put(MyDatabaseHelper.COL_MP_ORDERING, getLastLocationOrder(geoSessionId) + 1);

        long id = connection.getDb().insert(MyDatabaseHelper.TAB_GEO_LOCATIONS, null, cv);
        location.id = (int)id;
    }

    @Override
    public void addLocations(int geoSessionId, List<GeoLocation> locations) {
        for (GeoLocation location : locations){
            addLocation(geoSessionId, location);
        }
    }

    @Override
    public void deleteAllLocations(int geoSessionId) {
        connection.getDb().delete(MyDatabaseHelper.TAB_GEO_LOCATIONS, String.format("%s=?", MyDatabaseHelper.COL_GL_GEO_SESSION_ID), new String[] { String.valueOf(geoSessionId) });
    }

    @Override
    public void deleteLocation(GeoLocation geoLocation) {
        connection.getDb().delete(MyDatabaseHelper.TAB_GEO_LOCATIONS, String.format("%s=?", MyDatabaseHelper.COL_GL_ID), new String[] { String.valueOf(geoLocation.id) });
    }
}
