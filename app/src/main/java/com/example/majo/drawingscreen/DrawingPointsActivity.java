package com.example.majo.drawingscreen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
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
import com.example.majo.Activities.SchemaMapListActivity;
import com.example.majo.Adapters.SimpleDeleteListAdapter;
import com.example.majo.BusinessObjects.DrawingPoint;
import com.example.majo.Activities.GeoSessionsListActivity;
import com.example.majo.BusinessObjects.MappedPoint;
import com.example.majo.merging.PointMerge;
import com.example.majo.persistence.DatabaseConnection;
import com.example.majo.persistence.DrawingPointPersistence;
import com.example.majo.persistence.GeoLocationPersistence;
import com.example.majo.persistence.IDatabaseConnection;
import com.example.majo.persistence.IDrawingPointPersistence;
import com.example.majo.persistence.IMappedPointsPersistence;
import com.example.majo.persistence.MappedPointsPersistence;
import com.example.majo.position.PositionService;

import java.util.List;


public class DrawingPointsActivity extends Activity implements AdapterView.OnItemClickListener, IOnPointListener {

    private IDatabaseConnection db;
    private IDrawingPointPersistence drawingPointPersistence;
    private GeoLocationPersistence geoLocationPersistence;
    private IMappedPointsPersistence mappedPointsPersistence;

    private DrawingScreenView drawingScreenView;
    private DrawingPointsLayer drawingPointsLayer;
    private PointsLayer mappedPointsLayer;

    private NavigationContext navigationContext;

    private boolean isLowerListVisible = false;
    SimpleDeleteListAdapter<LayerPoint> layerPointAdapter;
    SimpleDeleteListAdapter<LayerPoint> mappedPointAdapter;
    private boolean isShowingMappedPoints = false;

    List<LayerPoint> drawingPoints;
    List<LayerPoint> mappedPoints;


    PositionService positionService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.navigationContext = NavigationContext.getNavigationContextFromActivity(this);


