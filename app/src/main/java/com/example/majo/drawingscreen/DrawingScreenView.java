package com.example.majo.drawingscreen;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

/**
 * Created by majo on 11-Dec-14.
 */
public class DrawingScreenView extends SubsamplingScaleImageView implements View.OnTouchListener {

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
        return super.onTouchEvent(event);
    }

    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
    }


    private void initialise() {
        setOnTouchListener(this);
        float density = getResources().getDisplayMetrics().densityDpi;

        //strokeWidth = (int)(density/60f);


    }

}
