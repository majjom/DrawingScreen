package com.example.majo.persistence;

import com.example.majo.BusinessObjects.DrawingPoint;

import java.util.ArrayList;
import java.util.Random;

public class RandomDrawingPointPersistence implements IDrawingPointPersistence {

    private Random rnd = new Random();

    @Override
    public ArrayList<DrawingPoint> getAllPoints() {
        ArrayList<DrawingPoint> result = new ArrayList<DrawingPoint>();

        DrawingPoint previous = getRandomDrawingPoint(0, 0);
        int pointCount = rnd.nextInt(100);
        for (int i = 0; i < pointCount; i++) {
            result.add(previous);
            previous = getRandomDrawingPoint(Math.round(previous.x), Math.round(previous.y));
        }

        return result;
    }

    @Override
    public void addPoints(ArrayList<DrawingPoint> points) {
        // TODO
    }

    @Override
    public void deleteAllPoints() {

    }

    public DrawingPoint getRandomDrawingPoint(int previousX, int PreviousY){
        int x = previousX + rnd.nextInt(100) + 10;
        int y = PreviousY + rnd.nextInt(100) + 10;
        int radius = rnd.nextInt(20) + 5;
        return new DrawingPoint(x, y, radius);
    }
}
