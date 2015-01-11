package com.example.majo.GoogleMap;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * Created by majo on 11-Jan-15.
 */
public class GpsTrackerServiceHelper {

    public static Intent getServiceIntent(Context context){
        return new Intent(context, GpsTrackerService.class);
    }

    public static  boolean isServiceStarted(){
        return GpsTrackerService.getStaticInstance() != null;
    }

    public static IGpsTrackerService getTrackerService(){
        return GpsTrackerService.getStaticInstance();
    }


}
