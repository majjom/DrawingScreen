package com.example.majo.Activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.majo.Adapters.SimpleDeleteListAdapter;
import com.example.majo.BusinessObjects.SchemaMap;
import com.example.majo.drawingscreen.DrawingPointsActivity;
import com.example.majo.helper.NavigationContext;
import com.example.majo.drawingscreen.R;
import com.example.majo.persistence.DatabaseConnection;
import com.example.majo.persistence.IDatabaseConnection;
import com.example.majo.persistence.ISchemaMapPersistence;
import com.example.majo.persistence.SchemaMapPersistence;

public class SchemaMapListActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    private NavigationContext navigationContext;

    IDatabaseConnection dbConnection;
    ISchemaMapPersistence persistence;

    ListView schemaMapList;
    SimpleDeleteListAdapter<SchemaMap> schemaMapListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schema_map_list);

        this.navigationContext = NavigationContext.getNavigationContextFromActivity(this);

        // setup db connection
        this.dbConnection = new DatabaseConnection(this);
        this.persistence = new SchemaMapPersistence(this.dbConnection);

        // gui components basic setup
        this.schemaMapList = (ListView)findViewById(R.id.schemaMapList);
        this.schemaMapList.setOnItemClickListener(this);

        refreshList();
    }

    @Override
    protected void onResume() {
        super.onResume();

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


    private void refreshList(){
        this.schemaMapListAdapter = new SimpleDeleteListAdapter(SchemaMapListActivity.this, R.layout.list_item_simple_delete, this.persistence.getAllMaps());
        this.schemaMapList.setAdapter(schemaMapListAdapter);
    }



    /*on CLICK*/
    public void onSimpleDeleteListItemClick(View view) {
        SchemaMap itemToRemove = (SchemaMap)view.getTag();
        schemaMapListAdapter.remove(itemToRemove);
        this.persistence.deleteMap(itemToRemove);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SchemaMap itemClicked = (SchemaMap)parent.getItemAtPosition(position);

        Intent intent = new Intent(this, DrawingPointsActivity.class);
        this.navigationContext.setSchemaMapId(itemClicked.id);
        NavigationContext.setNavigationContext(intent, navigationContext);

        startActivity(intent);
    }

    public void onAddSchemaMapClick(View view) {
        SchemaMap map = new SchemaMap("name");
        this.persistence.addMap(map);

        refreshList();
    }
}
