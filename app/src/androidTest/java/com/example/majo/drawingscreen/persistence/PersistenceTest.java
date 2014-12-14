package com.example.majo.drawingscreen.persistence;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;

import com.example.majo.BusinessObjects.DrawingPoint;
import com.example.majo.BusinessObjects.GeoLocation;
import com.example.majo.BusinessObjects.MappedPoint;
import com.example.majo.drawingscreen.MainActivity;
import com.example.majo.persistence.DrawingPointPersistence;
import com.example.majo.persistence.GeoLocationPersistence;
import com.example.majo.persistence.IDrawingPointPersistence;
import com.example.majo.persistence.IGeoLocationPersistence;
import com.example.majo.persistence.IMappedPointsPersistence;
import com.example.majo.persistence.MappedPointsPersistence;

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

        float x = 100.22f;
        float y = 99.11f;
        float radius = 15.8f;

        DrawingPoint dp = new DrawingPoint(x, y, radius);
        ArrayList<DrawingPoint> points = new ArrayList<>();
        points.add(dp);

        // act
        db.deleteAllPoints();
        db.addPoints(points);
        ArrayList<DrawingPoint> returnPoints = db.getAllPoints();
        db.deleteAllPoints();

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
        db.deleteAllPoints(geoSessionId);
        db.addPoints(geoSessionId, points);
        ArrayList<GeoLocation> returnPoints = db.getAllPoints(geoSessionId);
        db.deleteAllPoints(geoSessionId);

        // assert
        assertEquals(1, returnPoints.size());
        GeoLocation returnPoint = returnPoints.get(0);

        assertEquals(geoLat, returnPoint.latitude);
        assertEquals(geoLon, returnPoint.longitude);
        assertEquals(geoAlt, returnPoint.altitude);
        assertEquals(geoRadius, returnPoint.radius);
    }
}
