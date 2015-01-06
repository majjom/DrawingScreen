package com.example.majo.drawingscreen;

import android.graphics.Color;
import android.location.Location;

import com.example.majo.BusinessObjects.GeoLocation;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by majo on 13-Dec-14.
 */
public class GoogleMapsWrapper {

    private GoogleMap map;

    private Marker marker;

    private Polyline polyLine;
    LatLng firstTravelPoint;


    LatLng lastCenteredPoint;
    int lastZoomLevel;




    // locationsCache drawn by GPS, shall be remembered so they can be sumited to DB
    private ArrayList<Location> locationsCache;


    public GoogleMapsWrapper(GoogleMap map){
        this.map = map;
        this.locationsCache = new ArrayList<>();

        this.lastCenteredPoint = null;
        this.lastZoomLevel = 13;
    }


    ArrayList<GeoLocation> getCachedGeoLocations(){
        ArrayList<GeoLocation> result = new ArrayList<>();
        for (Location travelPoint : this.locationsCache){
            result.add(LocationToGeoLocation(travelPoint));
        }
        return result;
    }

    public void clearAllTravelPoints(){
        clearLastTravelingMarker();
        clearPolyLine();
        map.clear();
        this.locationsCache = new ArrayList<>();
    }

    public void addTravelPoint(Location travelPoint){
        if (travelPoint == null) return;

        // add to cache
        this.locationsCache.add(travelPoint);

        // draw
        addTravelPointLatLng(new LatLng(travelPoint.getLatitude(), travelPoint.getLongitude()));

    }

    public void addTravelPoints(ArrayList<Location> travelPoints){
        if (travelPoints == null) return;

        // add to cache
        this.locationsCache.addAll(travelPoints);

        // draw
        ArrayList<LatLng> latLon = new ArrayList<>();
        for (Location travelPoint : travelPoints){
            latLon.add(new LatLng(travelPoint.getLatitude(), travelPoint.getLongitude()));
        }
        addTravelPointsLatLng(latLon);
    }

    public void addTravelPointLatLng(LatLng travelPoint){
        if (travelPoint == null) return;
        setNewTravelingMarker(travelPoint);
        addPolyLinePoint(travelPoint);
        centerMap(travelPoint, this.lastZoomLevel);
    }

    public void addTravelPointsLatLng(ArrayList<LatLng> travelPoints){
        if (travelPoints == null) return;
        if (travelPoints.size() <= 0) return;

        for (LatLng travelPoint : travelPoints){
            addPolyLinePoint(travelPoint);
        }

        LatLng travelPoint = travelPoints.get(travelPoints.size() - 1);
        setNewTravelingMarker(travelPoint);
        centerMap(travelPoint, this.lastZoomLevel);
    }










    public void drawStoredGeoLocations(ArrayList<GeoLocation> travelPoints){
        if (travelPoints == null) return;

        ArrayList<LatLng> latLon = new ArrayList<>();
        for (GeoLocation travelPoint : travelPoints){
            latLon.add(new LatLng(travelPoint.latitude, travelPoint.longitude));
        }

        map.addPolyline(new PolylineOptions().addAll(latLon)
                .width(5).color(Color.MAGENTA).geodesic(true));

        if (latLon.size() > 0) {
            centerMap(latLon.get(latLon.size() - 1), this.lastZoomLevel);
        }
    }

    public void reZoomMap(int mapZoom){
        if (this.lastCenteredPoint == null) return;
        centerMap(this.lastCenteredPoint, mapZoom);
    }







    private void centerMap(LatLng mapCenter, int mapZoom){
        this.lastCenteredPoint = mapCenter;
        this.lastZoomLevel = mapZoom;
        CameraUpdate newCamera = CameraUpdateFactory.newLatLngZoom(mapCenter, mapZoom);
        map.moveCamera(newCamera);
    }




    private GeoLocation LocationToGeoLocation(Location location){
        return new GeoLocation(location.getLatitude(), location.getLongitude(), location.getAltitude(), 10);
    }





    private void clearLastTravelingMarker() {
        if (marker != null){
            marker.remove();
        }
    }

    private void setNewTravelingMarker(LatLng markPosition){
        clearLastTravelingMarker();
        marker = map.addMarker(new MarkerOptions().position(markPosition));
    }







    private void clearPolyLine(){
        if (this.polyLine != null){
            this.polyLine.remove();
            this.polyLine = null;
            this.firstTravelPoint = null;
        }
    }

    private void addPolyLinePoint(LatLng point){
        if (this.firstTravelPoint == null){
            // first point
            this.firstTravelPoint = point;
        }else {
            if (this.polyLine == null){
                // fist two points, stating the line
                this.polyLine = map.addPolyline(new PolylineOptions().add(this.firstTravelPoint, point)
                        .width(5).color(Color.GREEN).geodesic(true));
            } else {
                // continuing the line
                List<LatLng> points = this.polyLine.getPoints();
                points.add(point);
                this.polyLine.setPoints(points);
            }
        }
    }


}
