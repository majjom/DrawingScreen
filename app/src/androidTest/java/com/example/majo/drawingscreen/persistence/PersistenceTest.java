package com.example.majo.drawingscreen.persistence;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;

import com.example.majo.BusinessObjects.DrawingPoint;
import com.example.majo.BusinessObjects.GeoLocation;
import com.example.majo.BusinessObjects.GeoSession;
import com.example.majo.BusinessObjects.MappedPoint;
import com.example.majo.BusinessObjects.SchemaMap;
import com.example.majo.drawingscreen.DrawingPointsActivity;
import com.example.majo.persistenceLocalDatabase.DatabaseConnection;
import com.example.majo.persistenceLocalDatabase.DrawingPointPersistence;
import com.example.majo.persistenceLocalDatabase.GeoLocationPersistence;
import com.example.majo.persistenceLocalDatabase.GeoSessionPersistence;
import com.example.majo.persistenceLocalDatabase.IDatabaseConnection;
import com.example.majo.persistenceLocalDatabase.IDrawingPointPersistence;
import com.example.majo.persistenceLocalDatabase.IGeoLocationPersistence;
import com.example.majo.persistenceLocalDatabase.IGeoSessionPersistence;
import com.example.majo.persistenceLocalDatabase.IMappedPointsPersistence;
import com.example.majo.persistenceLocalDatabase.ISchemaMapPersistence;
import com.example.majo.persistenceLocalDatabase.MappedPointsPersistence;
import com.example.majo.persistenceLocalDatabase.SchemaMapPersistence;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by majo on 14-Dec-14.
 */
public class PersistenceTest extends ActivityInstrumentationTestCase2<DrawingPointsActivity> {

    DrawingPointsActivity activity;

