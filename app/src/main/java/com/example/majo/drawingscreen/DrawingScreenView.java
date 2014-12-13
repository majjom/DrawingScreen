package com.example.majo.drawingscreen;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.majo.persistence.DrawingPointPersistence;
import com.example.majo.persistence.IDrawingPointPersistence;

import static com.example.majo.drawingscreen.FingerGesture.*;

/**
 * Created by majo on 11-Dec-14.
 */
public class DrawingScreenView extends SubsamplingScaleImageView implements View.OnTouchListener {

    private boolean isDrawingMode = false;

    private BitmapLayer drawingPointsBitmap;
    private BitmapLayer allPointsBitmap;

    private BitmapLayer positionBitmap;

    // for simulating painting
    //private PointF vPrevious;
    //private PointF vStart;
    private int strokeWidth = 5;



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

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        // show the layers
        if (isImageReady()) {
            canvas.save();
            canvas.translate(sourceToViewCoord(0, 0).x, sourceToViewCoord(0, 0).y);
            canvas.scale(getScale(), getScale());

            drawBitmapLayer(this.allPointsBitmap, canvas);
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








    private PointF vStartPoint;
    private PointF vPreviousPoint;
    private boolean isPainting = false;

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
                        this.drawingPointsBitmap.removeLastPoint();
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
                this.drawingPointsBitmap.removeLastPoint();
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





    public void drawPosition(DrawingPoint point){
        initiatePositionBitmapLayer();
        this.positionBitmap.removeAllPoints();
        this.positionBitmap.addPoint(point);
        invalidate();
    }





    public void undoPoint(){
        if (this.drawingPointsBitmap != null){
            this.drawingPointsBitmap.removeLastPoint();
            invalidate();
        }
    }

    public void undoDrawing(){
        if (this.drawingPointsBitmap != null){
            this.drawingPointsBitmap.removeAllPoints();
            invalidate();
        }
    }






    public void submitDrawing(){
        initiateAllPointsBitmapLayer();
        initiateDrawingPointsBitmapLayer();
        this.allPointsBitmap.addPoints(this.drawingPointsBitmap.getPoints());
        this.drawingPointsBitmap.removeAllPoints();
        setAllPointsVisible(true);
        invalidate();
    }



    public boolean isAllPointsVisible(){
        if (this.allPointsBitmap == null) return false;
        return this.allPointsBitmap.isVisible();
    }

    public void toggleAllPointsVisible(){
        setAllPointsVisible(!isAllPointsVisible());
    }

    public void setAllPointsVisible(boolean allPointsVisible){
        initiateAllPointsBitmapLayer();
        this.allPointsBitmap.setVisibility(allPointsVisible);
        invalidate();
    }

    public void loadAllPoints(){
        IDrawingPointPersistence db = new DrawingPointPersistence();
        setShowAllPoints(true);
        //this.allPointsBitmap.addPoints(db.getDrawingPoints());
    }

    public void toggleShowAllPoints(){
        initiateAllPointsBitmapLayer();
        setShowAllPoints(!this.allPointsBitmap.isVisible());
    }

    public void setShowAllPoints(boolean showAllPoints){
        initiateAllPointsBitmapLayer();
        this.allPointsBitmap.setVisibility(showAllPoints);
        invalidate();
    }






    public boolean isDrawingMode(){
        return this.isDrawingMode;
    }

    public void toggleDrawingMode(){
        setDrawingMode(!this.isDrawingMode);
    }

    public void setDrawingMode(boolean isDrawingMode){
        initiateDrawingPointsBitmapLayer();
        this.isDrawingMode = isDrawingMode;
        invalidate();
    }






    private void initiateDrawingPointsBitmapLayer(){
        // if the bitmap is not initiated yet
        if (this.drawingPointsBitmap == null) {
            // but only when the background bitmap is already loaded
            if (getSWidth() > 0 && getSHeight() > 0) {
                this.drawingPointsBitmap = new BitmapLayer(getSWidth(), getSHeight());
            }
        }
    }

    private void initiateAllPointsBitmapLayer(){
        // if the bitmap is not initiated yet
        if (this.allPointsBitmap == null) {
            // but only when the background bitmap is already loaded
            if (getSWidth() > 0 && getSHeight() > 0) {
                this.allPointsBitmap = new BitmapLayer(getSWidth(), getSHeight());
                this.allPointsBitmap.setPaintColor(Color.MAGENTA);
            }
        }
    }

    private void initiatePositionBitmapLayer(){
        // if the bitmap is not initiated yet
        if (this.positionBitmap == null) {
            // but only when the background bitmap is already loaded
            if (getSWidth() > 0 && getSHeight() > 0) {
                this.positionBitmap = new BitmapLayer(getSWidth(), getSHeight());
                this.positionBitmap.setPaintColor(Color.RED);
            }
        }
    }













}
