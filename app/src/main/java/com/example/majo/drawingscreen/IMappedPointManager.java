package com.example.majo.drawingscreen;

import com.example.majo.BusinessObjects.DrawingPoint;
import com.example.majo.BusinessObjects.MappedPoint;

import java.util.List;

/**
 * Created by moravekm on 21-Jan-15.
 */
public interface IMappedPointManager {
    // data
    int getSchemaMapId();

    void refreshPointsFromDb();

    List<MappedPoint> getPoints();

    void removeAllPoints();
    void removePoint(MappedPoint point);

    // highlighting
    List<MappedPoint> getHighlightedPoints();
    void toggleHighlightPoint(MappedPoint point);
    void toggleHighlightPoint(float x, float y);
    void clearHighlight();


    // color management
    void setHighlightColor(int color);
    int getHighlightColor();

    void setColor(int color);
    int getColor();
}
