package com.example.majo.merging;

import android.location.Location;

import com.example.majo.BusinessObjects.DrawingPoint;
import com.example.majo.BusinessObjects.GeoLocation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by majo on 14-Dec-14.
 */
public class CartesianInterpolation {

    /**
     * Interpolates given point by count.
     * @param points The points to be interpolated.
     * @param count The count of points to be returned.
     * @return returns list of COUNT points which are "static distance" far away from each other and which interpolate the given input point array.
     */
    public static ArrayList<CartesianPoint> interpolateByCount(ArrayList<CartesianPoint> points, int count){
        ArrayList<CartesianPoint> result = new ArrayList<>();

        // special border cases
        if (points.size() == 0) {
            return result;
        }
        if (count == 0) {
            return result;
        }

        if (points.size() == 1) {
            result.add(points.get(0)); // first - the only one
            return result;
        }
        if (count == 1){
            result.add(points.get(0)); // first - the only one
            return result;
        }

        if (count <= 2){
            if (points.size() > 0) {
                result.add(points.get(0)); //fist
                result.add(points.get(points.size() - 1)); // last
            }
            return result;
        }

        // general case
        int pathSize = getPathSize(points);
        int distance = Math.round(pathSize / (count - 1)); // first is already there
        result = interpolateByDistance(points, distance);

        // there might be last extra point there (or one missing)
        if (result.size() > count){
            result.remove(result.size() - 1);
        }
        if (result.size() < count){
            result.add(points.get(points.size() - 1));
        }

        return result;
    }

    /**
     * Interpolates given point by static distance.
     * @param points The points to be interpolated.
     * @param distance The static distance.
     * @return returns list of point which are "distance" far away from each other and which interpolate the given input point array.
     */
    public static ArrayList<CartesianPoint> interpolateByDistance(ArrayList<CartesianPoint> points, int distance){
        ArrayList<CartesianPoint> result = new ArrayList<>();
        if (points == null) return null;
        if (points.size() == 0) return result;

        CartesianPoint first = points.remove(0);
        result.add(first); // the first point is always in

        // iteratively
        Queue<CartesianPoint> queue = copyToQueue(points);
        CartesianPoint candidate = getNextPointIterative(first, distance, queue);
        while (candidate != null){
            result.add(candidate);
            candidate = getNextPointIterative(candidate, distance, queue);
        }

        return result;
    }

    /**
     * Calculates the next Point which is in radius distance from base and lies on the poly line defined by points.
     * @param base the base point where to start from.
     * @param radius the radius (distance) of the calculated point. The circle defined by [base, radius] defined where the returned point lies.
     * @param points set of points forming a poly line. The poly line defined where the returned point lies.
     * @return Returns Point found or NULL if no such can be found.
     */
    public static CartesianPoint getNextPoint(CartesianPoint base, int radius, ArrayList<CartesianPoint> points){
        return getNextPointIterative(base, radius, copyToQueue(points));
    }

    /**
     * Calculates the next Point which is in radius distance from base and lies on the poly line defined by points.
     * Returns NULL if no such point can be found
     * @param base the base point where to start from.
     * @param radius the radius (distance) of the calculated point. The circle defined by [base, radius] defined where the returned point lies.
     * @param points set of points forming a poly line. The poly line defined where the returned point lies. These points are also shrunk and the queue after run contains all points which were not consumed by the algorithm.
     * @return Returns Point found or NULL if no such can be found.
     */
    public static CartesianPoint getNextPointIterative(CartesianPoint base, int radius, Queue<CartesianPoint> points){
        // some border cases
        if (base == null) return null;
        if (points == null) return null;
        if (points.size() == 0) return null;
        if (radius <= 0) return base;


        CartesianPoint result = null;

        // main thing
        CartesianPoint linePointA = base;
        CartesianPoint linePointB = points.peek(); // retireve but don't remove

        while(!points.isEmpty()){
            CartesianPoint candidate = intersectionOfLineAndCircleFirst(linePointA, linePointB, base, radius);
            if (candidate != null) {
                if ((candidate.x == linePointB.x) && (candidate.y == linePointB.y)){
                    points.poll(); // consume one guy from the queue, because is matching with the candidate so must be consumed
                }
                result = candidate;
                break; // finish the while cycle. We have our result
            } else {
                points.poll(); // consume one guy from the queue, this was not a good one

                linePointA = linePointB;
                linePointB = points.peek(); // just take another point from queue, but leave in queue
            }
        }

        return result;
    }



    private static Queue<CartesianPoint> copyToQueue(ArrayList<CartesianPoint> points){
        Queue<CartesianPoint> result = new LinkedList<>();
        for (CartesianPoint point : points){
            result.add(point);
        }
        return result;
    }

    private static CartesianPoint intersectionOfLineAndCircleFirst(CartesianPoint linePointA, CartesianPoint linePointB, CartesianPoint circleCentre, int radius){
        ArrayList<CartesianPoint> result = intersectionOfLineAndNonZeroCenterCircle(linePointA, linePointB, circleCentre, radius);
        if ((result == null) || (result.size() == 0)) return null;
        return result.get(0);
    }

