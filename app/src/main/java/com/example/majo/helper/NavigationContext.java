package com.example.majo.helper;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import java.io.Serializable;

/**
 * Created by majo on 11-Jan-15.
 */
public class NavigationContext implements Serializable {

    private static final boolean isDebug = true;
    private static final String TAG = "NavigationContext";
    private static final String EXTRA_NAVIGATION_CONTEXT = "EXTRA_NAVIGATION_CONTEXT";

    private int schemaMapId;
    private int drawingPointId;
    private int mappedPointId;
    private int geoSessionId;
    private int geoLocationId;

    private boolean isSelectingGeoSessionsForMapping;


    public int getSchemaMapId() {
        return schemaMapId;
    }

    public void setSchemaMapId(int schemaMapId) {
        this.schemaMapId = schemaMapId;
        Log("M:" + this.toString());
    }


    public int getDrawingPointId() {
        return drawingPointId;
    }

    public void setDrawingPointId(int drawingPointId) {
        this.drawingPointId = drawingPointId;
        Log("DP:" + this.toString());
    }

    public int getMappedPointId() {
        return mappedPointId;
    }

    public void setMappedPointId(int mappedPointId) {
        this.mappedPointId = mappedPointId;
        Log("MP:" + this.toString());
    }

    public int getGeoSessionId() {
        return geoSessionId;
    }

    public void setGeoSessionId(int geoSessionId) {
        this.geoSessionId = geoSessionId;
        Log("GS:" + this.toString());
    }

    public int getGeoLocationId() {
        return geoLocationId;
    }

    public void setGeoLocationId(int geoLocationId) {
        this.geoLocationId = geoLocationId;
        Log("GL:" + this.toString());
    }


    public boolean getIsSelectingGeoSessionForMapping() {
        return isSelectingGeoSessionsForMapping;
    }

    public void setSelectingGeoSessionsForMapping(boolean selectingGeoSessionsForMapping) {
        this.isSelectingGeoSessionsForMapping = selectingGeoSessionsForMapping;
        Log("GL:" + this.toString());
    }


    public NavigationContext(){
        this.schemaMapId = -1;
        this.drawingPointId = -1;
        this.mappedPointId = -1;
        this.geoLocationId = -1;
        this.geoSessionId = -1;
    }


    public static NavigationContext getNavigationContextFromActivity(Activity activity){
        NavigationContext result =  (NavigationContext)activity.getIntent().getSerializableExtra(EXTRA_NAVIGATION_CONTEXT);
        if (result == null) {
            result = new NavigationContext();
        }
        Log("get:" + result.toString());

        return result;
    }

    public static NavigationContext getNavigationContextFromActivity(Activity activity, Intent intent){
        NavigationContext result =  (NavigationContext)intent.getSerializableExtra(EXTRA_NAVIGATION_CONTEXT);
        if (result == null) {
            result = new NavigationContext();
        }
        Log("get:" + result.toString());

        return result;
    }


    public static void setNavigationContext(Intent intent, NavigationContext navigationContext){
        if ((intent == null) || (navigationContext == null)) {
            Log("set:NULL");
            return;
        }
        intent.putExtra(EXTRA_NAVIGATION_CONTEXT, navigationContext);
        Log("set:" + navigationContext.toString());
    }

    public String toString(){
        String result = "";
        if (this.schemaMapId > -1) result += "M:" + String.valueOf(this.schemaMapId);
        if (this.drawingPointId > -1) result += "DP:" + String.valueOf(this.drawingPointId);
        if (this.mappedPointId > -1) result += "MP:" + String.valueOf(this.mappedPointId);
        if (this.geoSessionId > -1) result += "GS:" + String.valueOf(this.geoSessionId);
        if (this.geoLocationId > -1) result += "GL:" + String.valueOf(this.geoLocationId);
        if (this.isSelectingGeoSessionsForMapping) result += "selMPTrue";
        if (result == "") result = "undef";
        return result;
    }

    private static void Log(String text){
        if (isDebug) {
            Log.d(TAG, text);
        }
    }

}
