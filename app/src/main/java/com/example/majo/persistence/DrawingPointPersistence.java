package com.example.majo.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.majo.drawingscreen.DrawingPoint;

import java.util.ArrayList;

/**
 * Created by majo on 13-Dec-14.
 */
public class DrawingPointPersistence implements IDrawingPointPersistence {

    MyDatabaseHelper dbCreator;
    SQLiteDatabase db;

    public DrawingPointPersistence(Context context){
        this.dbCreator = new MyDatabaseHelper(context);
        this.db = dbCreator.getWritableDatabase();
    }

    @Override
    public ArrayList<DrawingPoint> getDrawingPoints() {
        ArrayList<DrawingPoint> result = new ArrayList<>();

        String[] columns = new String[] { "_id", MyDatabaseHelper.COL_PICTURE_X, MyDatabaseHelper.COL_PICTURE_Y, MyDatabaseHelper.COL_PICTURE_RADIUS};
        Cursor cur = db.query(MyDatabaseHelper.TABLE_POINTS, columns, null, null, null, null, "_id");

        while(cur.moveToNext()){
            int x = cur.getInt(cur.getColumnIndex(MyDatabaseHelper.COL_PICTURE_X));
            int y = cur.getInt(cur.getColumnIndex(MyDatabaseHelper.COL_PICTURE_Y));
            int radius = cur.getInt(cur.getColumnIndex(MyDatabaseHelper.COL_PICTURE_RADIUS));
            result.add(new DrawingPoint(x, y, radius));
        }

        cur.close();
        return result;
    }

    @Override
    public void addDrawingPoints(ArrayList<DrawingPoint> points) {
        for (DrawingPoint point : points){
            ContentValues cv = new ContentValues(3);
            int x = Math.round(point.x);
            int y = Math.round(point.y);
            int radius = Math.round(point.radius);
            cv.put(MyDatabaseHelper.COL_PICTURE_X, x);
            cv.put(MyDatabaseHelper.COL_PICTURE_Y, y);
            cv.put(MyDatabaseHelper.COL_PICTURE_RADIUS, radius);
            db.insert(MyDatabaseHelper.TABLE_POINTS, null, cv);
        }
    }

    @Override
    public void deleteAllDrawingPoints() {
        db.delete(MyDatabaseHelper.TABLE_POINTS, null, null);
    }

    public void onDestroy(){
        if (this.db != null){
            this.db.close();
        }
    }

}
