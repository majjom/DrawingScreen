package com.example.majo.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.majo.BusinessObjects.DrawingPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by majo on 13-Dec-14.
 */
public class DrawingPointPersistence implements IDrawingPointPersistence {

    IDatabaseConnection connection;

    public DrawingPointPersistence(IDatabaseConnection connection){
        this.connection = connection;
    }

    @Override
    public List<DrawingPoint> getAllPoints(int mapId) {
        List<DrawingPoint> result = new ArrayList<>();

        String[] columns = new String[] { MyDatabaseHelper.COL_DP_ID, MyDatabaseHelper.COL_DP_X, MyDatabaseHelper.COL_DP_Y, MyDatabaseHelper.COL_DP_RADIUS};
        Cursor cur = connection.getDb().query(MyDatabaseHelper.TAB_DRAWING_POINTS, columns, String.format("%s=?", MyDatabaseHelper.COL_DP_MAP_ID), new String[] { String.valueOf(mapId) }, null, null, MyDatabaseHelper.COL_DP_ORDERING);

        while(cur.moveToNext()){
            int id = cur.getInt(cur.getColumnIndex(MyDatabaseHelper.COL_DP_ID));
            float x = ConversionHelper.intToDrawingPoint(cur.getInt(cur.getColumnIndex(MyDatabaseHelper.COL_DP_X)));
            float y = ConversionHelper.intToDrawingPoint(cur.getInt(cur.getColumnIndex(MyDatabaseHelper.COL_DP_Y)));
            float radius = ConversionHelper.intToDrawingPoint(cur.getInt(cur.getColumnIndex(MyDatabaseHelper.COL_DP_RADIUS)));
            DrawingPoint dp = new DrawingPoint(x, y, radius);
            dp.id = id;

            result.add(dp);
        }

        cur.close();
        return result;
    }

    @Override
    public void addPoints(int mapId, List<DrawingPoint> points) {
        int order = 1;
        for (DrawingPoint point : points){
            ContentValues cv = new ContentValues(5);

            int x = ConversionHelper.drawingPointToInt(point.x);
            int y = ConversionHelper.drawingPointToInt(point.y);
            int radius = ConversionHelper.drawingPointToInt(point.radius);

            cv.put(MyDatabaseHelper.COL_DP_MAP_ID, mapId);
            cv.put(MyDatabaseHelper.COL_DP_X, x);
            cv.put(MyDatabaseHelper.COL_DP_Y, y);
            cv.put(MyDatabaseHelper.COL_DP_RADIUS, radius);

            cv.put(MyDatabaseHelper.COL_MP_ORDERING, order);

            long id = connection.getDb().insert(MyDatabaseHelper.TAB_DRAWING_POINTS, null, cv);
            point.id = (int)id;
            order++;
        }
    }

    @Override
    public void deleteAllPoints(int mapId) {
        connection.getDb().delete(MyDatabaseHelper.TAB_DRAWING_POINTS, String.format("%s=?", MyDatabaseHelper.COL_DP_MAP_ID), new String[] { String.valueOf(mapId) });
    }

    @Override
    public void deleteDrawingPoint(DrawingPoint drawingPoint){
        connection.getDb().delete(MyDatabaseHelper.TAB_DRAWING_POINTS, String.format("%s=?", MyDatabaseHelper.COL_DP_ID), new String[] { String.valueOf(drawingPoint.id) });
    }


}
