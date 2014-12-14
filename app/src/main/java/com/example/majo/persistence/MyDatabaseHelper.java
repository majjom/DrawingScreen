package com.example.majo.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by majo on 13-Dec-14.
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "mydb";
    private static final int DB_VERSION = 4;

    public static final String TAB_DRAWING_POINTS = "DrawingPoints";
    public static final String COL_DP_X = "X";
    public static final String COL_DP_Y = "Y";
    public static final String COL_DP_RADIUS = "Radius";

    public static final String TAB_MAPPED_POINTS = "MappedPoints";
    public static final String COL_MP_DRAWING_X = "DrawingX";
    public static final String COL_MP_DRAWING_Y = "DrawingY";
    public static final String COL_MP_DRAWING_RADIUS = "DrawingRadius";
    public static final String COL_MP_GEO_LAT = "GeoLat";
    public static final String COL_MP_GEO_LON = "GeoLon";
    public static final String COL_MP_GEO_ALT = "GeoAlt";
    public static final String COL_MP_GEO_RAD = "GeoRad";

    public static final String TAB_GEO_LOCATIONS = "GeoLocations";
    public static final String COL_GL_LATITUDE = "Latitude";
    public static final String COL_GL_LONGITUDE = "Longitude";
    public static final String COL_GL_ALTITUDE = "Altitude";
    public static final String COL_GL_RADIUS = "Radius";

    public MyDatabaseHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // table Drawing points
        db.execSQL(String.format("CREATE TABLE %s (_id INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER, %s INTEGER, %s INTEGER);)"
                , TAB_DRAWING_POINTS, COL_DP_X, COL_DP_Y, COL_DP_RADIUS));
        // table Mapped points
        db.execSQL(String.format("CREATE TABLE %s (_id INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER);)"
                , TAB_MAPPED_POINTS, COL_MP_DRAWING_X, COL_MP_DRAWING_Y, COL_MP_DRAWING_RADIUS, COL_MP_GEO_LAT, COL_MP_GEO_LON, COL_MP_GEO_ALT, COL_MP_GEO_RAD));
        // table Geo locations
        db.execSQL(String.format("CREATE TABLE %s (_id INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER);)"
                , TAB_GEO_LOCATIONS, COL_GL_LATITUDE, COL_GL_LONGITUDE, COL_GL_ALTITUDE, COL_GL_RADIUS));

        // put initial values here

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", TAB_DRAWING_POINTS));
        onCreate(db);
    }
}
