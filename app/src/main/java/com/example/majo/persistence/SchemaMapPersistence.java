package com.example.majo.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.majo.BusinessObjects.GeoSession;
import com.example.majo.BusinessObjects.GeoSessionProxy;
import com.example.majo.BusinessObjects.SchemaMap;
import com.example.majo.BusinessObjects.SchemaMapProxy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by moravekm on 19-Dec-14.
 */
public class SchemaMapPersistence implements ISchemaMapPersistence {

    private IDatabaseConnection connection;
    private IGeoSessionPersistence geoSessionPersistence;
    private IMappedPointsPersistence mappedPointsPersistence;
    private IDrawingPointPersistence drawingPointPersistence;

    public SchemaMapPersistence(IDatabaseConnection connection){
        this(connection, new GeoSessionPersistence(connection), new MappedPointsPersistence(connection), new DrawingPointPersistence(connection));
    }


    public SchemaMapPersistence(IDatabaseConnection connection, IGeoSessionPersistence geoSessionPersistence, IMappedPointsPersistence mappedPointsPersistence, IDrawingPointPersistence drawingPointPersistence) {
        this.connection = connection;
        this.geoSessionPersistence = geoSessionPersistence;
        this.mappedPointsPersistence = mappedPointsPersistence;
        this.drawingPointPersistence = drawingPointPersistence;
    }

    @Override
    public List<SchemaMap> getAllMaps() {
        List<SchemaMap> result = new ArrayList<>();

        String[] columns = new String[] { MyDatabaseHelper.COL_MA_ID, MyDatabaseHelper.COL_MA_NAME, MyDatabaseHelper.COL_MA_DATE_CREATED };
        Cursor cur = this.connection.getDb().query(MyDatabaseHelper.TAB_MAPS, columns, null, null, null, null, MyDatabaseHelper.COL_GS_ID);

        while(cur.moveToNext()){
            int id = cur.getInt(cur.getColumnIndex(MyDatabaseHelper.COL_GS_ID));
            String name = cur.getString(cur.getColumnIndex(MyDatabaseHelper.COL_GS_NAME));
            Date dateCreated = ConversionHelper.longToDate(cur.getLong(cur.getColumnIndex(MyDatabaseHelper.COL_GS_DATE_CREATED)));

            // the lazy loading proxy
            SchemaMapProxy smProxy = new SchemaMapProxy(name, dateCreated, this.geoSessionPersistence, this.drawingPointPersistence, this.mappedPointsPersistence);
            smProxy.id = id;

            result.add(smProxy);
        }

        cur.close();
        return result;
    }

    @Override
    public void addMap(SchemaMap map) {
        ContentValues cv = new ContentValues(3);

        long dateCreated = ConversionHelper.dateToLong(map.dateCreated);

        cv.put(MyDatabaseHelper.COL_MA_NAME, map.name);
        cv.put(MyDatabaseHelper.COL_MA_DATE_CREATED, dateCreated);

        long id = this.connection.getDb().insert(MyDatabaseHelper.TAB_MAPS, null, cv);
        map.id = (int)id;

        // add children
        this.geoSessionPersistence.addSessions(map.id, map.getGeoSessions());
        this.drawingPointPersistence.addPoints(map.id, map.getDrawingPoints());
        this.mappedPointsPersistence.addPoints(map.id, map.getMappedPoints());

    }

    @Override
    public void addMaps(List<SchemaMap> maps) {
        for (SchemaMap map : maps){
            addMap(map);
        }
    }

    @Override
    public void deleteAllMaps() {
        for (SchemaMap map : getAllMaps()){
            deleteMap(map);
        }
    }

    @Override
    public void deleteMap(SchemaMap map) {
        // first delete children
        this.geoSessionPersistence.deleteAllSessions(map.id);
        this.drawingPointPersistence.deleteAllPoints(map.id);
        this.mappedPointsPersistence.deleteAllPoints(map.id);

        // delete parent
        this.connection.getDb().delete(MyDatabaseHelper.TAB_MAPS, String.format("%s=?", MyDatabaseHelper.COL_MA_ID), new String[]{String.valueOf(map.id)});
    }
}
