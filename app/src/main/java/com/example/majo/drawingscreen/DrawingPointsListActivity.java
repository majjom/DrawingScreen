package com.example.majo.drawingscreen;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.majo.BusinessObjects.DrawingPoint;
import com.example.majo.persistence.DrawingPointPersistence;
import com.example.majo.persistence.IDrawingPointPersistence;


public class DrawingPointsListActivity extends ActionBarActivity {

    ListView drawingPoints;

    IDrawingPointPersistence db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing_points_list);

        drawingPoints = (ListView)findViewById(R.id.drawingPoints);
        db = new DrawingPointPersistence(this);

        ArrayAdapter<DrawingPoint> aa = new ArrayAdapter<DrawingPoint>(this, android.R.layout.simple_list_item_1, db.getAllPoints(0));
        drawingPoints.setAdapter(aa);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_drawing_points_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
