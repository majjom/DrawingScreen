package com.example.majo.BusinessObjects;

/**
 * Created by moravekm on 19-Dec-14.
 */

import com.example.majo.persistence.IDrawingPointPersistence;
import com.example.majo.persistence.IGeoSessionPersistence;
import com.example.majo.persistence.IMappedPointsPersistence;

import java.util.ArrayList;
import java.util.Date;

/**
 * The proxy supporting LAZY loading of the children.
 */
public class SchemaMapProxy extends SchemaMap {
    private IGeoSessionPersistence sessionChildrenDb;
    private boolean hasLoadedGeoSessions;

    private IDrawingPointPersistence drawingPointsChildrenDb;
    private boolean hasLoadedDrawingPoints;

    private IMappedPointsPersistence mappedPointsChildrenDb;
    private boolean hasLoadedMappedPoints;

    public SchemaMapProxy(String name, Date dateCreated, IGeoSessionPersistence sessionChildrenDb, IDrawingPointPersistence drawingPointsChildrenDb, IMappedPointsPersistence mappedPointsChildrenDb) {
        super(name, dateCreated);

        this.sessionChildrenDb = sessionChildrenDb;
        this.hasLoadedGeoSessions = false;

        this.drawingPointsChildrenDb = drawingPointsChildrenDb;
        this.hasLoadedDrawingPoints = false;

        this.mappedPointsChildrenDb = mappedPointsChildrenDb;
        this.hasLoadedMappedPoints = false;
    }

    @Override
    public ArrayList<GeoSession> getGeoSessions(){
        if (this.hasLoadedGeoSessions == false){
            this.geoSessions = this.sessionChildrenDb.getAllGeoSessions(this.id);
            this.hasLoadedGeoSessions = true;
            return this.geoSessions;
        }
        else
        {
            return this.geoSessions;
        }
    }

    @Override
    public ArrayList<DrawingPoint> getDrawingPoints(){
        if (this.hasLoadedDrawingPoints == false){
            this.drawingPoints = this.drawingPointsChildrenDb.getAllPoints(this.id);
            this.hasLoadedDrawingPoints = true;
            return this.drawingPoints;
        }
        else
        {
            return this.drawingPoints;
        }
    }

    @Override
    public ArrayList<MappedPoint> getMappedPoints(){
        if (this.hasLoadedMappedPoints == false){
            this.mappedPoints = this.mappedPointsChildrenDb.getAllPoints(this.id);
            this.hasLoadedMappedPoints = true;
            return this.mappedPoints;
        }
        else
        {
            return this.mappedPoints;
        }
    }
}
