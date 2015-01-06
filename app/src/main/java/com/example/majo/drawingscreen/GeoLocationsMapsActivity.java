package com.example.majo.drawingscreen;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.majo.BusinessObjects.GeoLocation;
import com.example.majo.persistence.GeoLocationPersistence;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;

public class GeoLocationsMapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.


    private GeoLocationPersistence persistence;

    private LocationManager mManager;
    private Location mCurrentLocation;

    private TextView mLocationView;

    GoogleMapsWrapper googleMapsWrapper;

    TextView text;

    private int geoSessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_locations_maps);

        // todo get from context
        this.geoSessionId = 1;

        mManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        SupportMapFragment mMapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        googleMapsWrapper = new GoogleMapsWrapper(mMapFragment.getMap());

        this.text = (TextView)findViewById(R.id.textView);

        // load from db
        this.drawGeoLocationsFromDb();

    }

    @Override
    protected void onResume() {
        super.onResume();

        // try to enable Location manager
        if (!mManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){

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


        // get cached location, if it exists
        mCurrentLocation = mManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        this.googleMapsWrapper.addTravelPoint(mCurrentLocation);
        if (mCurrentLocation != null){
            this.text.setText(mCurrentLocation.toString());
        }

        // register for regular updates
        int minTime = 0;
        float minDistance = 10;
        mManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, mListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mManager.removeUpdates(mListener);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.persistence!=null){
            this.persistence.onDestroy();
        }
    }



    private LocationListener mListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            mCurrentLocation = location;
            googleMapsWrapper.addTravelPoint(mCurrentLocation);
            if (mCurrentLocation != null){
                text.setText(mCurrentLocation.toString());
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public void drawGeoLocationsFromDb(){
        persistence = new GeoLocationPersistence(this);
        ArrayList<GeoLocation> locationsFromDb = persistence.getAllLocations(this.geoSessionId);
        googleMapsWrapper.drawStoredGeoLocations(locationsFromDb);
    }


    public void onClearMapClick(View view) {
        this.googleMapsWrapper.clearAllTravelPoints();
    }

    public void onClearAllPointsClick(View view) {
        this.googleMapsWrapper.clearAllTravelPoints();
        this.persistence.deleteAllLocations(this.geoSessionId);
    }

    public void onAppendPointsClick(View view) {
        ArrayList<GeoLocation> cache = this.googleMapsWrapper.getCachedGeoLocations();
        this.persistence.addLocations(this.geoSessionId, cache);
        this.googleMapsWrapper.clearAllTravelPoints();
        this.drawGeoLocationsFromDb();
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
        this.googleMapsWrapper.reZoomMap(zoomLevel);
        zoomLevelButton.setText(Integer.toString(zoomLevel));
    }
}
