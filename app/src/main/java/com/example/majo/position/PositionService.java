package com.example.majo.position;

import android.location.Location;
import android.os.Bundle;

import com.example.majo.BusinessObjects.DrawingPoint;
import com.example.majo.BusinessObjects.MappedPoint;
import com.example.majo.persistence.IMappedPointsPersistence;

import java.util.ArrayList;

/**
 * Created by majo on 14-Dec-14.
 */
public class PositionService implements IPositionService {

    private final String RADIUS = "Radius";

    private IMappedPointsPersistence db;

    DrawingPoint lastPosition;
    ArrayList<MPLComposite> cache;


    public PositionService(IMappedPointsPersistence db){
        this.db = db;
    }


    @Override
    public DrawingPoint getCurrentPosition() {
        return lastPosition;
    }

    @Override
    public DrawingPoint getCurrentPosition(Location location) {
        lastPosition = convertToDrawingPoint(firstOrDefault(getMatchingPoints(location)));
        return getCurrentPosition();
    }

    private MappedPoint firstOrDefault(ArrayList<MappedPoint> points){
        if ((points == null) || (points.size() <= 0)) return null;
        return points.get(1);
    }


    private ArrayList<MappedPoint> getMatchingPoints(Location location) {
        ArrayList<MappedPoint> result = new ArrayList<>();

        // 01) create cache
        if (cache == null){
            cache = convertToMPLComposite(db.getAllPoints(0));
        }

        // 02) check distances (linear complexity)
        for (MPLComposite compositePoint : cache){
            if (location.distanceTo(compositePoint.location) <= compositePoint.point.geoRadius){
                result.add(compositePoint.point);
            }
        }

        return result;
    }

    private DrawingPoint convertToDrawingPoint(MappedPoint point){
        return new DrawingPoint(point.drawingX, point.drawingY, point.drawingRadius);
    }

    private ArrayList<MPLComposite> convertToMPLComposite(ArrayList<MappedPoint> points){
        ArrayList<MPLComposite> result = new ArrayList<>();
        for (MappedPoint point : points){
            result.add(new MPLComposite(convertToLocation(point), point));
        }
        return result;
    }

    private Location convertToLocation(MappedPoint point){
        Location result = new Location(""); // provider name is unecessary
        result.setLatitude(point.geoLatitude);
        result.setLongitude(point.geoLongitude);
        result.setAltitude(point.geoAltitude);

        Bundle bundle = new Bundle();
        bundle.putDouble(RADIUS, point.geoRadius);
        result.setExtras(bundle);

        return result;
    }
}
