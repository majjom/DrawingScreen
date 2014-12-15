package com.example.majo.drawingscreen.merging;

import android.test.InstrumentationTestCase;

import com.example.majo.merging.CartesianInterpolation;
import com.example.majo.merging.CartesianPoint;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by majo on 15-Dec-14.
 */
public class CartesianInterpolationTest extends InstrumentationTestCase {

    public void testGetNextPoint_Border(){
        // arrange
        CartesianPoint base = new CartesianPoint(10,20);
        Queue<CartesianPoint> points = new LinkedList<>();
        int radius = 15;

        // act
        CartesianPoint result1 = CartesianInterpolation.getNextPointIterative(null, radius, points);
        CartesianPoint result21 = CartesianInterpolation.getNextPointIterative(base, radius, null);
        CartesianPoint result22 = CartesianInterpolation.getNextPointIterative(base, 0, points);
        CartesianPoint result3 = CartesianInterpolation.getNextPointIterative(base, 0, points);

        // assert
        assertNull(result1);
        assertNull(result21);
        assertNull(result22);
        assertNull(result3);
    }

    public void testGetNextPoint_One_DistGreater(){
        // arrange
        CartesianPoint base = new CartesianPoint(10,20);

        CartesianPoint point01 = new CartesianPoint(20,20);
        Queue<CartesianPoint> points = new LinkedList<>();
        points.add(point01);
        int radius = 15;

        // act
        CartesianPoint result = CartesianInterpolation.getNextPointIterative(base, radius, points);

        // assert
        assertNull(result);
        assertEquals(0, points.size());
    }

    public void testGetNextPoint_One_DistLower(){
        // arrange
        CartesianPoint base = new CartesianPoint(10,20);

        CartesianPoint point01 = new CartesianPoint(20,20);
        Queue<CartesianPoint> points = new LinkedList<>();
        points.add(point01);
        int radius = 8;

        // act
        CartesianPoint result = CartesianInterpolation.getNextPointIterative(base, radius, points);

        // assert
        assertNotNull(result);
        assertEquals(1, points.size());
        assertEquals(18, result.x);
        assertEquals(20, result.y);
    }

    public void testGetNextPoint_One_DistLowerVertical(){
        // arrange
        CartesianPoint base = new CartesianPoint(20,10);

        CartesianPoint point01 = new CartesianPoint(20,20);
        Queue<CartesianPoint> points = new LinkedList<>();
        points.add(point01);
        int radius = 8;

        // act
        CartesianPoint result = CartesianInterpolation.getNextPointIterative(base, radius, points);

        // assert
        assertNotNull(result);
        assertEquals(1, points.size());
        assertEquals(20, result.x);
        assertEquals(18, result.y);
    }

    public void testGetNextPoint_One_DistExact(){
        // arrange
        CartesianPoint base = new CartesianPoint(10,20);

        CartesianPoint point01 = new CartesianPoint(20,20);
        Queue<CartesianPoint> points = new LinkedList<>();
        points.add(point01);
        int radius = 10;

        // act
        CartesianPoint result = CartesianInterpolation.getNextPointIterative(base, radius, points);

        // assert
        assertNotNull(result);
        assertEquals(0, points.size());
        assertEquals(20, result.x);
        assertEquals(20, result.y);
    }

    public void testGetNextPoint_Two_InAndOut(){
        // arrange
        CartesianPoint base = new CartesianPoint(10,20);

        CartesianPoint point01 = new CartesianPoint(20,20);
        CartesianPoint point02 = new CartesianPoint(30,20);
        Queue<CartesianPoint> points = new LinkedList<>();
        points.add(point01);
        points.add(point02);

        int radius = 15;

        // act
        CartesianPoint result = CartesianInterpolation.getNextPointIterative(base, radius, points);

        // assert
        assertNotNull(result);
        assertEquals(1, points.size());
        assertEquals(25, result.x);
        assertEquals(20, result.y);
    }

    public void testGetNextPoint_Two_InBoth(){
        // arrange
        CartesianPoint base = new CartesianPoint(10,20);

        CartesianPoint point01 = new CartesianPoint(20,20);
        CartesianPoint point02 = new CartesianPoint(30,20);
        Queue<CartesianPoint> points = new LinkedList<>();
        points.add(point01);
        points.add(point02);

        int radius = 25;

        // act
        CartesianPoint result = CartesianInterpolation.getNextPointIterative(base, radius, points);

        // assert
        assertNull(result);
        assertEquals(0, points.size());
    }

