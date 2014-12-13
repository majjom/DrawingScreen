package com.example.majo.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by majo on 13-Dec-14.
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "mydb";
    private static final int DB_VERSION = 3;

    public static final String TABLE_POINTS = "points";
    public static final String COL_PICTURE_X = "pictureX";
    public static final String COL_PICTURE_Y = "pictureY";
    public static final String COL_PICTURE_RADIUS = "pictureRadius";

    public MyDatabaseHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(String.format("CREATE TABLE %s (_id INTEGER PRIMARY KEY AUTOINCREMENT, %s INTEGER, %s INTEGER, %s INTEGER);)", TABLE_POINTS, COL_PICTURE_X, COL_PICTURE_Y, COL_PICTURE_RADIUS));

        // put initial values here

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(String.format("DROP TABLE IF EXISTS %s", TABLE_POINTS));
        onCreate(db);
    }
}
