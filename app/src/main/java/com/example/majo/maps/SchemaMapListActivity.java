package com.example.majo.maps;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.majo.Adapters.ImageListAdapter;
import com.example.majo.Adapters.SimpleDeleteListAdapter;
import com.example.majo.BusinessObjects.SchemaMap;
import com.example.majo.drawingscreen.DrawingPointsActivity;
import com.example.majo.helper.NavigationContext;
import com.example.majo.drawingscreen.R;
import com.example.majo.persistence.DatabaseConnection;
import com.example.majo.persistence.IDatabaseConnection;
import com.example.majo.persistence.ISchemaMapPersistence;
import com.example.majo.persistence.SchemaMapPersistence;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class SchemaMapListActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    private static final int SELECT_PICTURE = 1;

    private NavigationContext navigationContext;

    IDatabaseConnection dbConnection;
    ISchemaMapPersistence persistence;

    ListView schemaMapList;
    ImageListAdapter<SchemaMap> schemaMapListAdapter;

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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String selectedImagePath;
        //ADDED
        String filemanagerstring;

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImage = data.getData();
                try {
                    // create new DB entry
                    SchemaMap map = new SchemaMap("name");
                    this.persistence.addMap(map);

                    // store bitmap
                    InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                    Bitmap selectedImageBitmap = BitmapFactory.decodeStream(imageStream);
                    MapManager.storeBitmap(map.id, selectedImageBitmap, this);

                    // refresh stuff
                    refreshList();
                } catch (FileNotFoundException e){

                }
            }
        }
    }





    private void refreshList(){
        List<SchemaMap> maps = this.persistence.getAllMaps();
        this.schemaMapListAdapter = new ImageListAdapter<>(SchemaMapListActivity.this, R.layout.list_item_image, maps);
        this.schemaMapList.setAdapter(schemaMapListAdapter);
    }



    /*on CLICK*/
    public void onImageDeleteItemButtonClick(View view) {
        SchemaMap itemToRemove = (SchemaMap)view.getTag();
        schemaMapListAdapter.remove(itemToRemove);
        this.persistence.deleteMap(itemToRemove);
        MapManager.deleteBitmap(itemToRemove.id, this);
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

        // pick the image
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }
}
