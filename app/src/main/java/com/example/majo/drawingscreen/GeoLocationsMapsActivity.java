package com.example.majo.drawingscreen;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.majo.BusinessObjects.GeoLocation;
import com.example.majo.GoogleMap.GpsTrackerService;
import com.example.majo.GoogleMap.IGpsTrackerService;
import com.example.majo.GoogleMap.IPolyLineDrawer;
import com.example.majo.GoogleMap.LocationConverter;
import com.example.majo.GoogleMap.PolyLineDrawer;
import com.example.majo.persistence.DatabaseConnection;
import com.example.majo.persistence.GeoLocationPersistence;
import com.example.majo.persistence.IDatabaseConnection;
import com.example.majo.persistence.IGeoLocationPersistence;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;

public class GeoLocationsMapsActivity extends FragmentActivity {

    private LocationManager locationManager;

    IPolyLineDrawer googleMapsWrapper;

    IGpsTrackerService trackerService;

    TextView locationCountText;
    ImageButton trackerServiceStatusButton;

    private int geoSessionId;


    private IntentFilter filter = new IntentFilter(GpsTrackerService.BROADCAST_ON_LOCATION_CHANGED);
    private GpsLocationChangedBroadcastReceiver receiver = new GpsLocationChangedBroadcastReceiver();
    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_locations_maps);

        this.serviceIntent = new Intent(this, GpsTrackerService.class);

        // todo get from context
        this.geoSessionId = 1;

        // location manager just for initiation and getting cached position
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        // google map + wrapper
        SupportMapFragment mMapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        googleMapsWrapper = new PolyLineDrawer(mMapFragment.getMap());

        this.locationCountText = (TextView)findViewById(R.id.textView);
        this.trackerServiceStatusButton = (ImageButton)findViewById(R.id.trackingStatus);

        // load from persistence
        this.loadGeoLocationsFromDb();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // try to enable Location manager (if not than quit activity)
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){

            // ask user to enable this
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Location manager");
            builder.setMessage("Wanna enable GPS?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(i);
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

            builder.create().show();
        }

        // get cached location, if it exists and center map accordingly (don't save this point as data)
        this.googleMapsWrapper.putMarkerAndCenter(LocationConverter.LocationToLatLng(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)));

        // run the GpsTrackerService (if service is already running, than nothing will be done inside the service although the intent will be sent)
        startService(serviceIntent);

        //Bind to the service so the activity can call methods of the service [Activity -> Service]
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

        // register the receiver so the service can send messages back to activity [Service -> Activity]
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        // if the service is not tracking than it makes no sense for it to run in background
        // if we leave the activity -> stop service. If we than unbound it than service will die.
        // If service is tracking than don't kill it, just unbound. Service will survive.
        if(!trackerService.isTracking()) {
            // Stopping the service lets it die once unbound
            stopService(serviceIntent);
        }
        unregisterReceiver(receiver);

        super.onPause();
    }


    @Override
    protected void onDestroy() {
        //Unbind from the service
        unbindService(serviceConnection);

        super.onDestroy();
    }

    private void addLocation(Location location){
        if (location != null) {
            googleMapsWrapper.add(LocationConverter.LocationToLatLng(location));
            this.locationCountText.setText(String.valueOf(googleMapsWrapper.getPoints().size()));
        }
    }

    public void loadGeoLocationsFromDb(){
        // open DB, get data, close DB
        IDatabaseConnection db = new DatabaseConnection(this);
        IGeoLocationPersistence persistence = new GeoLocationPersistence(new DatabaseConnection(this));
        ArrayList<GeoLocation> locationsFromDb = persistence.getAllLocations(this.geoSessionId);
        googleMapsWrapper.add(LocationConverter.GeoLocationToLatLng(locationsFromDb));
        db.onDestroy();
    }

    private void updateTrackerServiceStatusButton(){
        if (trackerService == null) return;

        if (trackerService.isTracking()){
            trackerServiceStatusButton.setBackgroundResource(R.drawable.ic_pause);
        } else {
            trackerServiceStatusButton.setBackgroundResource(R.drawable.ic_play);
        }
    }






    /*Broadcast receiver*/
    public class GpsLocationChangedBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = intent.getParcelableExtra(GpsTrackerService.BROADCAST_EXTRA_LOCATION);
            addLocation(location);
        }
    }









    /* Communication with service - the async binding */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            trackerService = ((GpsTrackerService.TrackerBinder)service).getService();
            updateTrackerServiceStatusButton();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            trackerService = null;
        }
    };






    /* Buttons click */
    public void onClearMapClick(View view) {
        this.googleMapsWrapper.clear();

        // open DB, delete, close DB
        IDatabaseConnection db = new DatabaseConnection(this);
        IGeoLocationPersistence persistence = new GeoLocationPersistence(new DatabaseConnection(this));
        persistence.deleteAllLocations(this.geoSessionId);
        db.onDestroy();


    }

    public void onGeoLocationsListClick(View view) {
        Intent intent = new Intent(this, GeoLocationsListActivity.class);
        startActivity(intent);
    }

    public void onZoomLevelClick(View view) {
        Button zoomLevelButton = (Button)findViewById(R.id.zoomLevel);
        int zoomLevel = Integer.parseInt(zoomLevelButton.getText().toString());
        zoomLevel++;
        if (zoomLevel > 20) zoomLevel = 5;
        this.googleMapsWrapper.setZoom(zoomLevel);
        zoomLevelButton.setText(Integer.toString(zoomLevel));
    }

    public void onCenterPolylineClick(View view) {
        this.googleMapsWrapper.centerMapVisiblePath();
    }

    public void onTrackingStatusClick(View view) {
        if (trackerService == null) return;
        if (trackerService.isTracking()){
            trackerService.stopTracking();
        } else {
            trackerService.startTracking(this.geoSessionId, 0, 10);
        }
        updateTrackerServiceStatusButton();
    }
}
