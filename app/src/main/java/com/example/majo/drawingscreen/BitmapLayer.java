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
public class BitmapLayer {
    private Canvas canvas;
    private Bitmap bitmap;
    private Paint paint;
    private ArrayList<DrawingPoint> points;
    private boolean isVisible;

    public BitmapLayer(int width, int height){
        this.bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        this.canvas = new Canvas(this.bitmap);
        this.points = new ArrayList<>();
        this.isVisible = true;

        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
    }



    public void setVisibility(boolean isVisible){
        this.isVisible = isVisible;
    }

    public void toggleVisibility(){
        setVisibility(!this.isVisible);
    }

    public boolean isVisible(){
        return this.isVisible;
    }



    public ArrayList<DrawingPoint> getPoints(){
        return this.points;
    }




    public Bitmap getBitmap(){
        return this.bitmap;
    }


    public void setPaintColor(int color){
        this.paint.setColor(color);
    }




    public void clean(){
        this.bitmap.eraseColor(Color.TRANSPARENT);
    }





    public void removeAllPoints(){
        this.points = new ArrayList<>();
        clean();
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
    }

    private void removeLast(){
        if (this.points.size() > 0){
            this.points.remove(this.points.size() - 1);
        }
    }






    public void addPoint(DrawingPoint point) {
        this.points.add(point);
        drawPoint(point);
    }


    public void addPoints(List<DrawingPoint> points) {
        for (DrawingPoint point : points){
            this.addPoint(point);
        }
    }

    private void drawPoint(DrawingPoint point) {
        this.canvas.drawCircle(point.x, point.y, point.radius, this.paint);
    }
    private void drawPoints(ArrayList<DrawingPoint> points) {
        for (DrawingPoint point : points){
            this.drawPoint(point);
        }
    }





}
