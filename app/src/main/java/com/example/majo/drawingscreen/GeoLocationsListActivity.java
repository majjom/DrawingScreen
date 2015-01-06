package com.example.majo.drawingscreen;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.majo.BusinessObjects.DrawingPoint;
import com.example.majo.BusinessObjects.GeoLocation;
import com.example.majo.persistence.DrawingPointPersistence;
import com.example.majo.persistence.GeoLocationPersistence;
import com.example.majo.persistence.IDrawingPointPersistence;
import com.example.majo.persistence.IGeoLocationPersistence;


public class GeoLocationsListActivity extends ActionBarActivity {

    ListView geoLocations;

    IGeoLocationPersistence db;

    private int geoSessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_locations_list);

        // todo take this from context
        this.geoSessionId = 1;

        geoLocations = (ListView)findViewById(R.id.geoLocations);
        db = new GeoLocationPersistence(this);

        ArrayAdapter<GeoLocation> aa = new ArrayAdapter<GeoLocation>(this, android.R.layout.simple_list_item_1, db.getAllLocations(this.geoSessionId));
        geoLocations.setAdapter(aa);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_geo_locations_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
