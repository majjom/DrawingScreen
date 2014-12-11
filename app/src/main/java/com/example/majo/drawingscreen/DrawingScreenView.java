package com.example.majo.drawingscreen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by majo on 11-Dec-14.
 */
public class DrawingScreenView extends SubsamplingScaleImageView implements View.OnTouchListener {

    private boolean isDrawingMode = false;

    private Canvas pointCanvas;
    private Bitmap pointBitmap;
    private boolean pointLayerVisible = false;


    // for simulating painting
    private PointF vPrevious;
    private PointF vStart;
    private int strokeWidth = 10;



    public DrawingScreenView(Context context, AttributeSet attr) {
        super(context, attr);
        initialise();
    }

    public DrawingScreenView(Context context) {
        this(context, null);
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event){
        if (!isDrawingMode){
            return super.onTouchEvent(event);
        }

        boolean consumed = false;
        int touchCount = event.getPointerCount();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_1_DOWN:
                vStart = new PointF(event.getX(), event.getY());
                vPrevious = new PointF(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_POINTER_2_DOWN:
                // Abort any current isDrawing, user is zooming
                vStart = null;
                vPrevious = null;
                break;
            case MotionEvent.ACTION_MOVE:
                PointF sCurrentF = viewToSourceCoord(event.getX(), event.getY());
                PointF sCurrent = new PointF(sCurrentF.x, sCurrentF.y);
                PointF sStart = vStart == null ? null : new PointF(viewToSourceCoord(vStart).x, viewToSourceCoord(vStart).y);

                if (touchCount == 1 && vStart != null) {
                    float vDX = Math.abs(event.getX() - vPrevious.x);
                    float vDY = Math.abs(event.getY() - vPrevious.y);
                    if (vDX >= strokeWidth * 5 || vDY >= strokeWidth * 5) {
                        addPoint(new DrawingPoint(sCurrent.x, sCurrent.y, strokeWidth));

                        vPrevious.x = event.getX();
                        vPrevious.y = event.getY();
                    }
                    consumed = true;
                    invalidate();
                } else if (touchCount == 1) {
                    // Consume all one touch drags to prevent odd panning effects handled by the superclass.
                    consumed = true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                invalidate();
                vPrevious = null;
                vStart = null;
        }
        // Use parent to handle pinch and two-finger pan.
        return consumed || super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        // draw point layer
        if (pointLayerVisible) {
            if (pointBitmap != null) {
                canvas.save();
                canvas.translate(sourceToViewCoord(0, 0).x, sourceToViewCoord(0, 0).y);
                canvas.scale(getScale(), getScale());
                canvas.drawBitmap(pointBitmap, 0, 0, null);
                canvas.restore();
            }
        }
    }















    public void toggleDrawingMode(){
        this.isDrawingMode = !this.isDrawingMode;
        if (isDrawingMode){
            setPointLayerVisible(true);
        }
    }

    public void setDrawingMode(boolean isDrawingMode){
        this.isDrawingMode = isDrawingMode;
        if (isDrawingMode){
            setPointLayerVisible(true);
        }
    }

    public void setPointLayerVisible(boolean isVisible){
        this.pointLayerVisible = isVisible;
        invalidate();
    }

    public void togglePointLayerVisible(){
        this.pointLayerVisible = !this.pointLayerVisible;
        invalidate();
    }















    public void addPoint(DrawingPoint point) {
        initialiseLayer();
        pointCanvas.drawCircle(point.x, point.y, point.radius, getPointPaint());
        invalidate();
    }

    public void addPoints(ArrayList<DrawingPoint> points) {
        initialiseLayer();
        for(DrawingPoint drawingPoint : points){
            pointCanvas.drawCircle(drawingPoint.x, drawingPoint.y, drawingPoint.radius, getPointPaint());
        }
        invalidate();
    }

    public void erasePointLayer(){
        initialiseLayer();
        pointBitmap.eraseColor(Color.TRANSPARENT);
        invalidate();
    }









    private void initialiseLayer(){
        if (pointCanvas == null){
            pointBitmap = Bitmap.createBitmap(getSWidth(), getSHeight(), Bitmap.Config.ARGB_8888);
            pointCanvas = new Canvas(pointBitmap);
        }
    }

    private Paint getPointPaint(){
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(strokeWidth / 3);
        paint.setStyle(Paint.Style.FILL);

        return paint;
    }

    private void initialise() {
        setOnTouchListener(this);
        float density = getResources().getDisplayMetrics().densityDpi;

        //strokeWidth = (int)(density/60f);
    }

}
