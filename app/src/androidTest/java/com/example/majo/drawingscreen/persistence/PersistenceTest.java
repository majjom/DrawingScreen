package com.example.majo.drawingscreen.persistence;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;

import com.example.majo.BusinessObjects.DrawingPoint;
import com.example.majo.BusinessObjects.GeoLocation;
import com.example.majo.BusinessObjects.GeoSession;
import com.example.majo.BusinessObjects.MappedPoint;
import com.example.majo.BusinessObjects.SchemaMap;
import com.example.majo.drawingscreen.MainActivity;
import com.example.majo.persistence.DrawingPointPersistence;
import com.example.majo.persistence.GeoLocationPersistence;
import com.example.majo.persistence.GeoSessionPersistence;
import com.example.majo.persistence.IDrawingPointPersistence;
import com.example.majo.persistence.IGeoLocationPersistence;
import com.example.majo.persistence.IGeoSessionPersistence;
import com.example.majo.persistence.IMappedPointsPersistence;
import com.example.majo.persistence.ISchemaMapPersistence;
import com.example.majo.persistence.MappedPointsPersistence;
import com.example.majo.persistence.SchemaMapPersistence;

import java.util.ArrayList;

/**
 * Created by majo on 14-Dec-14.
 */
public class PersistenceTest extends ActivityInstrumentationTestCase2<MainActivity> {

    MainActivity activity;

