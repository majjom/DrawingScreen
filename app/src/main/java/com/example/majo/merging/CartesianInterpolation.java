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

    public static ArrayList<CartesianPoint> interpolate(ArrayList<CartesianPoint> points, int count){
        ArrayList<CartesianPoint> result = new ArrayList<>();
        if (points == null) return null;

        // special border cases
        if (points.size() == 0) {
            return result;
        }
        if (points.size() == 1) {
            result.add(points.get(0)); // first - the only one
            result.add(points.get(0)); // last - the only one
            return result;
        }
        if (count <= 2){
            result.add(points.get(0)); //fist
            result.add(points.get(points.size() - 1)); // last
            return result;
        }

        // general case
        int distance = Math.round(getPathSize(points) / count);
        CartesianPoint first = points.remove(0);
        result.add(first); // the first point is always in
        PPSComposite intermResult = getNextPoint(first, distance, points);
        while (intermResult.leftoverPoints.size() > 0){
            if (intermResult.point != null){
                result.add(intermResult.point);
            }
            intermResult = getNextPoint(first, distance, intermResult.leftoverPoints);
        }

        return result;
    }

    /**
     * For a given [base] find another point which is in distance of radius from the base, and lies somewhere between the other points
     *
     * @param base
     * @param radius
     * @param points
     * @return
     *  Returns the point found or NULL, if not such thing
     *  Returns also Array of points that are leftovers and have not been consumed.
     */
    private static PPSComposite getNextPoint(CartesianPoint base, int radius, ArrayList<CartesianPoint> points){
        PPSComposite result = new PPSComposite();
        result.leftoverPoints = new ArrayList<>();
        if ((points == null) || (points.size() == 0)) return result;

        for (int i = 0; i < points.size(); i++) {
            CartesianPoint theNextPoint = getOneOrZeroIntersection(base, points.get(i), base, radius);
            if (theNextPoint != null){
                result.point = theNextPoint;
                result.leftoverPoints = new ArrayList<>();
                for (int j = i; j < points.size(); j++) {
                    result.leftoverPoints.add(points.get(i));
                }
            }
        }

        return result;

    }

    private static CartesianPoint getOneOrZeroIntersection(CartesianPoint linePointA, CartesianPoint linePointB, CartesianPoint circleCentre, int radius){
        ArrayList<CartesianPoint> result = intersectionOfLineAndCircle(linePointA, linePointB, circleCentre, radius);
        if ((result == null) || (result.size() == 0)) return null;
        return result.get(0);
    }

    private static ArrayList<CartesianPoint> intersectionOfLineAndCircle(CartesianPoint linePointA, CartesianPoint linePointB, CartesianPoint circleCentre, int radius){
        ArrayList<CartesianPoint> result = new ArrayList<>();

        double a = get_a(linePointA, linePointB, circleCentre, radius);
        double b = get_b(linePointA, linePointB, circleCentre, radius);
        double determinant = getDeterminant(linePointA, linePointB, circleCentre, radius);
        if (a == 0) return result;

        if (determinant > 0){
            double t1 = (-1 * b + Math.sqrt(determinant)) / 2 * a;
            double t2 = (-1 * b - Math.sqrt(determinant)) / 2 * a;

            // if the spolocny bod lezi na usecke A-B
            if ((t1 >= -1) && (t1 <=1)){
                result.add(getLineSolution(linePointA, linePointB, t1));
            }
            if ((t2 >= -1) && (t2 <=1)){
                result.add(getLineSolution(linePointA, linePointB, t2));
            }
        } else {
            double t = (-1 * b ) / 2 * a;
            // if the spolocny bod lezi na usecke A-B
            if ((t >= -1) && (t <=1)){
                result.add(getLineSolution(linePointA, linePointB, t));
            }
        }
        // determinant is negative -> no solution -> empty result

        return result;
    }

    private static double get_a(CartesianPoint linePointA, CartesianPoint linePointB, CartesianPoint circleCentre, int radius){
        return Math.pow(linePointB.x - linePointA.x, 2) + Math.pow(linePointB.y - linePointA.y, 2);
    }

    private static double get_b(CartesianPoint linePointA, CartesianPoint linePointB, CartesianPoint circleCentre, int radius){
        return Math.pow(2*(linePointB.x - linePointA.x) * (linePointA.x - circleCentre.x) + 2*(linePointB.y - linePointA.y) * (linePointA.y - circleCentre.y), 2);
    }

    private static double get_c(CartesianPoint linePointA, CartesianPoint linePointB, CartesianPoint circleCentre, int radius){
        return Math.pow(linePointA.x - circleCentre.x, 2) + Math.pow(linePointA.y - circleCentre.y, 2) - Math.pow(radius, 2);
    }

    private static double getDeterminant(CartesianPoint linePointA, CartesianPoint linePointB, CartesianPoint circleCentre, int radius){
        double a = get_a(linePointA, linePointB, circleCentre, radius);
        double b = get_b(linePointA, linePointB, circleCentre, radius);
        double c = get_c(linePointA, linePointB, circleCentre, radius);
        return Math.sqrt(b*b - 4*a*c);
    }

    /**
     * Solution of line equation when the parameter t is known. Returns only point if it is in between the other two points!!!!
     * @param linePointA
     * @param linePointB
     * @param t
     * @return
     */
    private static CartesianPoint getLineSolution(CartesianPoint linePointA, CartesianPoint linePointB, double t){
        CartesianPoint result = new CartesianPoint();
        result.x = (int)Math.round(linePointA.x + t * (linePointB.x - linePointA.x));
        result.y = (int)Math.round(linePointA.y + t * (linePointB.y - linePointA.y));

        return result;
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
        return (int)Math.abs(Math.round(Math.sqrt((Math.pow((point2.x - point1.x), 2) + Math.pow((point2.y - point1.y), 2)))));
    }

}
