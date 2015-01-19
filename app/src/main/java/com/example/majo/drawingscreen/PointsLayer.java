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
public class PointsLayer implements IPointsLayer, IBitmapLayer {
    private Canvas canvas;
    private Bitmap bitmap;

    private Paint paint;
    private Paint highlightPaint;

    private List<LayerPoint> points;
    private boolean isVisible;

    IOnBitmapLayerRedraw redrawListener;

    public PointsLayer(Context context, String assetName){
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inJustDecodeBounds = true;

        try {
            InputStream str = context.getAssets().open(assetName, AssetManager.ACCESS_RANDOM);

            BitmapFactory.decodeStream(str, null, bitmapOptions);
            str.close();

            int width = bitmapOptions.outWidth;
            int height = bitmapOptions.outHeight;
            init(width, height);
        } catch (Exception e) {
            Log.e("PointsLayer", "Failed to initialise bitmap decoder", e);
        }
    }

    public PointsLayer(int width, int height){
        init(width, height);
    }

    private void init(int width, int height){
        this.bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        this.canvas = new Canvas(this.bitmap);

        this.paint = new Paint();
        this.paint.setColor(Color.GREEN);
        this.paint.setAntiAlias(true);
        this.paint.setStyle(Paint.Style.FILL);

        this.highlightPaint = new Paint();
        this.highlightPaint.setColor(Color.RED);
        this.highlightPaint.setAntiAlias(true);
        this.highlightPaint.setStyle(Paint.Style.FILL);

        this.points = new ArrayList<>();
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
    /*****************IPointsLayer - highlight points ***************/
    /******************************************************/

    public void setHighlightColor(int color){
        this.highlightPaint.setColor(color);

        if (isSomePointsHighlighted()) {
            clean();
            drawPoints(this.points);
            redrawRequest();
        }
    }

    public int getHighlightColor(){
        return this.highlightPaint.getColor();
    }

    @Override
    public void highlightPoint(LayerPoint point) {
        List<LayerPoint> points = new ArrayList<>();
        points.add(point);
        highlightPoints(points);
    }

    @Override
    public void highlightPoints(List<LayerPoint> points) {
        toggleHighlightLayerPoints(points);
    }

    public void undoHighlight(){
        if (isSomePointsHighlighted()){
            for (LayerPoint point : this.points){
                point.isHighlighted = false;
            }
            clean();
            drawPoints(this.points);
            redrawRequest();
        }
    }

    public List<LayerPoint> getHighlightedPoints(){
        List<LayerPoint> result = new ArrayList<>();
        for (LayerPoint point : this.points){
            if (point.isHighlighted) result.add(point);
        }
        return result;
    }


    public void toggleHighlightLayerPoints(List<LayerPoint> points) {
        if (points == null) return;

        for (LayerPoint point : points){
            int idx = this.points.indexOf(point);
            if (idx != -1) {
                point.isHighlighted = !point.isHighlighted;
            }
        }
        clean();
        drawPoints(this.points);
        redrawRequest();
    }














    /******************************************************/
    /*****************IPointsLayer - highlight points ***************/
    /******************************************************/
    public void setColor(int color){
        this.paint.setColor(color);
        clean();
        drawPoints(this.points);
        redrawRequest();
    }

    public int getColor(){
        return this.paint.getColor();
    }



    public List<LayerPoint> getPoints(){
        return this.points;
    }


    public LayerPoint getLastPoint(){
        LayerPoint lp = getLastLayerPoint();
        if (lp != null){
            return lp;
        }
        return null;
    }




    public void addPoint(LayerPoint point) {
        List<LayerPoint> points = new ArrayList<>();
        points.add(point);
        this.addPoints(points);
    }

    public void addPoints(List<LayerPoint> points) {
        this.points.addAll(points);
        drawPoints(points);
        redrawRequest();
    }






    public void removeAllPoints(){
        this.points = new ArrayList<>();
        clean();
        redrawRequest();
    }

    public void removePoint(LayerPoint point){
        List<LayerPoint> points = new ArrayList<>();
        points.add(point);
        removePoints(points);
    }

    public void removePoints(List<LayerPoint> points){
        this.points.removeAll(points);
        clean();
        drawPoints(this.points);
        redrawRequest();
    }

    public LayerPoint removeLastPoint(){
        LayerPoint lp = getLastLayerPoint();
        if (lp != null){
            this.removePoint(lp);
            return lp;
        }
        return null;
    }






    private LayerPoint getLastLayerPoint() {
        if (this.points.size() > 0) {
            return this.points.get(this.points.size() - 1);
        }
        return null;
    }


    private LayerPoint getCorresponding(DrawingPoint point){
        for (LayerPoint lp : this.points){
            if (lp.relatedDrawingPoint == point){
                return lp;
            }
        }

        return null;
    }


    private void redrawRequest(){
        if (this.redrawListener != null){
            this.redrawListener.onRedrawRequest();
        }
    }


    private void clean(){
        this.bitmap.eraseColor(Color.TRANSPARENT);
    }

    private void drawPoint(LayerPoint point) {
        if (point.isHighlighted){
            this.canvas.drawCircle(point.x, point.y, point.radius, this.highlightPaint);
        } else {
            this.canvas.drawCircle(point.x, point.y, point.radius, this.paint);
        }

    }

    private void drawPoints(List<LayerPoint> points) {
        for (LayerPoint point : points){
            this.drawPoint(point);
        }
    }

    private boolean isSomePointsHighlighted(){
        for (LayerPoint point : this.points){
            if (point.isHighlighted) return true;
        }
        return false;
    }





}
