package com.example.majo.GoogleMap;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.example.majo.persistence.DatabaseConnection;
import com.example.majo.persistence.GeoLocationPersistence;
import com.example.majo.persistence.IDatabaseConnection;
import com.example.majo.persistence.IGeoLocationPersistence;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by majo on 10-Jan-15.
 * Has access to persistance!!!!
 */
public class GpsTrackerService extends Service implements LocationListener, IGpsTrackerService {
    private static final String LOGTAG = "GpsTrackerService";


    /*reference to self for a singleton thingie*/
    public static GpsTrackerService staticInstance;




    private LocationManager locationManager;
    private List<Location> storedLocations;
    private boolean isTracking = false;

    public static final String BROADCAST_ON_LOCATION_CHANGED = "com.example.majo.GoogleMap.ON_LOCATION_CHANGED";
    public static final String BROADCAST_EXTRA_LOCATION = "EXTRA_LOCATION";
    public static final String BROADCAST_EXTRA_GEO_SESSION_ID = "BROADCAST_EXTRA_GEO_SESSION_ID";

    private IDatabaseConnection databaseConnection;
    private IGeoLocationPersistence persistence;
    private int geoSessionId;


    public static GpsTrackerService getStaticInstance(){
        return staticInstance;
    }

    public GpsTrackerService() {
    }

    public GpsTrackerService(IDatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    /* Service Setup Methods */
    @Override
    public void onCreate() {
        super.onCreate();

        staticInstance = this;

        storedLocations = new ArrayList<Location>();

        // open database
        if (this.databaseConnection == null) {
            this.databaseConnection = new DatabaseConnection(this);
        }
        this.persistence = new GeoLocationPersistence(this.databaseConnection);

        // get location manager
        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        Log.i(LOGTAG, "Tracking Service Running...");
    }

    @Override
    public void onDestroy() {
        staticInstance = null;

        // remove listening to updates
        locationManager.removeUpdates(this);

        // close database
        if (this.databaseConnection!=null){
            this.databaseConnection.destroy();
        }

        Log.i(LOGTAG, "Tracking Service Stopped...");
        super.onDestroy();
    }






    /* Service Iface Methods */
    public void startTracking(int geoSessionId, long minTime, float minDistance) {
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return;
        }

        // change geoSession
        Log.i(LOGTAG, String.format("Started tracking geoSessionId %d", geoSessionId));
        this.geoSessionId = geoSessionId;

        // attach to GPS callback
        if (isTracking) return;
        Log.i(LOGTAG, String.format("Started tracking GPS attached (%d, %s)", minTime, minDistance));
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, this);
        isTracking = true;
    }

    public void stopTracking() {
        Log.i(LOGTAG, "Stopped tracking");
        locationManager.removeUpdates(this);
        isTracking = false;
    }

    public boolean isTracking() {
        return isTracking;
    }

    public int getTrackedGeoSessionId(){
        if (this.isTracking){
            return this.geoSessionId;
        }
        return -1;
    }

    public int getLocationsCount() {
        return storedLocations.size();
    }

    public List<Location> getLocations() {
        return storedLocations;
    }










    /* Service Access Methods Binder */
    public class TrackerBinder extends Binder {
        public IGpsTrackerService getService() {
            return GpsTrackerService.this;
        }
    }

    private final IBinder binder = new TrackerBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return this.binder;
    }








    /* LocationListener Methods */
    @Override
    public void onLocationChanged(Location location) {
        Log.i(LOGTAG, String.format("Adding new location %d %s", storedLocations.size() + 1, location.toString()) );

        // accumulate in RAM and save to DB
        storedLocations.add(location);
        this.persistence.addLocation(this.geoSessionId, LocationConverter.LocationToGeoLocation(location));

        // broadcast new location to whoever is listening
        Intent intent = new Intent(BROADCAST_ON_LOCATION_CHANGED);
        intent.putExtra(BROADCAST_EXTRA_LOCATION, location);
        intent.putExtra(BROADCAST_EXTRA_GEO_SESSION_ID, geoSessionId);
        sendBroadcast(intent);
    }
    @Override
    public void onProviderDisabled(String provider) {
        //TODO
    }
    @Override
    public void onProviderEnabled(String provider) {
        //TODO
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO
    }
}
