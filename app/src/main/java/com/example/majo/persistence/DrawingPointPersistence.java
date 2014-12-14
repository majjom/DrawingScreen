package com.example.majo.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.majo.BusinessObjects.DrawingPoint;

import java.util.ArrayList;

/**
 * Created by majo on 13-Dec-14.
 */
public class DrawingPointPersistence extends DatabaseConnection implements IDrawingPointPersistence {


    public DrawingPointPersistence(Context context){
        super(context);
    }

    @Override
    public ArrayList<DrawingPoint> getAllPoints() {
        ArrayList<DrawingPoint> result = new ArrayList<>();

        String[] columns = new String[] { "_id", MyDatabaseHelper.COL_DP_X, MyDatabaseHelper.COL_DP_Y, MyDatabaseHelper.COL_DP_RADIUS};
        Cursor cur = db.query(MyDatabaseHelper.TAB_DRAWING_POINTS, columns, null, null, null, null, "_id");

        while(cur.moveToNext()){
            float x = ConversionHelper.intToDrawingPoint(cur.getInt(cur.getColumnIndex(MyDatabaseHelper.COL_DP_X)));
            float y = ConversionHelper.intToDrawingPoint(cur.getInt(cur.getColumnIndex(MyDatabaseHelper.COL_DP_Y)));
            float radius = ConversionHelper.intToDrawingPoint(cur.getInt(cur.getColumnIndex(MyDatabaseHelper.COL_DP_RADIUS)));
            result.add(new DrawingPoint(x, y, radius));
        }

        cur.close();
        return result;
    }

    @Override
    public void addPoints(ArrayList<DrawingPoint> points) {
        for (DrawingPoint point : points){
            ContentValues cv = new ContentValues(3);
            int x = ConversionHelper.drawingPointToInt(point.x);
            int y = ConversionHelper.drawingPointToInt(point.y);
            int radius = ConversionHelper.drawingPointToInt(point.radius);
            cv.put(MyDatabaseHelper.COL_DP_X, x);
            cv.put(MyDatabaseHelper.COL_DP_Y, y);
            cv.put(MyDatabaseHelper.COL_DP_RADIUS, radius);
            db.insert(MyDatabaseHelper.TAB_DRAWING_POINTS, null, cv);
        }
    }

    @Override
    public void deleteAllPoints() {
        db.delete(MyDatabaseHelper.TAB_DRAWING_POINTS, null, null);
    }


}
