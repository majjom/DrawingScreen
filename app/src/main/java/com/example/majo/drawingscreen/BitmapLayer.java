package com.example.majo.drawingscreen;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.example.majo.BusinessObjects.DrawingPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by majo on 12-Dec-14.
 */
public class BitmapLayer implements IBitmapLayer {
    private Canvas canvas;
    private Bitmap bitmap;
    private Paint paint;
    private List<DrawingPoint> points;
    private boolean isVisible;

    IBitmapLayerRedrawListener redrawListener;

    private int radius;

    public BitmapLayer(int width, int height, IBitmapLayerRedrawListener redrawListener){
        this.bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        this.canvas = new Canvas(this.bitmap);
        this.points = new ArrayList<>();
        this.isVisible = true;
        this.radius = 10;

        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
    }



    public void setVisibility(boolean isVisible){
        this.isVisible = isVisible;
        this.redrawListener.onRedrawRequest();
    }

    public boolean isVisible(){
        return this.isVisible;
    }

    public void setColor(int color){
        this.paint.setColor(color);
        clean();
        drawPoints(this.points);
        this.redrawListener.onRedrawRequest();
    }

    public int getColor(){
        return this.paint.getColor();
    }

    public void setRadius(int radius){
        this.radius = radius;
        clean();
        drawPoints(this.points);
        this.redrawListener.onRedrawRequest();
    }

    public int getRadius(){
        return this.radius;
    }






    public Bitmap getBitmap(){
        return this.bitmap;
    }










    public List<DrawingPoint> getPoints(){
        return this.points;
    }

    public void removeAllPoints(){
        this.points = new ArrayList<>();
        clean();
        this.redrawListener.onRedrawRequest();
    }

    public void removeLastPoint() {
        removeLastPoints(1);
    }

    public void removeLastPoints(int count) {
        if (this.points.size() > 0 && count > 0) {
            int countToRemove = Math.min(this.points.size(), count);
            for (int i = 0; i < countToRemove; i++) {
                removeLast();
            }
            clean();
            drawPoints(this.points);
        }
        this.redrawListener.onRedrawRequest();
    }

    @Override
    public void removePoint(DrawingPoint point) {
        if (this.points.remove(point)){
            clean();
            drawPoints(this.points);
            this.redrawListener.onRedrawRequest();
        }
    }


    public void addPoint(DrawingPoint point) {
        this.points.add(point);
        drawPoint(point);
        this.redrawListener.onRedrawRequest();
    }

    public void addPoints(List<DrawingPoint> points) {
        for (DrawingPoint point : points){
            this.addPoint(point);
        }
        this.redrawListener.onRedrawRequest();
    }


    private void clean(){
        this.bitmap.eraseColor(Color.TRANSPARENT);
    }

    private void removeLast(){
        if (this.points.size() > 0){
            this.points.remove(this.points.size() - 1);
        }
    }

    private void drawPoint(DrawingPoint point) {
        this.canvas.drawCircle(point.x, point.y, this.radius, this.paint);
    }

    private void drawPoints(List<DrawingPoint> points) {
        for (DrawingPoint point : points){
            this.drawPoint(point);
        }
    }





}
