package com.example.majo.BusinessObjects;

import com.example.majo.persistence.IGeoLocationPersistence;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by moravekm on 19-Dec-14.
 */

/**
 * The proxy supporting LAZY loading of the children.
 */
public class GeoSessionProxy extends GeoSession {

    private IGeoLocationPersistence childrenDb;
    private boolean hasLoadedGeoLocations;

    public GeoSessionProxy(String name, Date dateCreated, IGeoLocationPersistence childrenDb) {
        super(name, dateCreated);
        this.childrenDb = childrenDb;
        this.hasLoadedGeoLocations = false;
    }

    @Override
    public ArrayList<GeoLocation> getGeoLocations(){
        if (this.hasLoadedGeoLocations == false){
            this.geoLocations = this.childrenDb.getAllLocations(this.id);
            this.hasLoadedGeoLocations = true;
            return this.geoLocations;
        }
        else
        {
            return this.geoLocations;
        }
    }
}
