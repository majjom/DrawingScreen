package com.example.majo.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.majo.BusinessObjects.GeoSession;
import com.example.majo.BusinessObjects.GeoSessionProxy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by moravekm on 17-Dec-14.
 */
public class GeoSessionPersistence implements IGeoSessionPersistence {

    IDatabaseConnection connection;
    private IGeoLocationPersistence geoLocationPersistence;

    public GeoSessionPersistence(IDatabaseConnection connection, IGeoLocationPersistence geoLocationPersistence){
        this.connection = connection;
        this.geoLocationPersistence = geoLocationPersistence;
    }

    public GeoSessionPersistence(IDatabaseConnection connection){
        this(connection, new GeoLocationPersistence(connection));
    }

    @Override
    public List<GeoSession> getAllGeoSessions(int mapId) {
        List<GeoSession> result = new ArrayList<>();

        String[] columns = new String[] { MyDatabaseHelper.COL_GS_ID, MyDatabaseHelper.COL_GS_NAME, MyDatabaseHelper.COL_GS_DATE_CREATED };
        Cursor cur = this.connection.getDb().query(MyDatabaseHelper.TAB_GEO_SESSIONS, columns, String.format("%s=?", MyDatabaseHelper.COL_GS_MAP_ID), new String[] { String.valueOf(mapId) }, null, null, MyDatabaseHelper.COL_GS_ID);

        while(cur.moveToNext()){
            int id = cur.getInt(cur.getColumnIndex(MyDatabaseHelper.COL_GS_ID));
            String name = cur.getString(cur.getColumnIndex(MyDatabaseHelper.COL_GS_NAME));
            Date dateCreated = ConversionHelper.longToDate(cur.getLong(cur.getColumnIndex(MyDatabaseHelper.COL_GS_DATE_CREATED)));

            // the lazy loading proxy
            GeoSessionProxy gsp = new GeoSessionProxy(name, dateCreated, this.geoLocationPersistence);
            gsp.id = id;

            result.add(gsp);
        }

        cur.close();
        return result;
    }

    @Override
    public void addSession(int mapId, GeoSession session){
        ContentValues cv = new ContentValues(3);

        long dateCreated = ConversionHelper.dateToLong(session.dateCreated);

        cv.put(MyDatabaseHelper.COL_GS_MAP_ID, mapId);
        cv.put(MyDatabaseHelper.COL_GS_NAME, session.name);
        cv.put(MyDatabaseHelper.COL_GS_DATE_CREATED, dateCreated);

        long id = this.connection.getDb().insert(MyDatabaseHelper.TAB_GEO_SESSIONS, null, cv);
        session.id = (int)id;

        // add children
        this.geoLocationPersistence.addLocations(session.id, session.getGeoLocations());
    }

    @Override
    public void addSessions(int mapId, List<GeoSession> sessions){
        for (GeoSession session : sessions){
            addSession(mapId, session);
        }
    }

    @Override
    public void deleteAllSessions(int mapId) {
        for (GeoSession geoSession : getAllGeoSessions(mapId)){
            deleteSession(geoSession);
        }
    }

    @Override
    public void deleteSession(GeoSession geoSession) {
        // first delete children
        this.geoLocationPersistence.deleteAllLocations(geoSession.id);

        // delete parent
        this.connection.getDb().delete(MyDatabaseHelper.TAB_GEO_SESSIONS, String.format("%s=?", MyDatabaseHelper.COL_GS_ID), new String[] { String.valueOf(geoSession.id) });
    }
}