    public PersistenceTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        activity = getActivity();
    }

    @SmallTest
    public void testDrawingPointPersistence(){
        // arrange
        IDrawingPointPersistence db = new DrawingPointPersistence(activity);

        int mapId = 0;

        float x = 100.22f;
        float y = 99.11f;
        float radius = 15.8f;

        DrawingPoint dp = new DrawingPoint(x, y, radius);
        ArrayList<DrawingPoint> points = new ArrayList<>();
        points.add(dp);

        // act
        db.deleteAllPoints(mapId);
        db.addPoints(mapId, points);
        ArrayList<DrawingPoint> returnPoints = db.getAllPoints(mapId);
        db.deleteAllPoints(mapId);

        // assert
        assertEquals(1, returnPoints.size());
        DrawingPoint returnDp = returnPoints.get(0);
        assertEquals(x, returnDp.x);
        assertEquals(y, returnDp.y);
        assertEquals(radius, returnDp.radius);
    }

    @SmallTest
    public void testMappedPointPersistence(){
        // arrange
        IMappedPointsPersistence db = new MappedPointsPersistence(activity);

        int mapId = 0;

        float x = 100.22f;
        float y = 99.11f;
        float radius = 15.8f;

        double geoLat = 47.03303d;
        double geoLon = 8.43811d;
        double geoAlt = 14.43811d;
        double geoRadius = 8.43811d;

        MappedPoint point = new MappedPoint(x, y, radius, geoLat, geoLon, geoAlt, geoRadius);
        ArrayList<MappedPoint> points = new ArrayList<>();
        points.add(point);

        // act
        db.deleteAllPoints(mapId);
        db.addPoints(mapId, points);
        ArrayList<MappedPoint> returnPoints = db.getAllPoints(mapId);
        db.deleteAllPoints(mapId);

        // assert
        assertEquals(1, returnPoints.size());
        MappedPoint returnPoint = returnPoints.get(0);
        assertEquals(x, returnPoint.drawingX);
        assertEquals(y, returnPoint.drawingY);
        assertEquals(radius, returnPoint.drawingRadius);

        assertEquals(geoLat, returnPoint.geoLatitude);
        assertEquals(geoLon, returnPoint.geoLongitude);
        assertEquals(geoAlt, returnPoint.geoAltitude);
        assertEquals(geoRadius, returnPoint.geoRadius);
    }

    @SmallTest
    public void testGeoLocationPersistence(){
        // arrange
        IGeoLocationPersistence db = new GeoLocationPersistence(activity);

        int geoSessionId = 0;

        double geoLat = 47.03303d;
        double geoLon = 8.43811d;
        double geoAlt = 14.43811d;
        double geoRadius = 8.43811d;

        GeoLocation point = new GeoLocation(geoLat, geoLon, geoAlt, geoRadius);
        ArrayList<GeoLocation> points = new ArrayList<>();
        points.add(point);

        // act
        db.deleteAllLocations(geoSessionId);
        db.addLocations(geoSessionId, points);
        ArrayList<GeoLocation> returnPoints = db.getAllLocations(geoSessionId);
        db.deleteAllLocations(geoSessionId);

        // delete single test
        db.addLocations(geoSessionId, points);
        db.deleteLocation(points.get(0));
        ArrayList<GeoLocation> returnPoints2 = db.getAllLocations(geoSessionId);

        // assert
        assertEquals(1, returnPoints.size());
        GeoLocation returnPoint = returnPoints.get(0);

        assertEquals(geoLat, returnPoint.latitude);
        assertEquals(geoLon, returnPoint.longitude);
        assertEquals(geoAlt, returnPoint.altitude);
        assertEquals(geoRadius, returnPoint.radius);

        // assert single delete
        assertEquals(0, returnPoints2.size());
    }

    @SmallTest
    public void testGeoSessionPersistence(){
        // arrange
        IGeoSessionPersistence db = new GeoSessionPersistence(activity, new GeoLocationPersistence(activity));

        int mapId = 0;

        // children
        double geoLat = 47.03303d;
        double geoLon = 8.43811d;
        double geoAlt = 14.43811d;
        double geoRadius = 8.43811d;

        double geoLat2 = 32.03303d;
        double geoLon2 = 9.43811d;
        double geoAlt2 = 15.43811d;
        double geoRadius2 = 7.43811d;

        GeoLocation point = new GeoLocation(geoLat, geoLon, geoAlt, geoRadius);
        GeoLocation point2 = new GeoLocation(geoLat2, geoLon2, geoAlt2, geoRadius2);
        ArrayList<GeoLocation> geoLocations = new ArrayList<>();
        geoLocations.add(point);
        geoLocations.add(point2);

        // parents
        GeoSession session = new GeoSession("name1");
        session.addGeoLocations(geoLocations);

        GeoSession session2 = new GeoSession("name2");
        session2.addGeoLocations(geoLocations);

        ArrayList<GeoSession> sessions = new ArrayList<>();
        sessions.add(session);
        sessions.add(session2);

        // act
        db.deleteAllSessions(mapId);
        db.addSessions(mapId, sessions);
        ArrayList<GeoSession> returnSessions = db.getAllGeoSessions(mapId);

        // assert
        assertEquals(2, returnSessions.size());

        GeoSession returnSession = returnSessions.get(0);
        assertEquals(session.name, returnSession.name);
        assertEquals(session.dateCreated, returnSession.dateCreated);
        assertEquals(2, returnSession.getGeoLocations().size());

        GeoSession returnSession2 = returnSessions.get(1);
        assertEquals(session2.name, returnSession2.name);
        assertEquals(session2.dateCreated, returnSession2.dateCreated);
        assertEquals(2, returnSession2.getGeoLocations().size());

        db.deleteAllSessions(mapId);
    }

    public void testSchemaMapPersistence(){
        // arrange
        ISchemaMapPersistence db = new SchemaMapPersistence(activity, new GeoSessionPersistence(activity, new GeoLocationPersistence(activity)), new MappedPointsPersistence(activity), new DrawingPointPersistence(activity));

        int mapId = 0;

        // children lvl 1
        double geoLat = 47.03303d;
        double geoLon = 8.43811d;
        double geoAlt = 14.43811d;
        double geoRadius = 8.43811d;
        GeoLocation point = new GeoLocation(geoLat, geoLon, geoAlt, geoRadius);
        ArrayList<GeoLocation> geoLocations = new ArrayList<>();
        geoLocations.add(point);

        // children lvl 2
        GeoSession gs = new GeoSession("name1");
        gs.addGeoLocations(geoLocations);
        GeoSession gs2 = new GeoSession("name2");
        gs2.addGeoLocations(geoLocations);
        ArrayList<GeoSession> gss = new ArrayList<>();
        gss.add(gs);
        gss.add(gs2);

        DrawingPoint dp = new DrawingPoint(10, 20, 30);
        ArrayList<DrawingPoint> dps = new ArrayList<>();
        dps.add(dp);

        MappedPoint mp = new MappedPoint(10,20,30, 10,20,30,40);
        ArrayList<MappedPoint> mps = new ArrayList<>();
        mps.add(mp);

        // parent
        SchemaMap map = new SchemaMap("map");
        map.addDrawingPoints(dps);
        map.addGeoSessions(gss);
        map.addMappedPoints(mps);
        ArrayList<SchemaMap> sms = new ArrayList<>();
        sms.add(map);

        // act
        db.deleteAllMaps();
        db.addMaps(sms);
        ArrayList<SchemaMap> returnMaps = db.getAllMaps();

        // assert
        assertEquals(1, returnMaps.size());

        SchemaMap returnMap = returnMaps.get(0);
        assertEquals(map.name, returnMap.name);
        assertEquals(map.dateCreated, returnMap.dateCreated);
        assertEquals(1, returnMap.getDrawingPoints().size());
        assertEquals(2, returnMap.getGeoSessions().size());
        assertEquals(1, returnMap.getMappedPoints().size());

        assertEquals(1, returnMap.getGeoSessions().get(0).getGeoLocations().size());
        assertEquals(1, returnMap.getGeoSessions().get(1).getGeoLocations().size());

        db.deleteAllMaps();
    }
}
