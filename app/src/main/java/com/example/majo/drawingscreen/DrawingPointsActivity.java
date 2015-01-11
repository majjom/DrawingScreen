package com.example.majo.drawingscreen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.example.majo.BusinessObjects.DrawingPoint;
import com.example.majo.persistence.DrawingPointPersistence;

import java.util.ArrayList;


public class DrawingPointsActivity extends Activity {

    private DrawingScreenView imageView;

    private DrawingPointPersistence persistence;

    // todo put this away
    private int index;

    private int schemaMapId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // todo take this from context
        this.schemaMapId = 1;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_drawing_points);

        imageView = (DrawingScreenView)findViewById(R.id.imageView);
        imageView.setImageAsset("melchsee.jpg");

        persistence = new DrawingPointPersistence(this);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_draw_points) {
            //IDrawingPointPersistence persistence = new RandomDrawingPointPersistence();
            //imageView.addLocations(persistence.getAllLocations());
            //imageView.setPointLayerVisible(true);

            return true;
        }

        if (id == R.id.action_toggle_point_layer) {
            //imageView.togglePointLayerVisible();

            return true;
        }

        if (id == R.id.action_toggle_is_drawing_mode) {
            imageView.toggleDrawingMode();

            return true;
        }

        if (id == R.id.action_erase_points) {
            //imageView.erasePointLayer();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.persistence!=null){
            this.persistence.onDestroy();
        }
    }







    public void onDisableEnableDrawingClick(View view) {
        ImageButton btn = (ImageButton)findViewById(R.id.disableEnableDrawing);
        if (this.imageView.isDrawingMode()){
            btn.setBackgroundResource(R.drawable.drawing_disabled);
            this.imageView.toggleDrawingMode();
        } else {
            btn.setBackgroundResource(R.drawable.drawing_enabled);
            this.imageView.toggleDrawingMode();
        }
    }


    public void onDisableEnablePathVisibilityClick(View view) {
        ImageButton btn = (ImageButton)findViewById(R.id.disableEnablePathVisibility);
        if (this.imageView.isAllPointsVisible()){
            btn.setBackgroundResource(R.drawable.path_invisible);
            this.imageView.toggleAllPointsVisible();
        } else {
            this.imageView.loadAllPoints(this.persistence, this.schemaMapId);
            btn.setBackgroundResource(R.drawable.path_visible);
            //this.imageView.toggleAllPointsVisible();
        }
    }

    public void onUndoPointClick(View view) {
        this.imageView.undoPoint();
    }

    public void onUndoDrawingClick(View view) {
        this.imageView.undoDrawing();
    }

    public void onSubmitDrawingClick(View view) {
        this.imageView.submitDrawing(this.persistence, this.schemaMapId);
    }

    public void onDrawPositionClick(View view) {
        ArrayList<DrawingPoint> points =  this.persistence.getAllPoints(this.schemaMapId);
        if (points.size() == 0) {
            this.imageView.setPositionVisibility(false);
            return;
        } else
        {
            this.imageView.setPositionVisibility(true);
        }


        if (index > points.size() - 1){
            index = 0;
        }
        this.imageView.drawPosition(points.get(index));
        index++;

    }

    public void onDeleteAllStoredPointsClick(View view) {
        this.persistence.deleteAllPoints(this.schemaMapId);
        this.imageView.clearAllPoints();
        onDrawPositionClick(view);
    }

    public void onDrawPointsListClick(View view) {
        Intent intent = new Intent(this, DrawingPointsListActivity.class);
        startActivity(intent);
    }

    public void onGeoLocationsMapClick(View view) {
        Intent intent = new Intent(this, GeoLocationsMapsActivity.class);

        startActivity(intent);
    }
}
