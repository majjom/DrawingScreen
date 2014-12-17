package com.example.majo.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.majo.BusinessObjects.GeoLocation;
import com.example.majo.BusinessObjects.MappedPoint;

import java.util.ArrayList;

/**
 * Created by majo on 14-Dec-14.
 */
public class GeoLocationPersistence extends DatabaseConnection implements IGeoLocationPersistence {

    public GeoLocationPersistence(Context context){
        super(context);
    }

    @Override
    public ArrayList<GeoLocation> getAllPoints(int geoSessionId) {
        ArrayList<GeoLocation> result = new ArrayList<>();

        String[] columns = new String[] { MyDatabaseHelper.COL_GL_ID, MyDatabaseHelper.COL_GL_LATITUDE, MyDatabaseHelper.COL_GL_LONGITUDE, MyDatabaseHelper.COL_GL_ALTITUDE, MyDatabaseHelper.COL_GL_RADIUS };
        Cursor cur = db.query(MyDatabaseHelper.TAB_GEO_LOCATIONS, columns, String.format("%s=?", MyDatabaseHelper.COL_GL_GEO_SESSION_ID), new String[] { String.valueOf(geoSessionId) }, null, null, MyDatabaseHelper.COL_GL_ID);


        while(cur.moveToNext()){
            int id = cur.getInt(cur.getColumnIndex(MyDatabaseHelper.COL_GL_ID));
            double geoLat = ConversionHelper.intToGeoLocation(cur.getInt(cur.getColumnIndex(MyDatabaseHelper.COL_GL_LATITUDE)));
            double geoLon = ConversionHelper.intToGeoLocation(cur.getInt(cur.getColumnIndex(MyDatabaseHelper.COL_GL_LONGITUDE)));
            double geoAlt = ConversionHelper.intToGeoLocation(cur.getInt(cur.getColumnIndex(MyDatabaseHelper.COL_GL_ALTITUDE)));
            double geoRad = ConversionHelper.intToGeoLocation(cur.getInt(cur.getColumnIndex(MyDatabaseHelper.COL_GL_RADIUS)));

            result.add(new GeoLocation(id, geoLat, geoLon, geoAlt, geoRad));
        }

        cur.close();
        return result;
    }

    @Override
    public void addPoints(int geoSessionId, ArrayList<GeoLocation> points) {
        for (GeoLocation point : points){
            ContentValues cv = new ContentValues(4);

            int geoLat = ConversionHelper.geoLocationToInt(point.latitude);
            int geoLon = ConversionHelper.geoLocationToInt(point.longitude);
            int geoAlt = ConversionHelper.geoLocationToInt(point.altitude);
            int geoRad = ConversionHelper.geoLocationToInt(point.radius);

            cv.put(MyDatabaseHelper.COL_GL_GEO_SESSION_ID, geoSessionId);
            cv.put(MyDatabaseHelper.COL_GL_LATITUDE, geoLat);
            cv.put(MyDatabaseHelper.COL_GL_LONGITUDE, geoLon);
            cv.put(MyDatabaseHelper.COL_GL_ALTITUDE, geoAlt);
            cv.put(MyDatabaseHelper.COL_GL_RADIUS, geoRad);

            db.insert(MyDatabaseHelper.TAB_GEO_LOCATIONS, null, cv);
        }
    }

    @Override
    public void deleteAllPoints(int geoSessionId) {
        db.delete(MyDatabaseHelper.TAB_GEO_LOCATIONS, null, null);
    }
}
