package com.example.majo.drawingscreen;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.majo.Activities.GeoLocationsMapsActivity;
import com.example.majo.maps.MapManager;
import com.example.majo.maps.SchemaMapListActivity;
import com.example.majo.Adapters.SimpleDeleteListAdapter;
import com.example.majo.BusinessObjects.DrawingPoint;
import com.example.majo.Activities.GeoSessionsListActivity;
import com.example.majo.BusinessObjects.MappedPoint;
import com.example.majo.helper.NavigationContext;
import com.example.majo.merging.PointMerge;
import com.example.majo.persistence.DatabaseConnection;
import com.example.majo.persistence.DrawingPointPersistence;
import com.example.majo.persistence.GeoLocationPersistence;
import com.example.majo.persistence.IDatabaseConnection;
import com.example.majo.persistence.IDrawingPointPersistence;
import com.example.majo.persistence.IMappedPointsPersistence;
import com.example.majo.persistence.MappedPointsPersistence;
import com.example.majo.trackingscreen.TrackingActivity;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;


public class DrawingPointsActivity extends Activity implements AdapterView.OnItemClickListener, IOnPointChanged {

    public static final int PICK_GEO_SESSION_REQUEST = 1;

    private IDatabaseConnection db;
    private IDrawingPointPersistence drawingPointPersistence;
    private IMappedPointsPersistence mappedPointsPersistence;
    private GeoLocationPersistence geoLocationPersistence;

//    private GeoLocationPersistence geoLocationPersistence;
//    private IMappedPointsPersistence mappedPointsPersistence;

    // common UI for DP + MP
    ListView lowerList;
    private DrawingScreenView drawingScreenView;

    // DP only
    private Layer drawingPointLayer;
    private SimpleDeleteListAdapter<DrawingPoint> drawingPointAdapter;
    private DrawingPointManager drawingPointManager;

    // MP only
    private Layer mappedPointLayer;
    private SimpleDeleteListAdapter<MappedPoint> mappedPointAdapter;
    private MappedPointManager mappedPointManager;


    private NavigationContext navigationContext;

    private boolean isLowerListVisible;
    private boolean isShowingMappedPoints;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.navigationContext = NavigationContext.getNavigationContextFromActivity(this);


