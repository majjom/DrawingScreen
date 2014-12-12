package com.example.majo.drawingscreen;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.example.majo.persistence.DrawingPointPersistence;
import com.example.majo.persistence.IDrawingPointPersistence;


public class MainActivity extends Activity {

    private DrawingScreenView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        imageView = (DrawingScreenView)findViewById(R.id.imageView);
        imageView.setImageAsset("melchsee.jpg");



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
            IDrawingPointPersistence db = new DrawingPointPersistence();
            //imageView.addPoints(db.getDrawingPoints());
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
            this.imageView.loadAllPoints();
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
        this.imageView.submitDrawing();
    }
}
