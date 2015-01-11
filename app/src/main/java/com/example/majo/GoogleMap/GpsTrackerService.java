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
import android.widget.Toast;

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

    private LocationManager locationManager;
    private List<Location> storedLocations;
    private boolean isTracking = false;

    public static final String BROADCAST_ON_LOCATION_CHANGED = "com.example.majo.GoogleMap.ON_LOCATION_CHANGED";
    public static final String BROADCAST_EXTRA_LOCATION = "EXTRA_LOCATION";

    private IDatabaseConnection databaseConnection;
    private IGeoLocationPersistence persistence;
    private int geoSessionId;

    public GpsTrackerService() {
    }

    public GpsTrackerService(IDatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    /* Service Setup Methods */
    @Override
    public void onCreate() {
        super.onCreate();

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
        // remove listening to updates
        locationManager.removeUpdates(this);

        // close database
        if (this.databaseConnection!=null){
            this.databaseConnection.onDestroy();
        }
        Log.i(LOGTAG, "Tracking Service Stopped...");
        super.onDestroy();
    }






    /* Service Iface Methods */
    public void startTracking(int geoSessionId, long minTime, float minDistance) {
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return;
        }
        this.geoSessionId = geoSessionId;
        if (isTracking) return;
        Log.i(LOGTAG, String.format("Started tracking (%d, %s)", minTime, minDistance));
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

    public int getLocationsCount() {
        return storedLocations.size();
    }

    public List<Location> getLocations() {
        return storedLocations;
    }










    /* Service Access Methods Binder */
    public class TrackerBinder extends Binder {
        public GpsTrackerService getService() {
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

        Intent intent = new Intent(BROADCAST_ON_LOCATION_CHANGED);
        intent.putExtra(BROADCAST_EXTRA_LOCATION, location);
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
