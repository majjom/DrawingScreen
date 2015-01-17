package com.example.majo.drawingscreen;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.util.ArrayList;
import java.util.List;

import static com.example.majo.drawingscreen.FingerGesture.*;

/**
 * Created by majo on 11-Dec-14.
 * Knows how to draw bitmaps as layers
 * Knows to get the on touch events
 *
 * Communication
 * Finger -> this[View.OnTouchListener]
 * layer -> this[IOnBitmapLayerRedraw]
 * Activity -> this[IDrawingScreenView]
 *
 * This -> layers[IBitmapLayer]
 * This -> Activity[OnPointListener]
 */
public class DrawingScreenView extends SubsamplingScaleImageView implements View.OnTouchListener, IOnBitmapLayerRedraw, IDrawingScreenView {

    /*View.OnTouchListener*/
    private PointF vStartPoint;
    private PointF vPreviousPoint;
    private boolean isPainting = false;

    /*IDrawingScreenView*/
    private int strokeWidth;
    private boolean isDrawingMode;



    IOnPointListener onPointListener;

    List<IBitmapLayer> layers;


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

        this.vStartPoint = null;
        this.vPreviousPoint = null;
        this.isPainting = false;

        this.strokeWidth = 5;
        this.isDrawingMode = false;

        this.layers = new ArrayList<>();

        this.onPointListener = null;
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
        if (!hasVisibleLayer()) return false;

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
                        removeLastDrawingPoint();
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
                removeLastDrawingPoint();
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




    /*inform all onPointListeners*/
    private void addDrawingPoint(float vX, float vY){
        if (this.onPointListener != null){
            PointF viewPoint = new PointF(vX, vY);
            this.onPointListener.addDrawingPoint(viewToSourceCoord(viewPoint).x, viewToSourceCoord(viewPoint).y);
        }
    }

    private void removeLastDrawingPoint(){
        if (this.onPointListener != null){
            this.onPointListener.removeLastDrawingPoint();
        }
    }




















    private boolean hasVisibleLayer(){
        if (this.layers.size() == 0){
            return false;
        }

        for (IBitmapLayer layer : this.layers){
            if (layer.isVisible()) return true;
        }

        return false;
    }









    /* drawing*/
    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        // show the layers
        if (isImageReady()) {
            if (hasVisibleLayer()) {
                canvas.save();
                canvas.translate(sourceToViewCoord(0, 0).x, sourceToViewCoord(0, 0).y);
                canvas.scale(getScale(), getScale());

                for (IBitmapLayer layer : this.layers) {
                    if (layer.isVisible()) {
                        canvas.drawBitmap(layer.getBitmap(), 0, 0, null);
                    }
                }

                canvas.restore();
            }
        }
    }








    /*layer mgmt*/
    @Override
    public void addLayer(IBitmapLayer layer){
        if (layer != null){
            layer.attachToRedrawListener(this);
            this.layers.add(layer);
        }
    }










    @Override
    public boolean isDrawingMode(){
        return this.isDrawingMode;
    }

    @Override
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





    public void setPointListener(IOnPointListener listener){
        this.onPointListener = listener;
    }






    @Override
    public void onRedrawRequest() {
        this.invalidate();
    }
}
