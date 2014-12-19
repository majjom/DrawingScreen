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
    public ArrayList<DrawingPoint> getAllPoints(int mapId) {
        ArrayList<DrawingPoint> result = new ArrayList<>();

        String[] columns = new String[] { MyDatabaseHelper.COL_DP_ID, MyDatabaseHelper.COL_DP_X, MyDatabaseHelper.COL_DP_Y, MyDatabaseHelper.COL_DP_RADIUS};
        Cursor cur = db.query(MyDatabaseHelper.TAB_DRAWING_POINTS, columns, String.format("%s=?", MyDatabaseHelper.COL_DP_MAP_ID), new String[] { String.valueOf(mapId) }, null, null, MyDatabaseHelper.COL_DP_ID);

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
    public void addPoints(int mapId, ArrayList<DrawingPoint> points) {
        for (DrawingPoint point : points){
            ContentValues cv = new ContentValues(4);

            int x = ConversionHelper.drawingPointToInt(point.x);
            int y = ConversionHelper.drawingPointToInt(point.y);
            int radius = ConversionHelper.drawingPointToInt(point.radius);

            cv.put(MyDatabaseHelper.COL_DP_MAP_ID, mapId);
            cv.put(MyDatabaseHelper.COL_DP_X, x);
            cv.put(MyDatabaseHelper.COL_DP_Y, y);
            cv.put(MyDatabaseHelper.COL_DP_RADIUS, radius);

            db.insert(MyDatabaseHelper.TAB_DRAWING_POINTS, null, cv);
        }
    }

    @Override
    public void deleteAllPoints(int mapId) {
        db.delete(MyDatabaseHelper.TAB_DRAWING_POINTS, String.format("%s=?", MyDatabaseHelper.COL_DP_MAP_ID), new String[] { String.valueOf(mapId) });
    }

    @Override
    public void deleteDrawingPoint(DrawingPoint drawingPoint){
        db.delete(MyDatabaseHelper.TAB_DRAWING_POINTS, String.format("%s=?", MyDatabaseHelper.COL_DP_ID), new String[] { String.valueOf(drawingPoint.id) });
    }


}
