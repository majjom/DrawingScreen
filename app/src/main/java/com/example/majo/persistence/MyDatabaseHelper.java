package com.example.majo.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by majo on 13-Dec-14.
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "mydb";
    private static final int DB_VERSION = 8;

    public static final String TAB_DRAWING_POINTS = "drawing_points";
    public static final String COL_DP_ID = "id";
    public static final String COL_DP_X = "x";
    public static final String COL_DP_Y = "y";
    public static final String COL_DP_RADIUS = "radius";
    public static final String COL_DP_MAP_ID = "map_id";

    public static final String TAB_MAPPED_POINTS = "mapped_points";
    public static final String COL_MP_ID = "id";
    public static final String COL_MP_DRAWING_X = "drawing_x";
    public static final String COL_MP_DRAWING_Y = "drawing_y";
    public static final String COL_MP_DRAWING_RADIUS = "drawing_radius";
    public static final String COL_MP_GEO_LAT = "geo_latitude";
    public static final String COL_MP_GEO_LON = "geo_longitude";
    public static final String COL_MP_GEO_ALT = "geo_altitude";
    public static final String COL_MP_GEO_RAD = "geo_radius";
    public static final String COL_MP_MAP_ID = "map_id";

    public static final String TAB_GEO_LOCATIONS = "geo_locations";
    public static final String COL_GL_ID = "id";
    public static final String COL_GL_LATITUDE = "latitude";
    public static final String COL_GL_LONGITUDE = "longitude";
    public static final String COL_GL_ALTITUDE = "altitude";
    public static final String COL_GL_RADIUS = "radius";
    public static final String COL_GL_GEO_SESSION_ID = "geo_session_id";

    public static final String TAB_GEO_SESSIONS = "geo_sessions";
    public static final String COL_GS_ID = "id";
    public static final String COL_GS_NAME = "name";
    public static final String COL_GS_DATE_CREATED = "date_created";
    public static final String COL_GS_MAP_ID = "map_id";

    public static final String TAB_MAPS = "maps";
    public static final String COL_MA_ID = "id";
    public static final String COL_MA_NAME = "name";
    public static final String COL_MA_DATE_CREATED = "date_created";

    public MyDatabaseHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String tag = "db_create";
        // ********* LVL 01 **************
        // table SchemaMap
        String createStmt = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s INTEGER);)"
                , TAB_MAPS, COL_MA_ID, COL_MA_NAME, COL_MA_DATE_CREATED);
        Log.d(tag, createStmt);
        db.execSQL(createStmt);

        // ********* LVL 02 **************
        // table Geo sessions
        createStmt = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER, %s TEXT, %s INTEGER, FOREIGN KEY(%s) REFERENCES %s(%s));)"
                , /*PK, FK*/TAB_GEO_SESSIONS, COL_GS_ID, COL_GS_MAP_ID,
                /*data*/ COL_GS_NAME, COL_GS_DATE_CREATED
                ,/*FK ref*/ COL_GS_MAP_ID, TAB_MAPS, COL_MA_ID);
        Log.d(tag, createStmt);
        db.execSQL(createStmt);

        // table Drawing points
        createStmt = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER, FOREIGN KEY(%s) REFERENCES %s(%s));)"
                , /*PK, FK*/TAB_DRAWING_POINTS, COL_DP_ID, COL_DP_MAP_ID,
                /*data*/ COL_DP_X, COL_DP_Y, COL_DP_RADIUS
                ,/*FK ref*/ COL_DP_MAP_ID, TAB_MAPS, COL_MA_ID);
        Log.d(tag, createStmt);
        db.execSQL(createStmt);
        // table Mapped points
        createStmt = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER, FOREIGN KEY(%s) REFERENCES %s(%s));)"
                , /*PK, FK*/ TAB_MAPPED_POINTS, COL_MP_ID, COL_MP_MAP_ID,
                /*data*/ COL_MP_DRAWING_X, COL_MP_DRAWING_Y, COL_MP_DRAWING_RADIUS, COL_MP_GEO_LAT, COL_MP_GEO_LON, COL_MP_GEO_ALT, COL_MP_GEO_RAD
                ,/*FK ref*/ COL_MP_MAP_ID, TAB_MAPS, COL_MA_ID);
        Log.d(tag, createStmt);
        db.execSQL(createStmt);

        // ********* LVL 03 **************
        // table Geo locations
        createStmt = String.format("CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER, FOREIGN KEY(%s) REFERENCES %s(%s));)"
                , /*PK, FK*/TAB_GEO_LOCATIONS, COL_GL_ID, COL_GL_GEO_SESSION_ID,
                /*data*/ COL_GL_LATITUDE, COL_GL_LONGITUDE, COL_GL_ALTITUDE, COL_GL_RADIUS
                /*FK ref*/, COL_GL_GEO_SESSION_ID, TAB_GEO_SESSIONS, COL_GS_ID);
        Log.d(tag, createStmt);
        db.execSQL(createStmt);

        // put initial values here

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", TAB_DRAWING_POINTS));
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", TAB_MAPPED_POINTS));
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", TAB_GEO_LOCATIONS));
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", TAB_GEO_SESSIONS));
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", TAB_MAPS));
        onCreate(db);
    }
}