    public PersistenceTest() {
        super(DrawingPointsActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        activity = getActivity();
    }

    @SmallTest
    public void testDrawingPointPersistence(){
        // arrange
        IDatabaseConnection db = new DatabaseConnection(activity);
        IDrawingPointPersistence persistence = new DrawingPointPersistence(db);

        int mapId = 0;

        float x = 100.22f;
        float y = 99.11f;
        float radius = 15.8f;

        DrawingPoint dp = new DrawingPoint(x, y, radius);
        ArrayList<DrawingPoint> points = new ArrayList<>();
        points.add(dp);

        // act
        persistence.deleteAllPoints(mapId);
        persistence.addPoints(mapId, points);
        List<DrawingPoint> returnPoints = persistence.getAllPoints(mapId);

        // assert
        assertEquals(1, returnPoints.size());
        DrawingPoint returnDp = returnPoints.get(0);
        assertEquals(x, returnDp.x);
        assertEquals(y, returnDp.y);
        assertEquals(radius, returnDp.radius);


        // act 2
        persistence.addPoint(mapId, dp);
        returnPoints = persistence.getAllPoints(mapId);
        persistence.deleteAllPoints(mapId);

        // assert 2
        assertEquals(2, returnPoints.size());
        DrawingPoint returnDp1 = returnPoints.get(0);
        DrawingPoint returnDp2 = returnPoints.get(1);
        assertEquals(returnDp1.order + 1, returnDp2.order);

        db.destroy();
    }

    @SmallTest
    public void testMappedPointPersistence(){
        // arrange
        IDatabaseConnection db = new DatabaseConnection(activity);
        IMappedPointsPersistence persistence = new MappedPointsPersistence(db);

        int mapId = 0;

        float x = 100.22f;
        float y = 99.11f;
        float radius = 15.8f;

        double geoLat = 47.03303d;
        double geoLon = 8.43811d;
        double geoAlt = 14.43811d;
        double geoRadius = 8.43811d;

        MappedPoint point = new MappedPoint(new DrawingPoint(x, y, radius), new GeoLocation(geoLat, geoLon, geoAlt, geoRadius));
        ArrayList<MappedPoint> points = new ArrayList<>();
        points.add(point);

        // act
        persistence.deleteAllPoints(mapId);
        persistence.addPoints(mapId, points);
        List<MappedPoint> returnPoints = persistence.getAllPoints(mapId);

        // assert
        assertEquals(1, returnPoints.size());
        MappedPoint returnPoint = returnPoints.get(0);
        assertEquals(x, returnPoint.drawingPoint.x);
        assertEquals(y, returnPoint.drawingPoint.y);
        assertEquals(radius, returnPoint.drawingPoint.radius);

        assertEquals(geoLat, returnPoint.geoLocation.latitude);
        assertEquals(geoLon, returnPoint.geoLocation.longitude);
        assertEquals(geoAlt, returnPoint.geoLocation.altitude);
        assertEquals(geoRadius, returnPoint.geoLocation.radius);

        // act 2
        persistence.addPoints(mapId, points);
        returnPoints = persistence.getAllPoints(mapId);
        persistence.deleteAllPoints(mapId);

        // assert
        assertEquals(2, returnPoints.size());
        MappedPoint returnPoint1 = returnPoints.get(0);
        MappedPoint returnPoint2 = returnPoints.get(1);
        assertEquals(returnPoint1.order + 1, returnPoint2.order);

        db.destroy();
    }

    @SmallTest
    public void testGeoLocationPersistence(){
        // arrange
        IDatabaseConnection db = new DatabaseConnection(activity);
        IGeoLocationPersistence persistence = new GeoLocationPersistence(db);

        int geoSessionId = 0;

        double geoLat = 47.03303d;
        double geoLon = 8.43811d;
        double geoAlt = 14.43811d;
        double geoRadius = 8.43811d;

        GeoLocation point = new GeoLocation(geoLat, geoLon, geoAlt, geoRadius);
        ArrayList<GeoLocation> points = new ArrayList<>();
        points.add(point);

        // act
        persistence.deleteAllLocations(geoSessionId);
        persistence.addLocations(geoSessionId, points);
        List<GeoLocation> returnPoints = persistence.getAllLocations(geoSessionId);
        persistence.deleteAllLocations(geoSessionId);

        // delete single test
        persistence.addLocations(geoSessionId, points);
        persistence.deleteLocation(points.get(0));
        List<GeoLocation> returnPoints2 = persistence.getAllLocations(geoSessionId);

        // assert
        assertEquals(1, returnPoints.size());
        GeoLocation returnPoint = returnPoints.get(0);

        assertEquals(geoLat, returnPoint.latitude);
        assertEquals(geoLon, returnPoint.longitude);
        assertEquals(geoAlt, returnPoint.altitude);
        assertEquals(geoRadius, returnPoint.radius);

        // assert single delete
        assertEquals(0, returnPoints2.size());

        db.destroy();
    }

    @SmallTest
    public void testGeoSessionPersistence(){
        // arrange
        IDatabaseConnection db = new DatabaseConnection(activity);
        IGeoSessionPersistence persistence = new GeoSessionPersistence(db, new GeoLocationPersistence(db));

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
        persistence.deleteAllSessions(mapId);
        persistence.addSessions(mapId, sessions);
        List<GeoSession> returnSessions = persistence.getAllGeoSessions(mapId);

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

        persistence.deleteAllSessions(mapId);

        db.destroy();
    }

    public void testSchemaMapPersistence(){
        // arrange
        IDatabaseConnection db = new DatabaseConnection(activity);
        ISchemaMapPersistence persistence = new SchemaMapPersistence(db, new GeoSessionPersistence(db, new GeoLocationPersistence(db)), new MappedPointsPersistence(db), new DrawingPointPersistence(db));

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

        MappedPoint mp = new MappedPoint(new DrawingPoint(10,20,30), new GeoLocation(10,20,30,40));
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
        persistence.deleteAllMaps();
        persistence.addMaps(sms);
        List<SchemaMap> returnMaps = persistence.getAllMaps();

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

        persistence.deleteAllMaps();
    }
}
