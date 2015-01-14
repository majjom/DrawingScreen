package com.example.majo.BusinessObjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by moravekm on 19-Dec-14.
 */
public class SchemaMap extends PersistedObject {
    public String name;
    public Date dateCreated;
    protected List<GeoSession> geoSessions;
    protected List<DrawingPoint> drawingPoints;
    protected List<MappedPoint> mappedPoints;

    public SchemaMap(String name){
        this(name, new Date());
    }

    public SchemaMap(String name, Date dateCreated){
        this.name = name;
        this.dateCreated = dateCreated;
        this.geoSessions = new ArrayList<>();
        this.drawingPoints = new ArrayList<>();
        this.mappedPoints = new ArrayList<>();
    }

    public List<GeoSession> getGeoSessions(){
        return this.geoSessions;
    }

    public void addGeoSessions(ArrayList<GeoSession> geoSessions){
        this.geoSessions.addAll(geoSessions);
    }

    public void addGeoSession(GeoSession geoSession){
        ArrayList<GeoSession> tmp = new ArrayList<>();
        tmp.add(geoSession);
        addGeoSessions(tmp);
    }



    public List<DrawingPoint> getDrawingPoints(){
        return this.drawingPoints;
    }

    public void addDrawingPoints(ArrayList<DrawingPoint> drawingPoints){
        this.drawingPoints.addAll(drawingPoints);
    }

    public void addDrawingPoint(DrawingPoint drawingPoint){
        ArrayList<DrawingPoint> tmp = new ArrayList<>();
        tmp.add(drawingPoint);
        addDrawingPoints(tmp);
    }


    public List<MappedPoint> getMappedPoints(){
        return this.mappedPoints;
    }

    public void addMappedPoints(ArrayList<MappedPoint> mappedPoint){
        this.mappedPoints.addAll(mappedPoint);
    }

    public void addMappedPoint(MappedPoint mappedPoint){
        ArrayList<MappedPoint> tmp = new ArrayList<>();
        tmp.add(mappedPoint);
        addMappedPoints(tmp);
    }


}
