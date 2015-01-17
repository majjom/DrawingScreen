package com.example.majo.drawingscreen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.majo.Activities.GeoLocationsMapsActivity;
import com.example.majo.Adapters.SimpleDeleteListAdapter;
import com.example.majo.BusinessObjects.DrawingPoint;
import com.example.majo.BusinessObjects.GeoLocation;
import com.example.majo.BusinessObjects.MappedPoint;
import com.example.majo.GoogleMap.LocationConverter;
import com.example.majo.drawingscreenlist.DrawingPointsListActivity;
import com.example.majo.Activities.GeoSessionsListActivity;
import com.example.majo.persistence.DatabaseConnection;
import com.example.majo.persistence.DrawingPointPersistence;
import com.example.majo.persistence.IDatabaseConnection;
import com.example.majo.persistence.IDrawingPointPersistence;
import com.example.majo.persistence.IMappedPointsPersistence;
import com.example.majo.persistence.MappedPointsPersistence;

import java.util.ArrayList;
import java.util.List;


public class DrawingPointsActivity extends Activity implements AdapterView.OnItemClickListener, IOnPointListener {

    private IDatabaseConnection db;
    private IDrawingPointPersistence drawingPointPersistence;
    private IMappedPointsPersistence mappedPointsPersistence;

    private DrawingScreenView drawingScreenView;
    private DrawingPointsLayer drawingPointsLayer;
    private PointsLayer mappedPointsLayer;

    private NavigationContext navigationContext;

    private boolean isLowerListListVisible = false;
    SimpleDeleteListAdapter<LayerPoint> layerPointAdapter;

    List<LayerPoint> drawingPoints;
    List<LayerPoint> mappedPoints;

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
        drawingPoints = PointConverter.drawingPointToLayerPoint(drawingPointPersistence.getAllPoints(this.navigationContext.getSchemaMapId()));
        mappedPoints = PointConverter.mappedPointToLayerPoint(mappedPointsPersistence.getAllPoints(this.navigationContext.getSchemaMapId()));




        // setup drawing screen
        String assetName = "melchsee_small.jpg";
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

        // connect layers with Screen drawing
        this.drawingScreenView.addLayer(this.drawingPointsLayer);
        this.drawingScreenView.addLayer(this.mappedPointsLayer);



        // lower list section
        this.layerPointAdapter = new SimpleDeleteListAdapter(DrawingPointsActivity.this, R.layout.list_item_simple_delete, this.drawingPoints);

        ListView lowerList = (ListView)findViewById(R.id.lowerList);
        lowerList.setAdapter(this.layerPointAdapter);
        lowerList.setOnItemClickListener(this);

        updateLowerListSection();
        updateGeoLocationsText();

        updateButtons();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.db!=null){
            this.db.destroy();
        }
    }







    private void updateButtons(){
        ImageButton btn = (ImageButton)findViewById(R.id.toggleDrawingMode);
        if (this.drawingScreenView.isDrawingMode()){
            btn.setBackgroundResource(R.drawable.drawing_enabled);
        } else {
            btn.setBackgroundResource(R.drawable.drawing_disabled);
        }

        btn = (ImageButton)findViewById(R.id.toggleLowerListSectionVisibility);
        if (this.isLowerListListVisible){
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
    }

    private void updateLowerListSection(){
        LinearLayout ll = (LinearLayout)findViewById(R.id.lowerListSection);
        if (isLowerListListVisible){
            ll.setVisibility(View.VISIBLE);
        } else {
            ll.setVisibility(View.GONE);
        }
    }

    private void updateGeoLocationsText(){
        TextView tw = (TextView)findViewById(R.id.lowerText);
        tw.setText(String.format("points:%d high:%s MapId:%d", this.drawingPointsLayer.getPoints().size(), this.drawingPointsLayer.getHighlightedPoints().size(), this.navigationContext.getSchemaMapId()));
    }





    /* On click buttons */
    public void onToggleDrawingModeClick(View view) {
        // business operation
        this.drawingScreenView.setDrawingMode(!this.drawingScreenView.isDrawingMode());
        updateButtons();
    }

    public void onToggleLowerListSectionVisibilityClick(View view) {
        this.isLowerListListVisible = !this.isLowerListListVisible;
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
        this.drawingPointsLayer.removeAllPoints();
        this.drawingPointPersistence.deleteAllPoints(this.navigationContext.getSchemaMapId());
        this.layerPointAdapter.clear();

        updateGeoLocationsText();
    }




























    /* List click */
    public void onSimpleDeleteListItemClick(View view) {
        LayerPoint itemToRemove = (LayerPoint)view.getTag();

        this.drawingPointsLayer.removePoint(itemToRemove);
        this.drawingPointPersistence.deleteDrawingPoint(itemToRemove.relatedDrawingPoint);
        this.layerPointAdapter.remove(itemToRemove);

        updateGeoLocationsText();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LayerPoint itemClicked = (LayerPoint)parent.getItemAtPosition(position);

        // this shows clicked location and hides the last clicked one
        this.drawingPointsLayer.highlightPoint(itemClicked);
        this.layerPointAdapter.notifyDataSetChanged();

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
