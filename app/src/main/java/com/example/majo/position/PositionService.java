package com.example.majo.position;

import android.location.Location;
import android.util.Log;

import com.example.majo.BusinessObjects.DrawingPoint;
import com.example.majo.BusinessObjects.MappedPoint;
import com.example.majo.persistence.IMappedPointsPersistence;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by majo on 14-Dec-14.
 */
public class PositionService implements IPositionService {

    private IMappedPointsPersistence db;
    private int mapId;

    private MappedPoint lastPosition;
    private ArrayList<MPLComposite> cache;

    public PositionService(IMappedPointsPersistence db, int mapId){
        this.db = db;
        this.mapId = mapId;
    }

    @Override
    public MappedPoint getLastPosition() {
        return lastPosition;
    }

    @Override
    public MappedPoint getCurrentPosition(Location location) {
        lastPosition = firstOrDefault(getMatchingPoints(location));
        return getLastPosition();
    }



    private MappedPoint firstOrDefault(ArrayList<MappedPoint> points){
        if ((points == null) || (points.size() <= 0)) return null;
        return points.get(0);
    }

    private ArrayList<MappedPoint> getMatchingPoints(Location location) {
        ArrayList<MappedPoint> result = new ArrayList<>();

        // 01) create cache
        if (cache == null){
            cache = convertToMPLComposite(db.getAllPoints(this.mapId));
        }

        // 02) check distances (linear complexity)
        for (MPLComposite compositePoint : cache){
            Log.d("dist", String.valueOf(compositePoint.point.id) + ";" + String.valueOf(location.distanceTo(compositePoint.location)) + "/" + String.valueOf(compositePoint.point.geoRadius));
            if (location.distanceTo(compositePoint.location) <= compositePoint.point.geoRadius){
                result.add(compositePoint.point);
            }
        }

        return result;
    }

    private DrawingPoint convertToDrawingPoint(MappedPoint point){
        if (point == null) return null;
        return new DrawingPoint(point.drawingX, point.drawingY, point.drawingRadius);
    }

    private ArrayList<MPLComposite> convertToMPLComposite(List<MappedPoint> points){
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

        /*
        Bundle bundle = new Bundle();
        bundle.putDouble(RADIUS, point.geoRadius);
        result.setExtras(bundle);
        */

        return result;
    }
}
