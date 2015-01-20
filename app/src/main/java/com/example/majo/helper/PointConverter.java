package com.example.majo.helper;

import com.example.majo.BusinessObjects.DrawingPoint;
import com.example.majo.BusinessObjects.GeoLocation;
import com.example.majo.BusinessObjects.MappedPoint;
import com.example.majo.drawingscreen.LayerPoint;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by majo on 17-Jan-15.
 */
public class PointConverter {

    public static List<LayerPoint> drawingPointToLayerPoint(List<DrawingPoint> points){
        if (points == null) return null;
        List<LayerPoint> result = new ArrayList<>();
        for (DrawingPoint point : points){
            result.add(drawingPointToLayerPoint(point));
        }
        return result;
    }

    public static LayerPoint drawingPointToLayerPoint(DrawingPoint point){
        return new LayerPoint(point);
    }



    public static List<DrawingPoint> layerPointToDrawingPoint(List<LayerPoint> points){
        if (points == null) return null;
        List<DrawingPoint> result = new ArrayList<>();
        for (LayerPoint point : points){
            result.add(layerPointToDrawingPoint(point));
        }
        return result;
    }

    public static DrawingPoint layerPointToDrawingPoint(LayerPoint point){
        return point.relatedDrawingPoint;
    }




    public static List<LayerPoint> mappedPointToLayerPoint(List<MappedPoint> points){
        if (points == null) return null;
        List<LayerPoint> result = new ArrayList<>();
        for (MappedPoint point : points){
            result.add(mappedPointToLayerPoint(point));
        }
        return result;
    }

    public static LayerPoint mappedPointToLayerPoint(MappedPoint point){
        return new LayerPoint(point);
    }



    public static List<MappedPoint> layerPointToMappedPoint(List<LayerPoint> points){
        if (points == null) return null;
        List<MappedPoint> result = new ArrayList<>();
        for (LayerPoint point : points){
            result.add(layerPointToMappedPoint(point));
        }
        return result;
    }

    public static MappedPoint layerPointToMappedPoint(LayerPoint point){
        return point.relatedMappedPoint;
    }

}
