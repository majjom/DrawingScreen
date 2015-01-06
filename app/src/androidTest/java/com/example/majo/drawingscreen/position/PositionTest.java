package com.example.majo.drawingscreen.position;

import android.location.Location;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;

import com.example.majo.BusinessObjects.DrawingPoint;
import com.example.majo.BusinessObjects.MappedPoint;
import com.example.majo.drawingscreen.DrawingPointsActivity;
import com.example.majo.persistence.IMappedPointsPersistence;
import com.example.majo.persistence.MappedPointsPersistence;
import com.example.majo.position.IPositionService;
import com.example.majo.position.PositionService;

import java.util.ArrayList;

/**
 * Created by majo on 14-Dec-14.
 */
public class PositionTest extends ActivityInstrumentationTestCase2<DrawingPointsActivity> {

    DrawingPointsActivity activity;

    public PositionTest() {
        super(DrawingPointsActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        activity = getActivity();
    }

    @SmallTest
    public void testPositionService(){
        // arrange
        int mapId = 0;
        IMappedPointsPersistence db = new MappedPointsPersistence(activity);
        IPositionService position = new PositionService(db);

        MappedPoint point1 = new MappedPoint(100, 200, 20, 47.03225d, 8.43479d, 0, 100);
        MappedPoint point2 = new MappedPoint(150, 200, 20, 47.04024d, 8.47048d, 0, 100);
        ArrayList<MappedPoint> points = new ArrayList<>();
        points.add(point1);
        points.add(point2);
        db.deleteAllPoints(mapId);
        db.addPoints(mapId, points);

        Location loc = new Location("");
        loc.setLatitude(47.03224d);
        loc.setLongitude(8.43485d);
        loc.setAltitude(0d);

        // act
        DrawingPoint resultPoint = position.getCurrentPosition(loc);
        DrawingPoint resultPoint2 = position.getLastPosition();

        // assert
        assertNotNull(resultPoint);
        assertEquals(point1.drawingX, resultPoint.x);
        assertEquals(point1.drawingY, resultPoint.y);
        assertEquals(point1.drawingRadius, resultPoint.radius);

        assertNotNull(resultPoint2);
        assertEquals(point1.drawingX, resultPoint2.x);
        assertEquals(point1.drawingY, resultPoint2.y);
        assertEquals(point1.drawingRadius, resultPoint2.radius);
    }
}
