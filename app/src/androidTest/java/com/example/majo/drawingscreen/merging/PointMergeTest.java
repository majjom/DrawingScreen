package com.example.majo.drawingscreen.merging;

import android.test.InstrumentationTestCase;

import com.example.majo.BusinessObjects.DrawingPoint;
import com.example.majo.BusinessObjects.GeoLocation;
import com.example.majo.BusinessObjects.MappedPoint;
import com.example.majo.merging.CartesianInterpolation;
import com.example.majo.merging.CartesianPoint;
import com.example.majo.merging.PointMerge;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by majo on 30-Jan-15.
 */
public class PointMergeTest extends InstrumentationTestCase {

    public void testMergePoints_2dp_2gl(){
        // arrange
        DrawingPoint dp01 = new DrawingPoint(10, 20, 10);
        DrawingPoint dp02 = new DrawingPoint(20, 20, 10);
        List<DrawingPoint> dps = new ArrayList<>();
        dps.add(dp01);
        dps.add(dp02);

        GeoLocation gl01 = new GeoLocation(47.03316, 8.43811, 0, 10);
        GeoLocation gl02 = new GeoLocation(47.03303, 8.43723, 0, 10);
        List<GeoLocation> gls = new ArrayList<>();
        gls.add(gl01);
        gls.add(gl02);

        // act
        List<MappedPoint> result = PointMerge.mergePoints(dps, gls);

        // assert
        assertEquals(2, result.size());

        assertTrue(areEqual(dp01, result.get(0).drawingPoint));
        assertTrue(areEqual(dp02, result.get(1).drawingPoint));

        assertTrue(areEqual(gl01, result.get(0).geoLocation));
        assertTrue(areEqual(gl02, result.get(1).geoLocation));
    }

    public void testMergePoints_3dp_2gl(){
        // arrange
        DrawingPoint dp01 = new DrawingPoint(10, 20, 10);
        DrawingPoint dp02 = new DrawingPoint(20, 20, 10);
        DrawingPoint dp03 = new DrawingPoint(30, 30, 10);
        List<DrawingPoint> dps = new ArrayList<>();
        dps.add(dp01);
        dps.add(dp02);
        dps.add(dp03);

        GeoLocation gl01 = new GeoLocation(47.03316, 8.43811, 0, 10);
        GeoLocation gl02 = new GeoLocation(47.03303, 8.43723, 0, 10);
        List<GeoLocation> gls = new ArrayList<>();
        gls.add(gl01);
        gls.add(gl02);

        // act
        List<MappedPoint> result = PointMerge.mergePoints(dps, gls);

        // assert
        assertEquals(2, result.size());

        assertTrue(areEqual(dp01, result.get(0).drawingPoint));
        assertTrue(areEqual(dp03, result.get(1).drawingPoint));

        assertTrue(areEqual(gl01, result.get(0).geoLocation));
        assertTrue(areEqual(gl02, result.get(1).geoLocation));
    }

    public void testMergePoints_2dp_3gl(){
        // arrange
        DrawingPoint dp01 = new DrawingPoint(10, 20, 10);
        DrawingPoint dp02 = new DrawingPoint(20, 40, 10);
        List<DrawingPoint> dps = new ArrayList<>();
        dps.add(dp01);
        dps.add(dp02);

        GeoLocation gl01 = new GeoLocation(47.03316, 8.43811, 0, 10);
        GeoLocation gl02 = new GeoLocation(47.03303, 8.43723, 0, 10);
        GeoLocation gl03 = new GeoLocation(47.03354, 8.43656, 0, 10);
        List<GeoLocation> gls = new ArrayList<>();
        gls.add(gl01);
        gls.add(gl02);
        gls.add(gl03);

        // act
        List<MappedPoint> result = PointMerge.mergePoints(dps, gls);

        // assert
        assertEquals(3, result.size());

        assertTrue(areEqual(dp01, result.get(0).drawingPoint));
        assertTrue(areEqual(dp02, result.get(2).drawingPoint));

        assertTrue(areEqual(gl01, result.get(0).geoLocation));
        assertTrue(areEqual(gl02, result.get(1).geoLocation));
        assertTrue(areEqual(gl03, result.get(2).geoLocation));
    }

    public void testMergePoints_2dp_4gl(){
        // arrange
        DrawingPoint dp01 = new DrawingPoint(10, 20, 10);
        DrawingPoint dp02 = new DrawingPoint(20, 80, 10);
        List<DrawingPoint> dps = new ArrayList<>();
        dps.add(dp01);
        dps.add(dp02);

        GeoLocation gl01 = new GeoLocation(47.03316, 8.43811, 0, 10);
        GeoLocation gl02 = new GeoLocation(47.03303, 8.43723, 0, 10);
        GeoLocation gl03 = new GeoLocation(47.03354, 8.43656, 0, 10);
        GeoLocation gl04 = new GeoLocation(47.03454, 8.43256, 0, 10);
        List<GeoLocation> gls = new ArrayList<>();
        gls.add(gl01);
        gls.add(gl02);
        gls.add(gl03);
        gls.add(gl04);

        // act
        List<MappedPoint> result = PointMerge.mergePoints(dps, gls);

        // assert
        assertEquals(4, result.size());

        assertTrue(areEqual(dp01, result.get(0).drawingPoint));
        assertTrue(areEqual(dp02, result.get(3).drawingPoint));

        assertTrue(areEqual(gl01, result.get(0).geoLocation));
        assertTrue(areEqual(gl02, result.get(1).geoLocation));
        assertTrue(areEqual(gl03, result.get(2).geoLocation));
        assertTrue(areEqual(gl04, result.get(3).geoLocation));
    }

    private boolean areEqual(DrawingPoint dp1, DrawingPoint dp2){
        return dp1.x == dp2.x && dp1.y == dp2.y && dp1.radius == dp2.radius;
    }

    private boolean areEqual(GeoLocation gl1, GeoLocation gl2){
        return gl1.latitude == gl2.latitude && gl1.longitude == gl2.longitude && gl1.altitude == gl2.altitude && gl1.radius == gl2.radius;
    }

}
