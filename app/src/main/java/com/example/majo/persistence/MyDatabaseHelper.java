package com.example.majo.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by majo on 13-Dec-14.
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "mydb";
    private static final int DB_VERSION = 6;

    public static final String TAB_DRAWING_POINTS = "drawing_points";
    public static final String COL_DP_ID = "id";
    public static final String COL_DP_X = "x";
    public static final String COL_DP_Y = "y";
    public static final String COL_DP_RADIUS = "radius";

    public static final String TAB_MAPPED_POINTS = "mapped_points";
    public static final String COL_MP_ID = "id";
    public static final String COL_MP_DRAWING_X = "drawing_x";
    public static final String COL_MP_DRAWING_Y = "drawing_y";
    public static final String COL_MP_DRAWING_RADIUS = "drawing_radius";
    public static final String COL_MP_GEO_LAT = "geo_latitude";
    public static final String COL_MP_GEO_LON = "geo_longitude";
    public static final String COL_MP_GEO_ALT = "geo_altitude";
    public static final String COL_MP_GEO_RAD = "geo_radius";

    public static final String TAB_GEO_LOCATIONS = "geo_locations";
    public static final String COL_GL_ID = "id";
    public static final String COL_GL_LATITUDE = "latitude";
    public static final String COL_GL_LONGITUDE = "longitude";
    public static final String COL_GL_ALTITUDE = "altitude";
    public static final String COL_GL_RADIUS = "radius";
    public static final String COL_GL_GEO_SESSION_ID = "GeoSessionId";

    public static final String TAB_GEO_SESSIONS = "geo_sessions";
    public static final String COL_GS_ID = "id";
    public static final String COL_GS_NAME = "name";
    public static final String COL_GS_DATE_CREATED = "date_created";

    public MyDatabaseHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // table Geo sessions
        db.execSQL(String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s INTEGER);)"
                , TAB_GEO_SESSIONS, COL_GS_ID, COL_GS_NAME, COL_GS_DATE_CREATED));

        // table Drawing points
        db.execSQL(String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER, %s INTEGER, %s INTEGER);)"
                , TAB_DRAWING_POINTS, COL_DP_ID, COL_DP_X, COL_DP_Y, COL_DP_RADIUS));
        // table Mapped points
        db.execSQL(String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER);)"
                , TAB_MAPPED_POINTS, COL_MP_ID, COL_MP_DRAWING_X, COL_MP_DRAWING_Y, COL_MP_DRAWING_RADIUS, COL_MP_GEO_LAT, COL_MP_GEO_LON, COL_MP_GEO_ALT, COL_MP_GEO_RAD));
        // table Geo locations
        db.execSQL(String.format("CREATE TABLE %s (id INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER, FOREIGN KEY(%s) REFERENCES %s(%s));)"
                , TAB_GEO_LOCATIONS, COL_GL_ID, COL_GL_GEO_SESSION_ID, COL_GL_LATITUDE, COL_GL_LONGITUDE, COL_GL_ALTITUDE, COL_GL_RADIUS
                /*FK*/, COL_GL_GEO_SESSION_ID, TAB_GEO_LOCATIONS, COL_GL_ID));


        // put initial values here

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", TAB_DRAWING_POINTS));
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", TAB_MAPPED_POINTS));
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", TAB_GEO_LOCATIONS));
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", TAB_GEO_SESSIONS));
        onCreate(db);
    }
}
