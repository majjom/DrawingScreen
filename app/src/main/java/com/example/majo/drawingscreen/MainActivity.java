package com.example.majo.drawingscreen;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.majo.persistence.DrawingPointPersistence;
import com.example.majo.persistence.IDrawingPointPersistence;


public class MainActivity extends ActionBarActivity {

    private DrawingScreenView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (DrawingScreenView)findViewById(R.id.imageView);
        imageView.setImageAsset("squirrel.jpg");



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            imageView.addPoints(db.getDrawingPoints());
            imageView.setPointLayerVisible(true);

            return true;
        }

        if (id == R.id.action_toggle_point_layer) {
            imageView.togglePointLayerVisible();

            return true;
        }

        if (id == R.id.action_toggle_is_drawing_mode) {
            imageView.toggleDrawingMode();

            return true;
        }

        if (id == R.id.action_erase_points) {
            imageView.erasePointLayer();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
