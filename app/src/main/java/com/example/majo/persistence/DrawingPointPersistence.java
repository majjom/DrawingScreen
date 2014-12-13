package com.example.majo.persistence;

import com.example.majo.drawingscreen.DrawingPoint;

import java.util.ArrayList;
import java.util.Random;

public class DrawingPointPersistence implements IDrawingPointPersistence {

    private Random rnd = new Random();

    @Override
    public ArrayList<DrawingPoint> getDrawingPoints() {
        ArrayList<DrawingPoint> result = new ArrayList<DrawingPoint>();

        DrawingPoint previous = getDrawingPoint(0, 0);
        int pointCount = rnd.nextInt(100);
        for (int i = 0; i < pointCount; i++) {
            result.add(previous);
            previous = getDrawingPoint(Math.round(previous.x), Math.round(previous.y));
        }

        return result;
    }

    public DrawingPoint getDrawingPoint(int previousX, int PreviousY){
        int x = previousX + rnd.nextInt(100) + 10;
        int y = PreviousY + rnd.nextInt(100) + 10;
        int radius = rnd.nextInt(20) + 5;
        return new DrawingPoint(x, y, radius);
    }

    public DrawingPoint getDrawingPoint(){
        return getDrawingPoint(100, 100);
    }
}
