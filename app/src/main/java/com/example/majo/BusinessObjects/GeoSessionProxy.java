package com.example.majo.BusinessObjects;

import com.example.majo.persistenceLocalDatabase.IGeoLocationPersistence;

import java.util.Date;
import java.util.List;

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
    public List<GeoLocation> getGeoLocations(){
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

    public String toString(){
        return String.format("%s %s %s no:%s %s", id, name, android.text.format.DateFormat.format("yyy-MM-dd hh:mm:ss", dateCreated), String.valueOf(this.getGeoLocations().size()), isBeingTracked ? "Tracked" : "");
    }
}
