package com.example.majo.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.majo.BusinessObjects.MappedPoint;

import java.util.ArrayList;

/**
 * Created by majo on 14-Dec-14.
 */
public class MappedPointsPersistence extends DatabaseConnection implements IMappedPointsPersistence {

    public MappedPointsPersistence(Context context){
        super(context);
    }

    @Override
    public ArrayList<MappedPoint> getAllPoints(int mapId) {
        ArrayList<MappedPoint> result = new ArrayList<>();

        String[] columns = new String[] { "_id", MyDatabaseHelper.COL_MP_DRAWING_X, MyDatabaseHelper.COL_MP_DRAWING_Y, MyDatabaseHelper.COL_MP_DRAWING_RADIUS
                , MyDatabaseHelper.COL_MP_GEO_LAT, MyDatabaseHelper.COL_MP_GEO_LON, MyDatabaseHelper.COL_MP_GEO_ALT, MyDatabaseHelper.COL_MP_GEO_RAD};
        Cursor cur = db.query(MyDatabaseHelper.TAB_MAPPED_POINTS, columns, null, null, null, null, "_id");

        while(cur.moveToNext()){
            float drawingX = ConversionHelper.intToDrawingPoint(cur.getInt(cur.getColumnIndex(MyDatabaseHelper.COL_MP_DRAWING_X)));
            float drawingY = ConversionHelper.intToDrawingPoint(cur.getInt(cur.getColumnIndex(MyDatabaseHelper.COL_MP_DRAWING_Y)));
            float drawingRadius = ConversionHelper.intToDrawingPoint(cur.getInt(cur.getColumnIndex(MyDatabaseHelper.COL_MP_DRAWING_RADIUS)));

            double geoLat = ConversionHelper.intToGeoLocation(cur.getInt(cur.getColumnIndex(MyDatabaseHelper.COL_MP_GEO_LAT)));
            double geoLon = ConversionHelper.intToGeoLocation(cur.getInt(cur.getColumnIndex(MyDatabaseHelper.COL_MP_GEO_LON)));
            double geoAlt = ConversionHelper.intToGeoLocation(cur.getInt(cur.getColumnIndex(MyDatabaseHelper.COL_MP_GEO_ALT)));
            double geoRad = ConversionHelper.intToGeoLocation(cur.getInt(cur.getColumnIndex(MyDatabaseHelper.COL_MP_GEO_RAD)));

            result.add(new MappedPoint(drawingX, drawingY, drawingRadius, geoLat, geoLon, geoAlt, geoRad));
        }

        cur.close();
        return result;
    }

    @Override
    public void addPoints(int mapId, ArrayList<MappedPoint> points) {
        for (MappedPoint point : points){
            ContentValues cv = new ContentValues(7);

            int drawingX = ConversionHelper.drawingPointToInt(point.drawingX);
            int drawingY = ConversionHelper.drawingPointToInt(point.drawingY);
            int drawingRadius = ConversionHelper.drawingPointToInt(point.drawingRadius);

            int geoLat = ConversionHelper.geoLocationToInt(point.geoLatitude);
            int geoLon = ConversionHelper.geoLocationToInt(point.geoLongitude);
            int geoAlt = ConversionHelper.geoLocationToInt(point.geoAltitude);
            int geoRad = ConversionHelper.geoLocationToInt(point.geoRadius);

            cv.put(MyDatabaseHelper.COL_MP_DRAWING_X, drawingX);
            cv.put(MyDatabaseHelper.COL_MP_DRAWING_Y, drawingY);
            cv.put(MyDatabaseHelper.COL_MP_DRAWING_RADIUS, drawingRadius);

            cv.put(MyDatabaseHelper.COL_MP_GEO_LAT, geoLat);
            cv.put(MyDatabaseHelper.COL_MP_GEO_LON, geoLon);
            cv.put(MyDatabaseHelper.COL_MP_GEO_ALT, geoAlt);
            cv.put(MyDatabaseHelper.COL_MP_GEO_RAD, geoRad);

            db.insert(MyDatabaseHelper.TAB_MAPPED_POINTS, null, cv);
        }
    }

    @Override
    public void deleteAllPoints(int mapId) {
        db.delete(MyDatabaseHelper.TAB_MAPPED_POINTS, null, null);
    }
}