        // setup window features
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_drawing_points);



        //set up database and load data
        db = new DatabaseConnection(this);
        this.drawingPointPersistence = new DrawingPointPersistence(db);
        this.mappedPointsPersistence = new MappedPointsPersistence(db);
        this.geoLocationPersistence = new GeoLocationPersistence(db);

        ListView lowerList = (ListView)findViewById(R.id.lowerList);
        lowerList.setOnItemClickListener(this);


        this.positionService = new PositionService(this.mappedPointsPersistence, this.navigationContext.getSchemaMapId());


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




        // TODO make this better (mapping DP with GL -> MP)
        if (this.navigationContext.getAssociatingMappedPoints()){
            this.navigationContext.setAssociatingMappedPoints(false);

            int mapId = this.navigationContext.getSchemaMapId();
            int geoSessionId = this.navigationContext.getGeoSessionId();

            List<MappedPoint> mappedPointList = PointMerge.mergePoints(this.drawingPointPersistence.getAllPoints(mapId), this.geoLocationPersistence.getAllLocations(geoSessionId));
            this.mappedPointsPersistence.addPoints(mapId, mappedPointList);

            this.drawingPointPersistence.deleteAllPoints(mapId);

            Toast.makeText(this, "Created mapped point count:" + String.valueOf(mappedPointList.size()), Toast.LENGTH_SHORT).show();
        }









        // get from DB and refresh




        drawingPoints = PointConverter.drawingPointToLayerPoint(drawingPointPersistence.getAllPoints(this.navigationContext.getSchemaMapId()));
        mappedPoints = PointConverter.mappedPointToLayerPoint(mappedPointsPersistence.getAllPoints(this.navigationContext.getSchemaMapId()));




        // setup drawing screen
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

        this.drawingScreenView = (DrawingScreenView)findViewById(R.id.imageView);
        this.drawingScreenView.setImageAsset(assetName);
        this.drawingScreenView.setPointListener(this);

        // setup layers
        this.drawingPointsLayer = new DrawingPointsLayer(this, assetName);
        this.mappedPointsLayer = new PointsLayer(this, assetName);
        this.drawingPointsLayer.addPoints(drawingPoints);
        this.mappedPointsLayer.addPoints(mappedPoints);
        this.drawingPointsLayer.setVisibility(false);
        this.mappedPointsLayer.setVisibility(false);
        this.mappedPointsLayer.setColor(Color.BLUE);

        // connect layers with Screen drawing
        this.drawingScreenView.addLayer(this.drawingPointsLayer);
        this.drawingScreenView.addLayer(this.mappedPointsLayer);



        // lower list section

        rebindListOfPoints();


        updateLowerListSection();
        updateGeoLocationsText();

        updateButtons();



















    }

    private void updateButtons(){
        ImageButton btn = (ImageButton)findViewById(R.id.toggleDrawingMode);
        if (this.drawingScreenView.isDrawingMode()){
            btn.setBackgroundResource(R.drawable.drawing_enabled);
        } else {
            btn.setBackgroundResource(R.drawable.drawing_disabled);
        }

        btn = (ImageButton)findViewById(R.id.toggleLowerListSectionVisibility);
        if (this.isLowerListVisible){
            btn.setBackgroundResource(R.drawable.path_visible);
        } else {
            btn.setBackgroundResource(R.drawable.path_invisible);
        }

        btn = (ImageButton)findViewById(R.id.toggleDrawingPointsVisibility);
        if (this.drawingPointsLayer.isVisible()){
            btn.setBackgroundResource(R.drawable.path_visible);
        } else {
            btn.setBackgroundResource(R.drawable.path_invisible);
        }

        btn = (ImageButton)findViewById(R.id.toggleMappedPointsVisibility);
        if (this.mappedPointsLayer.isVisible()){
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
    }

    private void updateLowerListSection(){
        LinearLayout ll = (LinearLayout)findViewById(R.id.lowerListSection);
        if (isLowerListVisible){
            ll.setVisibility(View.VISIBLE);
        } else {
            ll.setVisibility(View.GONE);
        }
    }

    private void updateGeoLocationsText(){
        TextView tw = (TextView)findViewById(R.id.lowerText);
        if (isShowingMappedPoints){
            tw.setText(String.format("MP:%d high:%s MapId:%d", this.mappedPointsLayer.getPoints().size(), this.mappedPointsLayer.getHighlightedPoints().size(), this.navigationContext.getSchemaMapId()));
        } else {
            tw.setText(String.format("points:%d high:%s MapId:%d", this.drawingPointsLayer.getPoints().size(), this.drawingPointsLayer.getHighlightedPoints().size(), this.navigationContext.getSchemaMapId()));
        }

    }

    private void rebindListOfPoints(){
        ListView lowerList = (ListView)findViewById(R.id.lowerList);
        if (isShowingMappedPoints){
            this.mappedPointAdapter = new SimpleDeleteListAdapter(DrawingPointsActivity.this, R.layout.list_item_simple_delete, this.mappedPoints);
            lowerList.setAdapter(this.mappedPointAdapter);
        } else {
            this.layerPointAdapter = new SimpleDeleteListAdapter(DrawingPointsActivity.this, R.layout.list_item_simple_delete, this.drawingPoints);
            lowerList.setAdapter(this.layerPointAdapter);
        }
    }





    /* On click buttons */
    public void onToggleDrawingModeClick(View view) {
        // business operation
        this.drawingScreenView.setDrawingMode(!this.drawingScreenView.isDrawingMode());
        updateButtons();
    }

    public void onToggleLowerListSectionVisibilityClick(View view) {
        this.isLowerListVisible = !this.isLowerListVisible;
        updateLowerListSection();
        updateButtons();
    }

    public void onToggleDrawingPointsVisibilityClick(View view) {
        // business operation
        this.drawingPointsLayer.setVisibility(!this.drawingPointsLayer.isVisible());
        updateButtons();
    }

    public void onToggleMappedPointsVisibilityClick(View view) {
        // business operation
        this.mappedPointsLayer.setVisibility(!this.mappedPointsLayer.isVisible());
        updateButtons();
    }

    public void onGeoLocationsMapClick(View view) {
        Intent intent = new Intent(this, GeoLocationsMapsActivity.class);

        startActivity(intent);
    }

    public void onGeoSessionsListClick(View view) {
        Intent intent = new Intent(this, GeoSessionsListActivity.class);

        startActivity(intent);
    }

    public void onDeleteAllDrawingPointsClick(View view) {
        if (isShowingMappedPoints){
            this.mappedPointsLayer.removeAllPoints();
            this.mappedPointsPersistence.deleteAllPoints(this.navigationContext.getSchemaMapId());
            this.mappedPointAdapter.clear();
        } else {
            this.drawingPointsLayer.removeAllPoints();
            this.drawingPointPersistence.deleteAllPoints(this.navigationContext.getSchemaMapId());
            this.layerPointAdapter.clear();
        }


        updateGeoLocationsText();
    }

    public void onSchemaMapListClick(View view) {
        Intent intent = new Intent(this, SchemaMapListActivity.class);

        startActivity(intent);
    }

    public void onSelectGeoSessionClick(View view) {
        Intent intent = new Intent(this, GeoSessionsListActivity.class);
        navigationContext.setAssociatingMappedPoints(true);
        NavigationContext.setNavigationContext(intent, navigationContext);

        startActivity(intent);
    }

    public void onDeleteAllMappedPointsClick(View view) {

    }


    public void onToggleDrawingMappedPointClick(View view) {
        this.isShowingMappedPoints = !isShowingMappedPoints;
        updateLowerListSection();
        updateGeoLocationsText();

        updateButtons();
        rebindListOfPoints();
    }

    public void onRefreshPositionClick(View view) {
        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        LayerPoint lp;

        if (loc == null){
            Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
            return;
        } else {

            MappedPoint mp = this.positionService.getCurrentPosition(loc);
            if (mp==null){
                Toast.makeText(this, "positionService.getCurrentPosition NULL", Toast.LENGTH_SHORT).show();
                return;
            }


            lp = getCorresponding(mp, this.mappedPoints);
            String toastText = loc.toString();
            if (lp != null){
                toastText += lp.toString();
            }
            Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();
        }




        this.mappedPointsLayer.undoHighlight();
        this.mappedPointsLayer.highlightPoint(lp);


        updateLowerListSection();
        updateGeoLocationsText();

        updateButtons();
        rebindListOfPoints();
    }

    private LayerPoint getCorresponding(MappedPoint mp, List<LayerPoint> lpl){
        for (LayerPoint lp : lpl){
            if (lp.relatedMappedPoint.id == mp.id) return lp;
        }
        return null;
    }






























    /* List click */
    public void onSimpleDeleteListItemClick(View view) {
        LayerPoint itemToRemove = (LayerPoint)view.getTag();

        if (isShowingMappedPoints){
            this.mappedPointsLayer.removePoint(itemToRemove);
            this.mappedPointsPersistence.deleteMappedPoint(itemToRemove.relatedMappedPoint);
            this.mappedPointAdapter.remove(itemToRemove);



        } else {

            this.drawingPointsLayer.removePoint(itemToRemove);
            this.drawingPointPersistence.deleteDrawingPoint(itemToRemove.relatedDrawingPoint);
            this.layerPointAdapter.remove(itemToRemove);

        }


        updateGeoLocationsText();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LayerPoint itemClicked = (LayerPoint)parent.getItemAtPosition(position);

        if (isShowingMappedPoints){
            this.mappedPointsLayer.highlightPoint(itemClicked);
            this.mappedPointAdapter.notifyDataSetChanged();
        } else {
            // this shows clicked location and hides the last clicked one
            this.drawingPointsLayer.highlightPoint(itemClicked);
            this.layerPointAdapter.notifyDataSetChanged();
        }



        updateGeoLocationsText();
    }













    /*callback from Image drawing view*/

    @Override
    public DrawingPoint addDrawingPoint(float vX, float vY) {
        LayerPoint lp = this.drawingPointsLayer.addPoint(vX, vY);

        if (lp != null) {
            this.drawingPointPersistence.addPoint(this.navigationContext.getSchemaMapId(), lp.relatedDrawingPoint);
            this.layerPointAdapter.add(lp);
        }

        updateGeoLocationsText();

        if (lp!=null) {
            return lp.relatedDrawingPoint;
        }

        return null;
    }

    @Override
    public DrawingPoint removeLastDrawingPoint() {
        LayerPoint lp = this.drawingPointsLayer.removeLastPoint();
        this.drawingPointPersistence.deleteDrawingPoint(lp.relatedDrawingPoint);
        this.layerPointAdapter.remove(lp);

        updateGeoLocationsText();
        return lp.relatedDrawingPoint;
    }


}
