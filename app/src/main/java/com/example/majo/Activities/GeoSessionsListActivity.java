package com.example.majo.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.majo.Adapters.SimpleDeleteListAdapter;
import com.example.majo.BusinessObjects.GeoSession;
import com.example.majo.GoogleMap.GpsTrackerServiceHelper;
import com.example.majo.GoogleMap.IGpsTrackerService;
import com.example.majo.helper.NavigationContext;
import com.example.majo.drawingscreen.R;
import com.example.majo.persistenceLocalDatabase.DatabaseConnection;
import com.example.majo.persistenceLocalDatabase.GeoSessionPersistence;
import com.example.majo.persistenceLocalDatabase.IDatabaseConnection;
import com.example.majo.persistenceLocalDatabase.IGeoSessionPersistence;

import java.util.List;


public class GeoSessionsListActivity extends Activity implements AdapterView.OnItemClickListener {
    private NavigationContext navigationContext;

    IDatabaseConnection dbConnection;
    IGeoSessionPersistence persistence;

    ListView geoSessionList;
    SimpleDeleteListAdapter<GeoSession> geoSessionListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_sessions_list);

        this.navigationContext = NavigationContext.getNavigationContextFromActivity(this);

        // setup db connection
        this.dbConnection = new DatabaseConnection(this);
        this.persistence = new GeoSessionPersistence(this.dbConnection);

        // gui components basic setup
        this.geoSessionList = (ListView)findViewById(R.id.geoSessionList);
        this.geoSessionList.setOnItemClickListener(this);

        refreshList();
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.navigationContext = NavigationContext.getNavigationContextFromActivity(this);

        refreshList();
    }

    @Override
    protected void onDestroy() {
        // destroy db connection
        if (this.dbConnection != null){
            this.dbConnection.destroy();
        }

        super.onDestroy();
    }

    private List<GeoSession> getGeoSessions(int trackedSessionId){
        List<GeoSession> geoSessions = this.persistence.getAllGeoSessions(this.navigationContext.getSchemaMapId());

        // update flag which one is being tracked right now
        for (GeoSession geoSession : geoSessions){
            if (geoSession.id == trackedSessionId){
                geoSession.isBeingTracked = true;
            }
        }

        return geoSessions;
    }

    private int getTrackedGeoSessionId(){
        IGpsTrackerService trackerService = GpsTrackerServiceHelper.getTrackerService();
        if (trackerService!=null){
            return trackerService.getTrackedGeoSessionId();
        }
        return -1;
    }

    private void refreshList(){
        // get session list from DB + add one flag from running service
        List<GeoSession> geoSessionList = getGeoSessions(getTrackedGeoSessionId());

        // populate from geoSessionListAdapter
        this.geoSessionListAdapter = new SimpleDeleteListAdapter(GeoSessionsListActivity.this, R.layout.list_item_simple_delete, geoSessionList);
        this.geoSessionList.setAdapter(geoSessionListAdapter);
    }






    /*on CLICK*/
    public void onSimpleDeleteListItemClick(View view) {
        GeoSession itemToRemove = (GeoSession)view.getTag();
        geoSessionListAdapter.remove(itemToRemove);
        this.persistence.deleteSession(itemToRemove);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        GeoSession itemClicked = (GeoSession)parent.getItemAtPosition(position);

        if (this.navigationContext.getIsSelectingGeoSessionForMapping()){

            // add result data
            Intent returnIntent = new Intent();
            this.navigationContext.setGeoSessionId(itemClicked.id);
            NavigationContext.setNavigationContext(returnIntent, navigationContext);

            setResult(RESULT_OK, returnIntent);

            finish();

        } else {
            Intent intent = new Intent(this, GeoLocationsMapsActivity.class);
            this.navigationContext.setGeoSessionId(itemClicked.id);
            NavigationContext.setNavigationContext(intent, navigationContext);

            startActivity(intent);
        }

    }
}
