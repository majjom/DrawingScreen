package com.example.majo.drawingscreen;

import android.app.Activity;
import android.content.Context;

/**
 * Created by majo on 11-Jan-15.
 */
public class ActivityExtras {
    public static final String EXTRA_GEO_SESSION_ID = "EXTRA_GEO_SESSION_ID";
    public static final String EXTRA_MAP_ID = "EXTRA_MAP_ID";


    public static int getGeoSessionIdFromIntent(Activity activity){
        return activity.getIntent().getIntExtra(EXTRA_GEO_SESSION_ID, -1);
    }
    public static int getMapIdFromIntent(Activity activity){
        return activity.getIntent().getIntExtra(EXTRA_MAP_ID, -1);
    }

}
