package com.example.majo.persistenceLocalDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by majo on 14-Dec-14.
 */
public class ConversionHelper {
    private static int drawingPointDecimalPlaces = 100;
    private static int geoLocationDecimalPlaces = 1000000; //1e6

    SimpleDateFormat iso8601Format = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");

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


    public static long dateToLong(Date date){
        return date.getTime();
    }

    public static Date longToDate(long date){
        return new Date(date);
    }
}
