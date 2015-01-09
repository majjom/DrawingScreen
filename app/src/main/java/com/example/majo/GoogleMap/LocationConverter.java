package com.example.majo.GoogleMap;

import android.location.Location;

import com.example.majo.BusinessObjects.GeoLocation;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by majo on 10-Jan-15.
 */
public class LocationConverter {
    public static LatLng GeoLocationToLatLng(GeoLocation geoLocation){
        if (geoLocation == null) return null;
        return new LatLng(geoLocation.latitude, geoLocation.longitude);
    }

    public static List<LatLng> GeoLocationToLatLng(List<GeoLocation> geoLocations){
        if (geoLocations == null) return null;
        List<LatLng> result = new ArrayList<>();
        for (GeoLocation geoLocation : geoLocations){
            result.add(GeoLocationToLatLng(geoLocation));
        }
        return result;
    }

    public static LatLng LocationToLatLng(Location location){
        if (location == null) return null;
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    public static List<LatLng> LocationToLatLng(List<Location> locations){
        if (locations == null) return null;
        List<LatLng> result = new ArrayList<>();
        for (Location location : locations){
            result.add(LocationToLatLng(location));
        }
        return result;
    }

    public static GeoLocation LocationToGeoLocation(Location location){
        if (location == null) return null;
        return new GeoLocation(location.getLatitude(), location.getLongitude(), location.getAltitude(), /*TODO */ 10);
    }
}
