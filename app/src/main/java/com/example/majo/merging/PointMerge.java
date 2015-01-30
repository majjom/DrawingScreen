package com.example.majo.merging;

import android.location.Location;

import com.example.majo.BusinessObjects.DrawingPoint;
import com.example.majo.BusinessObjects.GeoLocation;
import com.example.majo.BusinessObjects.MappedPoint;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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

        ArrayList<MappedPoint> result = new ArrayList<>();

        if (drawingPoints.size() == 0) return result;
        if (geoLocations.size() == 0) return result;

        if (drawingPoints.size() == 1){
            result.add(new MappedPoint(drawingPoints.get(0), geoLocations.get(0)));
            return result;
        }

        if (geoLocations.size() == 1){
            result.add(new MappedPoint(drawingPoints.get(0), geoLocations.get(0)));
            return result;
        }

        // remember, since we gonna modify the list
        int geoLocationsSize = geoLocations.size();


        /*
        // TODO this is too easy thing, rework needed
        ArrayList<MappedPoint> result = new ArrayList<>();
        if (drawingPoints.size() > geoLocations.size()){
            int i=0;
            for (GeoLocation geoLocation : geoLocations){
                result.add(new MappedPoint(drawingPoints.get(i), geoLocation));
                i++;
            }
        } else {
            int i=0;
            for (DrawingPoint drawingPoint : drawingPoints){
                result.add(new MappedPoint(drawingPoint, geoLocations.get(i)));
                i++;
            }
        }
        */

        // this is proper implementation

        double dpPathSize = getPathSizeForDrawingPoints(drawingPoints);
        double geoPathSize = getPathSizeForLocations(geoLocations);

        if (geoPathSize == 0) return null;

        double ratioGeoToDp = dpPathSize / geoPathSize;

        // previous point = previous point
        GeoLocation geoPrevious = geoLocations.remove(0);
        DrawingPoint dpPrevious = drawingPoints.remove(0);
        float radius = dpPrevious.radius;
        result.add(new MappedPoint(dpPrevious, geoPrevious));

        Queue<CartesianPoint> workingQueue = copyToQueue(drawingPoints);
        List<CartesianPoint> calculatedPoints = new ArrayList<>();
        CartesianPoint previousPoint = new CartesianPoint(dpPrevious.x, dpPrevious.y);
        for(GeoLocation geoLocation : geoLocations){
            double distance = getDistanceForLocations(geoPrevious, geoLocation);
            CartesianPoint nextPoint = CartesianInterpolation.getNextPointIterative(previousPoint, distance * ratioGeoToDp, workingQueue);
            if (nextPoint != null){
                result.add(new MappedPoint(convertToDrawingPoint(nextPoint, radius), geoLocation));
            }

            // shift
            geoPrevious = geoLocation;
            previousPoint = nextPoint;
        }

        // check if the last point was added, if not add it. Else try to smooth the last one if not smoothed.
        MappedPoint lastMappedPoint = result.get(result.size() - 1);
        DrawingPoint lastDrawingPoint = drawingPoints.get(drawingPoints.size() - 1);
        GeoLocation lastGeoLocation = geoLocations.get(geoLocations.size() - 1);
        if (result.size() < geoLocationsSize){
            result.add(new MappedPoint(lastDrawingPoint, lastGeoLocation));
        } else {
            // the last DP might be little in distance from the last point, make the last mapped point exactly the same coordinates as last DP.
            lastMappedPoint.drawingPoint.x = lastDrawingPoint.x;
            lastMappedPoint.drawingPoint.y = lastDrawingPoint.y;
        }

        return result;
    }



    private static Queue<CartesianPoint> copyToQueue(List<DrawingPoint> points){
        Queue<CartesianPoint> result = new LinkedList<>();
        for (DrawingPoint point : points){
            result.add(new CartesianPoint(point.x, point.y));
        }
        return result;
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







    private static CartesianPoint convertToCartesianPoint(DrawingPoint drawingPoint){
        return new CartesianPoint(drawingPoint.x, drawingPoint.y);
    }

    private static DrawingPoint convertToDrawingPoint(CartesianPoint cartesianPoint, float radius){
        return new DrawingPoint((float)cartesianPoint.x, (float)cartesianPoint.y, radius);
    }







    private static double getPathSizeForDrawingPoints(List<DrawingPoint> drawingPoints){
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

    private static double getPathSizeForLocations(List<GeoLocation> geoLocations){
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
    private static double getDistanceForLocations(Location loc1, Location loc2){
        if ((loc1 == null) || (loc2 == null)) return 0;
        return Math.abs(loc1.distanceTo(loc2));
    }

    private static double getDistanceForLocations(GeoLocation loc1, GeoLocation loc2){
        return  getDistanceForLocations(convertToLocation(loc1), convertToLocation(loc2));
    }

    private static double getDistanceForDrawingPoints(DrawingPoint point1, DrawingPoint point2){
        if ((point1 == null) || (point2 == null)) return 0;
        return Math.abs(Math.sqrt((Math.pow((point2.x - point1.x), 2) + Math.pow((point2.y - point1.y), 2))));
    }

    private static Location convertToLocation(GeoLocation geoLocation){
        Location result = new Location(""); // provider name is unecessary
        result.setLatitude(geoLocation.latitude);
        result.setLongitude(geoLocation.longitude);
        result.setAltitude(geoLocation.altitude);
        return result;
    }
}