    public void testGetNextPoint_Two_OutBoth(){
        // arrange
        CartesianPoint base = new CartesianPoint(10,20);

        CartesianPoint point01 = new CartesianPoint(20,20);
        CartesianPoint point02 = new CartesianPoint(30,20);
        Queue<CartesianPoint> points = new LinkedList<>();
        points.add(point01);
        points.add(point02);

        int radius = 5;

        // act
        CartesianPoint result = CartesianInterpolation.getNextPointIterative(base, radius, points);

        // assert
        assertNotNull(result);
        assertEquals(2, points.size());
        assertEquals(15, result.x);
        assertEquals(20, result.y);
    }

    public void testInterpolateByDistance_Two(){
        // arrange
        CartesianPoint point01 = new CartesianPoint(10,20);
        CartesianPoint point02 = new CartesianPoint(20,20);
        ArrayList<CartesianPoint> points = new ArrayList<>();
        points.add(point01);
        points.add(point02);

        int distance = 3;

        // act
        ArrayList<CartesianPoint> result = CartesianInterpolation.interpolateByDistance(points, distance);

        // assert
        assertNotNull(result);
        assertEquals(4, result.size());
        assertEquals(10, result.get(0).x);
        assertEquals(20, result.get(0).y);
        assertEquals(13, result.get(1).x);
        assertEquals(20, result.get(1).y);
        assertEquals(16, result.get(2).x);
        assertEquals(20, result.get(2).y);
        assertEquals(19, result.get(3).x);
        assertEquals(20, result.get(3).y);
    }

    public void testInterpolateByDistance_Three(){
        // arrange
        CartesianPoint point01 = new CartesianPoint(10,20);
        CartesianPoint point02 = new CartesianPoint(20,20);
        CartesianPoint point03 = new CartesianPoint(30,20);
        ArrayList<CartesianPoint> points = new ArrayList<>();
        points.add(point01);
        points.add(point02);
        points.add(point03);

        int distance = 3;

        // act
        ArrayList<CartesianPoint> result = CartesianInterpolation.interpolateByDistance(points, distance);

        // assert
        assertNotNull(result);
        assertEquals(7, result.size());
        assertEquals(10, result.get(0).x);
        assertEquals(20, result.get(0).y);
        assertEquals(13, result.get(1).x);
        assertEquals(20, result.get(1).y);
        assertEquals(16, result.get(2).x);
        assertEquals(20, result.get(2).y);
        assertEquals(19, result.get(3).x);
        assertEquals(20, result.get(3).y);
        assertEquals(22, result.get(4).x);
        assertEquals(20, result.get(4).y);
        assertEquals(25, result.get(5).x);
        assertEquals(20, result.get(5).y);
        assertEquals(28, result.get(6).x);
        assertEquals(20, result.get(6).y);
    }

    public void testInterpolateByCount_Two_Three(){
        // arrange
        CartesianPoint point01 = new CartesianPoint(10,20);
        CartesianPoint point02 = new CartesianPoint(20,20);
        ArrayList<CartesianPoint> points = new ArrayList<>();
        points.add(point01);
        points.add(point02);

        int count = 3;

        // act
        ArrayList<CartesianPoint> result = CartesianInterpolation.interpolateByCount(points, count);

        // assert
        assertNotNull(result);
        assertEquals(count, result.size());

        assertEquals(10, result.get(0).x);
        assertEquals(20, result.get(0).y);
        assertEquals(15, result.get(1).x);
        assertEquals(20, result.get(1).y);
        assertEquals(20, result.get(2).x);
        assertEquals(20, result.get(2).y);
    }

    public void testInterpolateByCount_Three_Ten(){
        // arrange
        CartesianPoint point01 = new CartesianPoint(10,20);
        CartesianPoint point02 = new CartesianPoint(20,20);
        CartesianPoint point03 = new CartesianPoint(30,20);
        ArrayList<CartesianPoint> points = new ArrayList<>();
        points.add(point01);
        points.add(point02);
        points.add(point03);

        int count = 10;

        // act
        ArrayList<CartesianPoint> result = CartesianInterpolation.interpolateByCount(points, count);

        // assert
        assertNotNull(result);
        assertEquals(count, result.size());

        assertEquals(10, result.get(0).x);
        assertEquals(20, result.get(0).y);


    }
}