        // setup window features
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_drawing_points);



        //set up database
        this.db = new DatabaseConnection(this);
        this.drawingPointPersistence = new DrawingPointPersistence(this.db);
        this.mappedPointsPersistence = new MappedPointsPersistence(this.db);
        this.geoLocationPersistence = new GeoLocationPersistence(this.db);

        // setup common DP + MP -------------------------------
        this.drawingScreenView = (DrawingScreenView)findViewById(R.id.imageView);
        //TODO remove this this.drawingScreenView.setImageAsset(this.getAssetName());
        this.drawingScreenView.setImageFile(MapManager.getBitmapFileNameComplete(this.navigationContext.getSchemaMapId(), this));

        this.lowerList = (ListView)findViewById(R.id.lowerList);
        this.lowerList.setOnItemClickListener(this);

        // setup DP -------------------------------
        // TODO remove this this.drawingPointLayer = new Layer(this, this.getAssetName());
        this.drawingPointLayer = new Layer(this, MapManager.getBitmapFileName(this.navigationContext.getSchemaMapId()));

        List<DrawingPoint> emptyDrawingPointList = new ArrayList<>();
        this.drawingPointAdapter = new SimpleDeleteListAdapter(DrawingPointsActivity.this, R.layout.list_item_simple_delete, emptyDrawingPointList); // first empty

        this.drawingPointManager = new DrawingPointManager(this.drawingPointLayer, this.drawingPointAdapter, this.drawingPointPersistence, this, this.navigationContext.getSchemaMapId());
        this.drawingPointManager.setColor(Color.BLUE);
        this.drawingPointManager.setHighlightColor(Color.RED);
        this.drawingPointManager.setRadius(10);



        // connecting DP -------------------------------
        this.lowerList.setAdapter(this.drawingPointAdapter);
        this.drawingScreenView.addLayer(this.drawingPointLayer);
        this.drawingScreenView.addPointListener(this.drawingPointManager);


        // setup MP -------------------------------
        // todo remove this this.mappedPointLayer = new Layer(this, this.getAssetName());
        this.mappedPointLayer = new Layer(this, MapManager.getBitmapFileName(this.navigationContext.getSchemaMapId()));
        this.mappedPointLayer.setVisibility(false);

        List<MappedPoint> emptyMappedPointList = new ArrayList<>();
        this.mappedPointAdapter = new SimpleDeleteListAdapter(DrawingPointsActivity.this, R.layout.list_item_simple_delete, emptyMappedPointList); // first empty

        this.mappedPointManager = new MappedPointManager(this.mappedPointLayer, this.mappedPointAdapter, this.mappedPointsPersistence, this, this.navigationContext.getSchemaMapId());
        this.mappedPointManager.setColor(Color.GREEN);
        this.mappedPointManager.setHighlightColor(Color.MAGENTA);

        // connecting MP -------------------------------
        this.drawingScreenView.addLayer(this.mappedPointLayer);
        this.drawingScreenView.addPointListener(this.mappedPointManager);


        // refresh rest of UI
        setIsLowerListVisible(false);
        setIsShowingMappedPoints(false);
        updateButtons();
        updateGeoLocationsText();


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

        this.navigationContext = NavigationContext.getNavigationContextFromActivity(this);

        this.mappedPointManager.refreshPointsFromDb();
        this.drawingPointManager.refreshPointsFromDb();
        updateButtons();
        updateGeoLocationsText();

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_GEO_SESSION_REQUEST) {
            if (resultCode == RESULT_OK) {
                this.navigationContext = NavigationContext.getNavigationContextFromActivity(this, data);
                List<MappedPoint> mappedPointList = PointMerge.mergePoints(this.drawingPointPersistence.getAllPoints(this.navigationContext.getSchemaMapId()),
                        this.geoLocationPersistence.getAllLocations(this.navigationContext.getGeoSessionId()));
                this.mappedPointsPersistence.addPoints(this.navigationContext.getSchemaMapId(), mappedPointList);

                this.drawingPointManager.removeAllPoints();

                Toast.makeText(this, "Created mapped point count:" + String.valueOf(mappedPointList.size()), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Nothing done", Toast.LENGTH_SHORT).show();
            }
        }
    }








    /* update gui */
    private void updateButtons(){
        ImageButton btn = (ImageButton)findViewById(R.id.toggleDrawingMode);
        if (this.drawingScreenView.getDrawingMode() == DrawingMode.DRAW){
            btn.setBackgroundResource(R.drawable.drawing_enabled);
        } else if (this.drawingScreenView.getDrawingMode() == DrawingMode.ZOOM){
            btn.setBackgroundResource(R.drawable.drawing_disabled);
        } else {
            btn.setBackgroundResource(R.drawable.delete_saved_data);
        }

        btn = (ImageButton)findViewById(R.id.toggleLowerListSectionVisibility);
        if (this.isLowerListVisible){
            btn.setBackgroundResource(R.drawable.path_visible);
        } else {
            btn.setBackgroundResource(R.drawable.path_invisible);
        }

        btn = (ImageButton)findViewById(R.id.toggleDrawingPointsVisibility);
        if (this.drawingPointLayer.isVisible()){
            btn.setBackgroundResource(R.drawable.path_visible);
        } else {
            btn.setBackgroundResource(R.drawable.path_invisible);
        }

        btn = (ImageButton)findViewById(R.id.toggleMappedPointsVisibility);
        if (this.mappedPointLayer.isVisible()){
            btn.setBackgroundResource(R.drawable.path_visible);
        } else {
            btn.setBackgroundResource(R.drawable.path_invisible);
        }

        btn = (ImageButton)findViewById(R.id.toggleDrawingMappedPoint);
        if (this.isShowingMappedPoints){
            btn.setBackgroundResource(R.drawable.path_visible);
        } else {
            btn.setBackgroundResource(R.drawable.path_invisible);
        }

        TextView tw = (TextView)findViewById(R.id.pointSizeView);
        tw.setText(String.valueOf(this.drawingPointManager.getRadius()));

        tw = (TextView)findViewById(R.id.strokeSizeView);
        tw.setText(String.valueOf(this.drawingScreenView.getStrokeWidth()));
    }



    private void updateGeoLocationsText(){
        TextView tw = (TextView)findViewById(R.id.lowerText);
        if (isShowingMappedPoints){
            tw.setText(String.format("MP:%d high:%s MapId:%d", this.mappedPointManager.getPoints().size(), this.mappedPointManager.getHighlightedPoints().size(), this.navigationContext.getSchemaMapId()));
        } else {
            tw.setText(String.format("points:%d high:%s MapId:%d", this.drawingPointManager.getPoints().size(), this.drawingPointManager.getHighlightedPoints().size(), this.navigationContext.getSchemaMapId()));
        }

    }







    /* setting modes */
    private void setIsLowerListVisible(boolean visible){
        LinearLayout ll = (LinearLayout)findViewById(R.id.lowerListSection);
        this.isLowerListVisible = visible;
        if (isLowerListVisible){
            ll.setVisibility(View.VISIBLE);
        } else {
            ll.setVisibility(View.GONE);
        }
        updateGeoLocationsText();
        updateButtons();
    }

    private void setIsShowingMappedPoints(boolean visible){
        this.isShowingMappedPoints = visible;
        if (isShowingMappedPoints){
            this.lowerList.setAdapter(this.mappedPointAdapter);
        } else {
            this.lowerList.setAdapter(this.drawingPointAdapter);
        }
        updateGeoLocationsText();
        updateButtons();
    }
















    /* On click buttons */
    public void onToggleDrawingModeClick(View view) {
        if (this.drawingScreenView.getDrawingMode() == DrawingMode.ZOOM){
            this.drawingScreenView.setDrawingMode(DrawingMode.DRAW);
        } else if (this.drawingScreenView.getDrawingMode() == DrawingMode.DRAW){
            this.drawingScreenView.setDrawingMode(DrawingMode.HIGHLIGHT);
        } else {
            this.drawingScreenView.setDrawingMode(DrawingMode.ZOOM);
        }

        updateButtons();
        updateGeoLocationsText();
    }

    public void onToggleLowerListSectionVisibilityClick(View view) {
        setIsLowerListVisible(!this.isLowerListVisible);

        updateButtons();
        updateGeoLocationsText();
    }

    public void onToggleDrawingPointsVisibilityClick(View view) {
        this.drawingPointLayer.setVisibility(!this.drawingPointLayer.isVisible());

        updateButtons();
        updateGeoLocationsText();
    }

    public void onToggleMappedPointsVisibilityClick(View view) {
        this.mappedPointLayer.setVisibility(!this.mappedPointLayer.isVisible());

        updateButtons();
        updateGeoLocationsText();
    }

    public void onGeoLocationsMapClick(View view) {
        Intent intent = new Intent(this, GeoLocationsMapsActivity.class);

        NavigationContext.setNavigationContext(intent, navigationContext);

        startActivity(intent);
    }

    public void onGeoSessionsListClick(View view) {
        Intent intent = new Intent(this, GeoSessionsListActivity.class);

        NavigationContext.setNavigationContext(intent, navigationContext);

        startActivity(intent);
    }

    public void onDeleteAllPointsClick(View view) {
        if (this.isShowingMappedPoints){
            this.mappedPointManager.removeAllPoints();
        } else {
            this.drawingPointManager.removeAllPoints();
        }

        updateGeoLocationsText();
        updateButtons();
    }

    public void onDeleteHighLightedPointsClick(View view) {
        if (this.isShowingMappedPoints){
            this.mappedPointManager.removePoints(this.mappedPointManager.getHighlightedPoints());
        } else {
            this.drawingPointManager.removePoints(this.drawingPointManager.getHighlightedPoints());
        }

        updateGeoLocationsText();
        updateButtons();
    }

    public void onUndoHighLightedPointsClick(View view) {
        if (this.isShowingMappedPoints){
            this.mappedPointManager.clearHighlight();
        } else {
            this.drawingPointManager.clearHighlight();
        }

        updateGeoLocationsText();
        updateButtons();
    }

    public void onSelectGeoSessionClick(View view) {
        Intent intent = new Intent(this, GeoSessionsListActivity.class);

        navigationContext.setSelectingGeoSessionsForMapping(true);
        NavigationContext.setNavigationContext(intent, navigationContext);

        startActivityForResult(intent, PICK_GEO_SESSION_REQUEST);
    }

    public void onToggleDrawingMappedPointClick(View view) {
        this.setIsShowingMappedPoints(!this.isShowingMappedPoints);

        updateGeoLocationsText();
        updateButtons();
    }

    public void onStartTrackingClick(View view) {
        Intent intent = new Intent(this, TrackingActivity.class);

        NavigationContext.setNavigationContext(intent, navigationContext);

        startActivity(intent);
    }

    public void onPointSizeViewClick(View view) {
        int radius = this.drawingPointManager.getRadius();

        if (radius <= 5){
            radius = 10;
        } else if (radius <= 10) {
            radius = 20;
        } else {
            radius = 5;
        }

        this.drawingPointManager.setRadius(radius);
        TextView tw = (TextView)findViewById(R.id.pointSizeView);
        tw.setText(String.valueOf(radius));
    }

    public void onStrokeSizeViewClick(View view) {
        int strokeWidth = this.drawingScreenView.getStrokeWidth();

        if (strokeWidth <= 5){
            strokeWidth = 10;
        } else if (strokeWidth <= 10) {
            strokeWidth = 20;
        } else {
            strokeWidth = 5;
        }

        this.drawingScreenView.setStrokeWidth(strokeWidth);
        TextView tw = (TextView)findViewById(R.id.strokeSizeView);
        tw.setText(String.valueOf(strokeWidth));
    }














    private LayerPoint getCorresponding(MappedPoint mp, List<LayerPoint> lpl){
        for (LayerPoint lp : lpl){
            if (lp.relatedMappedPoint.id == mp.id) return lp;
        }
        return null;
    }






























    /* List click */
    public void onSimpleDeleteListItemClick(View view) {
        Object itemToRemove = view.getTag();
        if (isShowingMappedPoints){
            this.mappedPointManager.removePoint((MappedPoint)itemToRemove);
        } else {
            this.drawingPointManager.removePoint((DrawingPoint)itemToRemove);
        }

        updateGeoLocationsText();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object itemClicked = parent.getItemAtPosition(position);
        if (isShowingMappedPoints){
            this.mappedPointManager.toggleHighlightPoint((MappedPoint) itemClicked);
        } else {
            this.drawingPointManager.toggleHighlightPoint((DrawingPoint) itemClicked);
        }
        updateGeoLocationsText();
    }













    /* IOnPointChanged*/
    @Override
    public void onPointChanged() {
        updateGeoLocationsText();
    }



}
