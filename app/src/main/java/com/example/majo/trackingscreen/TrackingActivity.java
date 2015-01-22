package com.example.majo.trackingscreen;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.majo.Adapters.SimpleDeleteListAdapter;
import com.example.majo.BusinessObjects.MappedPoint;
import com.example.majo.GoogleMap.LocationConverter;
import com.example.majo.distance.DistanceCalculator;
import com.example.majo.distance.DistancePoint;
import com.example.majo.drawingscreen.DrawingScreenView;
import com.example.majo.drawingscreen.IOnPointChanged;
import com.example.majo.drawingscreen.Layer;
import com.example.majo.drawingscreen.MappedPointManager;
import com.example.majo.drawingscreen.R;
import com.example.majo.helper.NavigationContext;
import com.example.majo.persistence.DatabaseConnection;
import com.example.majo.persistence.DrawingPointPersistence;
import com.example.majo.persistence.IDatabaseConnection;
import com.example.majo.persistence.IDrawingPointPersistence;
import com.example.majo.persistence.IMappedPointsPersistence;
import com.example.majo.persistence.MappedPointsPersistence;

import java.util.ArrayList;
import java.util.List;

public class TrackingActivity extends Activity implements LocationListener {

    private IDatabaseConnection db;
    private IMappedPointsPersistence mappedPointsPersistence;


    private boolean isTracking;
    private boolean isShowingMappedPoints;

    private LocationManager locationManager;

    private NavigationContext navigationContext;



    private DrawingScreenView drawingScreenView;

    private Layer mappedPointLayer;
    private MappedPointManager mappedPointManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);

        this.locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        this.navigationContext = NavigationContext.getNavigationContextFromActivity(this);

        // setup db
        this.db = new DatabaseConnection(this);
        this.mappedPointsPersistence = new MappedPointsPersistence(this.db);





        this.drawingScreenView = (DrawingScreenView)findViewById(R.id.imageView);
        this.drawingScreenView.setImageAsset(this.getAssetName());

        // setup MP -------------------------------
        this.mappedPointLayer = new Layer(this, this.getAssetName());
        this.mappedPointLayer.setVisibility(false);

        this.mappedPointManager = new MappedPointManager(this.mappedPointLayer, this.mappedPointsPersistence, this.navigationContext.getSchemaMapId());
        this.mappedPointManager.setColor(Color.GREEN);
        this.mappedPointManager.setHighlightColor(Color.MAGENTA);

        // connecting MP -------------------------------
        this.drawingScreenView.addLayer(this.mappedPointLayer);
        this.drawingScreenView.addPointListener(this.mappedPointManager);


        // load from DB
        this.mappedPointManager.refreshPointsFromDb();


        setIsTracking(false);
        setIsShowingMappedPoints(false);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.db!=null){
            this.db.destroy();
        }
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

        updateLocation(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
    }











    // TODO: do this properly
    private String getAssetName(){
        String assetName = "melchsee_small.jpg";
        if (this.navigationContext.getSchemaMapId() % 4 == 1){
            assetName = "melchsee.jpg";
        }
        if (this.navigationContext.getSchemaMapId() % 4 == 2){
            assetName = "Rigi.jpg";
        }
        if (this.navigationContext.getSchemaMapId() % 4 == 3){
            assetName = "squirrel.jpg";
        }
        return assetName;
    }







    private void setIsTracking(boolean isTracking){
        this.isTracking = isTracking;
        ImageButton btn = (ImageButton)findViewById(R.id.toggleTracking);
        if (this.isTracking){
            btn.setBackgroundResource(R.drawable.path_visible);
        } else {
            btn.setBackgroundResource(R.drawable.path_invisible);
        }

        if (this.isTracking) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, this);
        } else {
            locationManager.removeUpdates(this);
        }
    }

    private void setIsShowingMappedPoints(boolean isShowingMappedPoints){
        this.isShowingMappedPoints = isShowingMappedPoints;
        ImageButton btn = (ImageButton)findViewById(R.id.showMappedPointsTracking);
        if (this.isShowingMappedPoints){
            btn.setBackgroundResource(R.drawable.path_visible);
        } else {
            btn.setBackgroundResource(R.drawable.path_invisible);
        }

        this.mappedPointLayer.setVisibility(this.isShowingMappedPoints);
    }


    private void updateLocation(Location location){
        if (location == null) return;
        List<DistancePoint> resultPoints = DistanceCalculator.sortToDistance(location, this.mappedPointManager.getPoints());
        TextView trackedPointInfo = (TextView) findViewById(R.id.trackedPointInfo);

        if (resultPoints.size() > 0) {
            DistancePoint first = resultPoints.get(0);

            // show on map
            this.mappedPointManager.clearHighlight();
            this.mappedPointManager.highlightPoint(first.point);

            // update text info
            trackedPointInfo.setText(String.format("%s %s", first.toString(), location.toString()));

        } else {
            trackedPointInfo.setText(String.format("No match %s", location.toString()));
        }
    }










    public void onToggleTrackingClick(View view) {
        this.setIsTracking(!this.isTracking);
    }

    public void onShowMappedPointsTrackingClick(View view) {
        setIsShowingMappedPoints(!this.isShowingMappedPoints);
    }














    @Override
    public void onLocationChanged(Location location) {
        updateLocation(location);
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







}
