package com.example.majo.Activities;

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
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.majo.Adapters.SimpleDeleteListAdapter;
import com.example.majo.BusinessObjects.GeoLocation;
import com.example.majo.BusinessObjects.GeoSession;
import com.example.majo.GoogleMap.GpsTrackerService;
import com.example.majo.GoogleMap.GpsTrackerServiceHelper;
import com.example.majo.GoogleMap.IGpsTrackerService;
import com.example.majo.GoogleMap.IPolyLineDrawer;
import com.example.majo.GoogleMap.LocationConverter;
import com.example.majo.GoogleMap.PolyLineDrawer;
import com.example.majo.helper.NavigationContext;
import com.example.majo.drawingscreen.R;
import com.example.majo.persistenceLocalDatabase.DatabaseConnection;
import com.example.majo.persistenceLocalDatabase.GeoLocationPersistence;
import com.example.majo.persistenceLocalDatabase.GeoSessionPersistence;
import com.example.majo.persistenceLocalDatabase.IDatabaseConnection;
import com.example.majo.persistenceLocalDatabase.IGeoLocationPersistence;
import com.example.majo.persistenceLocalDatabase.IGeoSessionPersistence;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.List;

public class GeoLocationsMapsActivity extends FragmentActivity implements AdapterView.OnItemClickListener {

    private LocationManager locationManager;

    IPolyLineDrawer googleMapsWrapper;

    TextView geoLocationsText;
    ImageButton trackerServiceStatusButton;

    private NavigationContext navigationContext;

    private IGpsTrackerService trackerService;

    private IntentFilter filter = new IntentFilter(GpsTrackerService.BROADCAST_ON_LOCATION_CHANGED);
    private GpsLocationChangedBroadcastReceiver receiver = new GpsLocationChangedBroadcastReceiver();

    IDatabaseConnection dbConnection;
    IGeoLocationPersistence geoLocationPersistence;
    IGeoSessionPersistence geoSessionPersistence;

