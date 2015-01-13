package com.example.majo.drawingscreenlist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.majo.Adapters.GeoSessionListAdapter;
import com.example.majo.BusinessObjects.GeoSession;
import com.example.majo.GoogleMap.GpsTrackerServiceHelper;
import com.example.majo.GoogleMap.IGpsTrackerService;
import com.example.majo.drawingscreen.GeoLocationsMapsActivity;
import com.example.majo.drawingscreen.NavigationContext;
import com.example.majo.drawingscreen.R;
import com.example.majo.persistence.DatabaseConnection;
import com.example.majo.persistence.GeoSessionPersistence;
import com.example.majo.persistence.IDatabaseConnection;
import com.example.majo.persistence.IGeoSessionPersistence;

import java.util.List;


public class GeoSessionsListActivity extends Activity implements AdapterView.OnItemClickListener {
    private NavigationContext navigationContext;

    private int trackedSessionId;
    private List<GeoSession> geoSessionList;

    GeoSessionListAdapter adapter;

    IDatabaseConnection dbConnection;
    IGeoSessionPersistence persistence;

    ListView geoSessionsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_sessions_list);

        this.navigationContext = NavigationContext.getNavigationContextFromActivity(this);

        // setup db connection
        this.dbConnection = new DatabaseConnection(this);
        this.persistence = new GeoSessionPersistence(this.dbConnection);

        // gui components basic setup
        this.geoSessionsListView = (ListView)findViewById(R.id.geoSessions);
        this.geoSessionsListView.setOnItemClickListener(this);

        refreshAll();
    }

    @Override
    protected void onResume() {
        super.onResume();

        refreshAll();
    }

    @Override
    protected void onDestroy() {
        // destroy db connection
        if (this.dbConnection != null){
            this.dbConnection.destroy();
        }

        super.onDestroy();
    }

    private List<GeoSession> getGeoSessions(){
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

    private void refreshAll(){
        // get tracked session from service (must be before calling getGeoSessions)
        this.trackedSessionId = getTrackedGeoSessionId();

        // get session list from DB
        this.geoSessionList = getGeoSessions();

        // populate from adapter
        this.adapter = new GeoSessionListAdapter(GeoSessionsListActivity.this, R.layout.list_item_geo_session, this.geoSessionList);
        this.geoSessionsListView.setAdapter(adapter);
    }






    /*on CLICK*/
    public void onDeleteClick(View view) {
        GeoSession itemToRemove = (GeoSession)view.getTag();
        adapter.remove(itemToRemove);
        this.persistence.deleteSession(itemToRemove);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        GeoSession itemClicked = (GeoSession)parent.getItemAtPosition(position);

        Intent intent = new Intent(this, GeoLocationsMapsActivity.class);
        this.navigationContext.setGeoSessionId(itemClicked.id);
        NavigationContext.setNavigationContext(intent, navigationContext);

        startActivity(intent);
    }
}
