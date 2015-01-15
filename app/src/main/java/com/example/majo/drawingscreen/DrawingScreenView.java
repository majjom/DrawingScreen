package com.example.majo.drawingscreen;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.majo.BusinessObjects.DrawingPoint;

import java.util.List;

import static com.example.majo.drawingscreen.FingerGesture.*;

/**
 * Created by majo on 11-Dec-14.
 */
public class DrawingScreenView extends SubsamplingScaleImageView implements View.OnTouchListener, IDrawingScreenView {

    private BitmapLayer drawingPointsBitmap;
    private BitmapLayer mappedPointsBitmap;
    private BitmapLayer positionBitmap;

    private int strokeWidth = 5;
    private boolean isDrawingMode;

    private PointF vStartPoint;
    private PointF vPreviousPoint;
    private boolean isPainting = false;


    public DrawingScreenView(Context context) {
        this(context, null);
    }

    public DrawingScreenView(Context context, AttributeSet attr) {
        super(context, attr);

        // max zoom level
        setMinimumDpi(10);

        initialise();
    }

    private void initialise() {
        setOnTouchListener(this);
        float density = getResources().getDisplayMetrics().densityDpi;
        setDebug(true);

    }











   /* touch handling */

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        // 01) normal mode with zooming, touch, double touch....
        if (!isDrawingMode){
            return super.onTouchEvent(event);
        }

        // 02.1) drawing mode where touch is overriden, only one finger
        if (event.getPointerCount() == 1){
            boolean consumed = false;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_1_DOWN:
                    consumed = handleOneFingerGesture(event.getX(), event.getY(), DOWN);
                    break;
                case MotionEvent.ACTION_MOVE:
                    consumed = handleOneFingerGesture(event.getX(), event.getY(), MOVE);
                    consumed = true;
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    consumed = handleOneFingerGesture(event.getX(), event.getY(), UP);
            }
            // Use parent to handle pinch and two-finger pan.
            return consumed || super.onTouchEvent(event);
        }

        // 02.2) drawing mode, but fiddling with more fingers -> more fingers have to undo the first point as well. First fingers handled like UP gesture with one finger
        handleMultiFingerGesture(event.getX(), event.getY());
        return super.onTouchEvent(event);
    }

    /**
     *
     * @param x
     * @param y
     * @param gesture
     * @return returns TRUE if the gesture has been handled by routine and the parent shall not continue
     */
    private boolean handleOneFingerGesture(float x, float y, FingerGesture gesture){
        if (this.drawingPointsBitmap == null) return false;
        boolean handledDrawing = false;
        switch (gesture) {
            case DOWN:
                vStartPoint = new PointF(x, y);
                addDrawingPoint(x, y);
                break;
            case UP:
                if (vStartPoint!= null) {
                    if (isPainting) {
                        addDrawingPoint(x, y);
                    } else {
                        // the starting point was given to the drawing, it is the only one -> we are not drawing, so lets remove it
                        removeLastPoint();
                    }
                }
                // reset
                resetDrawing();
                break;
            case MOVE:
                if (vStartPoint!= null) {
                    if (vPreviousPoint == null) {
                        vPreviousPoint = vStartPoint;
                    }

                    // if reaching some distance, we are drawing and have the next point to get
                    float vDX = Math.abs(x - vPreviousPoint.x);
                    float vDY = Math.abs(y - vPreviousPoint.y);
                    if (vDX >= strokeWidth * 5 || vDY >= strokeWidth * 5) {
                        handledDrawing = true;
                        isPainting = true;
                        vPreviousPoint = new PointF(x, y);
                        addDrawingPoint(x, y);
                    }
                }
                break;
        }

        invalidate();
        return handledDrawing;
    }

    /**
     * Almost the same behavior as one finger when lifting up
     * @param x
     * @param y
     */
    private void handleMultiFingerGesture(float x, float y){
        if (vStartPoint!= null) {
            if (!isPainting) {
                // the starting point was given to the drawing, it is the only one -> we are not drawing, so lets remove it
                removeLastPoint();
            }
        }
        resetDrawing();
        invalidate();
    }

    private void resetDrawing(){
        isPainting = false;
        vStartPoint = null;
        vPreviousPoint = null;
    }

    private void addDrawingPoint(float vX, float vY){
        PointF viewPoint = new PointF(vX, vY);
        this.drawingPointsBitmap.addPoint(new DrawingPoint(viewToSourceCoord(viewPoint).x, viewToSourceCoord(viewPoint).y, 10));
    }

    private void removeLastPoint(){
        this.drawingPointsBitmap.removeLastPoint();
    }












    /* drawing*/
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        // show the layers
        if (isImageReady()) {
            canvas.save();
            canvas.translate(sourceToViewCoord(0, 0).x, sourceToViewCoord(0, 0).y);
            canvas.scale(getScale(), getScale());

            drawBitmapLayer(this.mappedPointsBitmap, canvas);
            drawBitmapLayer(this.drawingPointsBitmap, canvas);
            drawBitmapLayer(this.positionBitmap, canvas);
            canvas.restore();
        }
    }

    private void drawBitmapLayer(BitmapLayer bitmapLayer, Canvas canvas){
        if (bitmapLayer != null && bitmapLayer.isVisible()) {
            canvas.drawBitmap(bitmapLayer.getBitmap(), 0, 0, null);
        }
    }




























    public boolean isDrawingMode(){
        return this.isDrawingMode;
    }

    public void setDrawingMode(boolean isDrawingMode){
        this.isDrawingMode = isDrawingMode;
    }



    @Override
    public int getStrokeWidth() {
        return this.strokeWidth;
    }

    @Override
    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    @Override
    public void setRadius(int radius) {
        this.drawingPointsBitmap.setRadius(radius);
    }

    @Override
    public int getRadius() {
        return this.drawingPointsBitmap.getRadius();
    }

    @Override
    public void setColor(int color) {
        this.drawingPointsBitmap.setColor(color);
    }

    @Override
    public int getColor() {
        return this.drawingPointsBitmap.getColor();
    }

    @Override
    public List<DrawingPoint> getPoints() {
        return this.drawingPointsBitmap.getPoints();
    }

    @Override
    public void addPoint(DrawingPoint point) {
        this.drawingPointsBitmap.addPoint(point);
    }

    @Override
    public void addPoints(List<DrawingPoint> points) {
        this.drawingPointsBitmap.addPoints(points);
    }

    @Override
    public void removePoint(DrawingPoint point) {
        this.drawingPointsBitmap.removePoint(point);
    }

    @Override
    public void removeAllPoints() {
        this.drawingPointsBitmap.removeAllPoints();
    }




    @Override
    public void show() {
        this.drawingPointsBitmap.setVisibility(true);
    }

    @Override
    public void hide() {
        this.drawingPointsBitmap.setVisibility(true);
    }






    @Override
    public void loadMappedPoints(List<DrawingPoint> points) {
        this.mappedPointsBitmap.removeAllPoints();
        this.mappedPointsBitmap.addPoints(points);
    }

    @Override
    public void showMappedPoints(){
        this.mappedPointsBitmap.setVisibility(true);
    }

    @Override
    public void showMappedPoints(int color, int radius) {
        this.mappedPointsBitmap.setVisibility(true);
        this.mappedPointsBitmap.setColor(color);
        this.mappedPointsBitmap.setRadius(radius);
    }

    @Override
    public void hideMappedPoints() {
        this.mappedPointsBitmap.setVisibility(false);
    }





























}
