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
import com.example.majo.Activities.SchemaMapListActivity;
import com.example.majo.Adapters.SimpleDeleteListAdapter;
import com.example.majo.BusinessObjects.DrawingPoint;
import com.example.majo.Activities.GeoSessionsListActivity;
import com.example.majo.BusinessObjects.MappedPoint;
import com.example.majo.helper.NavigationContext;
import com.example.majo.helper.PointConverter;
import com.example.majo.merging.PointMerge;
import com.example.majo.persistence.DatabaseConnection;
import com.example.majo.persistence.DrawingPointPersistence;
import com.example.majo.persistence.GeoLocationPersistence;
import com.example.majo.persistence.IDatabaseConnection;
import com.example.majo.persistence.IDrawingPointPersistence;
import com.example.majo.persistence.IMappedPointsPersistence;
import com.example.majo.persistence.MappedPointsPersistence;

import java.util.ArrayList;
import java.util.List;


public class DrawingPointsActivity extends Activity implements AdapterView.OnItemClickListener, IOnPointChanged {

    private IDatabaseConnection db;

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
        db = new DatabaseConnection(this);

        // setup common DP + MP -------------------------------
        this.drawingScreenView = (DrawingScreenView)findViewById(R.id.imageView);
        this.drawingScreenView.setImageAsset(this.getAssetName());

        this.lowerList = (ListView)findViewById(R.id.lowerList);
        this.lowerList.setOnItemClickListener(this);

        // setup DP -------------------------------
        this.drawingPointLayer = new Layer(this, this.getAssetName());

        List<DrawingPoint> emptyDrawingPointList = new ArrayList<>();
        this.drawingPointAdapter = new SimpleDeleteListAdapter(DrawingPointsActivity.this, R.layout.list_item_simple_delete, emptyDrawingPointList); // first empty

        this.drawingPointManager = new DrawingPointManager(this.drawingPointLayer, this.drawingPointAdapter, new DrawingPointPersistence(this.db), this, this.navigationContext.getSchemaMapId());
        this.drawingPointManager.setColor(Color.BLUE);
        this.drawingPointManager.setHighlightColor(Color.RED);
        this.drawingPointManager.setRadius(10);;



        // connecting DP -------------------------------
        this.lowerList.setAdapter(this.drawingPointAdapter);
        this.drawingScreenView.addLayer(this.drawingPointLayer);
        this.drawingScreenView.setPointListener(this.drawingPointManager);


        // setup MP -------------------------------
        this.mappedPointLayer = new Layer(this, this.getAssetName());
        this.mappedPointLayer.setVisibility(false);

        List<MappedPoint> emptyMappedPointList = new ArrayList<>();
        this.mappedPointAdapter = new SimpleDeleteListAdapter(DrawingPointsActivity.this, R.layout.list_item_simple_delete, emptyMappedPointList); // first empty

        this.mappedPointManager = new MappedPointManager(this.mappedPointLayer, this.mappedPointAdapter, new MappedPointsPersistence(this.db), this.navigationContext.getSchemaMapId());
        this.mappedPointManager.setColor(Color.GREEN);
        this.mappedPointManager.setHighlightColor(Color.CYAN);

        // connecting DP -------------------------------
        this.drawingScreenView.addLayer(this.mappedPointLayer);


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




        /*
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
        */


        this.mappedPointManager.refreshPointsFromDb();
        this.drawingPointManager.refreshPointsFromDb();
        updateButtons();
        updateGeoLocationsText();

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
            /*
            List<DrawingPoint> points = this.drawingPointManager.getPoints();
            this.lowerList.setAdapter(new SimpleDeleteListAdapter(DrawingPointsActivity.this, R.layout.list_item_simple_delete, points));
            */
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

        startActivity(intent);
    }

    public void onGeoSessionsListClick(View view) {
        Intent intent = new Intent(this, GeoSessionsListActivity.class);

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
            // todo
            //this.mappedPointManager.removePoint();
        } else {
            this.drawingPointManager.removePoints(this.drawingPointManager.getHighlightedPoints());
        }

        updateGeoLocationsText();
        updateButtons();
    }

    public void onUndoHighLightedPointsClick(View view) {
        if (this.isShowingMappedPoints){
            // todo
            this.mappedPointManager.clearHighlight();
        } else {
            this.drawingPointManager.clearHighlight();
        }

        updateGeoLocationsText();
        updateButtons();
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


    public void onToggleDrawingMappedPointClick(View view) {
        this.setIsShowingMappedPoints(!this.isShowingMappedPoints);

        updateGeoLocationsText();
        updateButtons();
    }











    public void onRefreshPositionClick(View view) {
        /*
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
        */
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
            this.drawingPointManager.removePoint((DrawingPoint)itemToRemove);
        } else {
            this.mappedPointManager.removePoint((MappedPoint)itemToRemove);
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
