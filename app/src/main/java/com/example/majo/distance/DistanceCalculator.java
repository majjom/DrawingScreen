package com.example.majo.distance;

import android.location.Location;
import android.util.FloatMath;

import com.example.majo.BusinessObjects.GeoLocation;
import com.example.majo.BusinessObjects.MappedPoint;
import com.example.majo.drawingscreen.LayerPoint;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by majo on 20-Jan-15.
 */
public class DistanceCalculator {

    public static List<DistancePoint> sortToDistance(Location location, List<MappedPoint> points) {
        List<DistancePoint> result = new ArrayList<>();

        for (MappedPoint point : points){
            result.add(new DistancePoint(point, getDistance(point, location)));
        }

        Collections.sort(result, new Comparator<DistancePoint>() {
            @Override
            public int compare(DistancePoint lhs, DistancePoint rhs) {
                if (rhs.distance - lhs.distance > 0){
                    return -1;
                } else {
                    if (rhs.distance - lhs.distance < 0){
                        return 1;
                    }
                    return 0;
                }
            }
        });

        return result;
    }


    public static double getDistance(MappedPoint a, Location b) {
        return gps2m(a.geoLocation.latitude, a.geoLocation.longitude, b.getLatitude(), b.getLongitude());
    }

    private static double gps2m(double lat_a, double lng_a, double lat_b, double lng_b) {
        double pk = (float) (180/3.14169);

        double a1 = lat_a / pk;
        double a2 = lng_a / pk;
        double b1 = lat_b / pk;
        double b2 = lng_b / pk;

        double t1 = Math.cos(a1)*Math.cos(a2)*Math.cos(b1)*Math.cos(b2);
        double t2 = Math.cos(a1)*Math.sin(a2)*Math.cos(b1)*Math.sin(b2);
        double t3 = Math.sin(a1)*Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return Math.abs(6366000*tt);
    }
}
