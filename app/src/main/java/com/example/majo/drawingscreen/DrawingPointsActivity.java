package com.example.majo.drawingscreen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.example.majo.Activities.GeoLocationsMapsActivity;
import com.example.majo.BusinessObjects.DrawingPoint;
import com.example.majo.BusinessObjects.MappedPoint;
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


public class DrawingPointsActivity extends Activity {

    private DrawingScreenView imageView;

    private IDatabaseConnection db;
    private IDrawingPointPersistence drawingPointPersistence;
    private IMappedPointsPersistence mappedPointsPersistence;


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

        //set up database
        db = new DatabaseConnection(this);
        this.drawingPointPersistence = new DrawingPointPersistence(db);
        this.mappedPointsPersistence = new MappedPointsPersistence(db);

        // load data
        imageView.addPoints(drawingPointPersistence.getAllPoints(this.schemaMapId));
        imageView.loadMappedPoints(convertToDrawingPoints(mappedPointsPersistence.getAllPoints(this.schemaMapId)));

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.db!=null){
            this.db.destroy();
        }
    }




    private List<DrawingPoint> convertToDrawingPoints(List<MappedPoint> mappedPoints){
        List<DrawingPoint> result = new ArrayList<>();
        for (MappedPoint mappedPoint : mappedPoints){
            result.add(new DrawingPoint(mappedPoint.drawingX, mappedPoint.drawingY, mappedPoint.drawingRadius));
        }
        return result;
    }



    /* On click buttons */
    public void onDisableEnableDrawingClick(View view) {
        ImageButton btn = (ImageButton)findViewById(R.id.disableEnableDrawing);
        if (this.imageView.isDrawingMode()){
            btn.setBackgroundResource(R.drawable.drawing_disabled);
            this.imageView.setDrawingMode(!this.imageView.isDrawingMode());
        } else {
            btn.setBackgroundResource(R.drawable.drawing_enabled);
            this.imageView.setDrawingMode(!this.imageView.isDrawingMode());
        }
    }


    public void onDisableEnablePathVisibilityClick(View view) {
        ImageButton btn = (ImageButton)findViewById(R.id.disableEnablePathVisibility);
        if (this.imageView.isAllPointsVisible()){
            btn.setBackgroundResource(R.drawable.path_invisible);
            this.imageView.toggleAllPointsVisible();
        } else {
            this.imageView.loadAllPoints(this.drawingPointPersistence, this.schemaMapId);
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
        this.imageView.submitDrawing(this.drawingPointPersistence, this.schemaMapId);
    }

    public void onDrawPositionClick(View view) {
        List<DrawingPoint> points =  this.drawingPointPersistence.getAllPoints(this.schemaMapId);
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
        this.drawingPointPersistence.deleteAllPoints(this.schemaMapId);
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

    public void onGeoSessionsGotoClick(View view) {
        Intent intent = new Intent(this, GeoSessionsListActivity.class);

        startActivity(intent);
    }
}
