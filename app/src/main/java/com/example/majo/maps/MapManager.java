package com.example.majo.maps;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.util.Log;
import android.util.TypedValue;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by majo on 28-Jan-15.
 * /data/data/com.example.majo.drawingscreen/files
 */
public class MapManager {
    public static String getSmallBitmapFileName(int mapId){
        return "map_" + String.valueOf(mapId) + "_small";
    }

    public static String getSmallBitmapFileNameComplete(int mapId, Context context){
        return context.getFilesDir().getPath() + '/' + getSmallBitmapFileName(mapId);
    }

    public static String getBitmapFileName(int mapId){
        return "map_" + String.valueOf(mapId);
    }

    public static String getBitmapFileNameComplete(int mapId, Context context){
        return context.getFilesDir().getPath() + '/' + getBitmapFileName(mapId);
    }

    public static void deleteBitmap(int mapId, Context context){
        File file = new File(getBitmapFileNameComplete(mapId, context));
        file.delete();

        file = new File(getSmallBitmapFileNameComplete(mapId, context));
        file.delete();
    }

    public static void storeBitmap(int mapId, Bitmap bitmap, Context context){
        // store big
        try {
            FileOutputStream out = context.openFileOutput(getBitmapFileName(mapId), Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // store small
        Resources r = context.getResources();
        int sizeX = Math.round(pxFromDp(context, 80));
        int sizeY = Math.round(pxFromDp(context, 80));
        Log.d("Thumbnail", "Thumbnail size px " + String.valueOf(sizeX));

        Bitmap smallBitmap = ThumbnailUtils.extractThumbnail(bitmap, sizeX, sizeY);
        try {
            FileOutputStream out = context.openFileOutput(getSmallBitmapFileName(mapId), Context.MODE_PRIVATE);
            smallBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap loadSmallBitmap(int mapId, Context context){
        String pathToFilename = getSmallBitmapFileNameComplete(mapId, context);
        File bitmapSmallFile = new File(pathToFilename);
        if (bitmapSmallFile.exists()) {
            return BitmapFactory.decodeFile(pathToFilename);
        }
        return null;
    }

    public static Bitmap loadBitmap(int mapId, Context context){
        String pathToFilename = getBitmapFileNameComplete(mapId, context);
        File bitmapSmallFile = new File(pathToFilename);
        if (bitmapSmallFile.exists()) {
            return BitmapFactory.decodeFile(pathToFilename);
        }
        return null;
    }

    public static float pxFromDp(Context context, float dp)
    {
        return dp * context.getResources().getDisplayMetrics().density;
    }

}
