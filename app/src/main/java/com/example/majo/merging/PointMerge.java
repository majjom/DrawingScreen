package com.example.majo.merging;

import android.location.Location;

import com.example.majo.BusinessObjects.DrawingPoint;
import com.example.majo.BusinessObjects.GeoLocation;
import com.example.majo.BusinessObjects.MappedPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by majo on 14-Dec-14.
 */
public class PointMerge {

    private int minDistanceBetweenDrawingPoints;


    // todo put this away
    public double getPathSize(ArrayList<GeoLocation> geoLocations){
        return this.getPathSizeForLocations(geoLocations);
    }

    public PointMerge(){
        this(10);
    }

    public PointMerge(int minDistanceBetweenDrawingPoints){
        this.minDistanceBetweenDrawingPoints = minDistanceBetweenDrawingPoints;
    }


    public static List<MappedPoint> mergePoints(List<DrawingPoint> drawingPoints, List<GeoLocation> geoLocations){
        if ((drawingPoints == null) || (geoLocations == null)) {
            return null;
        }


        // TODO this is too easy thing, rework needed
        ArrayList<MappedPoint> result = new ArrayList<>();
        if (drawingPoints.size() > geoLocations.size()){
            int i=0;
            for (GeoLocation geoLocation : geoLocations){
                result.add(create(geoLocation, drawingPoints.get(i)));
                i++;
            }
        } else {
            int i=0;
            for (DrawingPoint drawingPoint : drawingPoints){
                result.add(create(geoLocations.get(i), drawingPoint));
                i++;
            }
        }


        /*


        int dpPathSize = getPathSizeForDrawingPoints(drawingPoints);
        double glPathSize = getPathSizeForLocations(geoLocations);

        // calculate how many points will be in the result - geoLocations determine or min distance on the map
        int dpMinCount = Math.round(dpPathSize / minDistanceBetweenDrawingPoints);
        int pointsCount = Math.max(dpMinCount, geoLocations.size());

        // calculate the "homogenuos" distance
        int dpDistance = Math.round(dpPathSize / pointsCount);
        double glDistance = glPathSize / pointsCount;

        // interpolate the drawing points
        for (int i = 0; i < pointsCount; i++) {

        }

        // interpolate the location points
*/


        return result;
    }

    private static MappedPoint create(GeoLocation geoLocation, DrawingPoint drawingPoint){
        return new MappedPoint(drawingPoint.x, drawingPoint.y, drawingPoint.radius, geoLocation.latitude, geoLocation.longitude, geoLocation.altitude, geoLocation.radius);
    }


    /**
     * Makes count geoLocations out of X geoLocations, interpolated.
     * @param geoLocations The input geo locations
     *                     if count <= 2 than only fist and last will be returned, or fist and first, or empty
     * @param count How many output locations will be returned
     *              if count <= 2 than only first and last are returned
     * @return
     */
    private ArrayList<GeoLocation> interpolateGeoLocations(ArrayList<GeoLocation> geoLocations, int count){
        ArrayList<GeoLocation> result = new ArrayList<>();

        // special border cases
        if (geoLocations.size() == 0) {
            return result;
        }
        if (geoLocations.size() == 1) {
            result.add(geoLocations.get(0)); // first - the only one
            result.add(geoLocations.get(0)); // last - the only one
            return result;
        }
        if (count <= 2){
            result.add(geoLocations.get(0)); //fist
            result.add(geoLocations.get(geoLocations.size() - 1)); // last
            return result;
        }

        // general case
        return result;
    }



    private int getPathSizeForDrawingPoints(ArrayList<DrawingPoint> drawingPoints){
        int result = 0;
        DrawingPoint previousDrawingPoint = null;
        for (DrawingPoint drawingPoint : drawingPoints){
            if (previousDrawingPoint != null){
                result += getDistanceForDrawingPoints(previousDrawingPoint, drawingPoint);
            }
            previousDrawingPoint = drawingPoint;
        }

        return result;
    }

    private double getPathSizeForLocations(ArrayList<GeoLocation> geoLocations){
        double result = 0;
        Location previousGeoLocation = null;
        for (GeoLocation geoLocation : geoLocations){
            if (previousGeoLocation != null)
            {
                result += getDistanceForLocations(previousGeoLocation, convertToLocation(geoLocation));
            }
            previousGeoLocation = convertToLocation(geoLocation);
        }
        return result;
    }





    /**
     * Gets distance in meters.
     * @return
     */
    private double getDistanceForLocations(Location loc1, Location loc2){
        if ((loc1 == null) || (loc2 == null)) return 0;
        return Math.abs(loc1.distanceTo(loc2));
    }

    private int getDistanceForDrawingPoints(DrawingPoint point1, DrawingPoint point2){
        if ((point1 == null) || (point2 == null)) return 0;
        return (int)Math.abs(Math.round(Math.sqrt((Math.pow((point2.x - point1.x), 2) + Math.pow((point2.y - point1.y), 2)))));
    }

    private Location convertToLocation(GeoLocation geoLocation){
        Location result = new Location(""); // provider name is unecessary
        result.setLatitude(geoLocation.latitude);
        result.setLongitude(geoLocation.longitude);
        result.setAltitude(geoLocation.altitude);
        return result;
    }
}
