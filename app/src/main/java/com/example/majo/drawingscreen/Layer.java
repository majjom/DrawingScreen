package com.example.majo.drawingscreen;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;


import com.example.majo.BusinessObjects.DrawingPoint;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by majo on 12-Dec-14.
 */
public class Layer implements IPointLayer, IBitmapLayer {
    private Canvas canvas;
    private Bitmap bitmap;

    private Paint paint;

    private boolean isVisible;

    IOnBitmapLayerRedraw redrawListener;

    public Layer(Context context, String assetName){
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inJustDecodeBounds = true;

        try {
            //InputStream str = context.getAssets().open(assetName, AssetManager.ACCESS_RANDOM);
            InputStream str = context.openFileInput(assetName);

            BitmapFactory.decodeStream(str, null, bitmapOptions);
            str.close();

            int width = bitmapOptions.outWidth;
            int height = bitmapOptions.outHeight;
            init(width, height);
        } catch (Exception e) {
            Log.e("PointsLayer", "Failed to initialise bitmap decoder", e);
        }
    }

    public Layer(int width, int height){
        init(width, height);
    }

    private void init(int width, int height){
        this.bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        this.canvas = new Canvas(this.bitmap);

        this.paint = new Paint();
        this.paint.setColor(Color.GREEN);
        this.paint.setAntiAlias(true);
        this.paint.setStyle(Paint.Style.FILL);

        this.isVisible = true;
        this.redrawListener = null;
    }


    /*******************************************/
    /*****************IBitmapLayer**************/
    /*******************************************/

    public void setVisibility(boolean isVisible){
        this.isVisible = isVisible;
        redrawRequest();
    }

    public boolean isVisible(){
        return this.isVisible;
    }

    public Bitmap getBitmap(){
        return this.bitmap;
    }

    public void attachToRedrawListener(IOnBitmapLayerRedraw listener){
        this.redrawListener = listener;
    }







    /******************************************************/
    /*****************IPointsLayer  ***************/



    public void clear(){
        this.bitmap.eraseColor(Color.TRANSPARENT);
        redrawRequest();
    }

    public void drawPoints(List<DrawingPoint> points, int color) {
        for (DrawingPoint point : points){
            this.drawPoint(point, color);
        }
        redrawRequest();
    }

    public void addPoint(DrawingPoint point, int color){
        this.drawPoint(point, color);
        redrawRequest();
    }

    private void drawPoint(DrawingPoint point, int color) {
        this.paint.setColor(color);
        this.canvas.drawCircle(point.x, point.y, point.radius, this.paint);
    }

    private void redrawRequest(){
        if (this.redrawListener != null){
            this.redrawListener.onRedrawRequest();
        }
    }







}
