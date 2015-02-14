package com.example.majo.drawingscreenlist;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.example.majo.Adapters.DrawingPointsAdapter;
import com.example.majo.BusinessObjects.DrawingPoint;
import com.example.majo.drawingscreen.R;
import com.example.majo.persistenceLocalDatabase.DatabaseConnection;
import com.example.majo.persistenceLocalDatabase.DrawingPointPersistence;
import com.example.majo.persistenceLocalDatabase.IDatabaseConnection;
import com.example.majo.persistenceLocalDatabase.IDrawingPointPersistence;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.Toast;

import java.util.List;


public class DrawingPointsListActivity extends ActionBarActivity {

    ListView drawingPoints;

    IDatabaseConnection db;
    IDrawingPointPersistence persistence;
    List<DrawingPoint> list;

    private int schemaMapId;




    private RecyclerView mRecyclerView;
    private DrawingPointsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // todo take this from context
        this.schemaMapId = 1;

        setContentView(R.layout.activity_drawing_points_list);

        /* Initialize recycler view */
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        db = new DatabaseConnection(this);
        persistence = new DrawingPointPersistence(db);
        list = persistence.getAllPoints(this.schemaMapId);

        /* attach mAdapter */
        mAdapter = new DrawingPointsAdapter(DrawingPointsListActivity.this, list);
        mRecyclerView.setAdapter(mAdapter);

        /* swipe listener */
        SwipeDismissRecyclerViewTouchListener touchListener =
                new SwipeDismissRecyclerViewTouchListener(
                        mRecyclerView,
                        new SwipeDismissRecyclerViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(RecyclerView recyclerView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {

                                    // TODO: this is temp solution for preventing blinking item onDismiss
                                    mLayoutManager.findViewByPosition(position).setVisibility(View.GONE);

                                    persistence.deleteDrawingPoint(list.get(position));
                                    list.remove(position);
                                    mAdapter.notifyItemRemoved(position);
                                }
                            }
                        });
        mRecyclerView.setOnTouchListener(touchListener);

        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        mRecyclerView.setOnScrollListener(touchListener.makeScrollListener());

        /*on touch listener*/
        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,
                new OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Toast.makeText(DrawingPointsListActivity.this, "Clicked " + list.get(position).toString(), Toast.LENGTH_SHORT).show();
                    }
                }));

    }

    @Override
    protected void onDestroy() {
        if (db != null){
            db.destroy();
        }
        super.onDestroy();
    }
}
