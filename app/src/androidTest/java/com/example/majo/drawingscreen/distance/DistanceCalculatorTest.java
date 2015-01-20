package com.example.majo.drawingscreen.distance;

import android.location.Location;
import android.test.InstrumentationTestCase;

import com.example.majo.distance.DistanceCalculator;
import com.example.majo.distance.DistancePoint;
import com.example.majo.merging.CartesianInterpolation;
import com.example.majo.merging.CartesianPoint;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by majo on 20-Jan-15.
 */
public class DistanceCalculatorTest extends InstrumentationTestCase {

    public void testGetDistance(){
        // arrange
        Location loc1 = new Location("loc1");
        LatLng loc11 = new LatLng(47.03225, 8.43479);
        loc1.setLatitude(loc11.latitude);
        loc1.setLongitude(loc11.longitude);

        Location loc2 = new Location("loc2");
        LatLng loc22 = new LatLng(47.03354, 8.43656);
        loc2.setLatitude(loc22.latitude);
        loc2.setLongitude(loc22.longitude);

        // act
        double distance = DistanceCalculator.getDistance(loc11, loc22);
        float distanceGoogle = loc1.distanceTo(loc2);

        // assert
        assertEquals((int)distance, (int)distanceGoogle);
    }

    public void testSortToDistance(){
        // arrange
        LatLng start = new LatLng(47.03225, 8.43479);
        List<LatLng> points = new ArrayList<>();

        LatLng loc1 = new LatLng(47.03316, 8.43811);
        LatLng loc2 = new LatLng(47.03303, 8.43723);
        LatLng loc3 = new LatLng(47.03354, 8.43656);
        points.add(loc1);
        points.add(loc2);
        points.add(loc3);

        // act
        List<DistancePoint> result = DistanceCalculator.sortToDistance(start, points);

        // assert
        assertEquals(result.get(0).point, loc3);
        assertEquals(result.get(1).point, loc2);
        assertEquals(result.get(2).point, loc1);
    }
}
