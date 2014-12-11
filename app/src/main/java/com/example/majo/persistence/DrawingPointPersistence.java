package com.example.majo.persistence;

import com.example.majo.drawingscreen.DrawingPoint;

import java.util.ArrayList;
import java.util.Random;

public class DrawingPointPersistence implements IDrawingPointPersistence {

    @Override
    public ArrayList<DrawingPoint> getDrawingPoints() {
        ArrayList<DrawingPoint> result = new ArrayList<DrawingPoint>();
        Random rnd = new Random();
        int pointCount = rnd.nextInt(100);
        int x = 0;
        int y = 0;
        int radius = 0;
        for (int i = 0; i < pointCount; i++) {
            x += rnd.nextInt(100) + 10;
            y += rnd.nextInt(100) + 10;
            radius = rnd.nextInt(20) + 10;

            result.add(new DrawingPoint(x, y, radius));
        }

        /*
        result.add(new DrawingPoint(100, 100, 20));
        result.add(new DrawingPoint(150, 180, 20));
        result.add(new DrawingPoint(250, 260, 20));
        result.add(new DrawingPoint(320, 500, 20));
        */

        return result;
    }
}
