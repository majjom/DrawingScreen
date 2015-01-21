package com.example.majo.drawingscreen.distance;

import android.location.Location;
import android.test.InstrumentationTestCase;

import com.example.majo.BusinessObjects.GeoLocation;
import com.example.majo.BusinessObjects.MappedPoint;
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
        LatLng latLng1 = new LatLng(47.03225, 8.43479);

        Location loc1 = new Location("loc1");
        loc1.setLatitude(latLng1.latitude);
        loc1.setLongitude(latLng1.longitude);
        MappedPoint mp1 = new MappedPoint(null, new GeoLocation(latLng1.latitude, latLng1.longitude, 0, 10));

        Location loc2 = new Location("loc2");
        LatLng loc22 = new LatLng(47.03354, 8.43656);
        loc2.setLatitude(loc22.latitude);
        loc2.setLongitude(loc22.longitude);

        // act
        double distance = DistanceCalculator.getDistance(mp1, loc2);
        float distanceGoogle = loc1.distanceTo(loc2);

        // assert
        assertEquals((int)distance, (int)distanceGoogle);
    }

    public void testSortToDistance(){
        // arrange
        Location start = new Location("loc1");
        start.setLatitude(47.03225);
        start.setLongitude(8.43479);


        List<MappedPoint> points = new ArrayList<>();

        MappedPoint loc1 = new MappedPoint(null, new GeoLocation(47.03316, 8.43811, 0, 10));
        MappedPoint loc2 = new MappedPoint(null, new GeoLocation(47.03303, 8.43723, 0, 10));
        MappedPoint loc3 = new MappedPoint(null, new GeoLocation(47.03354, 8.43656, 0, 10));
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
