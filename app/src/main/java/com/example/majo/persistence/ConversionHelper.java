package com.example.majo.persistence;

/**
 * Created by majo on 14-Dec-14.
 */
public class ConversionHelper {
    private static int drawingPointDecimalPlaces = 100;
    private static int geoLocationDecimalPlaces = 1000000; //1e6

    public static int drawingPointToInt(float drawingPointValue){
        return Math.round(drawingPointValue * drawingPointDecimalPlaces);
    }

    public static float intToDrawingPoint(int drawingPointValue){
        float result = (float) drawingPointValue / drawingPointDecimalPlaces;
        return result;
    }

    public static int geoLocationToInt(double geoLocationValue){
        return (int)Math.round(geoLocationValue * geoLocationDecimalPlaces);
    }

    public static double intToGeoLocation(int geoLocationValue){
        double result = (double) geoLocationValue / geoLocationDecimalPlaces;
        return result;
    }

}