    private boolean isGeoLocationListVisible = false;
    private LinearLayout geoLocationListSection;
    private ListView geoLocationList;
    SimpleDeleteListAdapter<GeoLocation> geoLocationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_locations_maps);

        // setup DB connection
        this.dbConnection = new DatabaseConnection(this);
        this.geoLocationPersistence = new GeoLocationPersistence(this.dbConnection);
        this.geoSessionPersistence = new GeoSessionPersistence(this.dbConnection);

        this.navigationContext = NavigationContext.getNavigationContextFromActivity(this);

        // location manager just for initiation and getting cached position
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        // google map + wrapper
        SupportMapFragment mMapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        googleMapsWrapper = new PolyLineDrawer(mMapFragment.getMap());

        this.geoLocationsText = (TextView)findViewById(R.id.geoLocationsText);
        this.trackerServiceStatusButton = (ImageButton)findViewById(R.id.trackingStatus);

        // load from geoLocationPersistence
        List<GeoLocation> locationsFromDb = geoLocationPersistence.getAllLocations(this.navigationContext.getGeoSessionId());
        googleMapsWrapper.add(LocationConverter.GeoLocationToLatLng(locationsFromDb));

        // geo locations list
        this.geoLocationList = (ListView)findViewById(R.id.geoLocationList);
        this.geoLocationListSection = (LinearLayout)findViewById(R.id.geoLocationListSection);
        this.geoLocationList.setOnItemClickListener(this);
        this.geoLocationAdapter = new SimpleDeleteListAdapter(GeoLocationsMapsActivity.this, R.layout.list_item_simple_delete, locationsFromDb);
        this.geoLocationList.setAdapter(geoLocationAdapter);

        updateGeoLocationListSection(this.isGeoLocationListVisible);
        updateGeoLocationsText();
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
        startService(GpsTrackerServiceHelper.getServiceIntent(this));

        //Bind to the service so the activity can call methods of the service [Activity -> Service]
        boolean isBound = bindService(GpsTrackerServiceHelper.getServiceIntent(this), serviceConnection, Context.BIND_AUTO_CREATE);

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
            stopService(GpsTrackerServiceHelper.getServiceIntent(this));
        }

        // unregister receiving broadcast
        unregisterReceiver(receiver);

        super.onPause();
    }


    @Override
    protected void onDestroy() {
        // destroy db connection
        if (this.dbConnection != null){
            this.dbConnection.destroy();
        }

        //Unbind from the service
        unbindService(serviceConnection);

        super.onDestroy();
    }






    /* adding and removing locations */
    private void addLocation(Location location){
        if (location != null) {
            googleMapsWrapper.add(LocationConverter.LocationToLatLng(location));
            // in DB the location already is

            geoLocationAdapter.add(LocationConverter.LocationToGeoLocation(location));

            // update GUI
            updateGeoLocationsText();
        }
    }

    private void removeLocation(GeoLocation location){
        geoLocationAdapter.remove(location);
        this.geoLocationPersistence.deleteLocation(location);
        this.googleMapsWrapper.remove(LocationConverter.GeoLocationToLatLng(location));

        // update GUI
        updateGeoLocationsText();
    }

    private void removeAllLocations(){
        this.googleMapsWrapper.clear();
        geoLocationPersistence.deleteAllLocations(this.navigationContext.getGeoSessionId());
        geoLocationAdapter.clear();

        // update GUI
        updateGeoLocationsText();
    }










    private void updateGeoLocationsText(){
        this.geoLocationsText.setText(String.format("points:%d dist:%s MapId:%d SessionId:%d", googleMapsWrapper.getPoints().size(), String.valueOf(Math.round(getPathSizeForLocations(this.geoLocationAdapter.getItems()))), this.navigationContext.getSchemaMapId(), this.navigationContext.getGeoSessionId()));
    }








    // TODO put this section away //////////////////////
    private double getPathSizeForLocations(List<GeoLocation> geoLocations){
        double result = 0;
        Location previousGeoLocation = null;
        for (GeoLocation geoLocation : geoLocations){
            if (previousGeoLocation != null)
            {
                result += getDistanceForLocations(previousGeoLocation, convertToLocation(geoLocation));
            }
            previousGeoLocation = convertToLocation(geoLocation);
        }

        return result;
    }

    private Location convertToLocation(GeoLocation geoLocation){
        Location result = new Location(""); // provider name is unecessary
        result.setLatitude(geoLocation.latitude);
        result.setLongitude(geoLocation.longitude);
        result.setAltitude(geoLocation.altitude);
        return result;
    }


    private double getDistanceForLocations(Location loc1, Location loc2){
        if ((loc1 == null) || (loc2 == null)) return 0;
        return Math.abs(loc1.distanceTo(loc2));
    }
    // TODO put this section away //////////////////////























    private void updateGeoLocationListSection(boolean isVisible){
        this.isGeoLocationListVisible = isVisible;
        ImageButton im = (ImageButton)findViewById(R.id.showGeoLocationsList);
        if (isVisible){
            this.geoLocationListSection.setVisibility(View.VISIBLE);
            im.setBackgroundResource(R.drawable.path_visible);
        } else {
            this.geoLocationListSection.setVisibility(View.GONE);
            im.setBackgroundResource(R.drawable.path_invisible);
        }
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
            int geoSessionIdTmp = intent.getIntExtra(GpsTrackerService.BROADCAST_EXTRA_GEO_SESSION_ID, -1);

            if (geoSessionIdTmp == navigationContext.getGeoSessionId()){
                addLocation(location);
            }
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
        removeAllLocations();
    }

    public void onShowGeoLocationsListClick(View view) {
        updateGeoLocationListSection(!this.isGeoLocationListVisible);
    }

    public void onZoomLevelPlusClick(View view) {
        TextView zoomLevelText = (TextView)findViewById(R.id.zoomLevelValueText);
        int zoomLevel = Integer.parseInt(zoomLevelText.getText().toString());
        zoomLevel++;
        if (zoomLevel > 20) zoomLevel = 20;
        this.googleMapsWrapper.setZoom(zoomLevel);
        zoomLevelText.setText(Integer.toString(zoomLevel));
    }

    public void onZoomLevelMinusClick(View view) {
        TextView zoomLevelText = (TextView)findViewById(R.id.zoomLevelValueText);
        int zoomLevel = Integer.parseInt(zoomLevelText.getText().toString());
        zoomLevel--;
        if (zoomLevel < 5) zoomLevel = 5;
        this.googleMapsWrapper.setZoom(zoomLevel);
        zoomLevelText.setText(Integer.toString(zoomLevel));
    }

    public void onCenterPolylineClick(View view) {
        this.googleMapsWrapper.centerMapVisiblePath();
    }

    public void onTrackingStatusClick(View view) {
        if (trackerService == null) return;
        if (trackerService.isTracking()){
            trackerService.stopTracking();
        } else {
            // todo: do something with name
            // create new session if needed
            if (this.navigationContext.getGeoSessionId() < 0){
                this.navigationContext.setGeoSessionId(createNewGeoSessionAndPersist("xxx").id);
            }
            trackerService.startTracking(this.navigationContext.getGeoSessionId(), 0, 10);
        }
        updateTrackerServiceStatusButton();
    }

    private GeoSession createNewGeoSessionAndPersist(String name){
        GeoSession geoSession = new GeoSession(name);
        // this also populates the ID
        this.geoSessionPersistence.addSession(this.navigationContext.getSchemaMapId(), geoSession);
        return geoSession;
    }










    /* List click */
    public void onSimpleDeleteListItemClick(View view) {
        GeoLocation itemToRemove = (GeoLocation)view.getTag();
        removeLocation(itemToRemove);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        GeoLocation itemClicked = (GeoLocation)parent.getItemAtPosition(position);

        // this shows clicked location and hides the last clicked one
        this.googleMapsWrapper.showLocation(LocationConverter.GeoLocationToLatLng(itemClicked));
    }
}