    private static ArrayList<CartesianPoint> intersectionOfLineAndNonZeroCenterCircle(CartesianPoint linePointA, CartesianPoint linePointB, CartesianPoint circleCentre, int radius) {
        // the whole thing CircleCentre -> 0,0
        CartesianPoint shiftedA = new CartesianPoint(linePointA.x - circleCentre.x, linePointA.y - circleCentre.y) ;
        CartesianPoint shiftedB = new CartesianPoint(linePointB.x - circleCentre.x, linePointB.y - circleCentre.y) ;

        ArrayList<CartesianPoint> shiftedRsults = intersectionOfLineAndCircle(shiftedA, shiftedB, radius);

        // shift the result back
        ArrayList<CartesianPoint> result = new ArrayList<>();
        for (CartesianPoint shiftedResult : shiftedRsults){
            result.add(new CartesianPoint(shiftedResult.x + circleCentre.x, shiftedResult.y + circleCentre.y));
        }
        return result;
    }

    /**
     * Returns 0intersection of the line defined by 2 points [A,B] and circle [centre,radius].
     * Only segment from A to B is taken into consideration when finding interception, not the whole line
     * Formulas taken from http://mathworld.wolfram.com/Circle-LineIntersection.html
     * @param linePointA
     * @param linePointB
     * @param radius
     * @return Returns 0,1,2 results
     */
    private static ArrayList<CartesianPoint> intersectionOfLineAndCircle(CartesianPoint linePointA, CartesianPoint linePointB, int radius){
        ArrayList<CartesianPoint> result = new ArrayList<>();
        if ((linePointA.x == linePointB.x) && (linePointA.y == linePointB.y)) return result; // special case, where the line is not correctly defined


        double dx = get_dx(linePointA, linePointB, radius);
        double dy = get_dy(linePointA, linePointB, radius);
        double dr = get_dr(linePointA, linePointB, radius);
        double D = get_D(linePointA, linePointB, radius);
        double discriminant = get_Discriminant(linePointA, linePointB, radius);

        if (discriminant >= 0) {
            double rightUpperX = sgn(dy) * dx * Math.sqrt(discriminant);
            double rightUpperY = Math.abs(dy) * Math.sqrt(discriminant);

            CartesianPoint result01 = new CartesianPoint();
            result01.x = (int) Math.round((D * dy + rightUpperX) / (dr * dr));
            result01.y = (int) Math.round((-1 * D * dx + rightUpperY) / (dr * dr));

            CartesianPoint result02 = new CartesianPoint();
            result02.x = (int) Math.round((D * dy - rightUpperX) / (dr * dr));
            result02.y = (int) Math.round((-1 * D * dx - rightUpperY) / (dr * dr));

            if (discriminant > 0) {
                if (liesPointInABLineSegment(result01, linePointA, linePointB)) {
                    result.add(result01);
                }
                if (liesPointInABLineSegment(result02, linePointA, linePointB)) {
                    result.add(result02);
                }
            }
            if (discriminant == 0) {
                // result1 and result2 are the same, choose one
                if (liesPointInABLineSegment(result01, linePointA, linePointB)) {
                    result.add(result01);
                }
            }
        }
        // discriminant is negative -> no solution -> empty result

        return result;
    }

    private static double get_dx(CartesianPoint linePointA, CartesianPoint linePointB, int radius) {
        return linePointB.x - linePointA.x;
    }

    private static double get_dy(CartesianPoint linePointA, CartesianPoint linePointB, int radius) {
        return linePointB.y - linePointA.y;
    }

    private static double get_dr(CartesianPoint linePointA, CartesianPoint linePointB, int radius) {
        double dx = get_dx(linePointA, linePointB, radius);
        double dy = get_dy(linePointA, linePointB, radius);
        return Math.sqrt(dx*dx + dy*dy);
    }

    private static double get_D(CartesianPoint linePointA, CartesianPoint linePointB, int radius) {
        return linePointA.x*linePointB.y - linePointB.x * linePointA.y;
    }

    private static double get_Discriminant(CartesianPoint linePointA, CartesianPoint linePointB, int radius) {
        double dr = get_dr(linePointA, linePointB, radius);
        double D = get_D(linePointA, linePointB, radius);
        return radius * radius * dr * dr - (D * D);
    }

    private static boolean  liesPointInABLineSegment(CartesianPoint point, CartesianPoint linePointA, CartesianPoint linePointB){
        boolean result = false;
        // special case
        if (linePointB.x == linePointA.x){
            result =  point.x == linePointA.x && linePointA.y <= point.y && point.y <= linePointB.y;
            return result;
        }

        // special case
        if (linePointB.y == linePointA.y){
            result = point.y == linePointA.y && linePointA.x <= point.x && point.x <= linePointB.x;
            return result;
        }

        // general case
        double t2 = (point.y - linePointA.y) / (linePointB.y - linePointA.y);
        double t1 = (point.x - linePointA.x) / (linePointB.x - linePointA.x);
        result = t1 == t2 && t1 >= 0 && t1 <= 1;
        return result;
    }

    private static int sgn(double input){
        if (input < 0) return -1;
        return 1;
    }

    private static int getPathSize(ArrayList<CartesianPoint> points){
        int result = 0;
        CartesianPoint previousPoint = null;
        for (CartesianPoint point : points){
            if (previousPoint != null){
                result += getDistance(previousPoint, point);
            }
            previousPoint = point;
        }

        return result;
    }

    private static double getDistance(CartesianPoint point1, CartesianPoint point2){
        if ((point1 == null) || (point2 == null)) return 0;
        return (int)Math.abs(Math.round(Math.sqrt(Math.pow((point2.x - point1.x), 2) + Math.pow((point2.y - point1.y), 2))));
    }
}
