package com.example.majo.drawingscreenlist;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.majo.BusinessObjects.GeoLocation;
import com.example.majo.drawingscreen.R;
import com.example.majo.persistenceLocalDatabase.DatabaseConnection;
import com.example.majo.persistenceLocalDatabase.GeoLocationPersistence;
import com.example.majo.persistenceLocalDatabase.IDatabaseConnection;
import com.example.majo.persistenceLocalDatabase.IGeoLocationPersistence;

import java.util.List;


public class GeoLocationsListActivity extends ActionBarActivity {

    ListView geoLocations;

    private int geoSessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_locations_list);

        // todo take this from context
        this.geoSessionId = 1;

        geoLocations = (ListView)findViewById(R.id.geoLocations);


        // open DB, get data, close DB
        IDatabaseConnection db = new DatabaseConnection(this);
        IGeoLocationPersistence persistence = new GeoLocationPersistence(new DatabaseConnection(this));
        List<GeoLocation> locations = persistence.getAllLocations(this.geoSessionId);
        db.destroy();

        ArrayAdapter<GeoLocation> aa = new ArrayAdapter<GeoLocation>(this, android.R.layout.simple_list_item_1, locations);
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
