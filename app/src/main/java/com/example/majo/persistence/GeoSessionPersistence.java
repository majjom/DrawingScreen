package com.example.majo.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.majo.BusinessObjects.GeoLocation;
import com.example.majo.BusinessObjects.GeoSession;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by moravekm on 17-Dec-14.
 */
public class GeoSessionPersistence extends DatabaseConnection implements IGeoSessionPersistence {
    public GeoSessionPersistence(Context context) {
        super(context);
    }

    @Override
    public ArrayList<GeoSession> getAllGeoSessions(int mapId) {
        ArrayList<GeoSession> result = new ArrayList<>();

        String[] columns = new String[] { MyDatabaseHelper.COL_GS_ID, MyDatabaseHelper.COL_GS_NAME, MyDatabaseHelper.COL_GS_DATE_CREATED };
        Cursor cur = db.query(MyDatabaseHelper.TAB_GEO_SESSIONS, columns, null, null, null, null, MyDatabaseHelper.COL_GS_ID);

        while(cur.moveToNext()){
            int id = cur.getInt(cur.getColumnIndex(MyDatabaseHelper.COL_GS_ID));
            String name = cur.getString(cur.getColumnIndex(MyDatabaseHelper.COL_GS_NAME));
            Date dateCreated = ConversionHelper.longToDate(cur.getLong(cur.getColumnIndex(MyDatabaseHelper.COL_GS_DATE_CREATED)));

            result.add(new GeoSession(id, name, dateCreated));
        }

        cur.close();
        return result;
    }

    @Override
    public void addSessions(int mapId, ArrayList<GeoSession> sessions){
        for (GeoSession session : sessions){
            ContentValues cv = new ContentValues(4);

            long dateCreated = ConversionHelper.dateToLong(session.dateCreated);

            cv.put(MyDatabaseHelper.COL_GS_NAME, session.name);
            cv.put(MyDatabaseHelper.COL_GL_LONGITUDE, dateCreated);

            db.insert(MyDatabaseHelper.TAB_GEO_SESSIONS, null, cv);
        }
    }

    @Override
    public void deleteAllSessions(int mapId) {
        db.delete(MyDatabaseHelper.TAB_GEO_SESSIONS, null, null);
    }
}
